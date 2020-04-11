package net.sf.l2j.gameserver.taskmanager;

import Dev.VoteGatekkeper.PvPZoneManager;

/**
 * @author Gabia
 */
public class TaskVoteZone implements Runnable
{
	@Override
	public void run()
	{
		PvPZoneManager.getInstance().MessengeTask();
		// Juvenil Walker Dev
	}
}
