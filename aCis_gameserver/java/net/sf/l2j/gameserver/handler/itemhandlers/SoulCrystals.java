package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class SoulCrystals implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
			return;
		
		final L2Skill skill = skills[0].getSkill();
		if (skill == null || skill.getId() != 2096)
			return;
		
		final Player player = (Player) playable;
		
		if (player.isCastingNow())
			return;
		
		if (!skill.checkCondition(player, player.getTarget(), false))
			return;
		
		// No message on retail, the use is just forgotten.
		if (player.isSkillDisabled(skill))
			return;
		
		player.getAI().setIntention(IntentionType.IDLE);
		if (!player.useMagic(skill, forceUse, false))
			return;
		
		int reuseDelay = skill.getReuseDelay();
		if (item.getEtcItem().getReuseDelay() > reuseDelay)
			reuseDelay = item.getEtcItem().getReuseDelay();
		
		player.addTimeStamp(skill, reuseDelay);
		if (reuseDelay != 0)
			player.disableSkill(skill, reuseDelay);
	}
}