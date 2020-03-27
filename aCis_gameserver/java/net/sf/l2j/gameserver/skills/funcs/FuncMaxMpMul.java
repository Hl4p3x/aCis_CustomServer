package net.sf.l2j.gameserver.skills.funcs;

import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMaxMpMul extends Func
{
	private static final FuncMaxMpMul INSTANCE = new FuncMaxMpMul();
	
	private FuncMaxMpMul()
	{
		super(null, Stats.MAX_MP, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.MEN_BONUS[effector.getMEN()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}