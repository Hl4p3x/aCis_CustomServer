package net.sf.l2j.gameserver.model.actor.stat;

import java.util.Map;

import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.PlayerLevelData;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.PlayerLevel;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.npc.RewardInfo;
import net.sf.l2j.gameserver.model.actor.instance.ClassMaster;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.ClanMember;
import net.sf.l2j.gameserver.model.zone.type.SwampZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scripting.QuestState;

public class PlayerStat extends PlayableStat
{
	private int _oldMaxHp; // stats watch
	private int _oldMaxMp; // stats watch
	private int _oldMaxCp; // stats watch
	
	public PlayerStat(Player activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean addExp(long value)
	{
		
		Player activeChar = getActiveChar();
		
		// Allowed to gain exp?
		if (!getActiveChar().getAccessLevel().canGainExp())
			return false;
		if (activeChar.cantGainXP())
			return false;
		if (!super.addExp(value))
			return false;
		
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the Player, remove its Karma (if necessary) and Launch increase level task.
	 * <ul>
	 * <li>Remove Karma when the player kills Monster</li>
	 * <li>Send StatusUpdate to the Player</li>
	 * <li>Send a Server->Client System Message to the Player</li>
	 * <li>If the Player increases its level, send SocialAction (broadcast)</li>
	 * <li>If the Player increases its level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...)</li>
	 * <li>If the Player increases its level, send UserInfo to the Player</li>
	 * </ul>
	 * @param addToExp The Experience value to add
	 * @param addToSp The SP value to add
	 */
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		Player activeChar = getActiveChar();
		
		if (!super.addExpAndSp(addToExp, addToSp))
			return false;
		
		if (activeChar.cantGainXP())
			return false;
		
		SystemMessage sm;
		
		if (addToExp == 0 && addToSp > 0)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_SP).addNumber(addToSp);
		else if (addToExp > 0 && addToSp == 0)
			sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S1_EXPERIENCE).addNumber((int) addToExp);
		else
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_EARNED_S1_EXP_AND_S2_SP).addNumber((int) addToExp).addNumber(addToSp);
		
		getActiveChar().sendPacket(sm);
		
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the Player, remove its Karma (if necessary) and Launch increase level task.
	 * <ul>
	 * <li>Remove Karma when the player kills Monster</li>
	 * <li>Send StatusUpdate to the Player</li>
	 * <li>Send a Server->Client System Message to the Player</li>
	 * <li>If the Player increases its level, send SocialAction (broadcast)</li>
	 * <li>If the Player increases its level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...)</li>
	 * <li>If the Player increases its level, send UserInfo to the Player</li>
	 * </ul>
	 * @param addToExp The Experience value to add
	 * @param addToSp The SP value to add
	 * @param rewards The list of players and summons, who done damage
	 * @return
	 */
	public boolean addExpAndSp(long addToExp, int addToSp, Map<Creature, RewardInfo> rewards)
	{
		// GM check concerning canGainExp().
		if (!getActiveChar().getAccessLevel().canGainExp())
			return false;
		
		// If this player has a pet, give the xp to the pet now (if any).
		if (getActiveChar().hasPet())
		{
			final Pet pet = (Pet) getActiveChar().getSummon();
			if (pet.getStat().getExp() <= (pet.getTemplate().getPetDataEntry(81).getMaxExp() + 10000) && !pet.isDead())
			{
				if (MathUtil.checkIfInShortRadius(Config.PARTY_RANGE, pet, getActiveChar(), true))
				{
					int ratio = pet.getPetData().getExpType();
					long petExp = 0;
					int petSp = 0;
					if (ratio == -1)
					{
						RewardInfo r = rewards.get(pet);
						RewardInfo reward = rewards.get(getActiveChar());
						if (r != null && reward != null)
						{
							double damageDoneByPet = ((double) (r.getDamage())) / reward.getDamage();
							petExp = (long) (addToExp * damageDoneByPet);
							petSp = (int) (addToSp * damageDoneByPet);
						}
					}
					else
					{
						// now adjust the max ratio to avoid the owner earning negative exp/sp
						if (ratio > 100)
							ratio = 100;
						
						petExp = Math.round(addToExp * (1 - (ratio / 100.0)));
						petSp = (int) Math.round(addToSp * (1 - (ratio / 100.0)));
					}
					
					addToExp -= petExp;
					addToSp -= petSp;
					pet.addExpAndSp(petExp, petSp);
				}
			}
		}
		return addExpAndSp(addToExp, addToSp);
	}
	
	@Override
	public boolean removeExpAndSp(long removeExp, int removeSp)
	{
		return removeExpAndSp(removeExp, removeSp, true);
	}
	
	public boolean removeExpAndSp(long removeExp, int removeSp, boolean sendMessage)
	{
		final int oldLevel = getLevel();
		
		if (!super.removeExpAndSp(removeExp, removeSp))
			return false;
		
		// Send messages.
		if (sendMessage)
		{
			if (removeExp > 0)
				getActiveChar().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EXP_DECREASED_BY_S1).addNumber((int) removeExp));
			
			if (removeSp > 0)
				getActiveChar().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(removeSp));
			
			if (getLevel() < oldLevel)
				getActiveChar().broadcastStatusUpdate();
		}
		return true;
	}
	
	@Override
	public final boolean addLevel(byte value)
	{
		if (getLevel() + value > PlayerLevelData.getInstance().getRealMaxLevel())
			return false;
		
		boolean levelIncreased = super.addLevel(value);
		
		if (levelIncreased)
		{
			if (!Config.DISABLE_TUTORIAL)
			{
				QuestState qs = getActiveChar().getQuestState("Tutorial");
				if (qs != null)
					qs.getQuest().notifyEvent("CE40", null, getActiveChar());
			}
			
			getActiveChar().setCurrentCp(getMaxCp());
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar(), 15));
			getActiveChar().sendPacket(SystemMessageId.YOU_INCREASED_YOUR_LEVEL);
			ClassMaster.showQuestionMark(getActiveChar());
		}
		
		// Refresh player skills (autoGet skills or all available skills if Config.AUTO_LEARN_SKILLS is activated).
		getActiveChar().giveSkills();
		
		final Clan clan = getActiveChar().getClan();
		if (clan != null)
		{
			final ClanMember member = clan.getClanMember(getActiveChar().getObjectId());
			if (member != null)
				member.refreshLevel();
			
			clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()));
		}
		
		// Recalculate the party level
		final Party party = getActiveChar().getParty();
		if (party != null)
			party.recalculateLevel();
		
		// Update the overloaded status of the player
		getActiveChar().refreshOverloaded();
		// Update the expertise status of the player
		getActiveChar().refreshExpertisePenalty();
		// Send UserInfo to the player
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));
		
		return levelIncreased;
	}
	
	@Override
	public final Player getActiveChar()
	{
		return (Player) super.getActiveChar();
	}
	
	@Override
	public final long getExp()
	{
		if (getActiveChar().isSubClassActive())
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();
		
		return super.getExp();
	}
	
	@Override
	public final void setExp(long value)
	{
		if (getActiveChar().isSubClassActive())
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
		else
			super.setExp(value);
	}
	
	@Override
	public final byte getLevel()
	{
		if (getActiveChar().isSubClassActive())
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();
		
		return super.getLevel();
	}
	
	@Override
	public final void setLevel(byte value)
	{
		value = (byte) Math.min(value, PlayerLevelData.getInstance().getRealMaxLevel());
		
		if (getActiveChar().isSubClassActive())
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
		else
			super.setLevel(value);
	}
	
	@Override
	public final int getMaxCp()
	{
		// Get the Max CP (base+modifier) of the player
		int val = (int) calcStat(Stats.MAX_CP, getActiveChar().getTemplate().getBaseCpMax(getActiveChar().getLevel()), null, null);
		if (val != _oldMaxCp)
		{
			_oldMaxCp = val;
			
			// Launch a regen task if the new Max CP is higher than the old one
			if (getActiveChar().getStatus().getCurrentCp() != val)
				getActiveChar().getStatus().setCurrentCp(getActiveChar().getStatus().getCurrentCp()); // trigger start of regeneration
		}
		return val;
	}
	
	@Override
	public final int getMaxHp()
	{
		// Get the Max HP (base+modifier) of the player
		int val = super.getMaxHp();
		if (val != _oldMaxHp)
		{
			_oldMaxHp = val;
			
			// Launch a regen task if the new Max HP is higher than the old one
			if (getActiveChar().getStatus().getCurrentHp() != val)
				getActiveChar().getStatus().setCurrentHp(getActiveChar().getStatus().getCurrentHp()); // trigger start of regeneration
		}
		
		return val;
	}
	
	@Override
	public final int getMaxMp()
	{
		// Get the Max MP (base+modifier) of the player
		int val = super.getMaxMp();
		
		if (val != _oldMaxMp)
		{
			_oldMaxMp = val;
			
			// Launch a regen task if the new Max MP is higher than the old one
			if (getActiveChar().getStatus().getCurrentMp() != val)
				getActiveChar().getStatus().setCurrentMp(getActiveChar().getStatus().getCurrentMp()); // trigger start of regeneration
		}
		
		return val;
	}
	
	@Override
	public final int getSp()
	{
		if (getActiveChar().isSubClassActive())
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();
		
		return super.getSp();
	}
	
	@Override
	public final void setSp(int value)
	{
		if (getActiveChar().isSubClassActive())
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
		else
			super.setSp(value);
		
		StatusUpdate su = new StatusUpdate(getActiveChar());
		su.addAttribute(StatusUpdate.SP, getSp());
		getActiveChar().sendPacket(su);
	}
	
	@Override
	public int getBaseRunSpeed()
	{
		if (getActiveChar().isMounted())
		{
			int base = (getActiveChar().isFlying()) ? getActiveChar().getPetDataEntry().getMountFlySpeed() : getActiveChar().getPetDataEntry().getMountBaseSpeed();
			
			if (getActiveChar().getLevel() < getActiveChar().getMountLevel())
				base /= 2;
			
			if (getActiveChar().checkFoodState(getActiveChar().getPetTemplate().getHungryLimit()))
				base /= 2;
			
			return base;
		}
		
		return super.getBaseRunSpeed();
	}
	
	public int getBaseSwimSpeed()
	{
		if (getActiveChar().isMounted())
		{
			int base = getActiveChar().getPetDataEntry().getMountSwimSpeed();
			
			if (getActiveChar().getLevel() < getActiveChar().getMountLevel())
				base /= 2;
			
			if (getActiveChar().checkFoodState(getActiveChar().getPetTemplate().getHungryLimit()))
				base /= 2;
			
			return base;
		}
		
		return getActiveChar().getTemplate().getBaseSwimSpeed();
	}
	
	@Override
	public float getMoveSpeed()
	{
		// get base value, use swimming speed in water
		float baseValue = (getActiveChar().isInWater()) ? getBaseSwimSpeed() : getBaseMoveSpeed();
		
		// apply zone modifier before final calculation
		if (getActiveChar().isInsideZone(ZoneId.SWAMP))
		{
			final SwampZone zone = ZoneManager.getInstance().getZone(getActiveChar(), SwampZone.class);
			if (zone != null)
				baseValue *= (100 + zone.getMoveBonus()) / 100.0;
		}
		
		// apply armor grade penalty before final calculation
		final int penalty = getActiveChar().getExpertiseArmorPenalty();
		if (penalty > 0)
			baseValue *= Math.pow(0.84, penalty);
		
		// calculate speed
		return (float) calcStat(Stats.RUN_SPEED, baseValue, null, null);
	}
	
	@Override
	public float getRealMoveSpeed(boolean isStillWalking)
	{
		// get base value, use swimming speed in water
		float baseValue = (getActiveChar().isInWater()) ? getBaseSwimSpeed() : ((isStillWalking || !getActiveChar().isRunning()) ? getBaseWalkSpeed() : getBaseRunSpeed());
		
		// apply zone modifier before final calculation
		if (getActiveChar().isInsideZone(ZoneId.SWAMP))
		{
			final SwampZone zone = ZoneManager.getInstance().getZone(getActiveChar(), SwampZone.class);
			if (zone != null)
				baseValue *= (100 + zone.getMoveBonus()) / 100.0;
		}
		
		// apply armor grade penalty before final calculation
		final int penalty = getActiveChar().getExpertiseArmorPenalty();
		if (penalty > 0)
			baseValue *= Math.pow(0.84, penalty);
		
		// calculate speed
		return (float) calcStat(Stats.RUN_SPEED, baseValue, null, null);
	}
	
	@Override
	public int getMAtk(Creature target, L2Skill skill)
	{
		if (getActiveChar().isMounted())
		{
			double base = getActiveChar().getPetDataEntry().getMountMAtk();
			
			if (getActiveChar().getLevel() < getActiveChar().getMountLevel())
				base /= 2;
			
			return (int) calcStat(Stats.MAGIC_ATTACK, base, null, null);
		}
		
		return super.getMAtk(target, skill);
	}
	
	@Override
	public int getMAtkSpd()
	{
		
		int val = super.getMAtkSpd();
		
		if (getActiveChar().getClassId().equals(ClassId.OVERLORD) || getActiveChar().getClassId().equals(ClassId.DOMINATOR))
		{
			if (val > Config.MAX_MATK_SPEED_OVER)
				return Config.MAX_MATK_SPEED_OVER;
		}
		
		else if (getActiveChar().getClassId().equals(ClassId.WARCRYER) || getActiveChar().getClassId().equals(ClassId.DOOMCRYER))
		{
			if (val > Config.MAX_MATK_SPEED_OVER)
				return Config.MAX_MATK_SPEED_OVER;
		}
		
		else if (val > Config.MAX_MATK_SPEED)
			return Config.MAX_MATK_SPEED;
		return val;
		
	}
	
	@Override
	public int getPAtk(Creature target)
	{
		if (getActiveChar().isMounted())
		{
			double base = getActiveChar().getPetDataEntry().getMountPAtk();
			
			if (getActiveChar().getLevel() < getActiveChar().getMountLevel())
				base /= 2;
			
			return (int) calcStat(Stats.POWER_ATTACK, base, null, null);
		}
		
		return super.getPAtk(target);
	}
	
	@Override
	public int getPAtkSpd()
	{
		if (getActiveChar().isFlying())
			return (getActiveChar().checkFoodState(getActiveChar().getPetTemplate().getHungryLimit())) ? 150 : 300;
		
		if (getActiveChar().isRiding())
		{
			int base = getActiveChar().getPetDataEntry().getMountAtkSpd();
			
			if (getActiveChar().checkFoodState(getActiveChar().getPetTemplate().getHungryLimit()))
				base /= 2;
			
			return (int) calcStat(Stats.POWER_ATTACK_SPEED, base, null, null);
		}
		
		int val = super.getPAtkSpd();
		if (getActiveChar().getClassId().equals(ClassId.PHANTOM_RANGER) || getActiveChar().getClassId().equals(ClassId.GHOST_SENTINEL))
		{
			if (val > Config.MAX_PATK_SPEED_GHOST)
				return Config.MAX_PATK_SPEED_GHOST;
		}
		else if (getActiveChar().getClassId().equals(ClassId.SILVER_RANGER) || getActiveChar().getClassId().equals(ClassId.MOONLIGHT_SENTINEL))
		{
			if (val > Config.MAX_PATK_SPEED_MOONL)
				return Config.MAX_PATK_SPEED_MOONL;
		}
		else if (val > Config.MAX_PATK_SPEED)
			return Config.MAX_PATK_SPEED;
		return val;
	}
	
	@Override
	public int getEvasionRate(Creature target)
	{
		int val = super.getEvasionRate(target);
		
		final int penalty = getActiveChar().getExpertiseArmorPenalty();
		if (penalty > 0)
			val -= (2 * penalty);
		
		return val;
	}
	
	@Override
	public int getAccuracy()
	{
		int val = super.getAccuracy();
		
		if (getActiveChar().getExpertiseWeaponPenalty())
			val -= 20;
		
		return val;
	}
	
	@Override
	public int getPhysicalAttackRange()
	{
		return (int) calcStat(Stats.POWER_ATTACK_RANGE, getActiveChar().getAttackType().getRange(), null, null);
	}
	
	@Override
	public long getExpForLevel(int level)
	{
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(level);
		if (pl == null)
			return 0;
		
		return pl.getRequiredExpToLevelUp();
	}
	
	@Override
	public long getExpForThisLevel()
	{
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(getLevel());
		if (pl == null)
			return 0;
		
		return pl.getRequiredExpToLevelUp();
	}
	
	@Override
	public long getExpForNextLevel()
	{
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(getLevel() + 1);
		if (pl == null)
			return 0;
		
		return pl.getRequiredExpToLevelUp();
	}
}