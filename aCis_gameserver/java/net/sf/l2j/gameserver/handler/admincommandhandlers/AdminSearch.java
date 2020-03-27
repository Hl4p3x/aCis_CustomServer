package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Williams
 *
 */
public class AdminSearch implements IAdminCommandHandler
{
	private static final int PAGE_LIMIT = 6;
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_search"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/search.htm");
		
		if (command.equals("admin_search"))
			html.replace("%list%", "<center><br><br><br>Set first an key word</center>");
		else if (command.startsWith("admin_search"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			int page = Integer.valueOf(st.nextToken());
			if (st.hasMoreTokens())
			{
				StringBuilder sb = new StringBuilder(String.valueOf(page));
				StringBuilder list = getList(activeChar, page, command.substring(14 + sb.length()));
				html.replace("%list%", list == null ? "" : list.toString());
			}
			else
				html.replace("%list%", "<center><br><br><br>Set first an key word</center>");
		}
		activeChar.sendPacket(html);
		return true;
	}
	
	public StringBuilder getList(Player activeChar, int page, String search)
	{
		List<Item> items = Arrays.asList(ItemData.getInstance().getTemplates()).stream().filter(item -> item != null && matches(item.getName(), search)).collect(Collectors.toList());
		
		if (items == null || items.isEmpty())
			return new StringBuilder("<center><br><br><br>There its no item : <font color=LEVEL>" + search + "</font></center>");
		
		final int max = Math.min(100, MathUtil.countPagesNumber(items.size(), PAGE_LIMIT));
		items = items.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, items.size()));
		
		final StringBuilder sb = new StringBuilder();
		
		int row = 0;
		for (Item item : items)
		{
			String name = item.getName();
			
			if (name.length() >= 43)
				name = name.substring(0, 40) + "...";
			
			sb.append("<table width=280 bgcolor=000000><tr>");
			sb.append("<td width=44 height=41 align=center><table bgcolor=FFFFFF cellpadding=6 cellspacing=\"-5\"><tr><td><button width=32 height=32 back=" + item.getIcon() + " fore=" + item.getIcon() + "></td></tr></table></td>");
			sb.append("<td width=236>" + name + "<br1><font color=B09878>Item ID : " + item.getItemId() + " " + (item.isQuestItem() ? "(Quest Item)" : "") + "</font></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=280 height=1>");
			row++;
		}
		
		for (int i = PAGE_LIMIT; i > row; i--)
			sb.append("<img height=42>");
		
		// Build page footer.
		sb.append("<img height=2><img src=L2UI.SquareGray width=280 height=1><table width=280 bgcolor=000000><tr>");
		sb.append("<td align=right width=70>" + (page > 1 ? "<button value=\"< PREV\" action=\"bypass admin_search " + (page - 1) + " " + search + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "") + "</td>");
		sb.append("<td align=center width=100>Page " + page + "</td>");
		sb.append("<td align=left width=70>" + (page < max ? "<button value=\"NEXT >\" action=\"bypass admin_search " + (page + 1) + " " + search + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "") + "</td>");
		sb.append("</tr></table><img src=L2UI.SquareGray width=280 height=1>");
		return sb;
	}
	
	public static boolean matches(String item, String seach)
	{
		for (String split : seach.toLowerCase().split(" "))
			if (!item.toLowerCase().contains(split))
				return false;
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}