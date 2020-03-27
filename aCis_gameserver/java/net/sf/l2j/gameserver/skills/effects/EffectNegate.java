package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.enums.skills.L2EffectType;
import net.sf.l2j.gameserver.enums.skills.L2SkillType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;

public class EffectNegate extends L2Effect
{
	public EffectNegate(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.NEGATE;
	}
	
	@Override
	public boolean onStart()
	{
		for (int negateSkillId : getSkill().getNegateId())
		{
			if (negateSkillId != 0)
				getEffected().stopSkillEffects(negateSkillId);
		}
		
		for (L2SkillType negateSkillType : getSkill().getNegateStats())
			getEffected().stopSkillEffects(negateSkillType, getSkill().getNegateLvl());
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}