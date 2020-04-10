package Dev.Tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Arena2x2 implements Runnable
{
	protected static final Logger _log = Logger.getLogger(Arena2x2.class.getName());

	public static List<Pair> registered;

	int free = Config.ARENA_EVENT_COUNT;

	Arena[] arenas = new Arena[Config.ARENA_EVENT_COUNT];

	Map<Integer, String> fights = new HashMap<>(Config.ARENA_EVENT_COUNT);

	public Arena2x2()
	{
		registered = new ArrayList<>();

		for (int i = 0; i < Config.ARENA_EVENT_COUNT; i++)
		{
			int[] coord = Config.ARENA_EVENT_LOCS[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
	}

	public static Arena2x2 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public boolean register(Player player, Player assist)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player || p.getAssist() == player)
			{
				player.sendMessage("Tournament: You already registered!");
				return false;
			}
			if (p.getLeader() == assist || p.getAssist() == assist)
			{
				player.sendMessage("Tournament: Your partner already registered!");
				return false;
			}
			
			
		}
		return registered.add(new Pair(player, assist));
	}

	public boolean isRegistered(Player player)
	{
		for (Pair p : registered)
			if (p.getLeader() == player || p.getAssist() == player)
				return true;
		return false;
	}

	public Map<Integer, String> getFights()
	{
		return fights;
	}

	public boolean remove(Player player)
	{
		for (Pair p : registered)
			if (p.getLeader() == player || p.getAssist() == player)
			{
				p.removeMessage();
				registered.remove(p);
				return true;
			}
		return false;
	}

	@Override
	public synchronized void run()
	{
		for (;;)
			if (registered.size() < 2 || free == 0)
				try
		{
					Thread.sleep(Config.ARENA_CALL_INTERVAL * 1000);

		}
		catch (InterruptedException localInterruptedException)
		{
		}
			else
			{
				List<Pair> opponents = selectOpponents();
				if (opponents != null && opponents.size() == 2)
				{
					Thread T = new Thread(new EvtArenaTask(opponents));
					T.setDaemon(true);
					T.start();
				}

				try
				{
					Thread.sleep(Config.ARENA_CALL_INTERVAL * 1000);
				}
				catch (InterruptedException localInterruptedException1)
				{
				}
			}
	}

	@SuppressWarnings("null")
	private List<Pair> selectOpponents()
	{
		List<Pair> opponents = new ArrayList<>();
		Pair pairOne = null;
		Pair pairTwo = null;
		int tries = 3;
		do
		{
			int first = 0;
			int second = 0;
			if (getRegisteredCount() < 2)
				return opponents;
			if (pairOne == null)
			{
				first = Rnd.get(getRegisteredCount());
				pairOne = registered.get(first);
				if (pairOne.check())
				{
					opponents.add(0, pairOne);
					registered.remove(first);
				}
				else
				{
					pairOne = null;
					registered.remove(first);
					return null;
				}
			}

			if (pairTwo == null)
			{
				second = Rnd.get(getRegisteredCount());
				pairTwo = registered.get(second);
				if (pairTwo.check())
				{
					opponents.add(1, pairTwo);
					registered.remove(second);
				}
				else
				{
					pairTwo = null;
					registered.remove(second);
					return null;
				}
			}

			if (pairOne != null && pairTwo != null)
				break;
			tries--;
		}
		while (tries > 0);
		return opponents;
	}

	public int getRegisteredCount()
	{
		return registered.size();
	}

	private class Pair
	{
		Player leader;
		Player assist;

		public Pair(Player leader, Player assist)
		{
			this.leader = leader;
			this.assist = assist;
		}

		public Player getAssist()
		{
			return assist;
		}

		public Player getLeader()
		{
			return leader;
		}

		public boolean check()
		{
			if ((leader == null || !leader.isOnline()) && (assist != null || assist.isOnline()))
			{
				assist.sendMessage("Tournament: You participation in Event was Canceled.");
				return false;
			}
			if ((assist == null || !assist.isOnline()) && (leader != null || leader.isOnline()))
			{
				leader.sendMessage("Tournament: You participation in Event was Canceled.");
				return false;
			}
			return true;
		}

		public boolean isDead()
		{

			
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()))
				return false;
			return !leader.isDead() || !assist.isDead();
			
		}
		
		

		public boolean isAlive()
		{
			
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()))
				return false;
			return !leader.isDead() || !assist.isDead();
			
			
		}

		public void teleportTo(int x, int y, int z)
		{
			if (leader != null && leader.isOnline())
			{

				leader.setCurrentCp(leader.getMaxCp());
				leader.setCurrentHp(leader.getMaxHp());
				leader.setCurrentMp(leader.getMaxMp());

				if (leader.isInObserverMode())
				{
					leader.setLastCords(x, y, z);
					leader.leaveOlympiadObserverMode();
				}	
				else
					leader.teleportTo(x, y, z, 0);
				leader.broadcastUserInfo();
			}

			if (assist != null && assist.isOnline())
			{
				assist.setCurrentCp(assist.getMaxCp());
				assist.setCurrentHp(assist.getMaxHp());
				assist.setCurrentMp(assist.getMaxMp());
				

				if (assist.isInObserverMode())
				{
					assist.setLastCords(x, y + 50, z);
					assist.leaveOlympiadObserverMode();
				}
				else
					assist.teleportTo(x, y + 50, z, 0);
				assist.broadcastUserInfo();
			}
		}

		public void EventTitle(String title, String color)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setTitle(title);
				leader.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				leader.broadcastUserInfo();
				leader.broadcastTitleInfo();
			}

			if (assist != null && assist.isOnline())
			{
				assist.setTitle(title);
				assist.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist.broadcastUserInfo();
				assist.broadcastTitleInfo();
			}
		}

		public void saveTitle()
		{
			if (leader != null && leader.isOnline())
			{
				leader._originalTitleColorTournament = leader.getAppearance().getTitleColor();
				leader._originalTitleTournament = leader.getTitle();
			}

			if (assist != null && assist.isOnline())
			{
				assist._originalTitleColorTournament = assist.getAppearance().getTitleColor();
				assist._originalTitleTournament = assist.getTitle();
			}
		}

		public void backTitle()
		{
			if (leader != null && leader.isOnline())
			{
				leader.setTitle(leader._originalTitleTournament);
				leader.getAppearance().setTitleColor(leader._originalTitleColorTournament);
				leader.broadcastUserInfo();
				leader.broadcastTitleInfo();
			}

			if (assist != null && assist.isOnline())
			{
				assist.setTitle(assist._originalTitleTournament);
				assist.getAppearance().setTitleColor(assist._originalTitleColorTournament);
				assist.broadcastUserInfo();
				assist.broadcastTitleInfo();
			}
		}

		public void rewards()
		{
			if (leader != null && leader.isOnline())

					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT, leader, true);

			if (assist != null && assist.isOnline())
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT, assist, true);

			sendPacket("Congratulations, your team won the event!", 5);
		}

		public void rewardsLost()
		{
			if (leader != null && leader.isOnline())
				leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT, leader, true);
			if (assist != null && assist.isOnline())
				assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT, assist, true);
			sendPacket("your team lost the event! =(", 5);
		}

		public void setInTournamentEvent(boolean val)
		{
			if (leader != null && leader.isOnline())
				leader.setInArenaEvent(val);
			if (assist != null && assist.isOnline())
				assist.setInArenaEvent(val);
		}

		public void removeMessage()
		{
			if (leader != null && leader.isOnline())
			{
				leader.sendMessage("Tournament: Your participation has been removed.");
				leader.setArenaProtection(false);
				leader.setArena2x2(false);
			}

			if (assist != null && assist.isOnline())
			{
				assist.sendMessage("Tournament: Your participation has been removed.");
				assist.setArenaProtection(false);
				leader.setArena2x2(false);
			}
		}

		public void setArenaProtection(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setArenaProtection(val);
				leader.setArena2x2(val);
			}

			if (assist != null && assist.isOnline())
			{
				assist.setArenaProtection(val);
				leader.setArena2x2(val);
			}
		}

		public void revive()
		{
			if (leader != null && leader.isOnline() && leader.isDead())
				leader.doRevive();
			if (assist != null && assist.isOnline() && assist.isDead())
				assist.doRevive();
		}

		public void setImobilised(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setIsInvul(val);
				leader.setStopArena(val);
			}

			if (assist != null && assist.isOnline())
			{
				assist.setIsInvul(val);
				assist.setStopArena(val);
			}
		}

		public void setArenaAttack(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setArenaAttack(val);
				leader.broadcastUserInfo();
			}

			if (assist != null && assist.isOnline())
			{
				assist.setArenaAttack(val);
				assist.broadcastUserInfo();
			}
		}


		public void removePet()
		{
			if (leader != null && leader.isOnline())
			{

				if (leader.getSummon() != null)
				{
					Summon summon = leader.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(leader);
				}
				if (leader.getMountType() == 1 || leader.getMountType() == 2)
					leader.dismount();
			}
			if (assist != null && assist.isOnline())
			{

				if (assist.getSummon() != null)
				{
					Summon summon = assist.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist);
				}
				if (assist.getMountType() == 1 || assist.getMountType() == 2)
					assist.dismount();
			}

	
		}
		

		public void removeSkills()
		{
			for (L2Effect effect : leader.getAllEffects())
				if (effect.getSkill().getId() == 406 || effect.getSkill().getId() == 139 || effect.getSkill().getId() == 176 || effect.getSkill().getId() == 420)
				{
					leader.stopSkillEffects(effect.getSkill().getId());
					leader.enableSkill(effect.getSkill());
				}

			for (L2Effect effect : assist.getAllEffects())
				if (effect.getSkill().getId() == 406 || effect.getSkill().getId() == 139 || effect.getSkill().getId() == 176 || effect.getSkill().getId() == 420)
				{
					assist.stopSkillEffects(effect.getSkill().getId());
					assist.enableSkill(effect.getSkill());
				}

			if (Config.ARENA_SKILL_PROTECT)
				if (leader != null && leader.isOnline())
				{
					for (L2Effect effect : leader.getAllEffects())
						if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
							leader.stopSkillEffects(effect.getSkill().getId());
					if (leader.getMountType() == 2)
					{
						leader.sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
						leader.enteredNoLanding(5);
					}
				}

			if (Config.ARENA_SKILL_PROTECT)
				if (assist != null && assist.isOnline())
				{
					for (L2Effect effect : assist.getAllEffects())
						if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
							assist.stopSkillEffects(effect.getSkill().getId());
					if (assist.getMountType() == 2)
					{
						assist.sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
						assist.enteredNoLanding(5);
					}
				}
		}

		public void sendPacket(String message, int duration)
		{
			if (leader != null && leader.isOnline())
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist != null && assist.isOnline())
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000));
		}

		public void inicarContagem(int duration)
		{
			if (leader != null && leader.isOnline())
				ThreadPool.schedule(new Arena2x2.countdown(leader, duration), 0L);
			if (assist != null && assist.isOnline())
				ThreadPool.schedule(new Arena2x2.countdown(assist, duration), 0L);
		}

		public void sendPacketinit(String message, int duration)
		{
			if (leader != null && leader.isOnline())
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist != null && assist.isOnline())
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (leader.getClassId() == ClassId.SHILLIEN_ELDER || leader.getClassId() == ClassId.SHILLIEN_SAINT || leader.getClassId() == ClassId.BISHOP || leader.getClassId() == ClassId.CARDINAL || leader.getClassId() == ClassId.ELVEN_ELDER || leader.getClassId() == ClassId.EVAS_SAINT)
				ThreadPool.schedule(new Runnable()
				{

					@Override
					public void run()
					{
						leader.getClient().closeNow();
					}
				}, 100L);

			if (assist.getClassId() == ClassId.SHILLIEN_ELDER || assist.getClassId() == ClassId.SHILLIEN_SAINT || assist.getClassId() == ClassId.BISHOP || assist.getClassId() == ClassId.CARDINAL || assist.getClassId() == ClassId.ELVEN_ELDER || assist.getClassId() == ClassId.EVAS_SAINT)
				ThreadPool.schedule(new Runnable()
				{

					@Override
					public void run()
					{
						assist.getClient().closeNow();
					}
				}, 100L);
		}
	}

	private class EvtArenaTask implements Runnable
	{
		private final Arena2x2.Pair pairOne;
		private final Arena2x2.Pair pairTwo;
		private final int pOneX;
		private final int pOneY;
		private final int pOneZ;
		private final int pTwoX;
		private final int pTwoY;
		private final int pTwoZ;
		private Arena2x2.Arena arena;

		public EvtArenaTask(List<Pair> opponents)
		{
			pairOne = opponents.get(0);
			pairTwo = opponents.get(1);
			Player leader = pairOne.getLeader();
			pOneX = leader.getX();
			pOneY = leader.getY();
			pOneZ = leader.getZ();
			leader = pairTwo.getLeader();
			pTwoX = leader.getX();
			pTwoY = leader.getY();
			pTwoZ = leader.getZ();
		}

		@Override
		public void run()
		{
			free -= 1;
			pairOne.saveTitle();
			pairTwo.saveTitle();
			portPairsToArena();
			pairOne.inicarContagem(Config.ARENA_WAIT_INTERVAL);
			pairTwo.inicarContagem(Config.ARENA_WAIT_INTERVAL);
			
			try
			{
				Thread.sleep(Config.ARENA_WAIT_INTERVAL * 1000);
			}
			catch (InterruptedException localInterruptedException1)
			{
			}

			pairOne.sendPacketinit("Started. Good Fight!", 3);
			pairTwo.sendPacketinit("Started. Good Fight!", 3);
			pairOne.EventTitle(Config.MSG_TEAM1, Config.TITLE_COLOR_TEAM1);
			pairTwo.EventTitle(Config.MSG_TEAM2, Config.TITLE_COLOR_TEAM2);
		 	
			pairOne.setImobilised(false);
			pairTwo.setImobilised(false);
			pairOne.setArenaAttack(true);
			pairTwo.setArenaAttack(true);
			while (check())
				try
			{

					Thread.sleep(Config.ARENA_CHECK_INTERVAL);
					continue;
			}
			catch (InterruptedException e)
			{
				 break;
			}
	        this.finishDuel();
            final Arena2x2 this$2 = Arena2x2.this;
            ++this$2.free;


		}
		
		

		private void finishDuel()
		{
			fights.remove(Integer.valueOf(arena.id));
			rewardWinner();
			pairOne.revive();
			pairTwo.revive();
			pairOne.teleportTo(pOneX, pOneY, pOneZ);
			pairTwo.teleportTo(pTwoX, pTwoY, pTwoZ);
			pairOne.backTitle();
			pairTwo.backTitle();
			pairOne.setInTournamentEvent(false);
			pairTwo.setInTournamentEvent(false);
			pairOne.setArenaProtection(false);
			pairTwo.setArenaProtection(false);
			pairOne.setArenaAttack(false);
			pairTwo.setArenaAttack(false);
			arena.setFree(true);
		}

		private void rewardWinner()
		{
			if (pairOne.isAlive() && !pairTwo.isAlive())
			{
				pairOne.rewards();
				pairTwo.rewardsLost();
			}
			
		
			else if (pairTwo.isAlive() && !pairOne.isAlive())
			{
				pairTwo.rewards();
				pairOne.rewardsLost();
			}
		}

		private boolean check()
		{
			return pairOne.isDead() && pairTwo.isDead();
		}

		private void portPairsToArena()
		{
			for (Arena2x2.Arena arena : arenas)
				if (arena.isFree)
				{
					this.arena = arena;
					arena.setFree(false);
					pairOne.removePet();
					pairTwo.removePet();
					pairOne.teleportTo(arena.x - 850, arena.y, arena.z);
					pairTwo.teleportTo(arena.x + 850, arena.y, arena.z);
					pairOne.setImobilised(true);
					pairTwo.setImobilised(true);
					pairOne.setInTournamentEvent(true);
					pairTwo.setInTournamentEvent(true);
					pairOne.removeSkills();
					pairTwo.removeSkills();
					fights.put(Integer.valueOf(this.arena.id), pairOne.getLeader().getName() + " vs " + pairTwo.getLeader().getName());
					Arena2x2.this.fights.put(this.arena.id, this.pairOne.getLeader().getName() + " vs " + this.pairTwo.getLeader().getName());
					break;
				}
		}
	}

	private class Arena
	{
		protected int x;
		protected int y;
		protected int z;
		protected boolean isFree = true;
		int id;

		public Arena(int id, int x, int y, int z)
		{
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public void setFree(boolean val)
		{
			isFree = val;
		}
	}

	protected class countdown implements Runnable
	{
		private final Player _player;
		private final int _time;

		public countdown(Player player, int time)
		{
			_time = time;
			_player = player;
		}

		@Override
		public void run()
		{
			if (_player.isOnline())
			{

				switch (_time)
				{
					case 60:
					case 120:
					case 180:
					case 240:
					case 300:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in " + _time + " second(s)..", 4000));
							_player.sendMessage(_time + " second(s) to start the battle.");
							_player.setIsParalyzed(true);
						}
						break;
					case 45:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("" + _time + " ..", 3000));
							_player.sendMessage(_time + " second(s) to start the battle!");
							_player.setIsParalyzed(true);
							_player.broadcastPacket(new SocialAction(_player, 1));
						}
						break;
					case 27:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 30 second(s)..", 4000));
							_player.sendMessage("30 second(s) to start the battle!");
							_player.setIsParalyzed(true);
							_player.broadcastPacket(new SocialAction(_player, 2));
						}
						break;
					case 20:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("" + _time + " ..", 3000));
							_player.sendMessage(_time + " second(s) to start the battle!");
							_player.setIsParalyzed(true);
							
						}
						break;
					case 15:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("" + _time + " ..", 3000));
							_player.sendMessage(_time + " second(s) to start the battle!");
							_player.setIsParalyzed(true);
							_player.broadcastPacket(new SocialAction(_player, 9));
						}
						break;
					case 10:
						if (_player.isOnline())
							_player.sendMessage(_time + " second(s) to start the battle!");
						_player.setIsParalyzed(true);
						_player.broadcastPacket(new SocialAction(_player, 5));
						break;
					case 5:
						if (_player.isOnline())
							_player.sendMessage(_time + " second(s) to start the battle!");
						_player.setIsParalyzed(true);
						break;
					case 4:
						if (_player.isOnline())
							_player.sendMessage(_time + " second(s) to start the battle!");
						_player.setIsParalyzed(true);
						break;
					case 3:
						if (_player.isOnline())
							_player.sendMessage(_time + " second(s) to start the battle!");
						_player.setIsParalyzed(true);
						break;
					case 2:
						if (_player.isOnline())
							_player.sendMessage(_time + " second(s) to start the battle!");
						_player.setIsParalyzed(true);
						break;
					case 1:
						if (_player.isOnline())
							_player.sendMessage(_time + " second(s) to start the battle!");
						_player.setIsParalyzed(false);
						break;
				}
				if (_time > 1)
					ThreadPool.schedule(new countdown(_player, _time - 1), 1000L);
			}
		}
	}
	


	private static class SingletonHolder
	{
		protected static final Arena2x2 INSTANCE = new Arena2x2();
	}
}
