package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.L2Skill;

public class PledgeSkillListAdd extends L2GameServerPacket
{
	private final int _id;
	private final int _lvl;
	
	public PledgeSkillListAdd(L2Skill skill)
	{
		_id = skill.getId();
		_lvl = skill.getLevel();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x3a);
		writeD(_id);
		writeD(_lvl);
	}
}