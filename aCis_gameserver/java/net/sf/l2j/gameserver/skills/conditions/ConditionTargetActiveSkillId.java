package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.item.kind.Item;

public class ConditionTargetActiveSkillId extends Condition
{
	private final int _skillId;
	
	public ConditionTargetActiveSkillId(int skillId)
	{
		_skillId = skillId;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effected.getSkill(_skillId) != null;
	}
}