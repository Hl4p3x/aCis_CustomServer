package net.sf.l2j.gameserver.handler.chathandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.actors.RestrictionType;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.BlockList;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

public class ChatShout implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.SHOUT
	};
	
	@Override
	public void handleChat(SayType type, Player activeChar, String target, String text)
	{
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.GLOBAL_CHAT))
			return;

		if (Config.TRADE_RESTRICTION_TYPE == RestrictionType.PVP && activeChar.getPvpKills() < Config.TRADE_RESTRICTION_VALUE)
		{
			activeChar.sendMessage("Você só pode usar o chat com " + Config.TRADE_RESTRICTION_VALUE + " (PVP).");
			return;
		}
		
		if (Config.TRADE_RESTRICTION_TYPE == RestrictionType.LEVEL && activeChar.getLevel() < Config.TRADE_RESTRICTION_VALUE)
		{
			activeChar.sendMessage("Você só pode usar o chat no level " + Config.TRADE_RESTRICTION_VALUE + ".");
			return;
		}
			
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		final int region = MapRegionData.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (!BlockList.isBlocked(player, activeChar) && region == MapRegionData.getInstance().getMapRegion(player.getX(), player.getY()))
				player.sendPacket(cs);
		}
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}