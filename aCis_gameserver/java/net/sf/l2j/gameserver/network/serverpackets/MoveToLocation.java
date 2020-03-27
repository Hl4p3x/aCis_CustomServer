package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.location.Location;

public final class MoveToLocation extends L2GameServerPacket
{
	private final int _objectId;
	private final Location _currentPosition = new Location(0, 0, 0);
	private final Location _destination = new Location(0, 0, 0);
	
	public MoveToLocation(Creature cha)
	{
		_objectId = cha.getObjectId();
		_currentPosition.set(cha.getPosition());
		_destination.set(cha.getMove().getDestination());
	}
	
	public MoveToLocation(Creature cha, Location destination)
	{
		_objectId = cha.getObjectId();
		_currentPosition.set(cha.getPosition());
		_destination.set(destination);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x01);
		writeD(_objectId);
		writeLoc(_destination);
		writeLoc(_currentPosition);
	}
}