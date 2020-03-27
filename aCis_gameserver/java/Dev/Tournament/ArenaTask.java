package Dev.Tournament;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;

import Dev.Tournament.Arena2x2;
import Dev.Tournament.Arena4x4;
import Dev.Tournament.Arena9x9;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

import net.sf.l2j.gameserver.handler.admincommandhandlers.AdminTournament;

import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.SayType;

public abstract class ArenaTask
{
	public static Spawn _npcSpawn1;
	public static Spawn _npcSpawn2;
	public static int _bossHeading = 0;
	public static boolean _started = false;
	public static boolean _aborted = false;

	public static void SpawnEvent()
	{
		spawnNpc1();

		
		World.announceToOnlinePlayers("Reward: " + ItemData.getInstance().getTemplate(Config.ARENA_REWARD_ID).getName());
		
		World.announceToOnlinePlayers("[Tournament]: Party Event PvP");
		World.announceToOnlinePlayers("[Tournament]: Teleport in the GK to (Tournament) Zone");
		World.announceToOnlinePlayers("[Tournament]: Duration: " + Config.TOURNAMENT_TIME + " minute(s)!");
		

		_aborted = false;
		_started = true;

		waiter(Config.TOURNAMENT_TIME * 60 * 1000);
		if (!_aborted)
			finishEvent();
	}

	public static void finishEvent()
	{

		
		World.announceToOnlinePlayers("[Tournament]: Event Finished!");

		unspawnNpc1();
		_started = false;
		if (!AdminTournament._arena_manual)
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		else
			AdminTournament._arena_manual = false;
		for (Player player : World.getInstance().getPlayers())
			if (player != null && player.isOnline())
			{
				if (player.isArenaProtection())
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							if (player.isOnline() && !player.isInArenaEvent() && !player.isArenaAttack())
							{
								Arena4x4.getInstance().remove(player);
								Arena9x9.getInstance().remove(player);
								Arena2x2.getInstance().remove(player);
								player.setArenaProtection(false);
								
								
							}
						}
					}, 25000L);
				
				new CreatureSay(SayType.CRITICAL_ANNOUNCE, null, "[Tournament] " + "Next Tournament: "  + ArenaEvent.getInstance().getNextTime() + " (GMT-3).");
	
			}
	}

	public static void spawnNpc1()
	{

		NpcTemplate template = NpcData.getInstance().getTemplate(Config.ARENA_NPC);
		try
		{
			_npcSpawn1 = new Spawn(template);
			_npcSpawn1.setLoc(loc1x(), loc1y(), loc1z(), Config.NPC_Heading);
			_npcSpawn1.setRespawnDelay(1);

			SpawnTable.getInstance().addSpawn(_npcSpawn1, false);

			_npcSpawn1.setRespawnState(true);
			_npcSpawn1.doSpawn(false);
			_npcSpawn1.getNpc().getStatus().setCurrentHp(9.99999999E8);
			_npcSpawn1.getNpc().isAggressive();
			_npcSpawn1.getNpc().decayMe();
			_npcSpawn1.getNpc().spawnMe(_npcSpawn1.getNpc().getX(), _npcSpawn1.getNpc().getY(), _npcSpawn1.getNpc().getZ());
			_npcSpawn1.getNpc().broadcastPacket(new MagicSkillUse(_npcSpawn1.getNpc(), _npcSpawn1.getNpc(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public static void spawnNpc2() {
        final NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.ARENA_NPC);
        try {
            (ArenaTask._npcSpawn2 = new Spawn(tmpl)).setLoc(loc2x(), loc2y(), loc2z(), Config.NPC_Heading);
            ArenaTask._npcSpawn2.setRespawnDelay(1);
            SpawnTable.getInstance().addSpawn(ArenaTask._npcSpawn2, false);
            ArenaTask._npcSpawn2.setRespawnState(true);
            ArenaTask._npcSpawn2.doSpawn(false);
            ArenaTask._npcSpawn2.getNpc().getStatus().setCurrentHp(9.99999999E8);
            ArenaTask._npcSpawn2.getNpc().isAggressive();
            ArenaTask._npcSpawn2.getNpc().decayMe();
            ArenaTask._npcSpawn2.getNpc().spawnMe(ArenaTask._npcSpawn2.getNpc().getX(), ArenaTask._npcSpawn2.getNpc().getY(), ArenaTask._npcSpawn2.getNpc().getZ());
            ArenaTask._npcSpawn2.getNpc().broadcastPacket(new MagicSkillUse(ArenaTask._npcSpawn2.getNpc(), ArenaTask._npcSpawn2.getNpc(), 1034, 1, 1, 1));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static boolean is_started()
	{
		return _started;
	}

	public static void unspawnNpc1()
	{
		if (_npcSpawn1 == null)
			return;
		_npcSpawn1.getNpc().deleteMe();
		_npcSpawn1.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_npcSpawn1, true);
	}

	public static void unspawnNpc2()
	{
		if (_npcSpawn2 == null)
			return;
		_npcSpawn2.getNpc().deleteMe();
		_npcSpawn2.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_npcSpawn2, true);
	}

	public static int loc1x()
	{
		int loc1x = Config.NPC_locx;
		return loc1x;
	}

	public static int loc1y()
	{
		int loc1y = Config.NPC_locy;
		return loc1y;
	}

	public static int loc1z()
	{
		int loc1z = Config.NPC_locz;
		return loc1z;
	}
	
	   public static int loc2x() {
	        final int loc2x = Config.NPC_locx;
	        return loc2x;
	    }
	    
	    public static int loc2y() {
	        final int loc2y = Config.NPC_locy;
	        return loc2y;
	    }
	    
	    public static int loc2z() {
	        final int loc2z = Config.NPC_locz;
	        return loc2z;
	    }

	protected static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000L);
		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted)
		{
			seconds--;
			switch (seconds)
			{
				case 3600:
					if (_started)
					{

						World.announceToOnlinePlayers("[Tournament]: Party Event PvP");
						World.announceToOnlinePlayers("[Tournament]: Teleport in the GK to (Tournament) Zone");
						World.announceToOnlinePlayers("[Tournament]: Reward: " + ItemData.getInstance().getTemplate(Config.ARENA_REWARD_ID).getName());
						World.announceToOnlinePlayers("[Tournament]: " + seconds / 60 / 60 + " hour(s) till event finish!");
					}
					break;
				case 60:
				case 120:
				case 180:
				case 240:
				case 300:
				case 600:
				case 900:
				case 1800:
					if (_started)
						World.announceToOnlinePlayers("[Tournament]: " + seconds / 60 + " minute(s) till event finish!");
					break;
				case 1:
				case 2:
				case 3:
				case 10:
				case 15:
				case 30:
					if (_started)
						World.announceToOnlinePlayers("[Tournament]: " + seconds + " second(s) till event finish!");
					break;
			}
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			while (startOneSecondWaiterStartTime + 1000L > System.currentTimeMillis())
				try
			{
					Thread.sleep(1L);
			}
			catch (InterruptedException ex) {}
		}
	}
    static {
        ArenaTask._bossHeading = 0;
        ArenaTask._started = false;
        ArenaTask._aborted = false;
    }
}
