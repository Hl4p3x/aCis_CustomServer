package net.sf.l2j.gameserver.network.serverpackets;

/**
 * @author Williams
 *
 */
public class PledgeImage extends L2GameServerPacket
{
	private final int _crestId;
	private final byte[] _data;
	
	public PledgeImage(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x6c);
		writeD(_crestId);
		
		if (_data != null)
		{
			writeD(_data.length);
			writeB(_data);
		}
		else
			writeD(0);
	}
}