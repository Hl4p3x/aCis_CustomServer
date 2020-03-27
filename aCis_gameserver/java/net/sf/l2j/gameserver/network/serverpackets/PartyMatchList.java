package net.sf.l2j.gameserver.network.serverpackets;

import java.util.List;

import net.sf.l2j.gameserver.data.manager.PartyMatchRoomManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.PartyMatchRoom;

public class PartyMatchList extends L2GameServerPacket
{
	private final List<PartyMatchRoom> _rooms;
	
	public PartyMatchList(Player player, int auto, int loc, int limit)
	{
		_rooms = PartyMatchRoomManager.getInstance().getAvailableRooms(player, loc, limit);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x96);
		writeD((!_rooms.isEmpty()) ? 1 : 0);
		writeD(_rooms.size());
		
		for (PartyMatchRoom room : _rooms)
		{
			writeD(room.getId());
			writeS(room.getTitle());
			writeD(room.getLocation());
			writeD(room.getMinLvl());
			writeD(room.getMaxLvl());
			writeD(room.getMembersCount());
			writeD(room.getMaxMembers());
			writeS(room.getLeader().getName());
		}
	}
}