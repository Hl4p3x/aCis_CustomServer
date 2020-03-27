package net.sf.l2j.gameserver.model.actor.ai.type;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.enums.items.ArmorType;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.ai.Desire;
import net.sf.l2j.gameserver.model.actor.instance.StaticObject;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.AutoAttackStart;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.ItemsOnGroundTaskManager;

public class PlayerAI extends PlayableAI
{
	private boolean _thinking; // to prevent recursive thinking
	private Desire _nextIntention = new Desire();
	
	public PlayerAI(Player player)
	{
		super(player);
	}
	
	@Override
	public Player getActor()
	{
		return (Player) _actor;
	}
	
	@Override
	protected void clientActionFailed()
	{
		_actor.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public Desire getNextIntention()
	{
		return _nextIntention;
	}
	
	@Override
	synchronized void changeIntention(IntentionType intention, Object arg0, Object arg1)
	{
		// do nothing unless CAST intention
		// however, forget interrupted actions when starting to use an offensive skill
		if (intention != IntentionType.CAST || (arg0 != null && ((L2Skill) arg0).isOffensive()))
		{
			_nextIntention.reset();
			super.changeIntention(intention, arg0, arg1);
			return;
		}
		
		// do nothing if next intention is same as current one.
		if (_desire.equals(intention, arg0, arg1))
			return;
		
		// save current intention so it can be used after cast
		_nextIntention.update(_desire);
		
		super.changeIntention(intention, arg0, arg1);
	}
	
	@Override
	protected void onEvtReadyToAct()
	{
		// Launch actions corresponding to the Event Think
		if (!_nextIntention.isBlank())
		{
			setIntention(_nextIntention.getIntention(), _nextIntention.getFirstParameter(), _nextIntention.getSecondParameter());
			_nextIntention.reset();
		}
		super.onEvtReadyToAct();
	}
	
	@Override
	protected void onEvtCancel()
	{
		_nextIntention.reset();
		super.onEvtCancel();
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
		if (_desire.getIntention() == IntentionType.CAST)
		{
			final L2Skill skill = (L2Skill) _desire.getFirstParameter();
			
			// Automatically attack target if skill is flagged as nextActionIsAttack, but only if registered next intention is different than MOVE_TO.
			if (skill.nextActionIsAttack() && getTarget().isAutoAttackable(_actor) && _nextIntention.getIntention() != IntentionType.MOVE_TO)
				_nextIntention.update(IntentionType.ATTACK, getTarget(), null);
			
			if (!_nextIntention.isBlank() && _nextIntention.getIntention() != IntentionType.CAST) // previous state shouldn't be casting
				setIntention(_nextIntention.getIntention(), _nextIntention.getFirstParameter(), _nextIntention.getSecondParameter());
			else
				setIntention(IntentionType.IDLE);
		}
	}
	
	@Override
	protected void onIntentionRest()
	{
		if (_desire.getIntention() != IntentionType.REST)
		{
			changeIntention(IntentionType.REST, null, null);
			setTarget(null);
			_actor.getMove().stopMove();
		}
	}
	
	@Override
	protected void onIntentionActive()
	{
		setIntention(IntentionType.IDLE);
	}
	
	@Override
	protected void onIntentionMoveTo(Location loc)
	{
		// Deny the action if we are currently resting.
		if (_desire.getIntention() == IntentionType.REST)
		{
			clientActionFailed();
			return;
		}
		
		// We delay MOVE_TO intention if character is disabled or is currently casting/attacking.
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isAttackingNow())
		{
			clientActionFailed();
			_nextIntention.update(IntentionType.MOVE_TO, loc, null);
			return;
		}
		
		// Set the Intention of this AbstractAI to MOVE_TO
		changeIntention(IntentionType.MOVE_TO, loc, null);
		
		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet MoveToLocation (broadcast)
		_actor.getMove().moveToLocation(loc);
	}
	
	@Override
	protected void onIntentionInteract(WorldObject object)
	{
		// Deny the action if we are currently resting.
		if (_desire.getIntention() == IntentionType.REST)
		{
			clientActionFailed();
			return;
		}
		
		// We delay INTERACT intention if character is disabled or is currently casting.
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow())
		{
			clientActionFailed();
			_nextIntention.update(IntentionType.INTERACT, object, null);
			return;
		}
		
		// Set the Intention of this AbstractAI to INTERACT
		changeIntention(IntentionType.INTERACT, object, null);
		
		// Set the AI interact target
		setTarget(object);
		
		// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
		_actor.getMove().moveToPawn(object, 60);
	}
	
	@Override
	public void startAttackStance()
	{
		// Initial check ; if the actor wasn't yet registered into AttackStanceTaskManager, broadcast AutoAttackStart packet. Check if a summon exists, if so, broadcast AutoAttackStart packet for the summon.
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(_actor))
		{
			final Summon summon = ((Player) _actor).getSummon();
			if (summon != null)
				summon.broadcastPacket(new AutoAttackStart(summon.getObjectId()));
			
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
		}
		
		// Set out of the initial if check to be able to refresh the time.
		AttackStanceTaskManager.getInstance().add(_actor);
	}
	
	private void thinkAttack()
	{
		final Creature target = (Creature) getTarget();
		if (target == null)
		{
			setTarget(null);
			setIntention(IntentionType.ACTIVE);
			return;
		}
		
		if (_actor.getMove().maybeMoveToPawn(target, _actor.getPhysicalAttackRange()))
			return;
		
		_actor.doAttack(target);
	}
	
	private void thinkCast()
	{
		Creature target = (Creature) getTarget();
		
		if (_skill.getTargetType() == SkillTargetType.TARGET_GROUND)
		{
			if (_actor.getMove().maybeMoveToPosition(((Player) _actor).getCurrentSkillWorldPosition(), _skill.getCastRange()))
			{
				_actor.setIsCastingNow(false);
				return;
			}
		}
		else
		{
			if (checkTargetLost(target))
			{
				// Notify the target
				if (_skill.isOffensive() && getTarget() != null)
					setTarget(null);
				
				_actor.setIsCastingNow(false);
				return;
			}
			
			if (_actor.getMove().maybeMoveToPawn(target, _skill.getCastRange()))
			{
				_actor.setIsCastingNow(false);
				return;
			}
		}
		
		_actor.doCast(_skill);
	}
	
	private void thinkPickUp()
	{
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isAttackingNow())
			return;
		
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
			return;
		
		if (_actor.getMove().maybeMoveToPawn(target, 36))
			return;
		
		if (_actor.isAlikeDead() || getActor().isFakeDeath() || getActor().isOperating())
			return;
		
		// Set the AI Intention to IDLE
		setIntention(IntentionType.IDLE);
		
		// Check if the WorldObject to pick up is a ItemInstance
		if (!(target instanceof ItemInstance))
			return;
		
		final ItemInstance item = (ItemInstance) target;
		
		// Send ActionFailed to this Player
		_actor.sendPacket(ActionFailed.STATIC_PACKET);
		
		synchronized (item)
		{
			if (!item.isVisible())
				return;
			
			if (!getActor().getInventory().validateWeight(item.getCount() * item.getItem().getWeight()))
			{
				_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
				return;
			}
			
			if (((getActor().isInParty() && getActor().getParty().getLootRule() == LootRule.ITEM_LOOTER) || !getActor().isInParty()) && !getActor().getInventory().validateCapacity(item))
			{
				getActor().sendPacket(SystemMessageId.SLOTS_FULL);
				return;
			}
			
			if (getActor().getActiveTradeList() != null)
			{
				getActor().sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
				return;
			}
			
			if (item.getOwnerId() != 0 && !getActor().isLooterOrInLooterParty(item.getOwnerId()))
			{
				if (item.getItemId() == 57)
					_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
				else if (item.getCount() > 1)
					_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item).addNumber(item.getCount()));
				else
					_actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item));
				
				return;
			}
			
			if (item.hasDropProtection())
				item.removeDropProtection();
			
			// Remove the ItemInstance from the world and send GetItem packets
			item.pickupMe(_actor);
			
			// item must be removed from ItemsOnGroundManager if is active
			ItemsOnGroundTaskManager.getInstance().remove(item);
		}
		
		// Auto use herbs - pick up
		if (item.getItemType() == EtcItemType.HERB)
		{
			IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(getActor(), item, false);
			
			item.destroyMe("Consume", getActor(), null);
		}
		// Cursed Weapons are not distributed
		else if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
		{
			getActor().addItem("Pickup", item, null, true);
		}
		else
		{
			// if item is instance of L2ArmorType or WeaponType broadcast an "Attention" system message
			if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType)
			{
				SystemMessage msg;
				if (item.getEnchantLevel() > 0)
					msg = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2_S3_S4).addString(_actor.getName()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()).addZoneName(_actor.getPosition());
				else
					msg = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PICKED_UP_S2_S3).addString(_actor.getName()).addItemName(item.getItemId()).addZoneName(_actor.getPosition());
				
				getActor().broadcastPacketInRadius(msg, 1400);
			}
			
			// Check if a Party is in progress
			if (getActor().isInParty())
				getActor().getParty().distributeItem(getActor(), item);
			// Target is adena
			else if (item.getItemId() == 57 && getActor().getInventory().getAdenaInstance() != null)
			{
				getActor().addAdena("Pickup", item.getCount(), null, true);
				item.destroyMe("Pickup", getActor(), null);
			}
			// Target is regular item
			else
				getActor().addItem("Pickup", item, null, true);
		}
		
		// Schedule a paralyzed task to wait for the animation to finish
		ThreadPool.schedule(() -> _actor.setIsParalyzed(false), (int) (700 / _actor.getStat().getMovementSpeedMultiplier()));
		_actor.setIsParalyzed(true);
	}
	
	private void thinkInteract()
	{
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow())
			return;
		
		WorldObject target = getTarget();
		if (checkTargetLost(target))
			return;
		
		if (_actor.getMove().maybeMoveToPawn(target, 60))
			return;
		
		if (!(target instanceof StaticObject))
			_actor.getActingPlayer().doInteract((Creature) target);
		
		setIntention(IntentionType.IDLE);
	}
	
	@Override
	protected void onEvtThink()
	{
		// Check if the actor can't use skills and if a thinking action isn't already in progress
		if (_thinking && _desire.getIntention() != IntentionType.CAST) // casting must always continue
			return;
		
		// Start thinking action
		_thinking = true;
		
		try
		{
			// Manage AI thoughts
			switch (_desire.getIntention())
			{
				case ATTACK:
					thinkAttack();
					break;
				case CAST:
					thinkCast();
					break;
				case PICK_UP:
					thinkPickUp();
					break;
				case INTERACT:
					thinkInteract();
					break;
			}
		}
		finally
		{
			// Stop thinking action
			_thinking = false;
		}
	}
}