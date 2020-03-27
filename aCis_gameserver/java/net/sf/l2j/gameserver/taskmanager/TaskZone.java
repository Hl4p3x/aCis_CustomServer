package net.sf.l2j.gameserver.taskmanager;

import Dev.VoteGatekkeper.PvPZoneManager;

public class TaskZone implements Runnable
{
	@Override
	public void run()
	{
		try
		{
			PvPZoneManager.getInstance().ZoneTask();
		}
		catch (Exception e)
		{
			e.getStackTrace();
		}
	}
}