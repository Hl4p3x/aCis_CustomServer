/*
* This program is free software: you can redistribute it and/or modify it under
* the terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>.
*/
package Dev.AioMenu;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.data.xml.TeleportLocationData;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.TeleportLocation;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AioMenu implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		2005
	};
	
	@Override

	public void useUserCommand(int command, Player activeChar)
	{
		mainHtml(activeChar, 0);
		return;
	}

	
	public static void mainHtml(Player activeChar, int time)
	{
		NpcHtmlMessage nhm = new NpcHtmlMessage(5);
		StringBuilder html = new StringBuilder("");
		html.append("<html><head><title>AIO Menu</title></head><body><center>");
		html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
		html.append("<table width=305 height=20 bgcolor=000000>");
		html.append("<tr>");
		html.append("<td align=center>Personal AIO Options</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
		if (activeChar.isAio())
		{
			html.append("<br><br>");
			html.append("<button value=\"Towns\" action=\"bypass -h aiopanel panelteleport\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
			html.append("Allow you to move to another Town.");
			html.append("<br>");
			html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html.append("<table width=305 height=20 bgcolor=000000>");
			html.append("<tr>");
			html.append("<td align=center>Color Name Options</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html.append("<br><br>");
			html.append("<a action=\"bypass -h aiopanel color Green\" <font color=\"009900\">Green</font></a>");
			html.append("<a action=\"bypass -h aiopanel color Blue\" <font color=\"3333ff\">Blue</font></a>");
			html.append("<a action=\"bypass -h aiopanel color Purple\" <font color=\"800080\">Purple</font></a>");
			html.append("<a action=\"bypass -h aiopanel color Yellow\" <font color=\"ffff00\">Yellow</font></a>");
			html.append("<a action=\"bypass -h aiopanel color Gold\" <font color=\"cca300\">Gold</font></a>");
			html.append("<br><br><br>");
			html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html.append("<table width=310 height=20 bgcolor=000000>");
			html.append("<tr>");
			html.append("<td align=center>Your AIO Status Period:</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			long delay = activeChar.getMemos().getLong("aioEndTime", 0);
			html.append("AIO Status ends at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(delay) + "");
		}
		else
		{
			html.append("<br>");
			html.append("Your character Isn't AIO.");
		}
		html.append("</center>");
		html.append("</body></html>");
		nhm.setHtml(html.toString());
		activeChar.sendPacket(nhm);
		return;
	}
	
	public static void Time(Player player, int time)
	{
		player.getMemos().set("aioEndTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
	}

	public static void bypass(Player activeChar, String command, StringTokenizer st)
	{
		if (command.equals("panelteleport"))
		{
			NpcHtmlMessage nhm = new NpcHtmlMessage(5);
			StringBuilder html = new StringBuilder("");
			html.append("<html><head><title>AIO Teleport Menu</title></head><body><center>");
			html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html.append("<table width=315 height=20 bgcolor=000000>");
			html.append("<tr>");
			html.append("<td align=center>Choose your destination</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			if (activeChar.isAio())
			{
				html.append("<br><br>");
				html.append("<button value=\"Giran\" action=\"bypass -h aiopanel teleportTo 1040\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Goddard\" action=\"bypass -h aiopanel teleportTo 1039\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Rune\" action=\"bypass -h aiopanel teleportTo 1041\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Aden\" action=\"bypass -h aiopanel teleportTo 1037\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Dion\" action=\"bypass -h aiopanel teleportTo 6\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Gludio\" action=\"bypass -h aiopanel teleportTo 1099\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Gludin\" action=\"bypass -h aiopanel teleportTo 5\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Heine\" action=\"bypass -h aiopanel teleportTo 1036\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Oren\" action=\"bypass -h aiopanel teleportTo 1038\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<button value=\"Schuttgart\" action=\"bypass -h aiopanel teleportTo 1035\" width=122 height=23 fore=\"SS_l2jBan.bf\" back=\"SS_l2jBan.bf\">");
				html.append("<br><br>");
			}
			else
			{
				html.append("<br>");
				html.append("Your character Isn't AIO.");
			}
			html.append("</center>");
			html.append("</body></html>");
			nhm.setHtml(html.toString());
			activeChar.sendPacket(nhm);
		}
		else if (command.equals("teleportTo"))
		{
			final TeleportLocation list = TeleportLocationData.getInstance().getTeleportLocation(Integer.parseInt(st.nextToken()));
			if (list != null)
			{
				if (activeChar.reduceAdena("Teleport", list.getPrice(), activeChar, true))
					activeChar.teleportTo(list, 0);
			}
			else
				activeChar.sendMessage("No teleport destination. Contact with server Admin");
			
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("color"))
			
		{
			NpcHtmlMessage nhm = new NpcHtmlMessage(5);
			StringBuilder html = new StringBuilder("");
			String type = st.nextToken();
			
			switch (type)
			{
				case "Green":

					activeChar.getAppearance().setNameColor(0x009900);
					activeChar.broadcastUserInfo();
					activeChar.sendMessage("Your color name has changed!");
					nhm.setHtml(html.toString());
					activeChar.sendPacket(nhm);
					break;
				case "Blue":

					activeChar.getAppearance().setNameColor(0xff7f00);
					activeChar.broadcastUserInfo();
					activeChar.sendMessage("Your color name has changed!");
					nhm.setHtml(html.toString());
					activeChar.sendPacket(nhm);
					break;
				case "Purple":
			
					activeChar.getAppearance().setNameColor(0x800080);
					activeChar.broadcastUserInfo();
					activeChar.sendMessage("Your color name has changed!");
					nhm.setHtml(html.toString());
					activeChar.sendPacket(nhm);
					break;
				case "Yellow":
				
					activeChar.getAppearance().setNameColor(0x00ffff);
					activeChar.broadcastUserInfo();
					activeChar.sendMessage("Your color name has changed!");
					nhm.setHtml(html.toString());
					activeChar.sendPacket(nhm);
					break;
				case "Gold":
				
					activeChar.getAppearance().setNameColor(0x0099ff);
					activeChar.broadcastUserInfo();
					activeChar.sendMessage("Your color name has changed!");
					nhm.setHtml(html.toString());
					activeChar.sendPacket(nhm);
					break;
			}
		}
	}
		
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}