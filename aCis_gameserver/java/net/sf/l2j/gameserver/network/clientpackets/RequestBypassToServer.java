package net.sf.l2j.gameserver.network.clientpackets;


import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.handler.custom.DressMeData;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Dressme;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Menu;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Vote;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.OlympiadManagerNpc;

import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.util.Mysql;

import Dev.AioMenu.AioMenu;
import Dev.AioMenu.VipMenu;
import Dev.StartPlayer.StartupManager;
import Dev.VoteEngine.Individual;

public final class RequestBypassToServer extends L2GameClientPacket
{
	public static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	public static final Logger REPORT_LOG = Logger.getLogger("report");
	
	public static String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (_command.isEmpty())
			return;
		
		if (!FloodProtectors.performAction(getClient(), Action.SERVER_BYPASS))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_command.startsWith("admin_"))
		{
			String command = _command.split(" ")[0];
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);
			if (ach == null)
			{
				if (player.isGM())
					player.sendMessage("The command " + command.substring(6) + " doesn't exist.");
				
				LOGGER.warn("No handler registered for admin command '{}'.", command);
				return;
			}
			
			if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
			{
				player.sendMessage("You don't have the access rights to use this command.");
				LOGGER.warn("{} tried to use admin command '{}' without proper Access Level.", player.getName(), command);
				return;
			}
			
			if (Config.GMAUDIT)
				GMAUDIT_LOG.info(player.getName() + " [" + player.getObjectId() + "] used '" + _command + "' command on: " + ((player.getTarget() != null) ? player.getTarget().getName() : "none"));
			
			ach.useAdminCommand(_command, player);
		}
		
		// MENU ACIS JUVENIL JUNIOR
		else if (_command.startsWith("setPartyRefuse"))
		{
			if (player.isPartyInvProt())
				player.setIsPartyInvProt(false);
			else
				player.setIsPartyInvProt(true);
			Menu.sendMainWindow(player);
		}
		else if (_command.startsWith("setTradeRefuse"))
		{
			if (player.isInTradeProt())
				player.setIsInTradeProt(false);
			else
				player.setIsInTradeProt(true);
			Menu.sendMainWindow(player);
		}
		else if (_command.startsWith("setMessageRefuse"))
		{
			if (player.getMessageRefusal())
				player.setMessageRefusal(false);
			else
				player.setMessageRefusal(true);
			Menu.sendMainWindow(player);
		}
		else if (_command.startsWith("setbuffsRefuse"))
		{
			if (player.isBuffProtected())
				player.setisBuffProtected(false);
			else
				player.setisBuffProtected(true);
			player.sendMessage("Buff protection.");
			Menu.sendMainWindow(player);
		}
		else if (_command.startsWith("setxpnot"))
		{
			if (player.cantGainXP())
				player.cantGainXP(false);
			else
				player.cantGainXP(true);
			player.sendMessage(" Xp effects.");
			Menu.sendMainWindow(player);
		}
		else if (_command.startsWith("setSsprot"))
		{
			if (player.isSSDisabled())
				player.setIsSSDisabled(false);
			else
				player.setIsSSDisabled(true);
			player.sendMessage("Soulshots effects.");
			Menu.sendMainWindow(player);
		}
		
		if (_command.startsWith("Heroic_Valor"))
		{
			
			
			L2Skill skill = SkillTable.getInstance().getInfo(1374, 1);
			skill.getEffects(player, player);
			
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Heroic Valor!");
			
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("P_Attak"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(5154, 10);
			skill.getEffects(player, player);
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Might Argument!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("M_Attak"))
		{
			L2Skill skill = SkillTable.getInstance().getInfo(5156, 10);
			skill.getEffects(player, player);
			
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Empower Valor!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("P_Def"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(5158, 10);
			skill.getEffects(player, player);
			
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Shield Argument!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("M_Def"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(5159, 10);
			skill.getEffects(player, player);
			
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Magic Barrier Argument!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("Malaria"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
			skill.getEffects(player, player);
			
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Malaria!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("Flu"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(4553, 4);
			skill.getEffects(player, player);
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Flu!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("Cholera"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(4552, 4);
			skill.getEffects(player, player);
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Cholera!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("Salvation"))
		{
			
			L2Skill skill = SkillTable.getInstance().getInfo(1410, 1);
			skill.getEffects(player, player);
			player.broadcastUserInfo();
			player.sendMessage("Felicitaciones, use buff Salvation!");
			VipMenu.bypass(player, null, null);
		}
		
		if (_command.startsWith("droplist"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int npcId = Integer.parseInt(st.nextToken());
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			if (st.hasMoreTokens())
				player.ignored(Integer.parseInt(st.nextToken()));
			
			Npc.sendNpcDrop(player, npcId, page);
		}
		
		if (_command.startsWith("aiopanel"))
		{
			String value = _command.substring(8);
			StringTokenizer st = new StringTokenizer(value);
			String command = st.nextToken();
			
			AioMenu.bypass(player, command, st);
		}
		
		
		else if (_command.startsWith("player_help "))
		{
			final String path = _command.substring(12);
			if (path.indexOf("..") != -1)
				return;
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/help/" + cmd[0]);
			if (cmd.length > 1)
			{
				final int itemId = Integer.parseInt(cmd[1]);
				html.setItemId(itemId);
				
				if (itemId == 7064 && cmd[0].equalsIgnoreCase("lidias_diary/7064-16.htm"))
				{
					final QuestState qs = player.getQuestState("Q023_LidiasHeart");
					if (qs != null && qs.getInt("cond") == 5 && qs.getInt("diary") == 0)
						qs.set("diary", "1");
				}
			}
			html.disableValidation();
			player.sendPacket(html);
		}
		else if (_command.startsWith("npc_"))
		{
			if (!player.validateBypass(_command))
				return;
			
			int endOfId = _command.indexOf('_', 5);
			String id;
			if (endOfId > 0)
				id = _command.substring(4, endOfId);
			else
				id = _command.substring(4);
			
			try
			{
				final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
				
				if (object != null && object instanceof Npc && endOfId > 0 && ((Npc) object).canInteract(player))
					((Npc) object).onBypassFeedback(player, _command.substring(endOfId + 1));
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		// Navigate throught Manor windows
		else if (_command.startsWith("manor_menu_select?"))
		{
			WorldObject object = player.getTarget();
			if (object instanceof Npc)
				((Npc) object).onBypassFeedback(player, _command);
		}
		else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
		{
			CommunityBoard.getInstance().handleCommands(getClient(), _command);
		}
		else if (_command.startsWith("Quest "))
		{
			if (!player.validateBypass(_command))
				return;
			
			String[] str = _command.substring(6).trim().split(" ", 2);
			if (str.length == 1)
				player.processQuestEvent(str[0], "");
			else
				player.processQuestEvent(str[0], str[1]);
		}
		else if (_command.startsWith("_match"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroFights(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("_diary"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("arenachange")) // change
		{
			final boolean isManager = player.getCurrentFolk() instanceof OlympiadManagerNpc;
			if (!isManager)
			{
				// Without npc, command can be used only in observer mode on arena
				if (!player.isInObserverMode() || player.isInOlympiadMode() || player.getOlympiadGameId() < 0)
					return;
			}
			
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
				return;
			}
			
			final int arenaId = Integer.parseInt(_command.substring(12).trim());
			player.enterOlympiadObserverMode(arenaId);
		}
		else if (_command.startsWith("send_report"))
		{
			StringTokenizer st = new StringTokenizer(_command);
			st.nextToken();
			
			String msg = "";
			String type = st.nextToken();
			
			GameClient info = player.getClient().getConnection().getClient();
			
			try
			{
				while (st.hasMoreTokens())
					msg = msg + st.nextToken() + " ";
				
				if (msg.equals(""))
				{
					player.sendMessage("A caixa de mensagem não pode estar vazia.");
					return;
				}
				
				switch (type)
				{
					case "Armaduras":
						break;
					case "Boss":
						break;
					case "Skills":
						break;
					case "Quests":
						break;
					case "Other":
						break;		
				}
				
				REPORT_LOG.info("Character Info: " + info + "\r\nBug Type: " + type + "\r\nMessage: " + msg);
				player.sendMessage("Relatério enviado. Os Gms irão verifica-la em breve, obrigado.");
				AdminData.getInstance();
				AdminData.broadcastMessageToGMs("Report Manager: "+ player.getName() + " enviou um relatório de bug.");
			}
			catch (Exception e)
			{
				player.sendMessage("Algo deu errado, tente novamente.");
			}
		}
		else if (_command.startsWith("antibot"))
		{
			StringTokenizer st = new StringTokenizer(_command);
			st.nextToken();
			
			if (st.hasMoreTokens())
			{
				player.checkCode(st.nextToken());
				return;
			}
			player.checkCode("Fail");
		}
		else if (_command.equals("bp_changedressmestatus"))
		{
			if (player.isDressMeEnabled())
			{
				player.setDressMeEnabled(false);
				player.broadcastUserInfo();
			}
			else
			{
				player.setDressMeEnabled(true);
				player.broadcastUserInfo();
			}
			
			Dressme.sendMainWindow(player);
		}	
		else if (_command.startsWith("bp_editWindow"))
		{
			String bp = _command.substring(14);
			StringTokenizer st = new StringTokenizer(bp);
			
			sendEditWindow(player, st.nextToken());
		}	
		
		else if (_command.startsWith("bp_getReward"))
		{
			
			
			
			if (Individual.isAccountTookTheReward(player))
			{
				player.sendMessage("12 hours have not passed since you last received the reward.");
				return;
			}
			if ((Config.ALLOW_HOPZONE_VOTE_REWARD && !Individual.hasVotedHopzone(player)) || (Config.ALLOW_TOPZONE_VOTE_REWARD && !Individual.hasVotedTopzone(player)) || (Config.ALLOW_NETWORK_VOTE_REWARD && !Individual.votedNet(player)))
			{
				player.sendMessage("You didn't vote on all sites. Press Vote to open the sites on your browser.");
				return;
			}
			
			Mysql.set("UPDATE characters SET lastVoteReward=? WHERE obj_Id=?",System.currentTimeMillis(), player.getObjectId());
			
			
			Individual.giveReward(player);
			player.sendMessage("Thank you for voting.");
			
			Vote.sendMainWindow(player);
			
			
		}
		
		
		else if (_command.startsWith("bp_setpart"))
		{
			String bp = _command.substring(11);
			StringTokenizer st = new StringTokenizer(bp);
			
			String part = st.nextToken();
			String type = st.nextToken();
			
			setPart(player, part, type);
		}		
		else if (_command.startsWith("bp_gettarget"))
		{
			String bp = _command.substring(13);
			StringTokenizer st = new StringTokenizer(bp);
			
			String part = st.nextToken();
			
			stealTarget(player, part);
		}		
		else if (_command.equals("bp_main"))
		{
			Dressme.sendMainWindow(player);
		}
		
		
		
		else  if (_command.startsWith("mageclass")) {
			StartupManager.getInstance().MageClasses(_command, player);
		} else if (_command.startsWith("fighterclass")) {
			StartupManager.getInstance().FighterClasses(_command, player);
		} else if (_command.startsWith("lightclass")) {
			StartupManager.getInstance().LightClasses(_command, player);
		} else if (_command.startsWith("class")) {
			StartupManager.getInstance().Classes(_command, player);       
		}
		
		
	}
	
	
	
	public static void sendEditWindow(Player p, String part)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("./data/html/custom/dressme/edit.htm");
		htm.replace("%part%", part);
		switch (part)
		{
			
			case "helmet":
			{
				if (p.getDressMeData() == null)
				{
					htm.replace("%partinfo%", "You have no custom Hair.");
				}
				else
				{
					htm.replace("%partinfo%", p.getDressMeData().getHelmetId() == 0 ? "You have no custom Hair." : ItemData.getInstance().getTemplate(p.getDressMeData().getHelmetId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_HELMET.keySet())
				{
					temp += s+";";
				}
				htm.replace("%dropboxdata%", temp);
				break;
			}
			
			
			case "chest":
			{
				if (p.getDressMeData() == null)
				{
					htm.replace("%partinfo%", "You have no custom chest.");
				}
				else
				{
					htm.replace("%partinfo%", p.getDressMeData().getChestId() == 0 ? "You have no custom chest." : ItemData.getInstance().getTemplate(p.getDressMeData().getChestId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_CHESTS.keySet())
				{
					temp += s+";";
				}
				htm.replace("%dropboxdata%", temp);
				break;
			}
			case "legs":
			{
				if (p.getDressMeData() == null)
				{
					htm.replace("%partinfo%", "You have no custom legs.");
				}
				else
				{
					htm.replace("%partinfo%", p.getDressMeData().getLegsId() == 0 ? "You have no custom legs." : ItemData.getInstance().getTemplate(p.getDressMeData().getLegsId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_LEGS.keySet())
				{
					temp += s+";";
				}
				htm.replace("%dropboxdata%", temp);
				break;
			}
			case "gloves":
			{
				if (p.getDressMeData() == null)
				{
					htm.replace("%partinfo%", "You have no custom gloves.");
				}
				else
				{
					htm.replace("%partinfo%", p.getDressMeData().getGlovesId() == 0 ? "You have no custom gloves." : ItemData.getInstance().getTemplate(p.getDressMeData().getGlovesId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_GLOVES.keySet())
				{
					temp += s+";";
				}
				htm.replace("%dropboxdata%", temp);
				break;
			}
			case "boots":
			{
				if (p.getDressMeData() == null)
				{
					htm.replace("%partinfo%", "You have no custom boots.");
				}
				else
				{
					htm.replace("%partinfo%", p.getDressMeData().getBootsId() == 0 ? "You have no custom boots." : ItemData.getInstance().getTemplate(p.getDressMeData().getBootsId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_BOOTS.keySet())
				{
					temp += s+";";
				}
				htm.replace("%dropboxdata%", temp);
				break;
			}
			case "weap":
			{
				if (p.getDressMeData() == null)
				{
					htm.replace("%partinfo%", "You have no custom weapon.");
				}
				else
				{
					htm.replace("%partinfo%", p.getDressMeData().getWeapId() == 0 ? "You have no custom weapon." : ItemData.getInstance().getTemplate(p.getDressMeData().getWeapId()).getName());
				}
				String temp = "";
				for (String s : Config.DRESS_ME_WEAPONS.keySet())
				{
					temp += s+";";
				}
				htm.replace("%dropboxdata%", temp);
				break;
			}
		}
		
		p.sendPacket(htm);
	}
	
	public static void setPart(Player p, String part, String type)
	{
		if (p.getDressMeData() == null)
		{
			DressMeData dmd = new DressMeData();
			p.setDressMeData(dmd);
		}
		
		switch (part)
		{
			
			case "helmet":
			{
				if (Config.DRESS_ME_HELMET.keySet().contains(type))
				{
					p.getDressMeData().setHelmetId(Config.DRESS_ME_HELMET.get(type));
				}
				
				break;
			}
			
			
			case "chest":
			{
				if (Config.DRESS_ME_CHESTS.keySet().contains(type))
				{
					p.getDressMeData().setChestId(Config.DRESS_ME_CHESTS.get(type));
				}
				
				break;
			}
			case "legs":
			{
				if (Config.DRESS_ME_LEGS.keySet().contains(type))
				{
					p.getDressMeData().setLegsId(Config.DRESS_ME_LEGS.get(type));
				}
				
				break;
			}
			case "gloves":
			{
				if (Config.DRESS_ME_GLOVES.keySet().contains(type))
				{
					p.getDressMeData().setGlovesId(Config.DRESS_ME_GLOVES.get(type));
				}
				
				break;
			}
			case "boots":
			{
				if (Config.DRESS_ME_BOOTS.keySet().contains(type))
				{
					p.getDressMeData().setBootsId(Config.DRESS_ME_BOOTS.get(type));
				}
				
				break;
			}
			case "weap":
			{
				if (Config.DRESS_ME_WEAPONS.keySet().contains(type))
				{
					p.getDressMeData().setWeapId(Config.DRESS_ME_WEAPONS.get(type));
				}
				
				break;
			}
		}
		
		p.broadcastUserInfo();
		//		sendEditWindow(p, part);
	}
	
	public static void stealTarget(Player p, String part)
	{
		if (p.getTarget() == null || !(p.getTarget() instanceof Player))
		{
			p.sendMessage("Invalid target.");
			return;
		}
		
		Player t = (Player)p.getTarget();
		
		if (p.getDressMeData() == null)
		{
			DressMeData dmd = new DressMeData();
			p.setDressMeData(dmd);
		}
		
		boolean returnMain = false;
		
		switch (part)
		{
			case "helmet":
			{
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FACE) == null)
				{
					p.getDressMeData().setHelmetId(0);
				}
				else
				{
					p.getDressMeData().setHelmetId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FACE).getItemId());
				}
				break;
			}
			
			case "chest":
			{
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) == null)
				{
					p.getDressMeData().setChestId(0);
				}
				else
				{
					p.getDressMeData().setChestId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItemId());
				}
				break;
			}
			case "legs":
			{
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null)
				{
					p.getDressMeData().setLegsId(0);
				}
				else
				{
					p.getDressMeData().setLegsId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS).getItemId());
				}
				break;
			}
			case "gloves":
			{
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null)
				{
					p.getDressMeData().setGlovesId(0);
				}
				else
				{
					p.getDressMeData().setGlovesId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES).getItemId());
				}
				break;
			}
			case "boots":
			{
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null)
				{
					p.getDressMeData().setBootsId(0);
				}
				else
				{
					p.getDressMeData().setBootsId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET).getItemId());
				}
				break;
			}
			case "weap":
			{
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) == null)
				{
					p.getDressMeData().setWeapId(0);
				}
				else
				{
					p.getDressMeData().setWeapId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getItemId());
				}
				break;
			}
			case "all":
			{
				
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FACE) == null)
				{
					p.getDressMeData().setHelmetId(0);
				}
				else
				{
					p.getDressMeData().setHelmetId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FACE).getItemId());
				}
				
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) == null)
				{
					p.getDressMeData().setChestId(0);
				}
				else
				{
					p.getDressMeData().setChestId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItemId());
				}
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null)
				{
					p.getDressMeData().setLegsId(0);
				}
				else
				{
					p.getDressMeData().setLegsId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS).getItemId());
				}
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null)
				{
					p.getDressMeData().setGlovesId(0);
				}
				else
				{
					p.getDressMeData().setGlovesId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES).getItemId());
				}
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null)
				{
					p.getDressMeData().setBootsId(0);
				}
				else
				{
					p.getDressMeData().setBootsId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET).getItemId());
				}
				if (t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) == null)
				{
					p.getDressMeData().setWeapId(0);
				}
				else
				{
					p.getDressMeData().setWeapId(t.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getItemId());
				}
				returnMain = true;
				break;
			}
		}
		
		p.broadcastUserInfo();
		if (!returnMain)
			sendEditWindow(p, part);
		else
			Dressme.sendMainWindow(p);
	}
	
	
}