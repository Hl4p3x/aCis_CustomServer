package net.sf.l2j.gameserver.model.actor.ai.type;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.enums.items.ArmorType;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PetItemList;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.ItemsOnGroundTaskManager;

public class SummonAI extends PlayableAI
{
	private static final int AVOID_RADIUS = 70;
	
	private volatile boolean _thinking; // to prevent recursive thinking
	private volatile boolean _startFollow = ((Summon) _actor).getFollowStatus();
	private Creature _lastAttack = null;
	
	public SummonAI(Summon summon)
	{
		super(summon);
	}
	
	@Override
	public Summon getActor()
	{
		return (Summon) _actor;
	}
	
	@Override
	protected void onIntentionIdle()
	{
		_actor.getMove().stopFollow();
		
		_startFollow = false;
		
		onIntentionActive();
	}
	
	@Override
	protected void onIntentionActive()
	{
		Summon summon = (Summon) _actor;
		if (_startFollow)
			setIntention(IntentionType.FOLLOW, summon.getOwner());
		else
			super.onIntentionActive();
	}
	
	private void thinkAttack()
	{
		final Creature target = (Creature) getTarget();
		
		if (checkTargetLostOrDead(target))
		{
			setTarget(null);
			return;
		}
		
		if (_actor.getMove().maybeMoveToPawn(target, _actor.getPhysicalAttackRange()))
			return;
		
		_actor.doAttack(target);
	}
	
	private void thinkCast()
	{
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
		{
			setTarget(null);
			return;
		}
		
		boolean val = _startFollow;
		if (_actor.getMove().maybeMoveToPawn(target, _skill.getCastRange()))
			return;
		
		((Summon) _actor).setFollowStatus(false);
		setIntention(IntentionType.IDLE);
		
		_startFollow = val;
		_actor.doCast(_skill);
	}
	
