package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.Config;



import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import Dev.Tournament.Arena2x2;
import Dev.Tournament.Arena4x4;
import Dev.Tournament.Arena9x9;

import net.sf.l2j.gameserver.model.actor.Player;

public class Tournament extends Folk
{
	
	public Tournament(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/tournament/9996.htm";
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());

		   if (Arena2x2.registered.size() == 0) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
	        }
	        else if (Arena2x2.registered.size() == 1) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
	        }
	        else if (Arena2x2.registered.size() == 2) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
	        }
	        else if (Arena2x2.registered.size() == 3) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
	        }
	        else if (Arena2x2.registered.size() == 4) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
	        }
	        else if (Arena2x2.registered.size() == 5) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
	        }
	        else if (Arena2x2.registered.size() == 6) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
	        }
	        else if (Arena2x2.registered.size() == 7) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
	        }
	        else if (Arena2x2.registered.size() == 8) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
	        }
	        else if (Arena2x2.registered.size() >= 9) {
	            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
	        }
		   
			if (Arena2x2.registered.size() == 0)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 1)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 2)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 3)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 4)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 5)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 6)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 7)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 8)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() >= 9)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		
			if (Arena2x2.registered.size() == 0)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 1)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 2)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 3)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 4)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 5)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 6)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 7)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() == 8)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena2x2.registered.size() >= 9)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		
	
	        player.sendPacket(html);
	    }
	
	
	public void showChatWindow1(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/tournament/9996.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());


	
			if (Arena4x4.registered.size() == 0)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 1)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
			else if (Arena4x4.registered.size() == 2)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
			else if (Arena4x4.registered.size() == 3)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
			else if (Arena4x4.registered.size() == 4)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
			else if (Arena4x4.registered.size() == 5)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
			else if (Arena4x4.registered.size() == 6)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
			else if (Arena4x4.registered.size() == 7)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
			else if (Arena4x4.registered.size() == 8)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
			else if (Arena4x4.registered.size() >= 9)
				html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
		
			   if (Arena4x4.registered.size() == 0) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 1) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 2) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 3) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 4) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 5) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 6) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 7) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() == 8) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena4x4.registered.size() >= 9) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
			   
			   
			if (Arena4x4.registered.size() == 0)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 1)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 2)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 3)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 4)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 5)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 6)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 7)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() == 8)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena4x4.registered.size() >= 9)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		
	
	        player.sendPacket(html);
	    }
	
	public void showChatWindow2(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/tournament/9996.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());


	
			if (Arena9x9.registered.size() == 0)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			else if (Arena9x9.registered.size() == 1)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
			else if (Arena9x9.registered.size() == 2)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
			else if (Arena9x9.registered.size() == 3)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
			else if (Arena9x9.registered.size() == 4)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
			else if (Arena9x9.registered.size() == 5)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
			else if (Arena9x9.registered.size() == 6)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
			else if (Arena9x9.registered.size() == 7)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
			else if (Arena9x9.registered.size() == 8)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
			else if (Arena9x9.registered.size() >= 9)
				html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
		
			   if (Arena9x9.registered.size() == 0) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 1) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 2) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 3) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 4) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 5) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 6) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 7) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() == 8) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
		        else if (Arena9x9.registered.size() >= 9) {
		            html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		        }
			   
				if (Arena9x9.registered.size() == 0)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 1)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 2)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 3)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 4)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 5)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 6)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 7)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() == 8)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
				else if (Arena9x9.registered.size() >= 9)
					html.replace("%4x4%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
			
	
	        player.sendPacket(html);
	    }
	
	

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Object className;
		if (command.startsWith("2x2"))
		{
			if (!Config.ALLOW_2X2_REGISTER)
			{
				player.sendPacket(SystemMessageId.ACADEMY_LIST_HEADER);
				return;
			}

			if (player.isArena2x2() || player.isArena4x4() || player.isArena9x9() ||player.isArenaProtection())
			{
				player.sendMessage("Tournament: You already registered!");
				return;
			}
			if (!player.isInParty())
			{
				player.sendMessage("Tournament: You dont have a party.");
				return;
			}
			if (!player.getParty().isLeader(player))
			{
				player.sendMessage("Tournament: You are not the party leader!");
				return;
			}
			if (player.getParty().getMembersCount() < 2)
			{
				player.sendMessage("Tournament: Your party does not have 2 members.");
				player.sendPacket(new ExShowScreenMessage("Your party does not have 2 members", 6000));
				return;
			}
			if (player.getParty().getMembersCount() > 2)
			{
				player.sendMessage("Tournament: Your Party can not have more than 2 members.");
				player.sendPacket(new ExShowScreenMessage("Your Party can not have more than 2 members", 6000));
				return;
			}

			Player assist = player.getParty().getMembers().get(1);
			
			className = PlayerData.getInstance().getClassNameById(player.getClassId().getId());
			String assist_className = PlayerData.getInstance().getClassNameById(assist.getClassId().getId());



			if ((player.getClassId() == ClassId.GLADIATOR || player.getClassId() == ClassId.DUELIST || player.getClassId() == ClassId.GRAND_KHAVATARI || player.getClassId() == ClassId.TYRANT) && (assist.getClassId() == ClassId.GLADIATOR || assist.getClassId() == ClassId.DUELIST || assist.getClassId() == ClassId.GRAND_KHAVATARI || assist.getClassId() == ClassId.TYRANT))
			{
				player.sendMessage("Tournament: Only 1 " + (String) className + " / " + assist_className + " allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only 1 " + (String) className + " / " + assist_className + " allowed per party.", 6000));
				return;
			}
			if (assist.getClassId() == ClassId.SHILLIEN_ELDER || assist.getClassId() == ClassId.SHILLIEN_SAINT || assist.getClassId() == ClassId.BISHOP || assist.getClassId() == ClassId.CARDINAL || assist.getClassId() == ClassId.ELVEN_ELDER || assist.getClassId() == ClassId.EVAS_SAINT)
			{
				assist.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				player.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				return;
			}
			if (player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.EVAS_SAINT)
			{
				assist.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				player.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				return;
			}
			if (player.isCursedWeaponEquipped() || assist.isCursedWeaponEquipped() || player.isInStoreMode() || assist.isInStoreMode() || player.getKarma() > 0 || assist.getKarma() > 0)
			{
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				return;
			}
			if (player.getClassId() == assist.getClassId())
			{
				player.sendMessage("Tournament: Only 1 " + (String) className + "'s allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only 1 " + (String) className + "'s allowed per party.", 6000));
				return;
			}
			if ((player.getClassId() == ClassId.HAWKEYE || player.getClassId() == ClassId.SAGGITARIUS || player.getClassId() == ClassId.MOONLIGHT_SENTINEL || player.getClassId() == ClassId.SILVER_RANGER || player.getClassId() == ClassId.GHOST_SENTINEL || player.getClassId() == ClassId.PHANTOM_RANGER) && (assist.getClassId() == ClassId.HAWKEYE || assist.getClassId() == ClassId.SAGGITARIUS || assist.getClassId() == ClassId.MOONLIGHT_SENTINEL || assist.getClassId() == ClassId.SILVER_RANGER || assist.getClassId() == ClassId.GHOST_SENTINEL || assist.getClassId() == ClassId.PHANTOM_RANGER))
			{
				player.sendMessage("Tournament: Only 1 Acher allowed per party.");
				player.sendPacket(new ExShowScreenMessage("OOnly 1 Acher allowed per party.", 6000));
				return;
			}
			if ((player.getClassId() == ClassId.ADVENTURER || player.getClassId() == ClassId.TREASURE_HUNTER || player.getClassId() == ClassId.WIND_RIDER || player.getClassId() == ClassId.PLAINS_WALKER || player.getClassId() == ClassId.GHOST_HUNTER || player.getClassId() == ClassId.ABYSS_WALKER) && (assist.getClassId() == ClassId.ADVENTURER || assist.getClassId() == ClassId.TREASURE_HUNTER || assist.getClassId() == ClassId.WIND_RIDER || assist.getClassId() == ClassId.PLAINS_WALKER || assist.getClassId() == ClassId.GHOST_HUNTER || assist.getClassId() == ClassId.ABYSS_WALKER))
			{
				player.sendMessage("Tournament: Only 1 Dagger allowed per party.");
				player.sendPacket(new ExShowScreenMessage("OOnly 1 Dagger allowed per party.", 6000));
				return;
			}
			if (OlympiadManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(assist))
			{
				player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				return;
			}


			if (Arena2x2.getInstance().register(player, assist))
			{
				player.sendMessage("Tournament: Your participation has been approved.");
				assist.sendMessage("Tournament: Your participation has been approved.");
				player.setArenaProtection(true);
				assist.setArenaProtection(true);
				player.setArena2x2(true);
				assist.setArena2x2(true);
				showChatWindow(player);
			}
			

			else
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
		}else
		{
			Object assist2;
			if (command.startsWith("4x4"))
			{

				if (!Config.ALLOW_4X4_REGISTER)
				{
					player.sendPacket(SystemMessageId.ACADEMY_LIST_HEADER);
					return;
				}

				if (player.isArena2x2() || player.isArena4x4() || player.isArena9x9() || player.isArenaProtection())
				{
					player.sendMessage("Tournament: You already registered!");
					return;
				}
				if (!player.isInParty())
				{
					player.sendMessage("Tournament: You dont have a party.");
					return;
				}
				if (!player.getParty().isLeader(player))
				{
					player.sendMessage("Tournament: You are not the party leader!");
					return;
				}
				if (player.getParty().getMembersCount() < 4)
				{
					player.sendMessage("Tournament: Your party does not have 4 members.");
					player.sendPacket(new ExShowScreenMessage("Your party does not have 4 members", 6000));
					return;
				}
				if (player.getParty().getMembersCount() > 4)
				{
					player.sendMessage("Tournament: Your Party can not have more than 4 members.");
					player.sendPacket(new ExShowScreenMessage("Your Party can not have more than 4 members", 6000));
					return;
				}

				Player assist = player.getParty().getMembers().get(1);
				assist2 = player.getParty().getMembers().get(2);
				Player assist3 = player.getParty().getMembers().get(3);

				if (player.isCursedWeaponEquipped() || assist.isCursedWeaponEquipped() || ((Player) assist2).isCursedWeaponEquipped() || assist3.isCursedWeaponEquipped() || player.isInStoreMode() || assist.isInStoreMode() || ((Player) assist2).isInStoreMode() || assist3.isInStoreMode() || player.getKarma() > 0 || assist.getKarma() > 0 || ((Player) assist2).getKarma() > 0 || assist3.getKarma() > 0)
				{
					player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					((Player) assist2).sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist3.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					return;
				}
				if (OlympiadManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(assist) || OlympiadManager.getInstance().isRegistered((Player) assist2) || OlympiadManager.getInstance().isRegistered(assist3))
				{
					player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					((Player) assist2).sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist3.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					return;
				}



				ClasseCheck(player);

				if (player.duelist_cont > Config.duelist_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.duelist_COUNT_4X4 + " Duelist's or " + Config.duelist_COUNT_4X4 + " Grand Khauatari's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.duelist_COUNT_4X4 + " Duelist's or " + Config.duelist_COUNT_4X4 + " Grand Khauatari'sallowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.dreadnought_cont > Config.dreadnought_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.dreadnought_COUNT_4X4 + " Dread Nought's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.dreadnought_COUNT_4X4 + " Dread Nought's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.tanker_cont > Config.tanker_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.tanker_COUNT_4X4 + " Tanker's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.tanker_COUNT_4X4 + " Tanker's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.dagger_cont > Config.dagger_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.dagger_COUNT_4X4 + " Dagger's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.dagger_COUNT_4X4 + " Dagger's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.archer_cont > Config.archer_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.archer_COUNT_4X4 + " Archer's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.archer_COUNT_4X4 + " Archer's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.bs_cont > Config.bs_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.bs_COUNT_4X4 + " Bishop's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.bs_COUNT_4X4 + " Bishop's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.archmage_cont > Config.archmage_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.archmage_COUNT_4X4 + " Archmage's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.archmage_COUNT_4X4 + " Archmage's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.soultaker_cont > Config.soultaker_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.soultaker_COUNT_4X4 + " Soultaker's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.soultaker_COUNT_4X4 + " Soultaker's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.mysticMuse_cont > Config.mysticMuse_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.mysticMuse_COUNT_4X4 + " Mystic Muse's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.mysticMuse_COUNT_4X4 + " Mystic Muse's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.stormScreamer_cont > Config.stormScreamer_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.stormScreamer_COUNT_4X4 + " Storm Screamer's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.stormScreamer_COUNT_4X4 + " Storm Screamer's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.titan_cont > Config.titan_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.titan_COUNT_4X4 + " Titan's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.titan_COUNT_4X4 + " Titan's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.dominator_cont > Config.dominator_COUNT_4X4)
				{
					player.sendMessage("Tournament: Only " + Config.dominator_COUNT_4X4 + " Dominator's or " + Config.dominator_COUNT_4X4 + " Doomcryer's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.dominator_COUNT_4X4 + " Dominator's or " + Config.dominator_COUNT_4X4 + " Doomcryer's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (Arena4x4.getInstance().register(player, assist, (Player) assist2, assist3) && player.getParty().getMembers().get(1) != null && player.getParty().getMembers().get(2) != null && player.getParty().getMembers().get(3) != null)
				{
					player.sendMessage("Tournament: Your participation has been approved.");
					assist.sendMessage("Tournament: Your participation has been approved.");
					((Player) assist2).sendMessage("Tournament: Your participation has been approved.");
					assist3.sendMessage("Tournament: Your participation has been approved.");

					player.setArenaProtection(true);
					assist.setArenaProtection(true);
					((Player) assist2).setArenaProtection(true);
					assist3.setArenaProtection(true);

					player.setArena4x4(true);
					assist.setArena4x4(true);
					((Player) assist2).setArena4x4(true);
					assist3.setArena4x4(true);
					clean(player);
					showChatWindow1(player);
				}
				else
					player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
			}
			else if (command.startsWith("9x9"))
			{

				if (!Config.ALLOW_9X9_REGISTER)
				{
					player.sendPacket(SystemMessageId.ACADEMY_LIST_HEADER);
					return;
				}

				if (player.isArena2x2() || player.isArena4x4() || player.isArena9x9() || player.isArenaProtection())
				{
					player.sendMessage("Tournament: You already registered!");
					return;
				}
				if (!player.isInParty())
				{
					player.sendMessage("Tournament: You dont have a party.");
					return;
				}
				if (!player.getParty().isLeader(player))
				{
					player.sendMessage("Tournament: You are not the party leader!");
					return;
				}
				if (player.getParty().getMembersCount() < 9)
				{
					player.sendMessage("Tournament: Your party does not have 9 members.");
					player.sendPacket(new ExShowScreenMessage("Your party does not have 9 members", 6000));
					return;
				}
				if (player.getParty().getMembersCount() > 9)
				{
					player.sendMessage("Tournament: Your Party can not have more than 9 members.");
					player.sendPacket(new ExShowScreenMessage("Your Party can not have more than 9 members", 6000));
					return;
				}

				Player assist = player.getParty().getMembers().get(1);
				Player assist21 = player.getParty().getMembers().get(2);
				Player assist3 = player.getParty().getMembers().get(3);
				Player assist4 = player.getParty().getMembers().get(4);
				Player assist5 = player.getParty().getMembers().get(5);
				Player assist6 = player.getParty().getMembers().get(6);
				Player assist7 = player.getParty().getMembers().get(7);
				Player assist8 = player.getParty().getMembers().get(8);

				if (player.isCursedWeaponEquipped() || assist.isCursedWeaponEquipped() || assist21.isCursedWeaponEquipped() || assist3.isCursedWeaponEquipped() || assist4.isCursedWeaponEquipped() || assist5.isCursedWeaponEquipped() || assist6.isCursedWeaponEquipped() || assist7.isCursedWeaponEquipped() || assist8.isCursedWeaponEquipped() || player.isInStoreMode() || assist.isInStoreMode() || assist21.isInStoreMode() || assist3.isInStoreMode() || assist4.isInStoreMode() || assist5.isInStoreMode() || assist6.isInStoreMode() || assist7.isInStoreMode() || assist8.isInStoreMode() || player.getKarma() > 0 || assist.getKarma() > 0 || assist21.getKarma() > 0 || assist3.getKarma() > 0 || assist4.getKarma() > 0 || assist5.getKarma() > 0 || assist6.getKarma() > 0 || assist7.getKarma() > 0 || assist8.getKarma() > 0)
				{
					player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist21.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist3.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist4.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist5.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist6.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist7.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					assist8.sendMessage("Tournament: You or your member does not have the necessary requirements.");
					return;
				}
				if (OlympiadManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(assist) || OlympiadManager.getInstance().isRegistered(assist21) || OlympiadManager.getInstance().isRegistered(assist3) || OlympiadManager.getInstance().isRegistered(assist4) || OlympiadManager.getInstance().isRegistered(assist5) || OlympiadManager.getInstance().isRegistered(assist6) || OlympiadManager.getInstance().isRegistered(assist7) || OlympiadManager.getInstance().isRegistered(assist8))
				{
					player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist21.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist3.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist4.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist5.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist6.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist7.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					assist8.sendMessage("Tournament: You or your member is registered in the Olympiad.");
					return;
				}


	
				ClasseCheck(player);

				if (player.duelist_cont > Config.duelist_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.duelist_COUNT_9X9 + " Duelist's or " + Config.duelist_COUNT_9X9 + " Grand Khauatari's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.duelist_COUNT_9X9 + " Duelist's or " + Config.duelist_COUNT_9X9 + " Grand Khauatari's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.dreadnought_cont > Config.dreadnought_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.dreadnought_COUNT_9X9 + " Dread Nought's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.dreadnought_COUNT_9X9 + " Dread Nought's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.tanker_cont > Config.tanker_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.tanker_COUNT_9X9 + " Tanker's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.tanker_COUNT_9X9 + " Tanker's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.dagger_cont > Config.dagger_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.dagger_COUNT_9X9 + " Dagger's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.dagger_COUNT_9X9 + " Dagger's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.archer_cont > Config.archer_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.archer_COUNT_9X9 + " Archer's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.archer_COUNT_9X9 + " Archer's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.bs_cont > Config.bs_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.bs_COUNT_9X9 + " Bishop's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.bs_COUNT_9X9 + " Bishop's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.archmage_cont > Config.archmage_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.archmage_COUNT_9X9 + " Archmage's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.archmage_COUNT_9X9 + " Archmage's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.soultaker_cont > Config.soultaker_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.soultaker_COUNT_9X9 + " Soultaker's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.soultaker_COUNT_9X9 + " Soultaker's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.mysticMuse_cont > Config.mysticMuse_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.mysticMuse_COUNT_9X9 + " Mystic Muse's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.mysticMuse_COUNT_9X9 + " Mystic Muse's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.stormScreamer_cont > Config.stormScreamer_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.stormScreamer_COUNT_9X9 + " Storm Screamer's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.stormScreamer_COUNT_9X9 + " Storm Screamer's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.titan_cont > Config.titan_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.titan_COUNT_9X9 + " Titan's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.titan_COUNT_9X9 + " Titan's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (player.dominator_cont > Config.dominator_COUNT_9X9)
				{
					player.sendMessage("Tournament: Only " + Config.dominator_COUNT_9X9 + " Dominator's or " + Config.dominator_COUNT_9X9 + " Doomcryer's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.dominator_COUNT_9X9 + " Dominator's or " + Config.dominator_COUNT_9X9 + " Doomcryer's allowed per party.", 6000));
					clean(player);
					return;
				}
				if (Arena9x9.getInstance().register(player, assist, assist21, assist3, assist4, assist5, assist6, assist7, assist8) && player.getParty().getMembers().get(1) != null && player.getParty().getMembers().get(2) != null && player.getParty().getMembers().get(3) != null && player.getParty().getMembers().get(4) != null && player.getParty().getMembers().get(5) != null && player.getParty().getMembers().get(6) != null && player.getParty().getMembers().get(7) != null && player.getParty().getMembers().get(8) != null)
				{
					player.sendMessage("Tournament: Your participation has been approved.");
					assist.sendMessage("Tournament: Your participation has been approved.");
					assist21.sendMessage("Tournament: Your participation has been approved.");
					assist3.sendMessage("Tournament: Your participation has been approved.");
					assist4.sendMessage("Tournament: Your participation has been approved.");
					assist5.sendMessage("Tournament: Your participation has been approved.");
					assist6.sendMessage("Tournament: Your participation has been approved.");
					assist7.sendMessage("Tournament: Your participation has been approved.");
					assist8.sendMessage("Tournament: Your participation has been approved.");

					player.setArenaProtection(true);
					assist.setArenaProtection(true);
					assist21.setArenaProtection(true);
					assist3.setArenaProtection(true);
					assist4.setArenaProtection(true);
					assist5.setArenaProtection(true);
					assist6.setArenaProtection(true);
					assist7.setArenaProtection(true);
					assist8.setArenaProtection(true);

					player.setArena9x9(true);
					assist.setArena9x9(true);
					assist21.setArena9x9(true);
					assist3.setArena9x9(true);
					assist4.setArena9x9(true);
					assist5.setArena9x9(true);
					assist6.setArena9x9(true);
					assist7.setArena9x9(true);
					assist8.setArena9x9(true);
					clean(player);
					showChatWindow2(player);
				}
				else
					player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
			}
			else if (command.startsWith("remove"))
			{
				if (!player.isInParty())
				{
					player.sendMessage("Tournament: You dont have a party.");
					return;
				}
				if (!player.getParty().isLeader(player))
				{
					player.sendMessage("Tournament: You are not the party leader!");
					return;
				}

				Arena2x2.getInstance().remove(player);
				Arena9x9.getInstance().remove(player);
				Arena4x4.getInstance().remove(player);
				showChatWindow(player);

			}
			else if (command.startsWith("observe_list"))
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				String filename = "data/html/mods/tournament/9996-1.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
			}
			else if (command.startsWith("observe_back"))
				showChatWindow(player);
			else if (command.startsWith("tournament_observe"))
			{

				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();

				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				int z = Integer.parseInt(st.nextToken());

				player.setArenaObserv(true);
				player.enterObserverMode(x, y, z);
			}
			else
				super.onBypassFeedback(player, command);
		}
	
}

	public void ClasseCheck(Player activeChar)
	{
		Party plparty = activeChar.getParty();
		for (Player player : plparty.getMembers())
			if (player != null)
				if (player.getParty() != null)
				{
					if (player.getClassId() == ClassId.GLADIATOR || player.getClassId() == ClassId.DUELIST || player.getClassId() == ClassId.GRAND_KHAVATARI || player.getClassId() == ClassId.TYRANT)
						activeChar.duelist_cont += 1;
					if (player.getClassId() == ClassId.WARLORD || player.getClassId() == ClassId.DREADNOUGHT)
						activeChar.dreadnought_cont += 1;
					if (player.getClassId() == ClassId.PALADIN || player.getClassId() == ClassId.PHOENIX_KNIGHT || player.getClassId() == ClassId.DARK_AVENGER || player.getClassId() == ClassId.HELL_KNIGHT || player.getClassId() == ClassId.EVAS_TEMPLAR || player.getClassId() == ClassId.TEMPLE_KNIGHT || player.getClassId() == ClassId.SHILLIEN_KNIGHT || player.getClassId() == ClassId.SHILLIEN_TEMPLAR)
						activeChar.tanker_cont += 1;
					if (player.getClassId() == ClassId.ADVENTURER || player.getClassId() == ClassId.TREASURE_HUNTER || player.getClassId() == ClassId.WIND_RIDER || player.getClassId() == ClassId.PLAINS_WALKER || player.getClassId() == ClassId.GHOST_HUNTER || player.getClassId() == ClassId.ABYSS_WALKER)
						activeChar.dagger_cont += 1;
					if (player.getClassId() == ClassId.HAWKEYE || player.getClassId() == ClassId.SAGGITARIUS || player.getClassId() == ClassId.MOONLIGHT_SENTINEL || player.getClassId() == ClassId.SILVER_RANGER || player.getClassId() == ClassId.GHOST_SENTINEL || player.getClassId() == ClassId.PHANTOM_RANGER)
						activeChar.archer_cont += 1;
					if (player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.EVAS_SAINT)
						activeChar.bs_cont += 1;
					if (player.getClassId() == ClassId.ARCHMAGE || player.getClassId() == ClassId.SORCERER)
						activeChar.archmage_cont += 1;
					if (player.getClassId() == ClassId.SOULTAKER || player.getClassId() == ClassId.NECROMANCER)
						activeChar.soultaker_cont += 1;
					if (player.getClassId() == ClassId.MYSTIC_MUSE || player.getClassId() == ClassId.SPELLSINGER)
						activeChar.mysticMuse_cont += 1;
					if (player.getClassId() == ClassId.STORM_SCREAMER || player.getClassId() == ClassId.SPELLHOWLER)
						activeChar.stormScreamer_cont += 1;
					if (player.getClassId() == ClassId.TITAN || player.getClassId() == ClassId.DESTROYER)
						activeChar.titan_cont += 1;
					if (player.getClassId() == ClassId.DOMINATOR || player.getClassId() == ClassId.OVERLORD || player.getClassId() == ClassId.DOOMCRYER || player.getClassId() == ClassId.WARCRYER)
						activeChar.dominator_cont += 1;
				}
	}

	public void clean(Player player)
	{
		player.duelist_cont = 0;
		player.dreadnought_cont = 0;
		player.tanker_cont = 0;
		player.dagger_cont = 0;
		player.archer_cont = 0;
		player.bs_cont = 0;
		player.archmage_cont = 0;
		player.soultaker_cont = 0;
		player.mysticMuse_cont = 0;
		player.stormScreamer_cont = 0;
		player.titan_cont = 0;
		player.dominator_cont = 0;
	}
}