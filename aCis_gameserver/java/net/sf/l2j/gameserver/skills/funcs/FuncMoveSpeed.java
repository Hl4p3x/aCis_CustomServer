package net.sf.l2j.gameserver.skills.funcs;

import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMoveSpeed extends Func
{
	private static final FuncMoveSpeed INSTANCE = new FuncMoveSpeed();
	
	private FuncMoveSpeed()
	{
		super(null, Stats.RUN_SPEED, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.DEX_BONUS[effector.getDEX()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}