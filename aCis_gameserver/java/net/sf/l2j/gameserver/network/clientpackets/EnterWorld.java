package net.sf.l2j.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.communitybbs.Manager.MailBBSManager;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.ClanHallManager;
import net.sf.l2j.gameserver.data.manager.CoupleManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.PetitionManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.AnnouncementData;
import net.sf.l2j.gameserver.data.xml.MapRegionData.TeleportType;
import net.sf.l2j.gameserver.data.xml.ScriptData;
import net.sf.l2j.gameserver.enums.CabalType;
import net.sf.l2j.gameserver.enums.PcCafeType;
import net.sf.l2j.gameserver.enums.SealType;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Menu;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.ClassMaster;
import net.sf.l2j.gameserver.model.clanhall.ClanHall;
import net.sf.l2j.gameserver.model.clanhall.SiegableHall;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.SubPledge;
import net.sf.l2j.gameserver.network.GameClient.GameClientState;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.Die;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExMailArrived;
import net.sf.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import net.sf.l2j.gameserver.network.serverpackets.FriendList;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PledgeSkillList;
import net.sf.l2j.gameserver.network.serverpackets.QuestList;
import net.sf.l2j.gameserver.network.serverpackets.ShortCutInit;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;

import Dev.AioMenu.AioMenu;
import Dev.AioMenu.VipMenu;
import Dev.HeroMenu.HeroMenu;
import Dev.PartyFarm.PartyFarm;
import Dev.StartPlayer.StartupManager;
import Dev.Tournament.ArenaTask;
import Dev.VoteEngine.Individual;

public class EnterWorld extends L2GameClientPacket
{
	private static final String LOAD_PLAYER_QUESTS = "SELECT name,var,value FROM character_quests WHERE charId=?";
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
		{
			getClient().closeNow();
			return;
		}
	
        switch (player.getClassId().getId()) {
            case 0: 
            case 10: 
            case 18: 
            case 25: 
            case 31: 
            case 38: 
            case 44: 
            case 49: 
            case 53: {
                Player.doNewChar(player, 1);
            }
        }
		
		
		getClient().setState(GameClientState.IN_GAME);
		
		final int objectId = player.getObjectId();
		
		if (player.isGM())
		{
			if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", player.getAccessLevel()))
				player.setIsInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_hide", player.getAccessLevel()))
				player.getAppearance().setVisible(false);

			if (Config.GM_STARTUP_SPEED && AdminData.getInstance().hasAccess("admin_gmspeed", player.getAccessLevel()))
				player.doCast(SkillTable.getInstance().getInfo(7029, 4));
			
			if (Config.GM_STARTUP_SILENCE && AdminData.getInstance().hasAccess("admin_silence", player.getAccessLevel()))
				player.setInRefusalMode(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminData.getInstance().hasAccess("admin_gmlist", player.getAccessLevel()))
			{
				AdminData.getInstance();
				AdminData.addGm(player, false);
			}
			else
			{
				AdminData.getInstance();
				AdminData.addGm(player, true);
			}
		}
		
		
		// Set dead status if applies
		if (player.getCurrentHp() < 0.5 && player.isMortal())
			player.setIsDead(true);
		
		player.getMacroList().sendUpdate();
		player.sendPacket(new ExStorageMaxCount(player));
		player.sendPacket(new HennaInfo(player));
		player.updateEffectIcons();
		player.sendPacket(new EtcStatusUpdate(player));
		
