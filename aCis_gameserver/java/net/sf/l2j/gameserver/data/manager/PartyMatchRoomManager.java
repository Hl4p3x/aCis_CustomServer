package net.sf.l2j.gameserver.data.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.PartyMatchRoom;

public class PartyMatchRoomManager
{
	private final Map<Integer, PartyMatchRoom> _rooms = new ConcurrentHashMap<>();
	
	private final Set<Player> _waitingMembers = ConcurrentHashMap.newKeySet();
	
	private AtomicInteger _currentId = new AtomicInteger();
	
	protected PartyMatchRoomManager()
	{
	}
	
	public int getNewRoomId()
	{
		return _currentId.incrementAndGet();
	}
	
	public PartyMatchRoom getRoom(int id)
	{
		return _rooms.get(id);
	}
	
	public void addRoom(int id, PartyMatchRoom room)
	{
		_rooms.put(id, room);
	}
	
	public void deleteRoom(int id)
	{
		final PartyMatchRoom room = _rooms.remove(id);
		if (room != null)
			room.disband();
	}
	
	public PartyMatchRoom[] getRooms()
	{
		return _rooms.values().toArray(new PartyMatchRoom[_rooms.size()]);
	}
	
	public int getRoomsCount()
	{
		return _rooms.size();
	}
	
	public List<PartyMatchRoom> getAvailableRooms(Player player, int loc, int limit)
	{
		final List<PartyMatchRoom> rooms = new ArrayList<>();
		
		for (PartyMatchRoom room : getRooms())
		{
			/*
			 * if (loc > 0 && loc != room.getLocation()) // TODO implement correctly bbs behavior. continue;
			 */
			if (limit == 0 && (player.getLevel() < room.getMinLvl() || player.getLevel() > room.getMaxLvl()))
				continue;
			
			rooms.add(room);
		}
		return rooms;
	}
	
	public Set<Player> getWaitingPlayers()
	{
		return _waitingMembers;
	}
	
	public void addWaitingPlayer(Player player)
	{
		_waitingMembers.add(player);
	}
	
	public void removeWaitingPlayer(Player player)
	{
		_waitingMembers.remove(player);
	}
	
	public List<Player> getAvailableWaitingMembers(Player player, int minLvl, int maxLvl)
	{
		final List<Player> members = new ArrayList<>();
		
		for (Player member : _waitingMembers)
		{
			if (member == player)
				continue;
			
			if (member.getLevel() < minLvl || member.getLevel() > maxLvl)
				continue;
			
			members.add(member);
		}
		return members;
	}
	
	public static PartyMatchRoomManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PartyMatchRoomManager INSTANCE = new PartyMatchRoomManager();
	}
}