package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.cache.CrestCache;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.AnnouncementData;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.data.xml.EnchantData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.MultisellData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.data.xml.PolymorphData;
import net.sf.l2j.gameserver.data.xml.PvPData;
import net.sf.l2j.gameserver.data.xml.TeleportLocationData;
import net.sf.l2j.gameserver.data.xml.WalkerRouteData;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>admin : the different admin menus.</li>
 * <li>gmlist : includes/excludes active character from /gmlist results.</li>
 * <li>kill : handles the kill command.</li>
 * <li>silence : toggles private messages acceptance mode.</li>
 * <li>tradeoff : toggles trade acceptance mode.</li>
 * <li>reload : reloads specified component.</li>
 * </ul>
 */
public class AdminAdmin implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_admin",
		"admin_gmlist",
		"admin_kill",
		"admin_silence",
		"admin_tradeoff",
		"admin_reload"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_admin"))
			showMainPage(activeChar, command);
		else if (command.startsWith("admin_gmlist"))
			activeChar.sendMessage((AdminData.getInstance().showOrHideGm(activeChar)) ? "Removed from GMList." : "Registered into GMList.");
		else if (command.startsWith("admin_kill"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (!st.hasMoreTokens())
			{
				final WorldObject obj = activeChar.getTarget();
				if (!(obj instanceof Creature))
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				else
					kill(activeChar, (Creature) obj);
				
				return true;
			}
			
			String firstParam = st.nextToken();
			Player player = World.getInstance().getPlayer(firstParam);
			if (player != null)
			{
				if (st.hasMoreTokens())
				{
					String secondParam = st.nextToken();
					if (StringUtil.isDigit(secondParam))
					{
						int radius = Integer.parseInt(secondParam);
						for (Creature knownChar : player.getKnownTypeInRadius(Creature.class, radius))
						{
							if (knownChar.equals(activeChar))
								continue;
							
							kill(activeChar, knownChar);
						}
						activeChar.sendMessage("Killed all characters within a " + radius + " unit radius around " + player.getName() + ".");
					}
					else
						activeChar.sendMessage("Invalid radius.");
				}
				else
					kill(activeChar, player);
			}
			else if (StringUtil.isDigit(firstParam))
			{
				int radius = Integer.parseInt(firstParam);
				for (Creature knownChar : activeChar.getKnownTypeInRadius(Creature.class, radius))
					kill(activeChar, knownChar);
				
				activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
			}
		}
		else if (command.startsWith("admin_silence"))
		{
			if (activeChar.isInRefusalMode()) // already in message refusal mode
			{
				activeChar.setInRefusalMode(false);
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
			}
			else
			{
				activeChar.setInRefusalMode(true);
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
			}
		}
		else if (command.startsWith("admin_tradeoff"))
		{
			try
			{
				String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on"))
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Trade refusal enabled");
				}
				else if (mode.equalsIgnoreCase("off"))
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Trade refusal disabled");
				}
			}
			catch (Exception e)
			{
				if (activeChar.getTradeRefusal())
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Trade refusal disabled");
				}
				else
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Trade refusal enabled");
				}
			}
		}
		else if (command.startsWith("admin_reload"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				do
				{
					String type = st.nextToken();
					if (type.startsWith("admin"))
					{
						AdminData.getInstance().reload();
						activeChar.sendMessage("Admin data has been reloaded.");
					}
					else if (type.startsWith("announcement"))
					{
						AnnouncementData.getInstance().reload();
						activeChar.sendMessage("The content of announcements.xml has been reloaded.");
					}
					else if (type.startsWith("config"))
					{
						Config.loadGameServer();
						activeChar.sendMessage("Configs files have been reloaded.");
					}
					else if (type.startsWith("crest"))
					{
						CrestCache.getInstance().reload();
						activeChar.sendMessage("Crests have been reloaded.");
					}
					else if (type.startsWith("cw"))
					{
						CursedWeaponManager.getInstance().reload();
						activeChar.sendMessage("Cursed weapons have been reloaded.");
					}
					else if (type.startsWith("door"))
					{
						DoorData.getInstance().reload();
						activeChar.sendMessage("Doors instance has been reloaded.");
					}
					else if (type.startsWith("enchant"))
					{
						EnchantData.getInstance().reload();
						activeChar.sendMessage("Enchant data has been	 reloaded.");
					}
					else if (type.startsWith("poly"))
					{
						PolymorphData.getInstance().reload();
						activeChar.sendMessage("Polymorph templates have been reloaded.");
					}
					else if (type.startsWith("htm"))
					{
						HtmCache.getInstance().reload();
						activeChar.sendMessage("The HTM cache has been reloaded.");
					}
					else if (type.startsWith("item"))
					{
						ItemData.getInstance().reload();
						activeChar.sendMessage("Items' templates have been reloaded.");
					}
					else if (type.equals("multisell"))
					{
						MultisellData.getInstance().reload();
						activeChar.sendMessage("The multisell instance has been reloaded.");
					}
					else if (type.equals("npc"))
					{
						NpcData.getInstance().reload();
						activeChar.sendMessage("NPCs templates have been reloaded.");
					}
					else if (type.startsWith("npcwalker"))
					{
						WalkerRouteData.getInstance().reload();
						activeChar.sendMessage("Walker routes have been reloaded.");
					}
					else if (type.startsWith("skill"))
					{
						SkillTable.getInstance().reload();
						activeChar.sendMessage("Skills' XMLs have been reloaded.");
					}
					else if (type.startsWith("teleport"))
					{
						TeleportLocationData.getInstance().reload();
						activeChar.sendMessage("Teleport locations have been reloaded.");
					}
					else if (type.startsWith("pvpdata"))
					{
						PvPData.getInstance().reload();
						activeChar.sendMessage("PvPData have been reloaded.");
					}
					else if (type.startsWith("zone"))
					{
						ZoneManager.getInstance().reload();
						activeChar.sendMessage("Zones have been reloaded.");
					}
					else
					{
						activeChar.sendMessage("Usage : //reload <admin|announcement|config|crest|cw>");
						activeChar.sendMessage("Usage : //reload <door|enchant|poly|htm|item|multisell|npc>");
						activeChar.sendMessage("Usage : //reload <npcwalker|skill|teleport|pvpdata|zone>");
					}
				}
				while (st.hasMoreTokens());
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage : //reload <admin|announcement|config|crest|cw>");
				activeChar.sendMessage("Usage : //reload <door|enchant|poly|htm|item|multisell|npc>");
				activeChar.sendMessage("Usage : //reload <npcwalker|skill|teleport|pvpdata|zone>");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void kill(Player player, Creature target)
	{
		if (target instanceof Player)
		{
			if (!((Player) target).isGM())
				target.stopAllEffects(); // e.g. invincibility effect
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, player, null);
		}
		else if (target.isChampion())
			target.reduceCurrentHp(target.getMaxHp() * Config.CHAMPION_HP + 1, player, null);
		else
			target.reduceCurrentHp(target.getMaxHp() + 1, player, null);
	}
	
	private static void showMainPage(Player player, String command)
	{
		String filename = "main";
		
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (st.hasMoreTokens())
		{
			final String parameter = st.nextToken();
			if (StringUtil.isDigit(parameter))
			{
				final int mode = Integer.parseInt(parameter);
				if (mode == 2)
					filename = "game";
				else if (mode == 3)
					filename = "effects";
				else if (mode == 4)
					filename = "server";
			}
			else if (HtmCache.getInstance().isLoadable("data/html/admin/" + parameter + "_menu.htm"))
				filename = parameter;
		}
		
		AdminHelpPage.showHelpPage(player, filename + "_menu.htm");
	}
}