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
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;

public class VipMenu implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		303
	};
	
	@Override
	
	public void useUserCommand(int command, Player activeChar)
	{
		mainHtml(activeChar, 0);
		return;
	}
	
	
	public static void mainHtml(Player activeChar, int time)
	{
		
		if (activeChar.isVip())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/VipMenu.htm");
			html.replace("%name%", activeChar.getName());
			html.replace("%TimeServer%", GameTimeTaskManager.getInstance().getGameTimeFormated());
			html.replace("%online%", World.getInstance().getPlayers().size());
			html.replace("%time%", new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date(activeChar.getMemos().getLong("vipTime", 0))));
			long now = Calendar.getInstance().getTimeInMillis();
			long endDay = activeChar.getMemos().getLong("vipTime");
			
			if (now > endDay)
				activeChar.setVip(false);
			else
			{
				activeChar.setVip(true);
			}
			activeChar.sendPacket(html);
		}else{
			NpcHtmlMessage nhm = new NpcHtmlMessage(5);
			StringBuilder html1 = new StringBuilder("");
			html1.append("<html><head><title>Vip Menu</title></head><body><center>");
			html1.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html1.append("<table width=305 height=20 bgcolor=000000>");
			html1.append("<tr>");
			html1.append("<td align=center>Personal Vip Options</td>");
			html1.append("</tr>");
			html1.append("</table>");
			html1.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html1.append("<br>");
			html1.append("Your character Isn't Vip.");
			
			html1.append("</center>");
			html1.append("</body></html>");
			nhm.setHtml(html1.toString());
			activeChar.sendPacket(nhm);
		}
		
	}
	
	public static void Time(Player player, int time)
	{
		player.getMemos().set("vipEndTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
		
		
	}
	
	public static void bypass(Player activeChar, String command, StringTokenizer st)
	{
		
		if (activeChar.isVip())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/VipMenu.htm");
			html.replace("%name%", activeChar.getName());
			html.replace("%TimeServer%", GameTimeTaskManager.getInstance().getGameTimeFormated());
			html.replace("%online%", World.getInstance().getPlayers().size());
			html.replace("%time%", new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date(activeChar.getMemos().getLong("vipTime", 0))));
			long now = Calendar.getInstance().getTimeInMillis();
			long endDay = activeChar.getMemos().getLong("vipTime");
			
			if (now > endDay)
				activeChar.setVip(false);
			else
			{
				activeChar.setVip(true);
			}
			activeChar.sendPacket(html);
		}else{
			NpcHtmlMessage nhm = new NpcHtmlMessage(5);
			StringBuilder html1 = new StringBuilder("");
			html1.append("<html><head><title>Vip Menu</title></head><body><center>");
			html1.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html1.append("<table width=305 height=20 bgcolor=000000>");
			html1.append("<tr>");
			html1.append("<td align=center>Personal Vip Options</td>");
			html1.append("</tr>");
			html1.append("</table>");
			html1.append("<img src=\"SS_l2jNetwork.lineo\" width=300 height=3>");
			html1.append("<br>");
			html1.append("Your character Isn't Vip.");
			
			html1.append("</center>");
			html1.append("</body></html>");
			nhm.setHtml(html1.toString());
			activeChar.sendPacket(nhm);
		}
		
		
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}