package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.pledge.Clan;

public class PledgeInfo extends L2GameServerPacket
{
	private final Clan _clan;
	
	public PledgeInfo(Clan clan)
	{
		_clan = clan;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x83);
		writeD(_clan.getClanId());
		writeS(_clan.getName());
		writeS(_clan.getAllyName());
	}
}