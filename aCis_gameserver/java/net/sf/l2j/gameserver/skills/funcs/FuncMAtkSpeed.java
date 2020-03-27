package net.sf.l2j.gameserver.skills.funcs;

import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMAtkSpeed extends Func
{
	private static final FuncMAtkSpeed INSTANCE = new FuncMAtkSpeed();
	
	private FuncMAtkSpeed()
	{
		super(null, Stats.MAGIC_ATTACK_SPEED, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.WIT_BONUS[effector.getWIT()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}