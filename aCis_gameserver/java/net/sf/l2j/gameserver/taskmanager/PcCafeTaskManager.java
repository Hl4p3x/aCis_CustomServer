package net.sf.l2j.gameserver.taskmanager;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.World;

/**
 * @author Williams
 *
 */
public class PcCafeTaskManager implements Runnable
{
	public PcCafeTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, Config.PCB_INTERVAL * 1000 * 60, Config.PCB_INTERVAL * 1000 * 60);
	}
	
	@Override
	public void run()
	{
		World.getInstance().getPlayers().stream().filter(player -> player.getLevel() >= Config.PCB_MIN_LEVEL && !player.getClient().isDetached()).forEach(player ->
		{	
			if (!player.isAFK())
				player.increasePcCafePoints(Rnd.get(Config.PCB_POINT_MIN, Config.PCB_POINT_MAX), Rnd.get(100) <= Config.PCB_CHANCE_DUAL_POINT);
		});
	}
	
	public static final PcCafeTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafeTaskManager INSTANCE = new PcCafeTaskManager();
	}	
}