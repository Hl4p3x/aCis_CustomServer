package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.enums.PcCafeType;

/**
 * Format: ch ddcdc
 * @author KenM
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private final int _score, _modify, _periodType, _remainingTime;
	private PcCafeType _pointType;
	
	public ExPCCafePointInfo(int score, int modify, PcCafeType type)
	{
		_score = score;
		_modify = modify;
		_remainingTime = 0;
		_pointType = type;
		_periodType = 1; // get point time
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x31);
		writeD(_score);
		writeD(_modify);
		writeC(_periodType);
		writeD(_remainingTime);
		writeC(_pointType.ordinal());
	}
}