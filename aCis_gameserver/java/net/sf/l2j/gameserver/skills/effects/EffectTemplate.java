package net.sf.l2j.gameserver.skills.effects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.enums.skills.AbnormalEffect;
import net.sf.l2j.gameserver.enums.skills.L2SkillType;
import net.sf.l2j.gameserver.model.ChanceCondition;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.basefuncs.FuncTemplate;
import net.sf.l2j.gameserver.skills.conditions.Condition;

public final class EffectTemplate
{
	static Logger _log = Logger.getLogger(EffectTemplate.class.getName());
	
	private final Class<?> _func;
	private final Constructor<?> _constructor;
	
	public final Condition attachCond;
	public final Condition applayCond;
	public final double value;
	public final int counter;
	public final int period; // in seconds
	public final AbnormalEffect abnormalEffect;
	public List<FuncTemplate> funcTemplates;
	public final String stackType;
	public final float stackOrder;
	public final boolean icon;
	public final double effectPower; // to handle chance
	public final L2SkillType effectType; // to handle resistances etc...
	
	public final int triggeredId;
	public final int triggeredLevel;
	public final ChanceCondition chanceCondition;
	
	public EffectTemplate(Condition pAttachCond, Condition pApplayCond, String func, double pValue, int pCounter, int pPeriod, AbnormalEffect pAbnormalEffect, String pStackType, float pStackOrder, boolean showicon, double ePower, L2SkillType eType, int trigId, int trigLvl, ChanceCondition chanceCond)
	{
		attachCond = pAttachCond;
		applayCond = pApplayCond;
		value = pValue;
		counter = pCounter;
		period = pPeriod;
		abnormalEffect = pAbnormalEffect;
		stackType = pStackType;
		stackOrder = pStackOrder;
		icon = showicon;
		effectPower = ePower;
		effectType = eType;
		
		triggeredId = trigId;
		triggeredLevel = trigLvl;
		chanceCondition = chanceCond;
		
		try
		{
			_func = Class.forName("net.sf.l2j.gameserver.skills.effects.Effect" + func);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		
		try
		{
			_constructor = _func.getConstructor(EffectTemplate.class, L2Skill.class, Creature.class, Creature.class);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public L2Effect getEffect(Creature caster, Creature target, L2Skill skill)
	{
		if (attachCond != null && !attachCond.test(caster, target, skill))
			return null;
		
		try
		{
			return (L2Effect) _constructor.newInstance(this, skill, target, caster);
		}
		catch (IllegalAccessException e)
		{
			_log.log(Level.WARNING, "", e);
			return null;
		}
		catch (InstantiationException e)
		{
			_log.log(Level.WARNING, "", e);
			return null;
		}
		catch (InvocationTargetException e)
		{
			_log.log(Level.WARNING, "Error creating new instance of Class " + _func + " Exception was: " + e.getTargetException().getMessage(), e.getTargetException());
			return null;
		}
	}
	
	public void attach(FuncTemplate f)
	{
		if (funcTemplates == null)
			funcTemplates = new ArrayList<>();
		
		funcTemplates.add(f);
	}
}