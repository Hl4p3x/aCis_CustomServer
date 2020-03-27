package net.sf.l2j.gameserver.model.actor.ai.type;

import net.sf.l2j.gameserver.enums.AiEventType;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.Desire;
import net.sf.l2j.gameserver.model.actor.ai.NextAction;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.network.serverpackets.AutoAttackStart;
import net.sf.l2j.gameserver.network.serverpackets.AutoAttackStop;
import net.sf.l2j.gameserver.network.serverpackets.Die;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;

abstract class AbstractAI
{
	protected final Creature _actor;
	protected final Desire _desire = new Desire();
	
	private NextAction _nextAction;
	
	/** Different targets this AI maintains */
	private WorldObject _target;
	
	/** The skill we are currently casting by INTENTION_CAST */
	protected L2Skill _skill;
	
	protected AbstractAI(Creature character)
	{
		_actor = character;
	}
	
	public Creature getActor()
	{
		return _actor;
	}
	
	public Desire getDesire()
	{
		return _desire;
	}
	
	/**
	 * Set the Intention of this AbstractAI.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method is USED by AI classes</B></FONT><BR>
	 * <BR>
	 * <B><U> Overridden in </U> : </B><BR>
	 * <B>L2AttackableAI</B> : Create an AI Task executed every 1s (if necessary)<BR>
	 * <B>L2PlayerAI</B> : Stores the current AI intention parameters to later restore it if necessary<BR>
	 * <BR>
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention
	 * @param arg1 The second parameter of the Intention
	 */
	synchronized void changeIntention(IntentionType intention, Object arg0, Object arg1)
	{
		_desire.update(intention, arg0, arg1);
	}
	
	/**
	 * Launch the CreatureAI onIntention method corresponding to the new Intention.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT><BR>
	 * <BR>
	 * @param intention The new Intention to set to the AI
	 */
	public final void setIntention(IntentionType intention)
	{
		setIntention(intention, null, null);
	}
	
	/**
	 * Launch the CreatureAI onIntention method corresponding to the new Intention.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT><BR>
	 * <BR>
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention (optional target)
	 */
	public final void setIntention(IntentionType intention, Object arg0)
	{
		setIntention(intention, arg0, null);
	}
	
	/**
	 * Launch the CreatureAI onIntention method corresponding to the new Intention.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT><BR>
	 * <BR>
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention (optional target)
	 * @param arg1 The second parameter of the Intention (optional target)
	 */
	public final void setIntention(IntentionType intention, Object arg0, Object arg1)
	{
		// Stop the follow mode if necessary
		if (intention != IntentionType.FOLLOW && intention != IntentionType.ATTACK)
			_actor.getMove().stopFollow();
		
		// Launch the onIntention method of the CreatureAI corresponding to the new Intention
		switch (intention)
		{
			case IDLE:
				onIntentionIdle();
				break;
			case ACTIVE:
				onIntentionActive();
				break;
			case REST:
				onIntentionRest();
				break;
			case ATTACK:
				onIntentionAttack((Creature) arg0);
				break;
			case CAST:
				onIntentionCast((L2Skill) arg0, (WorldObject) arg1);
				break;
			case MOVE_TO:
				onIntentionMoveTo((Location) arg0);
				break;
			case FOLLOW:
				onIntentionFollow((Creature) arg0);
				break;
			case PICK_UP:
				onIntentionPickUp((WorldObject) arg0);
				break;
			case INTERACT:
				onIntentionInteract((WorldObject) arg0);
				break;
		}
		
		// If do move or follow intention drop next action.
		if (_nextAction != null && _nextAction.getIntention() == intention)
			_nextAction = null;
	}
	
	/**
	 * Launch the CreatureAI onEvt method corresponding to the Event.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned periode)</B></FONT><BR>
	 * <BR>
	 * @param evt The event whose the AI must be notified
	 */
	public final void notifyEvent(AiEventType evt)
	{
		notifyEvent(evt, null, null);
	}
	
	/**
	 * Launch the CreatureAI onEvt method corresponding to the Event.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned periode)</B></FONT><BR>
	 * <BR>
	 * @param evt The event whose the AI must be notified
	 * @param arg0 The first parameter of the Event (optional target)
	 */
	public final void notifyEvent(AiEventType evt, Object arg0)
	{
		notifyEvent(evt, arg0, null);
	}
	
