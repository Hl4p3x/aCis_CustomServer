/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.taskmanager;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;

/**
 * @author Anarchy
 */
public class AutoPotionTask implements Runnable
{
	private int itemId;
	private Player player;
	
	public AutoPotionTask(int itemId, Player player)
	{
		this.itemId = itemId;
		this.player = player;
	}
	
	@Override
	public void run()
	{
		if (!player.isOnline())
		{
			player.stopAutoPotion(itemId);
			return;
		}
		if (player.isInOlympiadMode())
		{
			player.sendMessage("You cannot that in olympiad mode.");
			player.stopAutoPotion(itemId);
			return;
		}
		
		if (Config.AUTO_POTIONS_LIMITS.containsKey(itemId))
		{
			String type = Config.AUTO_POTIONS_LIMITS.get(itemId)[0];
			int val = Integer.parseInt(Config.AUTO_POTIONS_LIMITS.get(itemId)[1]);
			
			switch (type)
			{
				case "CP":
				{
					if ((player.getCurrentCp() / player.getMaxCp()) * 100 > val)
						return;
					break;
				}
				case "HP":
				{
					if ((player.getCurrentHp() / player.getMaxHp()) * 100 > val)
						return;
					break;
				}
				case "MP":
				{
					if ((player.getCurrentMp() / player.getMaxMp()) * 100 > val)
						return;
					break;
				}
			}
		}
		
		if (!player.destroyItemByItemId("auto potion use", itemId, 1, null, true))
		{
			player.stopAutoPotion(itemId);
			player.sendMessage("Incorrect item count.");
			return;
		}
		
		if (player.getInventory().getItemByItemId(itemId) == null)
		{
			player.stopAutoPotion(itemId);
			return;
		}
		IntIntHolder[] skills = player.getInventory().getItemByItemId(itemId).getEtcItem().getSkills();
		if (skills == null)
			return;
		
		for (IntIntHolder skill : skills)
		{
			if (skill == null)
				continue;
			
			L2Skill sk = skill.getSkill();
			if (sk == null)
				continue;
			
			player.doSimultaneousCast(sk);
		}
	}
	
	public int getItemId()
	{
		return itemId;
	}
}
