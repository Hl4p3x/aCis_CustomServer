package net.sf.l2j.gameserver.model.actor.container.creature;

import java.util.concurrent.Future;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.effects.EffectFusion;

public final class FusionSkill
{
	protected Creature _caster;
	protected Creature _target;
	
	protected Future<?> _geoCheckTask;
	
	protected int _skillCastRange;
	protected int _fusionId;
	protected int _fusionLevel;
	
	public FusionSkill(Creature caster, Creature target, L2Skill skill)
	{
		_skillCastRange = skill.getCastRange();
		_caster = caster;
		_target = target;
		_fusionId = skill.getTriggeredId();
		_fusionLevel = skill.getTriggeredLevel();
		
		final L2Effect effect = _target.getFirstEffect(_fusionId);
		if (effect != null)
			((EffectFusion) effect).increaseEffect();
		else
		{
			final L2Skill force = SkillTable.getInstance().getInfo(_fusionId, _fusionLevel);
			if (force != null)
				force.getEffects(_caster, _target);
		}
		
		_geoCheckTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (!MathUtil.checkIfInRange(_skillCastRange, _caster, _target, true))
				_caster.abortCast();
			
			if (!GeoEngine.getInstance().canSeeTarget(_caster, _target))
				_caster.abortCast();
		}, 1000, 1000);
	}
	
	public Creature getCaster()
	{
		return _caster;
	}
	
	public Creature getTarget()
	{
		return _target;
	}
	
	public void onCastAbort()
	{
		_caster.setFusionSkill(null);
		
		final L2Effect effect = _target.getFirstEffect(_fusionId);
		if (effect != null)
			((EffectFusion) effect).decreaseForce();
		
		_geoCheckTask.cancel(true);
	}
}