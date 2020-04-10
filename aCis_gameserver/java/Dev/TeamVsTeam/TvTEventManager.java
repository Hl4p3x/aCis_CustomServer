package Dev.TeamVsTeam;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.World;

/**
 * @author COMBATE
 *
 */
public class TvTEventManager implements Runnable
{
	/**
	 * The one and only instance of this class<br>
	 */
	private static TvTEventManager _instance = null;
	
	/**
	 * New instance only by getInstance()<br>
	 */
	private TvTEventManager()
	{
		if (Config.TVT_EVENT_ENABLED)
		{
			ThreadPool.schedule(this, 0);
			System.out.println("TeamVsTeam: is Enable");
		}
		else
			System.out.println("TeamVsTeam: is Disable");
	}
	
	/**
	 * Initialize new/Returns the one and only instance<br>
	 * <br>
	 * @return TvTManager<br>
	 */
	public static TvTEventManager getInstance()
	{
		if (_instance == null)
			_instance = new TvTEventManager();
		
		return _instance;
	}
	
	/**
	 * The task method to handle cycles of the event
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		TvTEvent.init();
		
		for (;;)
		{
			waiter(Config.TVT_EVENT_INTERVAL * 60); // in config given as minutes
			
			if (!TvTEvent.startParticipation())
			{
				World.announceToOnlinePlayers("TeamVsTeam: was cancelled.", true);
				System.out.println("TeamVsTeam: Error spawning event npc for participation.");
				continue;
			}
			if (Config.TVT_EVENT_ENABLED)
				World.announceToOnlinePlayers("TvT: Joinable in " + Config.TVT_NPC_LOC_NAME + "!" + Config.TVT_EVENT_PARTICIPATION_TIME + " Minutes.");
			else
				World.announceToOnlinePlayers("TvT: Joinable in " + Config.TVT_NPC_LOC_NAME + "!" + Config.TVT_EVENT_PARTICIPATION_TIME + " Minutes.");
			
			waiter(Config.TVT_EVENT_PARTICIPATION_TIME * 60); // in config given as minutes
			
			if (!TvTEvent.startFight())
			{
				World.announceToOnlinePlayers("TvT: Event cancelled due to lack of Participation.", true);
				System.out.println("TvTEventEngine: Lack of registration, abort event.");
				continue;
			}
			World.announceToOnlinePlayers("TeamVsTeam: Exgost Record!", true);
			World.announceToOnlinePlayers("TeamVsTeam: Teleporting participants in " + Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY + " seconds.", true);
			
			waiter(Config.TVT_EVENT_RUNNING_TIME * 60); // in config given as minutes
			World.announceToOnlinePlayers(TvTEvent.calculateRewards(), true);
			TvTEvent.sysMsgToAllParticipants("TeamVsTeam: Teleporting back to town in " + Config.TVT_NPC_LOC_NAME + " " + Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY + " seconds.");
			TvTEvent.stopFight();
		}
	}
	
	/**
	 * This method waits for a period time delay
	 * @param seconds
	 */
	void waiter(int seconds)
	{
		while (seconds > 1)
		{
			seconds--; // here because we don't want to see two time announce at the same time
			
			if (TvTEvent.isParticipating() || TvTEvent.isStarted())
			{
				switch (seconds)
				{
					case 3600: // 1 hour left
						if (TvTEvent.isParticipating())
							World.announceToOnlinePlayers("TeamVsTeam: " + seconds / 60 / 60 + " Hours Registration will close!", true);
						else if (TvTEvent.isStarted())
							TvTEvent.sysMsgToAllParticipants("TeamVsTeam" + seconds / 60 / 60 + " Hours End!");
						
						break;
					case 1800: // 30 minutes left
					case 900: // 15 minutes left
					case 600: // 10 minutes left
					case 300: // 5 minutes left
					case 240: // 4 minutes left
					case 180: // 3 minutes left
					case 120: // 2 minutes left
					case 60: // 1 minute left
						if (TvTEvent.isParticipating())
							World.announceToOnlinePlayers("TeamVsTeam: " + seconds / 60 + " Minutes Registration will close!", true);
						else if (TvTEvent.isStarted())
							TvTEvent.sysMsgToAllParticipants("TeamVsTeam: " + seconds / 60 + " Minutes End!");
						
						break;
					case 30: // 30 seconds left
						/**
						 * case 15: // 15 seconds left case 10: // 10 seconds left
						 */
					case 5: // 5 seconds left
						
						/**
						 * case 4: // 4 seconds left case 3: // 3 seconds left case 2: // 2 seconds left case 1: // 1 seconds left
						 */
						if (TvTEvent.isParticipating())
							World.announceToOnlinePlayers("TeamVsTeam: " + seconds + " seconds Registration will close", true);
						else if (TvTEvent.isStarted())
							TvTEvent.sysMsgToAllParticipants("TeamVsTeam: " + seconds + " seconds End!");
						
						break;
				}
			}
			TvTEvent.waiter(1);
		}
	}
}
