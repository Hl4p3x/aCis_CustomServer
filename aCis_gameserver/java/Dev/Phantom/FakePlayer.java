package Dev.Phantom;


import java.util.logging.Level;

import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.enums.CabalType;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.enums.SealType;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.skills.L2SkillType;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.instance.StaticObject;

import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.Appearance;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.model.pledge.ClanMember;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

import Dev.Phantom.Ai.FakePlayerAI;
import Dev.Phantom.Helpers.FakeHelpers;

/**
 * @author Rouxy
 *
 */
public class FakePlayer extends Player
{
	private FakePlayerAI _fakeAi;	
	private boolean _underControl = true;
	
	public boolean isUnderControl() {
		return _underControl;
	}
	
	public void setUnderControl(boolean underControl) {
		_underControl = underControl;
	}
	
	
	public FakePlayer(int objectId, PlayerTemplate template, String accountName, Appearance app)
	{
		super(objectId, template, accountName, app);
	}
		
	public FakePlayerAI getFakeAi()
	{
		return _fakeAi;
	}
	
	public void setFakeAi(FakePlayerAI _fakeAi)
	{
		this._fakeAi = _fakeAi;
	}
	
	public void assignDefaultAI(boolean b) {
		try {
			setFakeAi(FakeHelpers.getAIbyClassId(getClassId()).getConstructor(FakePlayer.class).newInstance(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkUseMagicConditions(L2Skill skill, boolean forceUse, boolean dontMove)
	{
		if(skill == null)
			return false;
		
		if (isDead() || isOutOfControl())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if(isSkillDisabled(skill))
			return false;
		
		L2SkillType sklType = skill.getSkillType();
		
		if (isFishing() && (sklType != L2SkillType.PUMPING && sklType != L2SkillType.REELING && sklType != L2SkillType.FISHING))
		{
			return false;
		}
		
		
		if (isSitting())
		{
			if (skill.isToggle())
			{
				L2Effect effect = getFirstEffect(skill.getId());
				if (effect != null)
				{
					effect.exit();
					return false;
				}
			}			
			return false;
		}
		
		if (skill.isToggle())
		{
			L2Effect effect = getFirstEffect(skill.getId());
			
			if (effect != null)
			{
				if (skill.getId() != 60)
					effect.exit();
				
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		if (isFakeDeath())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		WorldObject target = null;
		SkillTargetType sklTargetType = skill.getTargetType();
		Location worldPosition = getCurrentSkillWorldPosition();
		
		if (sklTargetType == SkillTargetType.TARGET_GROUND && worldPosition == null)
		{
			LOGGER.info("WorldPosition is null for skill: " + skill.getName() + ", player: " + getName() + ".");
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		switch (sklTargetType)
		{
			// Target the player if skill type is AURA, PARTY, CLAN or SELF
			case TARGET_AURA:
			case TARGET_FRONT_AURA:
			case TARGET_BEHIND_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_PARTY:
			case TARGET_ALLY:
			case TARGET_CLAN:
			case TARGET_GROUND:
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
			case TARGET_AREA_SUMMON:
				target = this;
				break;
			case TARGET_PET:
			case TARGET_SUMMON:
				
				break;
			default:
				target = getTarget();
				break;
		}
		
		// Check the validity of the target
		if (target == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (target instanceof Door)
		{
			if (!((Door) target).isAutoAttackable(this) // Siege doors only hittable during siege
				|| (((Door) target).isUnlockable() && skill.getSkillType() != L2SkillType.UNLOCK)) // unlockable doors
			{
				sendPacket(SystemMessageId.INCORRECT_TARGET);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		// Are the target and the player in the same duel?
		if (isInDuel())
		{
			if (target instanceof Playable)
			{
				// Get Player
				Player cha = target.getActingPlayer();
				if (cha.getDuelId() != getDuelId())
				{
					sendPacket(SystemMessageId.INCORRECT_TARGET);
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
			}
			// ************************************* Check skill availability *******************************************

			// Siege summon checks. Both checks send a message to the player if it return false.
			if (skill.isSiegeSummonSkill())
			{
				final Siege siege = CastleManager.getInstance().getActiveSiege(this);
				if (siege == null || !siege.checkSide(getClan(), SiegeSide.ATTACKER) || isInSiege() && isInsideZone(ZoneId.CASTLE))
				{
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_CALL_PET_FROM_THIS_LOCATION));
					return false;
				}

				if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) == CabalType.DAWN && SevenSignsManager.getInstance().getPlayerCabal(getObjectId()) == CabalType.DUSK)
				{
					sendPacket(SystemMessageId.SEAL_OF_STRIFE_FORBIDS_SUMMONING);
					return false;
				}
			}
		}
		
		// ************************************* Check casting conditions *******************************************
		
		// Check if all casting conditions are completed
		if (!skill.checkCondition(this, target, false))
		{
			// Send ActionFailed to the Player
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// ************************************* Check Skill Type *******************************************
		
		// Check if this is offensive magic skill
		if (skill.isOffensive())
		{
			if (isInsidePeaceZone(target))
			{
				// If Creature or target is in a peace zone, send a system message TARGET_IN_PEACEZONE ActionFailed
				sendPacket(SystemMessageId.TARGET_IN_PEACEZONE);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			if (isInOlympiadMode() && !isOlympiadStart())
			{
				// if Player is in Olympia and the match isn't already start, send ActionFailed
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			// Check if the target is attackable
			if (!target.isAttackable() && !getAccessLevel().allowPeaceAttack())
			{
				// If target is not attackable, send ActionFailed
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			
			// Check if a Forced ATTACK is in progress on non-attackable target
			if (!target.isAutoAttackable(this) && !forceUse)
			{
				switch (sklTargetType)
				{
					case TARGET_AURA:
					case TARGET_FRONT_AURA:
					case TARGET_BEHIND_AURA:
					case TARGET_AURA_UNDEAD:
					case TARGET_CLAN:
					case TARGET_ALLY:
					case TARGET_PARTY:
					case TARGET_SELF:
					case TARGET_GROUND:
					case TARGET_CORPSE_ALLY:
					case TARGET_AREA_SUMMON:
						break;
					default: // Send ActionFailed to the Player
						sendPacket(ActionFailed.STATIC_PACKET);
						return false;
				}
			}
			
			// Check if the target is in the skill cast range
			if (dontMove)
			{
				// Calculate the distance between the Player and the target
				if (sklTargetType == SkillTargetType.TARGET_GROUND)
				{
					if (!isInsideRadius(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), (int) (skill.getCastRange() + getCollisionRadius()), false, false))
					{
						// Send a System Message to the caster
						sendPacket(SystemMessageId.TARGET_TOO_FAR);
						
						// Send ActionFailed to the Player
						sendPacket(ActionFailed.STATIC_PACKET);
						return false;
					}
				}
				else if (skill.getCastRange() > 0 && !isInsideRadius(target, (int) (skill.getCastRange() + getCollisionRadius()), false, false))
				{
					// Send a System Message to the caster
					sendPacket(SystemMessageId.TARGET_TOO_FAR);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
			}
		}
		
		// Check if the skill is defensive
		if (!skill.isOffensive() && target instanceof Monster && !forceUse)
		{
			// check if the target is a monster and if force attack is set.. if not then we don't want to cast.
			switch (sklTargetType)
			{
				case TARGET_PET:
				case TARGET_SUMMON:
				case TARGET_AURA:
				case TARGET_FRONT_AURA:
				case TARGET_BEHIND_AURA:
				case TARGET_AURA_UNDEAD:
				case TARGET_CLAN:
				case TARGET_SELF:
				case TARGET_CORPSE_ALLY:
				case TARGET_PARTY:
				case TARGET_ALLY:
				case TARGET_CORPSE_MOB:
				case TARGET_AREA_CORPSE_MOB:
				case TARGET_GROUND:
					break;
				default:
				{
					switch (sklType)
					{
						case BEAST_FEED:
						case DELUXE_KEY_UNLOCK:
						case UNLOCK:
							break;
						default:
							sendPacket(ActionFailed.STATIC_PACKET);
							return false;
					}
					break;
				}
			}
		}
		
		// Check if the skill is Spoil type and if the target isn't already spoiled
		if (sklType == L2SkillType.SPOIL)
		{
			if (!(target instanceof Monster))
			{
				// Send a System Message to the Player
				sendPacket(SystemMessageId.INCORRECT_TARGET);
				
				// Send ActionFailed to the Player
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		// Check if the skill is Sweep type and if conditions not apply
		if (sklType == L2SkillType.SWEEP && target instanceof Attackable)
		{
			if (((Attackable) target).isDead())
			{
				final int spoilerId = ((Monster) target).getSpoilerId();
				if (spoilerId == 0)
				{
					// Send a System Message to the Player
					sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
				
				if (!isLooterOrInLooterParty(spoilerId))
				{
					// Send a System Message to the Player
					sendPacket(SystemMessageId.SWEEP_NOT_ALLOWED);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
			}
		}
		
		// Check if the skill is Drain Soul (Soul Crystals) and if the target is a MOB
		if (sklType == L2SkillType.DRAIN_SOUL)
		{
			if (!(target instanceof Monster))
			{
				// Send a System Message to the Player
				sendPacket(SystemMessageId.INCORRECT_TARGET);
				
				// Send ActionFailed to the Player
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		
		// Check if this is a Pvp skill and target isn't a non-flagged/non-karma player
		switch (sklTargetType)
		{
			case TARGET_PARTY:
			case TARGET_ALLY: // For such skills, checkPvpSkill() is called from L2Skill.getTargetList()
			case TARGET_CLAN: // For such skills, checkPvpSkill() is called from L2Skill.getTargetList()
			case TARGET_AURA:
			case TARGET_FRONT_AURA:
			case TARGET_BEHIND_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_GROUND:
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
			case TARGET_AREA_SUMMON:
				break;
			default:
				if (!checkPvpSkill(target, skill) && !getAccessLevel().allowPeaceAttack())
				{
					// Send a System Message to the Player
					sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					
					// Send ActionFailed to the Player
					sendPacket(ActionFailed.STATIC_PACKET);
					return false;
				}
		}
		

		
		
		// finally, after passing all conditions
		return true;
	}
	


	public void forceAutoAttack(Creature creature)
	{
		if (getTarget() == null)
			return;

		if (isInsidePeaceZone(getTarget()))
			return;

		if (isInOlympiadMode() && getTarget() != null && getTarget() instanceof Playable)
		{
			Player target = getTarget().getActingPlayer();
			if (target == null || target.isInOlympiadMode() && (!isOlympiadStart() || getOlympiadGameId() != target.getOlympiadGameId()))
				return;
		}

		if (getTarget() != null && !getTarget().isAttackable() && !getAccessLevel().allowPeaceAttack())
			return;

		if (isConfused())
			return;
		
		// GeoData Los Check or dz > 1000
		if (!GeoEngine.getInstance().canSeeTarget(this,getTarget()))
		{
			return;
		}
		
		getAI().setIntention(IntentionType.ACTIVE);	
	}
	
	public synchronized void despawnPlayer()
	{
		try
		{
			// Put the online status to false
			setOnlineStatus(false, true);
			
			// abort cast & attack and remove the target. Cancels movement aswell.
			abortAttack();
			abortCast();
			
			setTarget(null);
			
			removeMeFromPartyMatch();
			
			if (isFlying())
				removeSkill(FrequentSkill.WYVERN_BREATH.getSkill().getId(), false);
			
	
			
			// Cancel the cast of eventual fusion skill users on this target.
			for (Creature character : getKnownType(Creature.class))
				if (character.getFusionSkill() != null && character.getFusionSkill().getTarget() == this)
					character.abortCast();
				
			// Stop signets & toggles effects.
			for (L2Effect effect : getAllEffects())
			{
				if (effect.getSkill().isToggle())
				{
					effect.exit();
					continue;
				}
				
				switch (effect.getEffectType())
				{
					case SIGNET_GROUND:
					case SIGNET_EFFECT:
						effect.exit();
						break;
				default:
					break;
				}
			}
			
			// Remove the Player from the world
			decayMe();
			
			// If a party is in progress, leave it
			if (getParty() != null)
				getParty().removePartyMember(this, MessageType.DISCONNECTED);

			
			// Handle removal from olympiad game
			if (OlympiadManager.getInstance().isRegistered(this) || getOlympiadGameId() != -1)
				OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
			
			// set the status for pledge member list to OFFLINE
			if (getClan() != null)
			{
				ClanMember clanMember = getClan().getClanMember(getObjectId());
				if (clanMember != null)
					clanMember.setPlayerInstance(null);
			}
			
			// deals with sudden exit in the middle of transaction
			if (getActiveRequester() != null)
			{
				setActiveRequester(null);
				cancelActiveTrade();
			}
			
			// If the Player is a GM, remove it from the GM List
			if (isGM())
			{
				AdminData.getInstance();
				AdminData.deleteGm(this);
			}
			
			
			// Oust player from boat
			//if (getVehicle() != null)
				//getVehicle().oustPlayer(this, true, Location.DUMMY_LOC);
			
			// Update inventory and remove them from the world
			getInventory().deleteMe();
			
			// Update warehouse and remove them from the world
			clearWarehouse();
			
			// Update freight and remove them from the world
			clearFreight();
			clearDepositedFreight();
			
			if (isCursedWeaponEquipped())
				CursedWeaponManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
			
			if (getClanId() > 0)
				getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
			
			if (isSeated())
			{
				final WorldObject object = World.getInstance().getObject(_throneId);
				if (object instanceof StaticObject)
					((StaticObject) object).setBusy(false);
			}
			
			
			
			//World.getInstance().removePlayer(this); // force remove in case of crash during teleport
			
			// friends & blocklist update
			notifyFriends(false);
			getBlockList().playerLogout();
		}
		catch (Exception e)
		{
			LOGGER.info(Level.WARNING, "Exception on deleteMe()" + e.getMessage(), e);
		}
	}

	public void heal() {
		setCurrentCp(getMaxCp());
		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());
	}
	
	


  
}
