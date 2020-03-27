package net.sf.l2j.gameserver.skills.funcs;

import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMaxHpMul extends Func
{
	private static final FuncMaxHpMul INSTANCE = new FuncMaxHpMul();
	
	private FuncMaxHpMul()
	{
		super(null, Stats.MAX_HP, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.CON_BONUS[effector.getCON()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}