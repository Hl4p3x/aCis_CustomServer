package net.sf.l2j.gameserver.handler.usercommandhandlers;


import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;

public class Menu implements IUserCommandHandler
{
    private static final String ACTIVED = "<font color=00FF00>ON</font>";
    private static final String DESATIVED = "<font color=FF0000>OFF</font>";
    
	private static final int[] COMMAND_IDS =
	{
		118
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (id == 118)
		{
			if (Config.MENU)
				sendMainWindow(player);
		}
		
	}
		
		public static void sendMainWindow(Player player)
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/mods/menu.htm");
	        htm.replace("%online%", World.getInstance().getPlayers().size());
	        htm.replace("%partyRefusal%", player.isPartyInvProt() ? ACTIVED : DESATIVED);
	        htm.replace("%tradeRefusal%", player.isInTradeProt() ? ACTIVED : DESATIVED);
	        htm.replace("%buffsRefusal%", player.isBuffProtected() ? ACTIVED : DESATIVED);
	        htm.replace("%messageRefusal%", player.getMessageRefusal() ? ACTIVED : DESATIVED);    
	        htm.replace("%cantGainXP%", player.cantGainXP() ? ACTIVED : DESATIVED);
	        htm.replace("%Eff.Ss%", player.isSSDisabled() ? ACTIVED : DESATIVED);
	        htm.replace("%time%", GameTimeTaskManager.getInstance().getGameTimeFormated());
	        player.sendPacket(htm);
			return;
			
		}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}