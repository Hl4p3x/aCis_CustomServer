package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.ZoneShape;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminZone implements IAdminCommandHandler
{
	private static final List<Location> savedLocs = new ArrayList<>();
	private static final String fileName = "coordinates" + "%s" + ".xml";
	private static ZoneShape shape = ZoneShape.NONE;
	private static final int zDifference = 1000;
	private static final String maxLocs = "You have reached the maximum locations for this shape.";
	private static int radius = 0;
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_zone_check",
		"admin_zone_visual",
		"admin_create_zone",
		"admin_setType",
		"admin_setRad",
		"admin_saveLoc",
		"admin_reset",
		"admin_removeLoc",
		"admin_storeLocs"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (activeChar == null)
			return false;
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("admin_zone_check"))
			showHtml(activeChar);
		else if (actualCommand.equalsIgnoreCase("admin_zone_visual"))
		{
			try
			{
				String next = st.nextToken();
				if (next.equalsIgnoreCase("all"))
				{
					for (ZoneType zone : ZoneManager.getInstance().getZones(activeChar))
						zone.visualizeZone(activeChar.getZ());
					
					showHtml(activeChar);
				}
				else if (next.equalsIgnoreCase("clear"))
				{
					ZoneManager.getInstance().clearDebugItems();
					showHtml(activeChar);
				}
				else
				{
					int zoneId = Integer.parseInt(next);
					ZoneManager.getInstance().getZoneById(zoneId).visualizeZone(activeChar.getZ());
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Invalid parameter for //zone_visual.");
			}
		}
		else if (command.startsWith("admin_create_zone"))
			openHtml(activeChar);
		else if (command.startsWith("admin_setType"))
		{
			clear();
			shape = ZoneShape.valueOf(st.nextToken());
			switch (shape)
			{
				case NPoly:
					activeChar.sendMessage("You can add unlimited but atleast 3 coordinates in this shape.");
					break;
				case Cuboid:
					activeChar.sendMessage("You must add 2 coordinates (in order to make a square) in this shape.");
					break;
				case Cylinder:
					activeChar.sendMessage("You must add 1 coordinates and radius (in order to make a circle) in this shape.");
					break;
				default:
					activeChar.sendMessage("You have to select the zone shape first.");
			}
			openHtml(activeChar);
		}
		else if (command.startsWith("admin_saveLoc"))
		{
			if (canSaveLoc(activeChar))
			{
				final Location loc = new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ());
				if (savedLocs.add(loc))
					activeChar.sendMessage(loc + " saved..");
			}
			openHtml(activeChar);
		}
		else if (command.startsWith("admin_reset"))
		{
			clear();
			activeChar.sendMessage("Reset completed.");
			openHtml(activeChar);
		}
		else if (command.startsWith("admin_removeLoc"))
		{
			if (savedLocs.size() > 0)
				activeChar.sendMessage(savedLocs.remove(savedLocs.size() - 1) + " removed.");
			openHtml(activeChar);
		}
		else if (command.startsWith("admin_storeLocs"))
		{
			if (savedLocs.isEmpty())
			{
				activeChar.sendMessage("Empty locs..");
				return false;
			}
			store(activeChar);
		}
		else if (command.startsWith("admin_setRad"))
		{
			if (!st.hasMoreTokens() || !setRadius(parseInt(st.nextToken())))
				activeChar.sendMessage("Invalid value or shape.");
			else
				activeChar.sendMessage("Radius stored.");
			
			openHtml(activeChar);
		}
		
		return true;
	}

	private static int calcZ(boolean minZ)
	{
		return (savedLocs.stream().mapToInt(loc -> loc.getZ()).sum() / savedLocs.size()) + (minZ ? -zDifference : zDifference);
	}
	
	private static int parseInt(String nextToken)
	{
		try
		{
			return Integer.parseInt(nextToken);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	private static void store(Player gm)
	{
		if (!canStoreLocs(gm))
			return;
		final String fName = String.format(fileName, "_" + shape.name() + "_" + getTimeStamp());
		String filePath = "";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fName)))
		{
			final File file = new File(fName);
			filePath = file.getAbsolutePath().replaceAll("\\\\", "/");
			writer.write(getHeadLine());
			for (Location loc : savedLocs)
				writer.write(String.format("\t<node X=\"%s\" Y=\"%s\" />\r\n", loc.getX(), loc.getY()));
			
			writer.write("</zone>");
		}
		catch (IOException e)
		{
			gm.sendMessage(String.format("Couldn't store coordinates in %s file.", fName));
			e.printStackTrace();
		}
		gm.sendMessage("Coordinates has been successfully stored at " + filePath);
		clear();
		openHtml(gm);
	}
	
	private static String getTimeStamp()
	{
		return new SimpleDateFormat("hh-mm-ss").format(new Date());
	}
	
	private static boolean canStoreLocs(Player gm)
	{
		switch (shape)
		{
			case NPoly:
				if (savedLocs.size() < 3)
				{
					gm.sendMessage("You have to set atleast 3 coordinates!");
					return false;
				}
				return true;
			case Cuboid:
				if (savedLocs.size() != 2)
				{
					gm.sendMessage("You have to set 2 coordinates.");
					return false;
				}
				return true;
			case Cylinder:
				if (savedLocs.size() != 1)
				{
					gm.sendMessage("Only 1 location required for this shape.");
					return false;
				}
				return true;
			default:
				gm.sendMessage("You have to select the zone shape first.");
				return false;
		}
	}
	
	private static boolean canSaveLoc(Player activeChar)
	{
		switch (shape)
		{
			case NPoly:
				return true; // unlimited locs can be added
			case Cuboid:
				if (savedLocs.size() >= 2)
				{
					activeChar.sendMessage(maxLocs);
					return false;
				}
				return true;
			case Cylinder:
				if (savedLocs.size() >= 1)
				{
					activeChar.sendMessage("Only 1 locations required for this shape.");
					return false;
				}
				return true;
			default:
				activeChar.sendMessage("You have to select the zone shape first.");
				return false;
		}
	}
	
	private static String getHeadLine()
	{
		switch (shape)
		{
			case NPoly:
				return String.format("<zone shape='NPoly' minZ='%s' maxZ='%s'>\r\n", calcZ(true), calcZ(false));
			case Cuboid:
				return String.format("<zone shape='Cuboid' minZ='%s' maxZ='%s'>\r\n", calcZ(true), calcZ(false));
			case Cylinder:
				return String.format("<zone shape='Cylinder' minZ='%s' maxZ='%s' rad='%s'>\r\n", calcZ(true), calcZ(false), getRad());
			default:
				return "";
		}
	}
	
	private static boolean setRadius(int val)
	{
		if (shape == ZoneShape.Cylinder)
		{
			if (val == 0)
				return false;
			radius = val;
		}
		
		return shape == ZoneShape.Cylinder;
	}
	
	private static int getRad()
	{
		return radius;
	}
	
	private static void openHtml(Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/zone_create.htm");
		switch (shape)
		{
			case NONE:
				html.replace("%zoneShape%", "Empty");
				html.replace("%locsSize%", "Locations Saved: Null");
				html.replace("%proceed%", "");
				html.replace("%undo%", savedLocs.size() > 0 ? "<button value=\"Undo\" action=\"bypass admin_removeLoc\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">" : "");
				html.replace("%dist%", "");
				break;
			case NPoly:
				html.replace("%zoneShape%", shape.name());
				html.replace("%locsSize%", "Locations Saved: " + savedLocs.size());
				html.replace("%proceed%", savedLocs.size() > 2 ? "<button value=\"Store\" action=\"bypass admin_storeLocs\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">" : "");
				html.replace("%undo%", savedLocs.size() > 0 ? "<button value=\"Undo\" action=\"bypass admin_removeLoc\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">" : "");
				html.replace("%dist%", "");
				break;
			case Cuboid:
				html.replace("%zoneShape%", shape.name());
				html.replace("%locsSize%", "Locations Saved: " + savedLocs.size());
				html.replace("%proceed%", savedLocs.size() == 2 ? "<button value=\"Store\" action=\"bypass admin_storeLocs\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">" : "");
				html.replace("%undo%", savedLocs.size() > 0 ? "<button value=\"Undo\" action=\"bypass admin_removeLoc\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">" : "");
				html.replace("%dist%", "");
				break;
			case Cylinder:
				html.replace("%zoneShape%", shape.name());
				html.replace("%locsSize%", "Locations Saved: " + savedLocs.size());
				html.replace("%undo%", savedLocs.size() == 1 ? "<button value=\"Undo\" action=\"bypass admin_removeLoc\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">" : "");
				if (savedLocs.size() == 1)
				{
					if (radius == 0)
					{
						html.replace("%proceed%", "");
						html.replace("%dist%", "Set the radius: <edit var=\"Radius\" width=110 height=15> <button value=\"Save\" action=\"bypass admin_setRad $Radius\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">");
					}
					else
					{
						html.replace("%proceed%", "<button value=\"Store\" action=\"bypass admin_storeLocs\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\">");
						html.replace("%dist%", "");
					}
				}
				else
				{
					html.replace("%proceed%", "");
					html.replace("%dist%", "");
				}
				break;
		}
		activeChar.sendPacket(html);
	}
	
	private static void clear()
	{
		shape = ZoneShape.NONE;
		savedLocs.clear();
		radius = 0;
	}
	
	private static void showHtml(Player player)
	{
		int x = player.getX();
		int y = player.getY();
		int rx = (x - World.WORLD_X_MIN) / World.TILE_SIZE + World.TILE_X_MIN;
		int ry = (y - World.WORLD_Y_MIN) / World.TILE_SIZE + World.TILE_Y_MIN;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/zone.htm");
		
		html.replace("%MAPREGION%", "[x:" + MapRegionData.getMapRegionX(x) + " y:" + MapRegionData.getMapRegionY(y) + "]");
		html.replace("%GEOREGION%", rx + "_" + ry);
		html.replace("%CLOSESTTOWN%", MapRegionData.getInstance().getClosestTownName(x, y));
		html.replace("%CURRENTLOC%", x + ", " + y + ", " + player.getZ());
		
		final StringBuilder sb = new StringBuilder(100);
		
		for (ZoneId zone : ZoneId.VALUES)
		{
			if (player.isInsideZone(zone))
				StringUtil.append(sb, zone, "<br1>");
		}
		html.replace("%ZONES%", sb.toString());
		
		// Reset the StringBuilder for another use.
		sb.setLength(0);
		
		for (ZoneType zone : World.getInstance().getRegion(x, y).getZones())
		{
			if (zone.isCharacterInZone(player))
				StringUtil.append(sb, zone.getId(), " ");
		}
		html.replace("%ZLIST%", sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}