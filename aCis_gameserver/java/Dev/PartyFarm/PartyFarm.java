package Dev.PartyFarm;
import java.util.ArrayList;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.handler.admincommandhandlers.AdminPartyFarm;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

public class PartyFarm
{
	public static Spawn _monster;
	public static int _bossHeading = 0;
	public static String _eventName = "";
	public static boolean _started = false;
	public static boolean _aborted = false;
	protected static boolean _finish = false;
	static PartyFarm _instance;

	public static void bossSpawnMonster()
	{
		spawnMonsters();

		World.announceToOnlinePlayers("Teleport Now! " + Config.PARTY_FARMANNONCER);
		World.announceToOnlinePlayers("[Party Farm]: Duration: " + Config.EVENT_BEST_FARM_TIME + " minute(s)!");
		_aborted = false;
		_started = true;

		waiter(Config.EVENT_BEST_FARM_TIME * 60 * 1000);
		if (!_aborted)
			Finish_Event();
	}

	public static void Finish_Event()
	{
		unSpawnMonsters();

		_started = false;
		_finish = true;

		World.announceToOnlinePlayers("[Party Farm]: Finished!");
		if (!AdminPartyFarm._bestfarm_manual)
			InitialPartyFarm.getInstance().StartCalculationOfNextEventTime();
		else
			AdminPartyFarm._bestfarm_manual = false;
	}

	public static void spawnMonsters()
	{
		for (int i = 0; i < Config.MONSTER_LOCS_COUNT; i++)
		{
			int[] coord = Config.MONSTER_LOCS[i];
			monsters.add(spawnNPC(coord[0], coord[1], coord[2], Config.monsterId));
		}
	}

	public static boolean is_started()
	{
		return _started;
	}

	public static boolean is_finish()
	{
		return _finish;
	}

	protected static Spawn spawnNPC(int xPos, int yPos, int zPos, int npcId)
	{
		NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		try
		{
			Spawn spawn = new Spawn(template);
			spawn.setLoc(xPos, yPos, zPos, 0);
			spawn.setRespawnDelay(Config.PARTY_FARM_MONSTER_DALAY);

			SpawnTable.getInstance().addSpawn(spawn, false);

			spawn.setRespawnState(true);
			spawn.doSpawn(false);
			spawn.getNpc().isAggressive();
			spawn.getNpc().decayMe();
			spawn.getNpc().spawnMe(spawn.getNpc().getX(), spawn.getNpc().getY(), spawn.getNpc().getZ());
			spawn.getNpc().broadcastPacket(new MagicSkillUse(spawn.getNpc(), spawn.getNpc(), 1034, 1, 1, 1));
			return spawn;
		}
		catch (Exception e)
		{
		}
		return null;
	}

	protected static ArrayList<Spawn> monsters = new ArrayList<>();

	protected static void unSpawnMonsters()
	{
		for (Spawn s : monsters)
		{
			if (s == null)
			{
				monsters.remove(s);
				return;
			}
			
			s.getNpc().deleteMe();
			s.setRespawnState(false);
			SpawnTable.getInstance().deleteSpawn(s, true);

		}
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
						World.announceToOnlinePlayers("[Party Farm]: " + seconds / 60 / 60 + " hour(s) till event finish!");
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
						World.announceToOnlinePlayers("[Party Farm]: " + seconds / 60 + " minute(s) till event finish!");
					break;
				case 1:
				case 2:
				case 3:
				case 10:
				case 15:
				case 30:
					if (_started)
						World.announceToOnlinePlayers("[Party Farm]: " + seconds + " second(s) till event finish!");
					break;
			}
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			while (startOneSecondWaiterStartTime + 1000L > System.currentTimeMillis())
				try
			{
					Thread.sleep(1L);
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
	}

}
