package net.sf.l2j.gameserver.model.actor.ai.type;

import java.util.List;

import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.ArraysUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.SiegeGuard;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate.AIType;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate.SkillType;
import net.sf.l2j.gameserver.model.location.Location;

public class SiegeGuardAI extends AttackableAI
{
	public SiegeGuardAI(SiegeGuard guard)
	{
		super(guard);
	}
	
	/**
	 * Following conditions are checked for a siege defender :
	 * <ul>
	 * <li>if target isn't a player or a summon.</li>
	 * <li>if target is dead.</li>
	 * <li>if target is a GM in hide mode.</li>
	 * <li>if player is silent moving.</li>
	 * <li>if the target can't be seen and is a defender.</li>
	 * </ul>
	 * @param target The targeted Creature.
	 * @return True if the target is autoattackable (depends on the actor type).
	 */
	@Override
	protected boolean autoAttackCondition(Creature target)
	{
		if (!(target instanceof Playable) || target.isAlikeDead())
			return false;
		
		final Player player = target.getActingPlayer();
		if (player == null)
			return false;
		
		// Check if the target isn't GM on hide mode.
		if (player.isGM() && !player.getAppearance().isVisible())
			return false;
		
		// Check if the target isn't in silent move mode AND too far
		if (player.isSilentMoving() && !_actor.isInsideRadius(player, 250, false, false))
			return false;
		
		// Los Check Here
		return (_actor.isAutoAttackable(target) && GeoEngine.getInstance().canSeeTarget(_actor, target));
	}
	
	/**
	 * Manage AI standard thinks of a L2Attackable (called by onEvtThink).
	 * <ul>
	 * <li>Update every 1s the _globalAggro counter to come close to 0</li>
	 * <li>If the actor is Aggressive and can attack, add all autoAttackable Creature in its Aggro Range to its _aggroList, chose a target and order to attack it</li>
	 * <li>If the actor can't attack, order to it to return to its home location</li>
	 * </ul>
	 */
	@Override
	protected void thinkActive()
	{
		// Update every 1s the _globalAggro counter to come close to 0
		if (_globalAggro != 0)
		{
			if (_globalAggro < 0)
				_globalAggro++;
			else
				_globalAggro--;
		}
		
		// Add all autoAttackable Creature in L2Attackable Aggro Range to its _aggroList with 0 damage and 1 hate
		// A L2Attackable isn't aggressive during 10s after its spawn because _globalAggro is set to -10
		if (_globalAggro >= 0)
		{
			final Attackable npc = (Attackable) _actor;
			for (Creature target : npc.getKnownTypeInRadius(Creature.class, npc.getTemplate().getClanRange()))
			{
				if (autoAttackCondition(target)) // check aggression
				{
					// Get the hate level of the L2Attackable against this target, and add the attacker to the L2Attackable _aggroList
					if (npc.getHating(target) == 0)
						npc.addDamageHate(target, 0, 1);
				}
			}
			
			// Chose a target from its aggroList
			final Creature hated = (Creature) ((_actor.isConfused()) ? getTarget() : npc.getMostHated());
			if (hated != null)
			{
				// Get the hate level of the L2Attackable against this Creature target contained in _aggroList
				if (npc.getHating(hated) + _globalAggro > 0)
				{
					// Set the Creature movement type to run and send Server->Client packet ChangeMoveType to all others Player
					_actor.setRunning();
					
					// Set the AI Intention to ATTACK
					setIntention(IntentionType.ATTACK, hated);
				}
				return;
			}
		}
		// Order to the SiegeGuard to return to its home location because there's no target to attack
		getActiveChar().returnHome();
	}
	
