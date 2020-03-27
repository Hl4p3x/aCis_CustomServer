package net.sf.l2j.gameserver.network.serverpackets;

public class PetDelete extends L2GameServerPacket
{
	private final int _summonType;
	private final int _objId;
	
	public PetDelete(int summonType, int objId)
	{
		_summonType = summonType;
		_objId = objId;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xb6);
		writeD(_summonType);
		writeD(_objId);
	}
}