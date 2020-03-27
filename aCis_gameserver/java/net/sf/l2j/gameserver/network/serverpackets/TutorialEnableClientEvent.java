package net.sf.l2j.gameserver.network.serverpackets;

public class TutorialEnableClientEvent extends L2GameServerPacket
{
	private final int _eventId;
	
	public TutorialEnableClientEvent(int event)
	{
		_eventId = event;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xa2);
		writeD(_eventId);
	}
}