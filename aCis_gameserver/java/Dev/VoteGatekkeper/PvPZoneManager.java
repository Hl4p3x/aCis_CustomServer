package Dev.VoteGatekkeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;

import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.TheHourHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.type.VoteZone;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;
import net.sf.l2j.gameserver.taskmanager.TaskVoteZone;
import net.sf.l2j.gameserver.taskmanager.TaskZone;

public class PvPZoneManager
{
	
	
	private ScheduledFuture<?> _timer, _leftTime;
	private Map<ZoneType, Integer> _zones;
	private static ZoneType _zone;
	private ZoneType _newzone;
	private List<Creature> _character;
	private List<Integer> _vote;
	
	//reward
	private static Map<Integer, TheHourHolder> _player;
	private static TheHourHolder _topPlayer;
	
	public PvPZoneManager()
	{
		_zones = new ConcurrentHashMap<>();
		_player = new ConcurrentHashMap<>();
		_vote = new CopyOnWriteArrayList<>();
		_character = new ArrayList<>();

		for (ZoneType zone : ZoneManager.getInstance().getAllZones(VoteZone.class))
		{
			_zones.put(zone, 0);
		}
		ThreadPool.scheduleAtFixedRate(new TaskVoteZone(), Config.announceTimer * 60 * 1000, (Config.changeZoneTime + Config.announceTimer) * 60 * 1000);
	}
	
	public void MessengeTask()
	{
		ZoneType _newzone = checkZone();
		int time = Config.announceTimer * 60 * 1000;

		World.announceToOnlinePlayers("PvP Zone vai mudar para " + ((VoteZone) _newzone).getName() + " em " + Config.announceTimer + " Minuto's");
		_timer = ThreadPool.schedule(new TaskZone(), time);
	}
	
	
	
	public static ZoneType getZone()
	{
		return _zone;
	}
	
	public ZoneType getNewZone()
	{
		return _newzone;
	}
	
	public ZoneType checkZone()
	{
		ZoneType zone = null;
		int vote = 0;
		
		for (Entry<ZoneType, Integer> activeZone : _zones.entrySet())
		{
			if (activeZone.getValue() >= vote)
			{
				zone = activeZone.getKey();
				vote = activeZone.getValue();
			}
		}
		
		_newzone = zone;
		return zone;
	}
	
	public void setVoteZone(int player, String name)
	{
		for (Entry<ZoneType, Integer> activeZone : _zones.entrySet())
		{
			if (((VoteZone) activeZone.getKey()).getName().equals(name))
			{
				int vote = activeZone.getValue() + 1;
				activeZone.setValue(vote);
				break;
			}
		}
		_vote.add(player);
	}
	
