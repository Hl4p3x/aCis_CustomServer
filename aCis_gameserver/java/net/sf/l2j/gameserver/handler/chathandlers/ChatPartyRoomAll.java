package net.sf.l2j.gameserver.handler.chathandlers;

import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.CommandChannel;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyRoomAll implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.PARTYROOM_ALL
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		final Party party = player.getParty();
		if (party == null || !party.isLeader(player))
			return;
		
		final CommandChannel channel = party.getCommandChannel();
		if (channel == null)
			return;
		
		channel.broadcastCreatureSay(new CreatureSay(player, type, text), player);
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}