		// Clan checks.
		final Clan clan = player.getClan();
		if (clan != null)
		{
			player.sendPacket(new PledgeSkillList(clan));
			
			// Refresh player instance.
			clan.getClanMember(objectId).setPlayerInstance(player);
			
			final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(player);
			final PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(player);
			
			// Send packets to others members.
			for (Player member : clan.getOnlineMembers())
			{
				if (member == player)
					continue;
				
				member.sendPacket(msg);
				member.sendPacket(update);
			}
			
			// Send a login notification to sponsor or apprentice, if logged.
			if (player.getSponsor() != 0)
			{
				final Player sponsor = World.getInstance().getPlayer(player.getSponsor());
				if (sponsor != null)
					sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addCharName(player));
			}
			else if (player.getApprentice() != 0)
			{
				final Player apprentice = World.getInstance().getPlayer(player.getApprentice());
				if (apprentice != null)
					apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addCharName(player));
			}
			
			// Add message at connexion if clanHall not paid.
			final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (clanHall != null && !clanHall.getPaid())
				player.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				final Siege siege = castle.getSiege();
				if (!siege.isInProgress())
					continue;
				
				final SiegeSide type = siege.getSide(clan);
				if (type == SiegeSide.ATTACKER)
					player.setSiegeState((byte) 1);
				else if (type == SiegeSide.DEFENDER || type == SiegeSide.OWNER)
					player.setSiegeState((byte) 2);
			}
			
			for (SiegableHall hall : ClanHallManager.getInstance().getSiegableHalls())
			{
				if (!hall.isInSiege())
					continue;
				
				if (hall.isRegistered(clan))
				{
					player.setSiegeState((byte) 1);
					player.setInSiegableHallSiege(true);
				}
			}
			
			player.sendPacket(new PledgeShowMemberListUpdate(player));
			player.sendPacket(new PledgeShowMemberListAll(clan, 0));
			
			for (SubPledge sp : clan.getAllSubPledges())
				player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
		}
		
		// Updating Seal of Strife Buff/Debuff
		if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) != CabalType.NORMAL)
		{
			CabalType cabal = SevenSignsManager.getInstance().getPlayerCabal(objectId);
			if (cabal != CabalType.NORMAL)
			{
				if (cabal == SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
					player.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill(), false);
				else
					player.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill(), false);
			}
		}
		else
		{
			player.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill().getId(), false);
			player.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill().getId(), false);
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			player.setSpawnProtection(true);
		
		player.spawnMe();
		
		// Set the location of debug packet.
		player.getMove().getDebugMovePacket().setXYZ(player.getX(), player.getY(), -16000);
		
		// Engage and notify partner.
		if (Config.ALLOW_WEDDING)
		{
			for (Entry<Integer, IntIntHolder> coupleEntry : CoupleManager.getInstance().getCouples().entrySet())
			{
				final IntIntHolder couple = coupleEntry.getValue();
				if (couple.getId() == objectId || couple.getValue() == objectId)
				{
					player.setCoupleId(coupleEntry.getKey());
					break;
				}
			}
		}
		
		// Announcements, welcome & Seven signs period messages
		player.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
		player.sendPacket(SevenSignsManager.getInstance().getCurrentPeriod().getMessageId());
		
		
		
		
		AnnouncementData.getInstance().showAnnouncements(player, false);
		
		Menu.sendMainWindow(player);
		
		if (player.isVip())
		{
			VipMenu.bypass(player, null, null);
			player.sendMessage("" + player.getName() + "Use Command Vip Buffer /vip");
		}

		
		if (ArenaTask.is_started() && Config.ARENA_MESSAGE_ENABLED)
			player.sendPacket(new ExShowScreenMessage(Config.ARENA_MESSAGE_TEXT, Config.ARENA_MESSAGE_TIME, 2, true));

	
		
		if (PartyFarm.is_started() && Config.PARTY_FARM_BY_TIME_OF_DAY)
			player.sendPacket(new ExShowScreenMessage(Config.PARTY_FARM_MESSAGE_TEXT, 2));

		// if player is DE, check for shadow sense skill at night
		if (player.getRace() == ClassRace.DARK_ELF && player.hasSkill(L2Skill.SKILL_SHADOW_SENSE))
			player.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(L2Skill.SKILL_SHADOW_SENSE));
		
		// Load quests.
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_PLAYER_QUESTS))
		{
			ps.setInt(1, objectId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final String questName = rs.getString("name");
					
					// Test quest existence.
					final Quest quest = ScriptData.getInstance().getQuest(questName);
					if (quest == null)
					{
						LOGGER.warn("Unknown quest {} for player {}.", questName, player.getName());
						continue;
					}
					
					// Each quest get a single state ; create one QuestState per found <state> variable.
					final String var = rs.getString("var");
					if (var.equals("<state>"))
					{
						new QuestState(player, quest, rs.getByte("value"));
						
						// Notify quest for enterworld event, if quest allows it.
						if (quest.isTriggeredOnEnterWorld())
							quest.notifyEnterWorld(player);
					}
					// Feed an existing quest state.
					else
					{
						final QuestState qs = player.getQuestState(questName);
						if (qs == null)
						{
							LOGGER.warn("Unknown quest state {} for player {}.", questName, player.getName());
							continue;
						}
						
						qs.setInternal(var, rs.getString("value"));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load quests for player {}.", e, player.getName());
		}
		
		player.sendPacket(new QuestList(player));
		
		player.sendSkillList();
		player.sendPacket(new FriendList(player));
		player.sendPacket(new UserInfo(player));
		player.sendPacket(new ItemList(player, false));
		player.sendPacket(new ShortCutInit(player));
		
		// no broadcast needed since the player will already spawn dead to others
		if (player.isAlikeDead())
			player.sendPacket(new Die(player));
		
		// Unread mails make a popup appears.
		if (Config.ENABLE_COMMUNITY_BOARD && MailBBSManager.getInstance().checkUnreadMail(player) > 0)
		{
			player.sendPacket(SystemMessageId.NEW_MAIL);
			player.sendPacket(new PlaySound("systemmsg_e.1233"));
			player.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		// Clan notice, if active.
		if (Config.ENABLE_COMMUNITY_BOARD && clan != null && clan.isNoticeEnabled())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/clan_notice.htm");
			html.replace("%clan_name%", clan.getName());
			html.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
			sendPacket(html);
		}
		else if (Config.SERVER_NEWS)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/servnews.htm");
			sendPacket(html);
		}
		
		
		
		PetitionManager.getInstance().checkPetitionMessages(player);
		player.onPlayerEnter();
		
		sendPacket(new SkillCoolTime(player));
		
		// If player logs back in a stadium, port him in nearest town.
		if (Olympiad.getInstance().playerInStadia(player))
			player.teleportTo(TeleportType.TOWN);
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getX(), player.getY(), player.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
		
		if (player.getClanJoinExpiryTime() > System.currentTimeMillis())
			player.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		// Attacker or spectator logging into a siege zone will be ported at town.
		if (!player.isGM() && (!player.isInSiege() || player.getSiegeState() < 2) && player.isInsideZone(ZoneId.SIEGE))
			player.teleportTo(TeleportType.TOWN);

		// Announce Top PvP
		if (Config.ANNOUNCE_TOP && player.getPvpKills() > 0)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT char_name, pvpkills FROM characters WHERE accesslevel=0 ORDER BY pvpkills DESC LIMIT 1"))
			{ 
				try (ResultSet rset = ps.executeQuery()) 
				{ 
					while (rset.next())
					{
						int pvpkills = rset.getInt("pvpkills");
						String name = rset.getString("char_name"); 
						if (player.getName().equals(name)) 
							World.announceToOnlinePlayers(player.getClan() != null ? Config.ANNOUNCE_TOP_PVP_ENTER_BY_CLAN_MEMBER_MSG.replace("%player%", name).replace("%pvpkills%", String.valueOf(pvpkills)).replace("%clan%", player.getClan().getName()) : Config.ANNOUNCE_TOP_PVP_ENTER_BY_PLAYER_MSG.replace("%player%", name).replace("%pvpkills%", String.valueOf(pvpkills)), true);
					} 
				}
			} 
			catch (Exception e)
			{
				LOGGER.error("Couldn't announce top pvp.", e);
			}      
		}
		
		if (player.getSkill(9999) != null)
			Individual.giveReward(player);
		
		// Announce Top PK
		if (Config.ANNOUNCE_TOP && player.getPkKills() > 0)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT char_name, pkkills FROM characters WHERE accesslevel=0 ORDER BY pkkills DESC LIMIT 1"))
			{ 
				try (ResultSet rset = ps.executeQuery()) 
				{ 
					while (rset.next())
					{
						int pkkills = rset.getInt("pkkills");
						String name = rset.getString("char_name"); 
						if (player.getName().equals(name))
							World.announceToOnlinePlayers(player.getClan() != null ? Config.ANNOUNCE_TOP_PK_ENTER_BY_CLAN_MEMBER_MSG.replace("%player%", name).replace("%pkkills%", String.valueOf(pkkills)).replace("%clan%", player.getClan().getName()) : Config.ANNOUNCE_TOP_PK_ENTER_BY_PLAYER_MSG.replace("%player%", name).replace("%pkkills%", String.valueOf(pkkills)), true);
					} 
				}
			} 
			catch (Exception e)
			{
				LOGGER.error("Couldn't announce top pk.", e);
			}    
		}     
		
		// Announce Castle Lords
		if (Config.ANNOUNCE_CASTLE_LORDS)
		{
			if (player.getClan() != null)
			{
				if (player.getClan().getLeaderName().equals(player.getName()))
				{
					if (CastleManager.getInstance().getCastleByOwner(player.getClan()) != null)
						World.announceToOnlinePlayers(Config.ANNOUNCE_LORDS_ENTER_BY_CLAN_MEMBER_MSG.replace("%lord%", player.getName()).replace("%clan%", player.getClan().getName()).replace("%castle%", CastleManager.getInstance().getCastleByOwner(player.getClan()).getName()), true);
				}
			}
		}
		
		// announce hero
		if (player.getActiveClass() == player.getBaseClass() && player.isHero() && Config.ANNOUNCE_HERO_ONLY_BASECLASS)
			World.announceToOnlinePlayers(player.getClan() != null ? Config.ANNOUNCE_HERO_ENTER_BY_CLAN_MEMBER_MSG.replace("%player%", player.getName()).replace("%clan%", player.getClan().getName()).replace("%classe%", player.setClassName(player.getBaseClass())) : Config.ANNOUNCE_HERO_ENTER_BY_PLAYER_MSG.replace("%player%", player.getName()).replace("%classe%", player.setClassName(player.getBaseClass())), true);

		if (Config.PCB_INTERVAL > 0)
			player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), 0, PcCafeType.NORMAL));
		
		
		
		
		if (player.getMemos().getLong("aioTime", 0) > 0)	
		{
			long now = Calendar.getInstance().getTimeInMillis();
			long endDay = player.getMemos().getLong("aioTime");
			
			if (now > endDay)
				player.setAio(false);
			else
			{
				player.setAio(true);
				player.sendMessage("Seu Aio terminam em " + new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date(player.getMemos().getLong("aioTime", 0))) + ".");
				AioMenu.mainHtml(player, 0);
			}
		}
		
		if (player.getMemos().getLong("heroTime", 0) > 0)
		{
			long now = Calendar.getInstance().getTimeInMillis();
			long endDay = player.getMemos().getLong("heroTime");
			
			if (now > endDay)
				player.setHero(false);
			else
			{
				player.setHero(true);
				player.sendMessage("Seu Hero terminam em " + new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date(player.getMemos().getLong("heroTime", 0))) + ".");
				HeroMenu.mainHtml(player, 0);
			}
		}
 		
		if (player.getMemos().getLong("vipTime", 0) > 0)	
		{
			long now = Calendar.getInstance().getTimeInMillis();
			long endDay = player.getMemos().getLong("vipTime");
			
			if (now > endDay)
				player.setVip(false);
			else
			{
				player.setVip(true);
				
				player.sendMessage("Seu Vip terminam em " + new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date(player.getMemos().getLong("vipTime", 0))) + ".");
			}
		}
	    
		if (player.getMemos().getLong("newEndTime", 0) > 0)
			onEnterNewChar(player);
		
		if (player.getMemos().getLong("previewEndTime", 0) > 0)
			StartupManager.onEnterPreview(player);
		if (player.getMemos().getLong("equipEndTime", 0) > 0)
			StartupManager.onEnterEquip(player);
		if (player.getMemos().getLong("weaponEndTime", 0) > 0)
			StartupManager.onEnterWepEquip(player);
		if (player.getMemos().getLong("buffEndTime", 0) > 0)
			StartupManager.onEnterBuff(player);

        

	    
		onCheckNewbieStep(player);
		
		ClassMaster.showQuestionMark(player);
		

		
		if (player.isVip() && Config.ANNOUNCE_VIP_ENTER)
			World.announceToOnlinePlayers(player.getClan() != null ? Config.ANNOUNCE_VIP_ENTER_BY_CLAN_MEMBER_MSG.replace("%player%", player.getName()).replace("%clan%", player.getClan().getName()) : Config.ANNOUNCE_VIP_ENTER_BY_PLAYER_MSG.replace("%player%", player.getName()), true);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private static void onEnterNewChar(Player player)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = player.getMemos().getLong("newEndTime");
		
		if (now > endDay)
			Player.removeNewChar(player);
		else
		{
			player.setNewChar(true);
			player.broadcastUserInfo();
		}
	}
    
	private static void onCheckNewbieStep(Player player)
	{
		
		if (Config.ENABLE_STARTUP)
			if (player.getOnlineTime() == 0)
			{
		StartupManager.Welcome(player);

			}
		

	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}