package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.enums.skills.L2EffectType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;

public class EffectDebuff extends L2Effect
{
	public EffectDebuff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DEBUFF;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}