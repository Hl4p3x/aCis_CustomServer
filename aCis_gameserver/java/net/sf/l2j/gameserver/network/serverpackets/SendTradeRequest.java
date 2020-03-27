package net.sf.l2j.gameserver.network.serverpackets;

public class SendTradeRequest extends L2GameServerPacket
{
	private final int _senderID;
	
	public SendTradeRequest(int senderID)
	{
		_senderID = senderID;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x5e);
		writeD(_senderID);
	}
}