	private void thinkPickUp()
	{
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
			return;
		
		if (_actor.getMove().maybeMoveToPawn(target, 36))
			return;
		
		if (_actor.isDead())
			return;
		
		setIntention(IntentionType.IDLE);
		
		// The object must be an item.
		if (!(target instanceof ItemInstance))
			return;
		
		final ItemInstance item = (ItemInstance) target;
		
		// Can't pickup cursed weapons.
		if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
		{
			getActor().getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item.getItemId()));
			return;
		}
		
		// Can't pickup shots and arrows.
		if (item.getItem().getItemType() == EtcItemType.ARROW || item.getItem().getItemType() == EtcItemType.SHOT)
		{
			getActor().getOwner().sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return;
		}
		
		synchronized (target)
		{
			if (!target.isVisible())
				return;
			
			if (!getActor().getInventory().validateCapacity(item))
			{
				getActor().getOwner().sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return;
			}
			
			if (!getActor().getInventory().validateWeight(item, item.getCount()))
			{
				getActor().getOwner().sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
				return;
			}
			
			if (item.getOwnerId() != 0 && !getActor().getOwner().isLooterOrInLooterParty(item.getOwnerId()))
			{
				if (item.getItemId() == 57)
					getActor().getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1_ADENA).addNumber(item.getCount()));
				else if (item.getCount() > 1)
					getActor().getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S2_S1_S).addItemName(item.getItemId()).addNumber(item.getCount()));
				else
					getActor().getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FAILED_TO_PICKUP_S1).addItemName(item.getItemId()));
				
				return;
			}
			
			if (item.hasDropProtection())
				item.removeDropProtection();
			
			// If owner is in party and it isnt finders keepers, distribute the item instead of stealing it -.-
			final Party party = getActor().getOwner().getParty();
			if (party != null && party.getLootRule() != LootRule.ITEM_LOOTER)
				party.distributeItem(getActor().getOwner(), item);
			else
				item.pickupMe(_actor);
			
			// Item must be removed from ItemsOnGroundManager if it is active.
			ItemsOnGroundTaskManager.getInstance().remove(item);
		}
		
		// Auto use herbs - pick up
		if (item.getItemType() == EtcItemType.HERB)
		{
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(getActor(), item, false);
			
			item.destroyMe("Consume", getActor().getOwner(), null);
			getActor().broadcastStatusUpdate();
		}
		else
		{
			// if item is instance of L2ArmorType or WeaponType broadcast an "Attention" system message
			if (item.getItemType() instanceof ArmorType || item.getItemType() instanceof WeaponType)
			{
				SystemMessage msg;
				if (item.getEnchantLevel() > 0)
					msg = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PET_PICKED_UP_S2_S3).addCharName(getActor().getOwner()).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
				else
					msg = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_S1_PET_PICKED_UP_S2).addCharName(getActor().getOwner()).addItemName(item.getItemId());
				
				getActor().getOwner().broadcastPacketInRadius(msg, 1400);
			}
			
			SystemMessage sm2;
			if (item.getItemId() == 57)
				sm2 = SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1_ADENA).addItemNumber(item.getCount());
			else if (item.getEnchantLevel() > 0)
				sm2 = SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId());
			else if (item.getCount() > 1)
				sm2 = SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S2_S1_S).addItemName(item.getItemId()).addItemNumber(item.getCount());
			else
				sm2 = SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1).addItemName(item.getItemId());
			
			getActor().getOwner().sendPacket(sm2);
			getActor().getInventory().addItem("Pickup", item, getActor().getOwner(), getActor());
			getActor().getOwner().sendPacket(new PetItemList(getActor()));
		}
		
		if (getActor().getFollowStatus())
			getActor().followOwner();
	}
	
	private void thinkInteract()
	{
		final WorldObject target = getTarget();
		if (checkTargetLost(target))
			return;
		
		if (_actor.getMove().maybeMoveToPawn(target, 36))
			return;
		
		setIntention(IntentionType.IDLE);
	}
	
	@Override
	protected void onEvtThink()
	{
		if (_thinking || _actor.isCastingNow() || _actor.isAllSkillsDisabled())
			return;
		
		_thinking = true;
		try
		{
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
			_thinking = false;
		}
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
		if (_lastAttack == null)
			((Summon) _actor).setFollowStatus(_startFollow);
		else
		{
			setIntention(IntentionType.ATTACK, _lastAttack);
			_lastAttack = null;
		}
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		super.onEvtAttacked(attacker);
		
		avoidAttack(attacker);
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
		super.onEvtEvaded(attacker);
		
		avoidAttack(attacker);
	}
	
	@Override
	public void startAttackStance()
	{
		_actor.getActingPlayer().getAI().startAttackStance();
	}
	
	private void avoidAttack(Creature attacker)
	{
		final Player owner = ((Summon) _actor).getOwner();
		
		// Must have a owner, the attacker can't be the owner and the owner must be in a short radius. The owner must be under attack stance (the summon CAN'T be under attack stance with current writing style).
		if (owner == null || owner == attacker || !owner.isInsideRadius(_actor, 2 * AVOID_RADIUS, true, false) || !AttackStanceTaskManager.getInstance().isInAttackStance(owner))
			return;
		
		// Current summon intention must be ACTIVE or FOLLOW type.
		if (_desire.getIntention() != IntentionType.ACTIVE && _desire.getIntention() != IntentionType.FOLLOW)
			return;
		
		// Summon mustn't be under movement, must be alive and not be movement disabled.
		if (_actor.isMoving() || _actor.isDead() || _actor.isMovementDisabled())
			return;
		
		final int ownerX = owner.getX();
		final int ownerY = owner.getY();
		final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - _actor.getY(), ownerX - _actor.getX());
		
		final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
		final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
		
		// If the location is valid, move the summon.
		_actor.getMove().moveToLocation(targetX, targetY, _actor.getZ());
	}
	
	public void notifyFollowStatusChange()
	{
		_startFollow = !_startFollow;
		switch (_desire.getIntention())
		{
			case ACTIVE:
			case FOLLOW:
			case IDLE:
			case MOVE_TO:
			case PICK_UP:
				((Summon) _actor).setFollowStatus(_startFollow);
		}
	}
	
	public void setStartFollowController(boolean val)
	{
		_startFollow = val;
	}
	
	@Override
	protected void onIntentionCast(L2Skill skill, WorldObject target)
	{
		if (_desire.getIntention() == IntentionType.ATTACK)
			_lastAttack = (Creature) getTarget();
		else
			_lastAttack = null;
		
		super.onIntentionCast(skill, target);
	}
}