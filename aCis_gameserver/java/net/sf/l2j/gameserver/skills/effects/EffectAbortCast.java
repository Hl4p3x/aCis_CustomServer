package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.enums.skills.L2EffectType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;

public class EffectAbortCast extends L2Effect
{
	public EffectAbortCast(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.ABORT_CAST;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() == null || getEffected() == getEffector() || getEffected().isRaidRelated())
			return false;
		
		getEffected().breakCast();
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}