	/**
	 * Manage AI attack thinks of a L2Attackable (called by onEvtThink).
	 * <ul>
	 * <li>Update the attack timeout if actor is running</li>
	 * <li>If target is dead or timeout is expired, stop this attack and set the Intention to ACTIVE</li>
	 * <li>Call all WorldObject of its Faction inside the Faction Range</li>
	 * <li>Chose a target and order to attack it with magic skill or physical attack</li>
	 * </ul>
	 */
	@Override
	protected void thinkAttack()
	{
		final SiegeGuard actor = getActiveChar();
		if (actor.isCastingNow())
			return;
		
		/**
		 * RETURN HOME<br>
		 * Check if the siege guard isn't too far ; if yes, then move him back to home.
		 */
		if (!actor.isInsideZone(ZoneId.SIEGE))
		{
			actor.returnHome();
			return;
		}
		
		// Pickup most hated character.
		Creature attackTarget = actor.getMostHated();
		
		// If target doesn't exist, is too far or if timeout is expired.
		if (attackTarget == null || _attackTimeout < System.currentTimeMillis() || MathUtil.calculateDistance(actor, attackTarget, true) > 2000)
		{
			// Stop hating this target after the attack timeout or if target is dead
			actor.stopHating(attackTarget);
			
			// Search the nearest target. If a target is found, continue regular process, else drop angry behavior.
			attackTarget = targetReconsider(actor.getTemplate().getClanRange(), false);
			if (attackTarget == null)
			{
				setIntention(IntentionType.ACTIVE);
				actor.setWalking();
				return;
			}
		}
		
		/**
		 * COMMON INFORMATIONS<br>
		 * Used for range and distance check.
		 */
		
		final int actorCollision = (int) actor.getCollisionRadius();
		final int combinedCollision = (int) (actorCollision + attackTarget.getCollisionRadius());
		final double dist = Math.sqrt(actor.getPlanDistanceSq(attackTarget.getX(), attackTarget.getY()));
		
		int range = combinedCollision;
		if (attackTarget.isMoving())
			range += 15;
		
		if (actor.isMoving())
			range += 15;
		
		/**
		 * Target setup.
		 */
		
		setTarget(attackTarget);
		actor.setTarget(attackTarget);
		
		/**
		 * Cast a spell.
		 */
		
		if (willCastASpell())
		{
			// This list is used in order to avoid multiple calls on skills lists. Tests are made one after the other, and content is replaced when needed.
			List<L2Skill> defaultList;
			
			// -------------------------------------------------------------------------------
			// Heal
			defaultList = actor.getTemplate().getSkills(SkillType.HEAL);
			if (!defaultList.isEmpty())
			{
				final String[] clans = actor.getTemplate().getClans();
				
				// Go through all characters around the actor that belongs to its faction.
				for (Creature cha : actor.getKnownTypeInRadius(Creature.class, 1000))
				{
					// Don't bother about dead, not visible, or healthy characters.
					if (cha.isAlikeDead() || !GeoEngine.getInstance().canSeeTarget(actor, cha) || (cha.getCurrentHp() / cha.getMaxHp() > 0.75))
						continue;
					
					// Will affect only defenders or NPCs from same faction.
					if (!actor.isAttackingDisabled() && (cha instanceof Player && actor.getCastle().getSiege().checkSides(((Player) cha).getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER)) || (cha instanceof Npc && ArraysUtil.contains(clans, ((Npc) cha).getTemplate().getClans())))
					{
						for (L2Skill sk : defaultList)
						{
							if (!MathUtil.checkIfInRange(sk.getCastRange(), actor, cha, true))
								continue;
							
							actor.setTarget(cha);
							actor.doCast(sk);
							actor.setTarget(attackTarget);
							return;
						}
					}
				}
			}
			
			// -------------------------------------------------------------------------------
			// Buff
			defaultList = actor.getTemplate().getSkills(SkillType.BUFF);
			if (!defaultList.isEmpty())
			{
				for (L2Skill sk : defaultList)
				{
					if (!checkSkillCastConditions(sk))
						continue;
					
					if (actor.getFirstEffect(sk) == null)
					{
						actor.setTarget(actor);
						actor.doCast(sk);
						actor.setTarget(attackTarget);
						return;
					}
				}
			}
			
			// -------------------------------------------------------------------------------
			// Debuff - 10% luck to get debuffed.
			defaultList = actor.getTemplate().getSkills(SkillType.DEBUFF);
			if (Rnd.get(100) < 10 && !defaultList.isEmpty())
			{
				for (L2Skill sk : defaultList)
				{
					if (!checkSkillCastConditions(sk) || (sk.getCastRange() + range <= dist && !canAura(sk)))
						continue;
					
					if (!GeoEngine.getInstance().canSeeTarget(actor, attackTarget))
						continue;
					
					if (attackTarget.getFirstEffect(sk) == null)
					{
						actor.doCast(sk);
						return;
					}
				}
			}
			
			// -------------------------------------------------------------------------------
			// General attack skill - short range is checked, then long range.
			defaultList = actor.getTemplate().getSkills(SkillType.SHORT_RANGE);
			if (!defaultList.isEmpty() && dist <= 150)
			{
				final L2Skill skill = Rnd.get(defaultList);
				if (cast(skill, dist, skill.getCastRange()))
					return;
			}
			else
			{
				defaultList = actor.getTemplate().getSkills(SkillType.LONG_RANGE);
				if (!defaultList.isEmpty() && dist > 150)
				{
					final L2Skill skill = Rnd.get(defaultList);
					if (cast(skill, dist, skill.getCastRange()))
						return;
				}
			}
		}
		
		/**
		 * MELEE CHECK<br>
		 * The mob failed a skill check ; make him flee if AI authorizes it, else melee attack.
		 */
		
		// The range takes now in consideration physical attack range.
		range += actor.getPhysicalAttackRange();
		
		if (actor.isMovementDisabled())
		{
			// If distance is too big, choose another target.
			if (dist > range)
				attackTarget = targetReconsider(range, true);
			
			// Any AI type, even healer or mage, will try to melee attack if it can't do anything else (desesperate situation).
			if (attackTarget != null)
				actor.doAttack(attackTarget);
			
			return;
		}
		
		/**
		 * MOVE AROUND CHECK<br>
		 * In case many mobs are trying to hit from same place, move a bit, circling around the target
		 */
		
		if (Rnd.get(100) <= 3)
		{
			for (Attackable nearby : actor.getKnownTypeInRadius(Attackable.class, actorCollision))
			{
				if (nearby == attackTarget)
					continue;
				
				int newX = combinedCollision + Rnd.get(40);
				if (Rnd.nextBoolean())
					newX = attackTarget.getX() + newX;
				else
					newX = attackTarget.getX() - newX;
				
				int newY = combinedCollision + Rnd.get(40);
				if (Rnd.nextBoolean())
					newY = attackTarget.getY() + newY;
				else
					newY = attackTarget.getY() - newY;
				
				if (!actor.isInsideRadius(newX, newY, actorCollision, false))
					actor.getMove().moveToLocation(newX, newY, actor.getZ());
				
				return;
			}
		}
		
		/**
		 * FLEE CHECK<br>
		 * Test the flee possibility. Archers got 25% chance to flee.
		 */
		
		if (actor.getTemplate().getAiType() == AIType.ARCHER && dist <= (60 + combinedCollision) && Rnd.get(4) < 1)
		{
			final Location loc = actor.getMove().getDestination();
			loc.setFleeing(actor, attackTarget, Config.MAX_DRIFT_RANGE);
			
			setIntention(IntentionType.MOVE_TO, loc);
			return;
		}
		
		/**
		 * BASIC MELEE ATTACK
		 */
		
		if (_actor.getMove().maybeMoveToPawn(getTarget(), actor.getPhysicalAttackRange()))
			return;
		
		_actor.doAttack((Creature) getTarget());
	}
	
