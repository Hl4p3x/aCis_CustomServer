package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.enums.AiEventType;
import net.sf.l2j.gameserver.enums.skills.L2EffectFlag;
import net.sf.l2j.gameserver.enums.skills.L2EffectType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Folk;
import net.sf.l2j.gameserver.model.actor.instance.SiegeFlag;
import net.sf.l2j.gameserver.model.actor.instance.SiegeSummon;

public class EffectFear extends L2Effect
{
	public EffectFear(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FEAR;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Player && getEffector() instanceof Player)
		{
			switch (getSkill().getId())
			{
				case 1376:
				case 1169:
				case 65:
				case 1092:
				case 98:
				case 1272:
				case 1381:
				case 763:
					break;
				
				default:
					return false;
			}
		}
		
		if (getEffected() instanceof Folk || getEffected() instanceof SiegeFlag || getEffected() instanceof SiegeSummon)
			return false;
		
		getEffected().abortAttack();
		getEffected().abortCast();
		getEffected().getMove().stopMove();
		
		getEffected().getAI().notifyEvent(AiEventType.AFRAID, getEffector());
		getEffected().updateAbnormalEffect();
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopEffects(L2EffectType.FEAR);
		getEffected().updateAbnormalEffect();
	}
	
	@Override
	public boolean onActionTime()
	{
		getEffected().getAI().notifyEvent(AiEventType.AFRAID, getEffector());
		return true;
	}
	
	@Override
	public boolean onSameEffect(L2Effect effect)
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.FEAR.getMask();
	}
}