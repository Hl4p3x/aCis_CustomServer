package net.sf.l2j.gameserver.handler.usercommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import Dev.VoteEngine.Individual;



public class Vote implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		130
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (id == 130)
		{
			if (Config.MENU)
				sendMainWindow(player);
			
		}
	}
		
		public static void sendMainWindow(Player player)
		{

			
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/mods/vote.htm");
			htm.replace("%name%", player.getName());
			String status = Individual.cdInTopList(player, "lastVoteReward") ? Individual.Cd(player, "lastVoteReward", false) : "You can vote";
			htm.replace("%status%", status);
			
			player.sendPacket(htm);
			return;
			
		}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}