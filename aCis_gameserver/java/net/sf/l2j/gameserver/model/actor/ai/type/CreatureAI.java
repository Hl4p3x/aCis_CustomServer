package net.sf.l2j.gameserver.model.actor.ai.type;

import net.sf.l2j.commons.util.ArraysUtil;

import net.sf.l2j.gameserver.enums.AiEventType;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.skills.L2SkillType;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.ai.Desire;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance.ItemLocation;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.SpawnLocation;

public class CreatureAI extends AbstractAI
{
	public CreatureAI(Creature character)
	{
		super(character);
	}
	
	public Desire getNextIntention()
	{
		return null;
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
	}
	
	@Override
	protected void onIntentionIdle()
	{
		// Set the AI Intention to IDLE
		changeIntention(IntentionType.IDLE, null, null);
		
		// Init cast and attack target
		setTarget(null);
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
	}
	
	@Override
	protected void onIntentionActive()
	{
		// Check if the Intention is not already Active
		if (_desire.getIntention() != IntentionType.ACTIVE)
		{
			// Set the AI Intention to ACTIVE
			changeIntention(IntentionType.ACTIVE, null, null);
			
			// Init cast and attack target
			setTarget(null);
			
			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			_actor.getMove().stopMove();
			
			// Also enable random animations for this Creature if allowed
			// This is only for mobs - town npcs are handled in their constructor
			if (_actor instanceof Attackable)
				((Npc) _actor).startRandomAnimationTimer();
			
			// Launch the Think Event
			onEvtThink();
		}
	}
	
	@Override
	protected void onIntentionRest()
	{
		setIntention(IntentionType.IDLE);
	}
	
