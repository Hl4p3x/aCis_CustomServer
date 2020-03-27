package net.sf.l2j.gameserver.handler.usercommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class Dressme implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (id == 118)
		{
		
			
			if (Config.ALLOW_DRESS_ME_SYSTEM)
				sendMainWindow(player);
		}
	}
	
	public static void sendMainWindow(Player player)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("./data/html/custom/dressme/main.htm");
		htm.replace("%enabled%", player.isDressMeEnabled() ? "enabled" : "disabled");
		if (player.getDressMeData() == null)
		{
			htm.replace("%chestinfo%", "You have no custom chest.");
			htm.replace("%legsinfo%", "You have no custom legs.");
			htm.replace("%bootsinfo%", "You have no custom boots.");
			htm.replace("%glovesinfo%", "You have no custom gloves.");
			htm.replace("%weapinfo%", "You have no custom weapon.");
		}
		else
		{
			htm.replace("%chestinfo%", player.getDressMeData().getChestId() == 0 ? "You have no custom chest." : ItemData.getInstance().getTemplate(player.getDressMeData().getChestId()).getName());
			htm.replace("%legsinfo%", player.getDressMeData().getLegsId() == 0 ? "You have no custom legs." : ItemData.getInstance().getTemplate(player.getDressMeData().getLegsId()).getName());
			htm.replace("%bootsinfo%", player.getDressMeData().getBootsId() == 0 ? "You have no custom boots." : ItemData.getInstance().getTemplate(player.getDressMeData().getBootsId()).getName());
			htm.replace("%glovesinfo%", player.getDressMeData().getGlovesId() == 0 ? "You have no custom gloves." : ItemData.getInstance().getTemplate(player.getDressMeData().getGlovesId()).getName());
			htm.replace("%weapinfo%", player.getDressMeData().getWeapId() == 0 ? "You have no custom weapon." : ItemData.getInstance().getTemplate(player.getDressMeData().getWeapId()).getName());
		}
		
		player.sendPacket(htm);
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}