	/**
	 * Launch the CreatureAI onEvt method corresponding to the Event.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned periode)</B></FONT><BR>
	 * <BR>
	 * @param evt The event whose the AI must be notified
	 * @param arg0 The first parameter of the Event (optional target)
	 * @param arg1 The second parameter of the Event (optional target)
	 */
	public final void notifyEvent(AiEventType evt, Object arg0, Object arg1)
	{
		if ((!_actor.isVisible() && !_actor.isTeleporting()) || !_actor.hasAI())
			return;
		
		switch (evt)
		{
			case THINK:
				onEvtThink();
				break;
			case ATTACKED:
				onEvtAttacked((Creature) arg0);
				break;
			case AGGRESSION:
				onEvtAggression((Creature) arg0, ((Number) arg1).intValue());
				break;
			case STUNNED:
				onEvtStunned((Creature) arg0);
				break;
			case PARALYZED:
				onEvtParalyzed((Creature) arg0);
				break;
			case SLEEPING:
				onEvtSleeping((Creature) arg0);
				break;
			case ROOTED:
				onEvtRooted((Creature) arg0);
				break;
			case CONFUSED:
				onEvtConfused((Creature) arg0);
				break;
			case MUTED:
				onEvtMuted((Creature) arg0);
				break;
			case EVADED:
				onEvtEvaded((Creature) arg0);
				break;
			case READY_TO_ACT:
				if (!_actor.isCastingNow() && !_actor.isCastingSimultaneouslyNow())
					onEvtReadyToAct();
				break;
			case ARRIVED:
				if (!_actor.isCastingNow() && !_actor.isCastingSimultaneouslyNow())
					onEvtArrived();
				break;
			case ARRIVED_BLOCKED:
				onEvtArrivedBlocked((SpawnLocation) arg0);
				break;
			case CANCEL:
				onEvtCancel();
				break;
			case DEAD:
				onEvtDead();
				break;
			case FAKE_DEATH:
				onEvtFakeDeath();
				break;
			case FINISH_CASTING:
				onEvtFinishCasting();
				break;
			case AFRAID:
				onEvtAfraid((Creature) arg0);
				break;
		}
		
		// Do next action.
		if (_nextAction != null && _nextAction.getEvent() == evt)
		{
			_nextAction.run();
			_nextAction = null;
		}
	}
	
	/**
	 * Manage the Idle Intention : Stop Attack, Movement and Stand Up the actor.
	 */
	protected abstract void onIntentionIdle();
	
	/**
	 * Manage the Active Intention : Stop Attack, Movement and Launch Think Event.
	 */
	protected abstract void onIntentionActive();
	
	/**
	 * Manage the Rest Intention. Set the AI Intention to IDLE.
	 */
	protected abstract void onIntentionRest();
	
	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event.
	 * @param target : The Creature used as target.
	 */
	protected abstract void onIntentionAttack(Creature target);
	
	/**
	 * Launch a spell.
	 * @param skill : The L2Skill to cast.
	 * @param target : The WorldObject used as target.
	 */
	protected abstract void onIntentionCast(L2Skill skill, WorldObject target);
	
	/**
	 * Launch a movement to a {@link Location} if conditions are met.
	 * @param loc : The Location used as destination.
	 */
	protected abstract void onIntentionMoveTo(Location loc);
	
	/**
	 * Follow the {@link Creature} set as parameter if conditions are met.
	 * @param target : The Creature used as target.
	 */
	protected abstract void onIntentionFollow(Creature target);
	
	/**
	 * Manage the PickUp Intention : Set the pick up target and Launch a Move To Pawn Task (offset=20).
	 * @param item : The WorldObject used as target.
	 */
	protected abstract void onIntentionPickUp(WorldObject item);
	
	protected abstract void onIntentionInteract(WorldObject object);
	
	protected abstract void onEvtThink();
	
