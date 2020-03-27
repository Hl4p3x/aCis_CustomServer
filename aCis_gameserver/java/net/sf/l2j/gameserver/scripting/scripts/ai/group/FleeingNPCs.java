package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * A fleeing NPC.<br>
 * <br>
 * His behavior is to always flee, and never attack.
 */
public class FleeingNPCs extends L2AttackableAIScript
{
	public FleeingNPCs()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20432);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		// Wait the NPC to be immobile to move him again.
		if (!npc.isMoving())
		{
			final Location loc = npc.getMove().getDestination();
			loc.setFleeing(npc, attacker, Config.MAX_DRIFT_RANGE);
			
			npc.getAI().setIntention(IntentionType.MOVE_TO, loc);
		}
		return null;
	}
}