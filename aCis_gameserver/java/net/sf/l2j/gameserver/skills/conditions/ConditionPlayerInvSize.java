package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;

public class ConditionPlayerInvSize extends Condition
{
	private final int _size;
	
	public ConditionPlayerInvSize(int size)
	{
		_size = size;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (effector instanceof Player)
		{
			final Player player = (Player) effector;
			return player.getInventory().getSize() <= (player.getInventoryLimit() - _size);
		}
		return true;
	}
}