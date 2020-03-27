package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.skills.L2EffectFlag;
import net.sf.l2j.gameserver.enums.skills.L2EffectType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;

final class EffectBetray extends L2Effect
{
	public EffectBetray(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BETRAY;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffector() instanceof Player && getEffected() instanceof Summon)
		{
			Player targetOwner = getEffected().getActingPlayer();
			getEffected().getAI().setIntention(IntentionType.ATTACK, targetOwner);
			return true;
		}
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().getAI().setIntention(IntentionType.IDLE);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.BETRAYED.getMask();
	}
}