package net.sf.l2j.gameserver.network.serverpackets;

public class OpenUrl extends L2GameServerPacket
{
	private final String _url;
	
	public OpenUrl(String url)
	{
		_url = url;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xFF);
		writeC(0x03);
		writeS(_url);
	}
}