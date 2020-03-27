package net.sf.l2j.gameserver.network.serverpackets;

import java.util.Collection;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.pledge.Clan;

public class PledgeSkillList extends L2GameServerPacket
{
	private final Collection<L2Skill> _skills;
	
	public PledgeSkillList(Clan clan)
	{
		_skills = clan.getClanSkills().values();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x39);
		
		writeD(_skills.size());
		
		for (L2Skill skill : _skills)
		{
			writeD(skill.getId());
			writeD(skill.getLevel());
		}
	}
}