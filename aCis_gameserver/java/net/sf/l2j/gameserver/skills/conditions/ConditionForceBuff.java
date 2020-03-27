package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.skills.effects.EffectFusion;

public class ConditionForceBuff extends Condition
{
	private static final short BATTLE_FORCE = 5104;
	private static final short SPELL_FORCE = 5105;
	
	private final byte[] _forces;
	
	public ConditionForceBuff(byte[] forces)
	{
		_forces = forces;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (_forces[0] > 0)
		{
			L2Effect force = effector.getFirstEffect(BATTLE_FORCE);
			if (force == null || ((EffectFusion) force)._effect < _forces[0])
				return false;
		}
		
		if (_forces[1] > 0)
		{
			L2Effect force = effector.getFirstEffect(SPELL_FORCE);
			if (force == null || ((EffectFusion) force)._effect < _forces[1])
				return false;
		}
		return true;
	}
}