	private static String timeToLeft(ScheduledFuture<?> timer)
	{
		long time = timer.getDelay(TimeUnit.MILLISECONDS);
		return String.format("%d mins, %d sec", TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
	}

	public StringBuilder getMessage(int player, StringBuilder tb)
	{
		try
		{
			if (getZone() != null)
			{
				tb.append("Zona atual e <font color=FFDF00>" + ((VoteZone) getZone()).getName() + " </font><br1>");
				tb.append("There are <font color=FFDF00>" + ((VoteZone) getZone()).getCharactersInside().size() + " players inside </font><br1>");
				
			}

			if (getNewZone() != null && !getNewZone().equals(getZone()))
			{
				tb.append("<br1>Proxima Zone e <font color=FFDF00>" + ((VoteZone) getNewZone()).getName() + "</font><br1>");
				tb.append("Ativado em <font color=FFDF00>" + timeToLeft(_timer) + "</font> ");
				tb.append("<br1><img src=\"l2ui.squaregray\" width=\"230\" height=\"1s\"><br1>");
			}
			else
			{
				boolean has = _vote.contains(player);
				tb.append("<br><img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\">");
				tb.append("<table border=\"0\" width=\"250\" height=\"12\"><tr>");
				tb.append("<td width=\"60\"> Zone's </td><td width=\"60\"> Vote's </td></tr></table>");
				tb.append("<img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\">");
				for (Entry<ZoneType, Integer> zone : _zones.entrySet())
				{
					if (!((VoteZone) zone.getKey()).isActive())
					{
						String name = ((VoteZone) zone.getKey()).getName();
						tb.append("<table border=\"0\" width=\"250\" height=\"12\"><tr>");

						if (has)
							tb.append("<td width=\"60\">" + name + "</td>");
						else
							tb.append("<td width=\"60\"><a action=\"bypass -h npc_%objectId%_voteZone " + player + " " + name + " \">" + name + "</a></td>");

						tb.append("<td width=\"60\"><font color=FFDF00>" + zone.getValue() + "</font></td></tr></table>");
						tb.append("<img src=\"l2ui.squaregray\" width=\"270\" height=\"1s\">");
					}
				}
				tb.append("<br>Vote Time Left <font color=FFDF00>" + timeToLeft(_leftTime) + "</font>");
				
			
			}
		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
		return tb;
	}
	
	public void ZoneTask()
	{
		for (Entry<ZoneType, Integer> zone : _zones.entrySet())
		{
			zone.setValue(0);
		}
		_vote.clear();
		
		if (_zone != null)
		{
			((VoteZone) _zone).active(false);
			
			if (Config.deleteNpc)
			{
				if (!_character.isEmpty()) 
				{
					for (Creature mob : _character)
					{
						mob.spawnMe();
					}
					_character.clear();
				}
			}
		}
		
		if (_newzone != null)
		{
			((VoteZone) _newzone).active(true);
			
			if (Config.deleteNpc)
			{
				for (Creature character : _newzone.getCharactersInside())
				{
					if (!(character instanceof Playable))
					{
						_character.add(character);
						character.decayMe();
					}
				}
			}
			
			if (_zone != null)
			{
				for (Creature character : _zone.getCharactersInside())
				{
					if (character instanceof Playable)
						character.teleToLocation(((SpawnZoneType) getNewZone()).getSpawnLoc(), 20);
				}
			}
			_zone = _newzone;
			
			if (Config.rewardPvp)
			{
				if (_topPlayer != null)
				{
					World.announceToOnlinePlayers("PvP Zone: King " + _topPlayer.getName() + " Fez " + _topPlayer.getKills() + " PvPs");
					giveReward(_topPlayer.getObj());
				}

				resetZonePvp();

				World.announceToOnlinePlayers("PvP Zone: Iniciado, Vote em " + Config.changeZoneTime + " Minutos.");
			}
		}
	}

	public void giveReward(int obj)
	{
		Player player = World.getInstance().getPlayer(obj);
		
		if (player != null && player.isOnline())
		{
			player.addItem("PlayerOfTheHour", Config.rewardId, Config.rewardCount, player, true);
		}
		else
		{
			ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), Config.rewardId);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement stm_items = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)"))
			{
				stm_items.setInt(1, obj);
				stm_items.setInt(2, item.getItemId());
				stm_items.setInt(3, Config.rewardCount);
				stm_items.setString(4, "INVENTORY");
				stm_items.setInt(5, 0);
				stm_items.setInt(6, item.getEnchantLevel());
				stm_items.setInt(7, item.getObjectId());
				stm_items.setInt(8, 0);
				stm_items.setInt(9, 0);
				stm_items.setInt(10, -60);
				stm_items.setLong(11, System.currentTimeMillis());
				stm_items.executeUpdate();
				stm_items.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void addKillsInZone(Player activeChar)
	{
		if (!_player.containsKey(activeChar.getObjectId()))
			_player.put(activeChar.getObjectId(), new TheHourHolder(activeChar.getName(), 1, activeChar.getObjectId()));
		else
			_player.get(activeChar.getObjectId()).setPvpKills();
		
		if (_topPlayer == null)
			_topPlayer = new TheHourHolder(activeChar.getName(), 1, activeChar.getObjectId());
		
		else if (_player.get(activeChar.getObjectId()).getKills() > _topPlayer.getKills())
			_topPlayer = _player.get(activeChar.getObjectId());
	}
	
	public void resetZonePvp()
	{
		_player.clear();
		_topPlayer = null;
	}
	
	private static class SingletonHolder
	{
		protected static final PvPZoneManager _instance = new PvPZoneManager();
	}
	
	public static PvPZoneManager getInstance()
	{
		return SingletonHolder._instance;
	}
}