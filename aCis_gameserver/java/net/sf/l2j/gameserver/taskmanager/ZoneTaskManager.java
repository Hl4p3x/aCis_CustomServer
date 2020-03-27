package net.sf.l2j.gameserver.taskmanager;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.SpawnType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.MultiZone;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * @author Williams
 *
 */
public final class ZoneTaskManager implements Runnable
{
	private int currentZoneId;
	private int timer;
	
	public ZoneTaskManager()
	{
		if (getZonesCount() > 1)
			ThreadPool.scheduleAtFixedRate(this, 10000, 1000);
	}
	
	@Override
	public void run()
	{
		switch (timer)
		{
			case 0:
				selectZone();
				break;
				
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 10:
			case 20:
			case 30:
				World.announceToOnlinePlayers("Random zone will change in " + timer + " second(s).", true);
				break;
				
			case 60:
			case 120:
			case 180:
			case 240:
			case 300:
			case 600:
			case 1200:
			case 1800:
				World.announceToOnlinePlayers("Random zone will change in " + (timer / 60) + " minute(s).", true);
				break;
				
			case 3600:
			case 7200:
				World.announceToOnlinePlayers("Random zone will change in " + (timer / 60 / 60) + " hour(s).", true);
				break;
		}
		timer--;
	}
	
	private void selectZone()
	{
		currentZoneId = currentZoneId == getZonesCount() ? 1 : ++currentZoneId;
		
		final MultiZone zone = ZoneManager.getInstance().getZoneById(currentZoneId, MultiZone.class);
		if (zone == null)
		{
			AdminData.getInstance().broadcastToGMs(new CreatureSay(0, SayType.PARTYROOM_COMMANDER, "RandomZone", "No zone with id: " + currentZoneId));
			return;
		}
		
		timer = zone.getTime() * 60;
		
		World.announceToOnlinePlayers("New Random Zone: " + zone.getZoneName() + ". Duration: " + zone.getTime() + " min(s).", true);
		World.getInstance().getPlayers().stream().filter(p -> p.isInsideZone(ZoneId.MULTI) && zone.isActive()).forEach(p -> teleport(p));
	}
	
	private final void teleport(Player player)
	{
		if (player.isDead())
			player.doRevive();
		
		player.teleportTo(getCurrentZone().getRndSpawn(SpawnType.NORMAL), 0);
	}
	
	public void onEnter(Player player)
	{
		if (currentZoneId > 0)
			player.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "RandomZone", "Current zone: " + getCurrentZone().getZoneName()));
	}
	
	public final int getCurrentZoneId()
	{
		return currentZoneId;
	}
	
	private static final int getZonesCount()
	{
		return ZoneManager.getInstance().getAllZones(MultiZone.class).size();
	}
	
	public final MultiZone getCurrentZone()
	{
		return ZoneManager.getInstance().getAllZones(MultiZone.class).stream().filter(t -> t.getId() == currentZoneId).findFirst().orElse(null);
	}
	
	public static final ZoneTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneTaskManager INSTANCE = new ZoneTaskManager();
	}
}