	protected abstract void onEvtAttacked(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Aggro.
	 * @param target : The Creature used as attacker.
	 * @param aggro : The amount of aggro.
	 */
	protected abstract void onEvtAggression(Creature target, int aggro);
	
	/**
	 * Launch actions corresponding to the effect Stun.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtStunned(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Paralyze.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtParalyzed(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Sleep.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtSleeping(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Rooted.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtRooted(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Confusion.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtConfused(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Mute.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtMuted(Creature attacker);
	
	/**
	 * Launch actions corresponding to the effect Stun.
	 * @param attacker : The Creature used as attacker.
	 */
	protected abstract void onEvtEvaded(Creature attacker);
	
	/**
	 * Launch actions corresponding to the Event ReadyToAct.
	 */
	protected abstract void onEvtReadyToAct();
	
	/**
	 * Launch actions corresponding to the Event Arrived.
	 */
	protected abstract void onEvtArrived();
	
	/**
	 * Launch actions corresponding to the Event ArrivedBlocked.
	 * @param loc : The Location used as destination.
	 */
	protected abstract void onEvtArrivedBlocked(SpawnLocation loc);
	
	/**
	 * Launch actions corresponding to the Event Cancel.
	 */
	protected abstract void onEvtCancel();
	
	/**
	 * Launch actions corresponding to the death of the actor.
	 */
	protected abstract void onEvtDead();
	
	/**
	 * Launch actions corresponding to the effect Fake Death.
	 */
	protected abstract void onEvtFakeDeath();
	
	/**
	 * Finalize the casting of a skill. Drop latest intention before the actual CAST.
	 */
	protected abstract void onEvtFinishCasting();
	
	/**
	 * Launch actions corresponding to the fear of the actor.
	 * @param effector : The Creature inspiring fear to the actor.
	 */
	protected abstract void onEvtAfraid(Creature effector);
	
	/**
	 * Cancel action client side by sending Server->Client packet ActionFailed to the Player actor.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><BR>
	 * <BR>
	 */
	protected void clientActionFailed()
	{
	}
	
	/**
	 * Activate the attack stance on clients, broadcasting {@link AutoAttackStart} packets. Refresh the timer if already on stance.
	 */
	public void startAttackStance()
	{
		// Initial check ; if the actor wasn't yet registered into AttackStanceTaskManager, broadcast AutoAttackStart packet.
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(_actor))
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
		
		// Set out of the initial if check to be able to refresh the time.
		AttackStanceTaskManager.getInstance().add(_actor);
	}
	
	/**
	 * Deactivate the attack stance on clients, broadcasting {@link AutoAttackStop} packet if the actor was indeed registered on {@link AttackStanceTaskManager}.
	 */
	public void stopAttackStance()
	{
		// If we successfully remove the actor from AttackStanceTaskManager, we also broadcast AutoAttackStop packet.
		if (AttackStanceTaskManager.getInstance().remove(_actor))
			_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
	}
	
	/**
	 * Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die <I>(broadcast)</I>.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><BR>
	 * <BR>
	 */
	protected void clientNotifyDead()
	{
		// Broadcast Die packet
		_actor.broadcastPacket(new Die(_actor));
		
		// Init AI
		_desire.update(IntentionType.IDLE, null, null);
		_target = null;
		
		// Cancel the follow task if necessary
		_actor.getMove().stopFollow();
		
		// Stop the actor auto-attack
		stopAttackStance();
	}
	
	/**
	 * Send the state of this actor to a {@link Player}.
	 * @param player : The Player to notify with the state of this actor.
	 */
	public void describeStateToPlayer(Player player)
	{
		if (_desire.getIntention() == IntentionType.MOVE_TO)
			_actor.getMove().describeMovementTo(player);
		// else if (getIntention() == CtrlIntention.CAST) TODO
	}
	
	public WorldObject getTarget()
	{
		return _target;
	}
	
	protected void setTarget(WorldObject target)
	{
		_target = target;
	}
	
	/**
	 * Stop all Ai tasks and futures.
	 */
	public void stopAITask()
	{
		_actor.getMove().stopFollow();
	}
	
	/**
	 * @param nextAction the _nextAction to set
	 */
	public void setNextAction(NextAction nextAction)
	{
		_nextAction = nextAction;
	}
	
	@Override
	public String toString()
	{
		return "Actor: " + _actor;
	}
}