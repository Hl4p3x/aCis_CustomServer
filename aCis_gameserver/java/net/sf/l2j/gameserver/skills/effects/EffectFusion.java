package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.skills.L2EffectType;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;

public class EffectFusion extends L2Effect
{
	public int _effect;
	public int _maxEffect;
	
	public EffectFusion(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
		
		_effect = getSkill().getLevel();
		_maxEffect = SkillTable.getInstance().getMaxLevel(getSkill().getId());
	}
	
	@Override
	public boolean onActionTime()
	{
		return true;
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FUSION;
	}
	
	public void increaseEffect()
	{
		if (_effect < _maxEffect)
		{
			_effect++;
			updateBuff();
		}
	}
	
	public void decreaseForce()
	{
		_effect--;
		if (_effect < 1)
			exit();
		else
			updateBuff();
	}
	
	private void updateBuff()
	{
		exit();
		SkillTable.getInstance().getInfo(getSkill().getId(), _effect).getEffects(getEffector(), getEffected());
	}
}