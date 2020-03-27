package net.sf.l2j.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.manager.RaidBossManager;
import net.sf.l2j.gameserver.data.manager.RaidPointManager;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.type.AttackableAI;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.group.CommandChannel;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class manages all classic raid bosses.<br>
 * <br>
 * Raid Bosses (RB) are mobs which are supposed to be defeated by a party of several players. It extends most of {@link Monster} aspects.<br>
 * <br>
 * They automatically teleport if out of their initial spawn area, and can randomly attack a Player from their Hate List once attacked.<br>
 * <br>
 * Their looting rights are affected by {@link CommandChannel}s. The first who attacks got the priority over loots. Those rights are lost if no attack has been done for 900sec.
 */
public class RaidBoss extends Monster
{
	private ScheduledFuture<?> _maintenanceTask;
	
	public RaidBoss(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setRaid(true);
	}
	
	@Override
	public void onSpawn()
	{
		// No random walk allowed.
		setNoRndWalk(true);
		
		// Basic behavior.
		super.onSpawn();
		
		// "AI task" for regular bosses.
		_maintenanceTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			// Don't bother with dead bosses.
			if (!isDead())
			{
				// The boss isn't in combat, check the teleport possibility.
				if (!isInCombat())
				{
					// Gordon is excluded too.
					if (getNpcId() != 29095 && Rnd.nextBoolean())
					{
						// Spawn must exist.
						final Spawn spawn = getSpawn();
						if (spawn == null)
							return;
						
						// If the boss is above drift range (or 200 minimum), teleport him on his spawn.
						if (!isInsideRadius(spawn.getLoc(), Math.max(Config.MAX_DRIFT_RANGE, 200), true, false))
							teleportTo(spawn.getLoc(), 0);
					}
				}
				// Randomized attack if the boss is already attacking.
				else if (Rnd.get(5) == 0)
					((AttackableAI) getAI()).aggroReconsider();
			}
			
			// For each minion (if any), randomize the attack.
			if (hasMinions())
			{
				for (Monster minion : getMinionList().getSpawnedMinions())
				{
					// Don't bother with dead minions.
					if (minion.isDead() || !minion.isInCombat())
						return;
					
					// Randomized attack if the boss is already attacking.
					if (Rnd.get(3) == 0)
						((AttackableAI) minion.getAI()).aggroReconsider();
				}
			}
		}, 1000, 60000);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		if (killer != null)
		{
			final Player player = killer.getActingPlayer();
			if (player != null)
			{
				broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
				broadcastPacket(new PlaySound("systemmsg_e.1209"));
				
				final Party party = player.getParty();
				if (party != null)
				{
					for (Player member : party.getMembers())
					{
						if (Config.RAIDBOSS_NOBLES > 0 && getNpcId() == Config.RAIDBOSS_NOBLES && !member.isNoble() && member.isInsideRadius(getX(), getY(), getZ(), 1000, false, false))
						{
							member.setNoble(true, true);
							member.getInventory().addItem("Nobles Circlets", 7694, 1, member, null);
							member.sendPacket(new ItemList(member, true));
						}
						
						RaidPointManager.getInstance().addPoints(member, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
						if (member.isNoble())
							HeroManager.getInstance().setRBkilled(member.getObjectId(), getNpcId());
					}
				}
				else
				{
					if (Config.RAIDBOSS_NOBLES > 0 && getNpcId() == Config.RAIDBOSS_NOBLES && !player.isNoble())
					{
						player.setNoble(true, true);
						player.getInventory().addItem("Nobles Circlets", 7694, 1, player, null);
					}
					
					RaidPointManager.getInstance().addPoints(player, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
					if (player.isNoble())
						HeroManager.getInstance().setRBkilled(player.getObjectId(), getNpcId());
				}

				if (Config.ENABLE_BOSS_DEFEATED_MSG)
					World.announceToOnlinePlayers(player.getClan() != null ? Config.RAID_BOSS_DEFEATED_BY_CLAN_MEMBER_MSG.replace("%raidboss%", getName()).replace("%player%", killer.getName()).replace("%clan%", player.getClan().getName()) : Config.RAID_BOSS_DEFEATED_BY_PLAYER_MSG.replace("%raidboss%", getName()).replace("%player%", killer.getName()), true);
			}
		}
		
		RaidBossManager.getInstance().onDeath(this);
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		super.deleteMe();
	}
}