	/**
	 * Method used when the actor can't attack his current target (immobilize state, for exemple).
	 * <ul>
	 * <li>If the actor got an hate list, pickup a new target from it.</li>
	 * <li>If the selected target is a defenser, drop from the list and pickup another.</li>
	 * </ul>
	 * @param range The range to check (skill range for skill ; physical range for melee).
	 * @param rangeCheck That boolean is used to see if a check based on the distance must be made (skill check).
	 * @return The new Creature victim.
	 */
	@Override
	protected Creature targetReconsider(int range, boolean rangeCheck)
	{
		final Attackable actor = getActiveChar();
		
		// Verify first if aggro list is empty, if not search a victim following his aggro position.
		if (!actor.getAggroList().isEmpty())
		{
			// Store aggro value && most hated, in order to add it to the random target we will choose.
			final Creature previousMostHated = actor.getMostHated();
			final int aggroMostHated = actor.getHating(previousMostHated);
			
			for (Creature obj : actor.getHateList())
			{
				if (!autoAttackCondition(obj))
					continue;
				
				if (rangeCheck)
				{
					// Verify the distance, -15 if the victim is moving, -15 if the npc is moving.
					double dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY())) - obj.getCollisionRadius();
					if (actor.isMoving())
						dist -= 15;
					
					if (obj.isMoving())
						dist -= 15;
					
					if (dist > range)
						continue;
				}
				
				// Stop to hate the most hated.
				actor.stopHating(previousMostHated);
				
				// Add previous most hated aggro to that new victim.
				actor.addDamageHate(obj, 0, (aggroMostHated > 0) ? aggroMostHated : 2000);
				return obj;
			}
		}
		return null;
	}
	
	private SiegeGuard getActiveChar()
	{
		return (SiegeGuard) _actor;
	}
}