	@Override
	protected void onIntentionAttack(Creature target)
	{
		if (target == null)
		{
			clientActionFailed();
			return;
		}
		
		if (_desire.getIntention() == IntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
			clientActionFailed();
			return;
		}
		
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isAfraid())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
			clientActionFailed();
			return;
		}
		
		// Check if the Intention is already ATTACK
		if (_desire.getIntention() == IntentionType.ATTACK)
		{
			// Check if the AI already targets the Creature
			if (getTarget() != target)
			{
				// Set the AI attack target (change target)
				setTarget(target);
				
				_actor.getMove().stopFollow();
				
				// Launch the Think Event
				notifyEvent(AiEventType.THINK, null);
			}
			else
			{
				clientActionFailed(); // else client freezes until cancel target
				
				if (_actor instanceof Playable && _actor.isAttackingNow() && !target.isAutoAttackable(_actor))
					changeIntention(IntentionType.IDLE, null, null);
			}
		}
		else
		{
			// Set the Intention of this AbstractAI to ATTACK
			changeIntention(IntentionType.ATTACK, target, null);
			
			// Set the AI attack target
			setTarget(target);
			
			_actor.getMove().stopFollow();
			
			// Launch the Think Event
			notifyEvent(AiEventType.THINK, null);
		}
	}
	
	@Override
	protected void onIntentionCast(L2Skill skill, WorldObject target)
	{
		if (_desire.getIntention() == IntentionType.REST && skill.isMagic())
		{
			clientActionFailed();
			_actor.setIsCastingNow(false);
			return;
		}
		
		// Set the AI cast target
		setTarget(target);
		
		// Set the AI skill used by INTENTION_CAST
		_skill = skill;
		
		// Change the Intention of this AbstractAI to CAST
		changeIntention(IntentionType.CAST, skill, target);
		
		// Launch the Think Event
		notifyEvent(AiEventType.THINK, null);
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
			return;
		}
		
		// Set the Intention of this AbstractAI to MOVE_TO
		changeIntention(IntentionType.MOVE_TO, loc, null);
		
		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet MoveToLocation (broadcast)
		_actor.getMove().moveToLocation(loc);
	}
	
	@Override
	protected void onIntentionFollow(Creature target)
	{
		if (_desire.getIntention() == IntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
			clientActionFailed();
			return;
		}
		
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
			clientActionFailed();
			return;
		}
		
		if (_actor.isMovementDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
			clientActionFailed();
			return;
		}
		
		// Dead actors can`t follow
		if (_actor.isDead())
		{
			clientActionFailed();
			return;
		}
		
		// do not follow yourself
		if (_actor == target)
		{
			clientActionFailed();
			return;
		}
		
		// Set the Intention of this AbstractAI to FOLLOW
		changeIntention(IntentionType.FOLLOW, target, null);
		
		// Create and Launch an AI Follow Task to execute every 1s
		_actor.getMove().startFollow(target);
	}
	
	@Override
	protected void onIntentionPickUp(WorldObject object)
	{
		// Actor is resting, return.
		if (_desire.getIntention() == IntentionType.REST)
		{
			clientActionFailed();
			return;
		}
		
		// Actor is currently busy casting, return.
		if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isAttackingNow())
		{
			clientActionFailed();
			return;
		}
		
		if (object instanceof ItemInstance && ((ItemInstance) object).getLocation() != ItemLocation.VOID)
			return;
		
		// Set the target.
		setTarget(object);
		
		// Set the Intention of this AbstractAI to PICK_UP
		changeIntention(IntentionType.PICK_UP, object, null);
		
		// Move to the Pawn.
		_actor.getMove().moveToPawn(object, 36);
	}
	
	@Override
	protected void onIntentionInteract(WorldObject object)
	{
	}
	
	@Override
	protected void onEvtThink()
	{
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
	
	@Override
	protected void onEvtStunned(Creature attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
		
		// Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the stunning periode)
		onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtParalyzed(Creature attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
		
		// Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the stunning periode)
		onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtSleeping(Creature attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
	}
	
	@Override
	protected void onEvtRooted(Creature attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
		
		// Launch actions corresponding to the Event onAttacked
		onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtConfused(Creature attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
		
		// Launch actions corresponding to the Event onAttacked
		onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtMuted(Creature attacker)
	{
		// Break a cast and send Server->Client ActionFailed packet and a System Message to the Creature
		onEvtAttacked(attacker);
	}
	
	@Override
	protected void onEvtEvaded(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtReadyToAct()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	@Override
	protected void onEvtArrived()
	{
		_actor.revalidateZone(true);
		
		_actor.getMove().cancelMoveTask();
		
		if (_actor instanceof Attackable)
			((Attackable) _actor).setIsReturningToSpawnPoint(false);
		
		// If the Intention was MOVE_TO, set the Intention to ACTIVE
		if (_desire.getIntention() == IntentionType.MOVE_TO)
			setIntention(IntentionType.ACTIVE);
		
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	@Override
	protected void onEvtArrivedBlocked(SpawnLocation loc)
	{
		// If the Intention was MOVE_TO, set the Intention to ACTIVE
		if (_desire.getIntention() == IntentionType.MOVE_TO || _desire.getIntention() == IntentionType.CAST)
			setIntention(IntentionType.ACTIVE);
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
		
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	@Override
	protected void onEvtCancel()
	{
		_actor.abortCast();
		
		// Stop an AI Follow Task
		_actor.getMove().stopFollow();
		
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	@Override
	protected void onEvtDead()
	{
		// Stop an AI Tasks
		stopAITask();
		
		// Kill the actor client side.
		clientNotifyDead();
		
		if (!(_actor instanceof Playable))
			_actor.setWalking();
	}
	
	@Override
	protected void onEvtFakeDeath()
	{
		// Stop an AI Follow Task
		_actor.getMove().stopFollow();
		
		// Stop the actor movement and send Server->Client packet StopMove/StopRotation (broadcast)
		_actor.getMove().stopMove();
		
		// Init AI
		_desire.update(IntentionType.IDLE, null, null);
		setTarget(null);
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
	}
	
	@Override
	protected void onEvtAfraid(Creature effector)
	{
		if (!(_actor instanceof Pet))
			_actor.setRunning();
		
		final Location loc = _actor.getMove().getDestination();
		loc.setFleeing(_actor, effector, 500);
		
		setIntention(IntentionType.MOVE_TO, loc);
	}
	
	/**
	 * @param target The targeted WorldObject
	 * @return true if the target is lost or dead (fake death isn't considered), and set intention to ACTIVE.
	 */
	protected boolean checkTargetLostOrDead(Creature target)
	{
		if (target == null || target.isDead())
		{
			setIntention(IntentionType.ACTIVE);
			return true;
		}
		return false;
	}
	
	/**
	 * @param target : The targeted WorldObject
	 * @return true if the target is lost, and set intention to ACTIVE.
	 */
	protected boolean checkTargetLost(WorldObject target)
	{
		if (target == null)
		{
			setIntention(IntentionType.ACTIVE);
			return true;
		}
		return false;
	}
	
	public boolean canAura(L2Skill sk)
	{
		if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_AURA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_BEHIND_AURA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_FRONT_AURA)
		{
			for (WorldObject target : _actor.getKnownTypeInRadius(Creature.class, sk.getSkillRadius()))
			{
				if (target == getTarget())
					return true;
			}
		}
		return false;
	}
	
	public boolean canAOE(L2Skill sk)
	{
		if (sk.getSkillType() != L2SkillType.NEGATE || sk.getSkillType() != L2SkillType.CANCEL)
		{
			if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_AURA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_BEHIND_AURA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_FRONT_AURA)
			{
				boolean cancast = true;
				for (Creature target : _actor.getKnownTypeInRadius(Creature.class, sk.getSkillRadius()))
				{
					if (!GeoEngine.getInstance().canSeeTarget(_actor, target))
						continue;
					
					if (target instanceof Attackable && !_actor.isConfused())
						continue;
					
					if (target.getFirstEffect(sk) != null)
						cancast = false;
				}
				
				if (cancast)
					return true;
			}
			else if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_AREA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_BEHIND_AREA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_FRONT_AREA)
			{
				boolean cancast = true;
				for (Creature target : ((Creature) getTarget()).getKnownTypeInRadius(Creature.class, sk.getSkillRadius()))
				{
					if (!GeoEngine.getInstance().canSeeTarget(_actor, target))
						continue;
					
					if (target instanceof Attackable && !_actor.isConfused())
						continue;
					
					L2Effect[] effects = target.getAllEffects();
					if (effects.length > 0)
						cancast = true;
				}
				if (cancast)
					return true;
			}
		}
		else
		{
			if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_AURA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_BEHIND_AURA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_FRONT_AURA)
			{
				boolean cancast = false;
				for (Creature target : _actor.getKnownTypeInRadius(Creature.class, sk.getSkillRadius()))
				{
					if (!GeoEngine.getInstance().canSeeTarget(_actor, target))
						continue;
					
					if (target instanceof Attackable && !_actor.isConfused())
						continue;
					
					L2Effect[] effects = target.getAllEffects();
					if (effects.length > 0)
						cancast = true;
				}
				if (cancast)
					return true;
			}
			else if (sk.getTargetType() == L2Skill.SkillTargetType.TARGET_AREA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_BEHIND_AREA || sk.getTargetType() == L2Skill.SkillTargetType.TARGET_FRONT_AREA)
			{
				boolean cancast = true;
				for (Creature target : ((Creature) getTarget()).getKnownTypeInRadius(Creature.class, sk.getSkillRadius()))
				{
					if (!GeoEngine.getInstance().canSeeTarget(_actor, target))
						continue;
					
					if (target instanceof Attackable && !_actor.isConfused())
						continue;
					
					if (target.getFirstEffect(sk) != null)
						cancast = false;
				}
				
				if (cancast)
					return true;
			}
		}
		return false;
	}
	
	public boolean canParty(L2Skill sk)
	{
		if (sk.getTargetType() != L2Skill.SkillTargetType.TARGET_PARTY)
			return false;
		
		int count = 0;
		int ccount = 0;
		
		final String[] actorClans = ((Npc) _actor).getTemplate().getClans();
		for (Attackable target : _actor.getKnownTypeInRadius(Attackable.class, sk.getSkillRadius()))
		{
			if (!GeoEngine.getInstance().canSeeTarget(_actor, target))
				continue;
			
			if (!ArraysUtil.contains(actorClans, target.getTemplate().getClans()))
				continue;
			
			count++;
			
			if (target.getFirstEffect(sk) != null)
				ccount++;
		}
		
		if (ccount < count)
			return true;
		
		return false;
	}
}