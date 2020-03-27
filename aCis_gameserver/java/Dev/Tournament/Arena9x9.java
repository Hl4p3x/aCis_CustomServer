package Dev.Tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Arena9x9 implements Runnable
{
	public static List<Pair> registered;
	int free = Config.ARENA_EVENT_COUNT_9X9;

	Arena[] arenas = new Arena[Config.ARENA_EVENT_COUNT_9X9];

	Map<Integer, String> fights = new HashMap<>(Config.ARENA_EVENT_COUNT_9X9);

	public Arena9x9()
	{
		registered = new ArrayList<>();

		for (int i = 0; i < Config.ARENA_EVENT_COUNT_9X9; i++)
		{
			int[] coord = Config.ARENA_EVENT_LOCS_9X9[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
	}

	public static Arena9x9 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public boolean register(Player player, Player assist, Player assist2, Player assist3, Player assist4, Player assist5, Player assist6, Player assist7, Player assist8)
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
				player.sendMessage("Tournament: " + assist.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist2 || p.getAssist2() == assist2)
			{
				player.sendMessage("Tournament: " + assist2.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist3 || p.getAssist3() == assist3)
			{
				player.sendMessage("Tournament: " + assist3.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist4 || p.getAssist4() == assist4)
			{
				player.sendMessage("Tournament: " + assist4.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist5 || p.getAssist5() == assist5)
			{
				player.sendMessage("Tournament: " + assist5.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist6 || p.getAssist6() == assist6)
			{
				player.sendMessage("Tournament: " + assist6.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist7 || p.getAssist7() == assist7)
			{
				player.sendMessage("Tournament: " + assist7.getName() + " already registered!");
				return false;
			}
			if (p.getLeader() == assist8 || p.getAssist8() == assist8)
			{
				player.sendMessage("Tournament: " + assist8.getName() + " already registered!");
				return false;
			}
		}
		return registered.add(new Pair(player, assist, assist2, assist3, assist4, assist5, assist6, assist7, assist8));
	}

	public boolean isRegistered(Player player)
	{
		for (Pair p : registered)
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player || p.getAssist3() == player || p.getAssist4() == player || p.getAssist5() == player || p.getAssist6() == player || p.getAssist7() == player || p.getAssist8() == player)
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
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player || p.getAssist3() == player || p.getAssist4() == player || p.getAssist5() == player || p.getAssist6() == player || p.getAssist7() == player || p.getAssist8() == player)
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
		private final Player leader;
		private final Player assist;
		private final Player assist2;
		private final Player assist3;

		public Pair(Player leader, Player assist, Player assist2, Player assist3, Player assist4, Player assist5, Player assist6, Player assist7, Player assist8)
		{
			this.leader = leader;
			this.assist = assist;
			this.assist2 = assist2;
			this.assist3 = assist3;
			this.assist4 = assist4;
			this.assist5 = assist5;
			this.assist6 = assist6;
			this.assist7 = assist7;
			this.assist8 = assist8;
		}

		public Player getAssist()
		{
			return assist;
		}

		public Player getAssist2()
		{
			return assist2;
		}

		public Player getAssist3()
		{
			return assist3;
		}

		public Player getAssist4()
		{
			return assist4;
		}

		public Player getAssist5()
		{
			return assist5;
		}

		public Player getAssist6()
		{
			return assist6;
		}

		public Player getAssist7()
		{
			return assist7;
		}

		public Player getAssist8()
		{
			return assist8;
		}

		public Player getLeader()
		{
			return leader;
		}

		public boolean check()
		{
			if (leader == null || !leader.isOnline())
			{

				if (assist != null || assist.isOnline())
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist2 != null || assist2.isOnline())
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist3 != null || assist3.isOnline())
					assist3.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist4 != null || assist4.isOnline())
					assist4.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist5 != null || assist5.isOnline())
					assist5.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist6 != null || assist6.isOnline())
					assist6.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist7 != null || assist7.isOnline())
					assist7.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist8 != null || assist8.isOnline())
					assist8.sendMessage("Tournament: You participation in Event was Canceled.");
				return false;
			}
			if ((assist == null || !assist.isOnline() || assist2 == null || !assist2.isOnline() || assist3 == null || !assist3.isOnline() || assist4 == null || !assist4.isOnline() || assist5 == null || !assist5.isOnline() || assist6 == null || !assist6.isOnline() || assist7 == null || !assist7.isOnline() || assist8 == null || !assist8.isOnline()) && (leader != null || leader.isOnline()))
			{
				leader.sendMessage("Tournament: You participation in Event was Canceled.");

				if (assist != null || assist.isOnline())
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist2 != null || assist2.isOnline())
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist3 != null || assist3.isOnline())
					assist3.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist4 != null || assist4.isOnline())
					assist4.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist5 != null || assist5.isOnline())
					assist5.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist6 != null || assist6.isOnline())
					assist6.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist7 != null || assist7.isOnline())
					assist7.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist8 != null || assist8.isOnline())
					assist8.sendMessage("Tournament: You participation in Event was Canceled.");
				return false;
			}
			return true;
		}

		public boolean isDead()
		{
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !assist2.isOnline() || !assist2.isInsideZone(ZoneId.ARENA_EVENT) || !assist2.isArenaAttack()) && (assist3 == null || assist3.isDead() || !assist3.isOnline() || !assist3.isInsideZone(ZoneId.ARENA_EVENT) || !assist3.isArenaAttack()) && (assist4 == null || assist4.isDead() || !assist4.isOnline() || !assist4.isInsideZone(ZoneId.ARENA_EVENT) || !assist4.isArenaAttack()) && (assist5 == null || assist5.isDead() || !assist5.isOnline() || !assist5.isInsideZone(ZoneId.ARENA_EVENT) || !assist5.isArenaAttack()) && (assist6 == null || assist6.isDead() || !assist6.isOnline() || !assist6.isInsideZone(ZoneId.ARENA_EVENT) || !assist6.isArenaAttack()) && (assist7 == null || assist7.isDead() || !assist7.isOnline() || !assist7.isInsideZone(ZoneId.ARENA_EVENT) || !assist7.isArenaAttack()) && (assist8 == null || assist8.isDead() || !assist8.isOnline() || !assist8.isInsideZone(ZoneId.ARENA_EVENT) || !assist8.isArenaAttack()))
				return false;
			return !leader.isDead() || !assist.isDead() || !assist2.isDead() || !assist3.isDead() || !assist4.isDead() || !assist5.isDead() || !assist6.isDead() || !assist7.isDead() || !assist8.isDead();
		}

		public boolean isAlive()
		{
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !assist2.isOnline() || !assist2.isInsideZone(ZoneId.ARENA_EVENT) || !assist2.isArenaAttack()) && (assist3 == null || assist3.isDead() || !assist3.isOnline() || !assist3.isInsideZone(ZoneId.ARENA_EVENT) || !assist3.isArenaAttack()) && (assist4 == null || assist4.isDead() || !assist4.isOnline() || !assist4.isInsideZone(ZoneId.ARENA_EVENT) || !assist4.isArenaAttack()) && (assist5 == null || assist5.isDead() || !assist5.isOnline() || !assist5.isInsideZone(ZoneId.ARENA_EVENT) || !assist5.isArenaAttack()) && (assist6 == null || assist6.isDead() || !assist6.isOnline() || !assist6.isInsideZone(ZoneId.ARENA_EVENT) || !assist6.isArenaAttack()) && (assist7 == null || assist7.isDead() || !assist7.isOnline() || !assist7.isInsideZone(ZoneId.ARENA_EVENT) || !assist7.isArenaAttack()) && (assist8 == null || assist8.isDead() || !assist8.isOnline() || !assist8.isInsideZone(ZoneId.ARENA_EVENT) || !assist8.isArenaAttack()))
				return false;
			return !leader.isDead() || !assist.isDead() || !assist2.isDead() || !assist3.isDead() || !assist4.isDead() || !assist5.isDead() || !assist6.isDead() || !assist7.isDead() || !assist8.isDead();
		}

		public void teleportTo(int x, int y, int z)
		{
			if (leader != null && leader.isOnline())
			{
				leader.getAppearance().isVisible();
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
				assist.getAppearance().isVisible();
				assist.setCurrentCp(assist.getMaxCp());
				assist.setCurrentHp(assist.getMaxHp());
				assist.setCurrentMp(assist.getMaxMp());

				if (assist.isInObserverMode())
				{
					assist.setLastCords(x, y + 200, z);
					assist.leaveOlympiadObserverMode();
				}
				else
					assist.teleportTo(x, y + 200, z, 0);
				assist.broadcastUserInfo();
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.getAppearance().isVisible();
				assist2.setCurrentCp(assist2.getMaxCp());
				assist2.setCurrentHp(assist2.getMaxHp());
				assist2.setCurrentMp(assist2.getMaxMp());

				if (assist2.isInObserverMode())
				{
					assist2.setLastCords(x, y + 150, z);
					assist2.leaveOlympiadObserverMode();
				}
				else
					assist2.teleportTo(x, y + 150, z, 0);
				assist2.broadcastUserInfo();
			}
			if (assist3 != null && assist3.isOnline())
			{
				assist3.getAppearance().isVisible();
				assist3.setCurrentCp(assist3.getMaxCp());
				assist3.setCurrentHp(assist3.getMaxHp());
				assist3.setCurrentMp(assist3.getMaxMp());

				if (assist3.isInObserverMode())
				{
					assist3.setLastCords(x, y + 100, z);
					assist3.leaveOlympiadObserverMode();
				}
				else
					assist3.teleportTo(x, y + 100, z, 0);
				assist3.broadcastUserInfo();
			}
			if (assist4 != null && assist4.isOnline())
			{
				assist4.getAppearance().isVisible();
				assist4.setCurrentCp(assist4.getMaxCp());
				assist4.setCurrentHp(assist4.getMaxHp());
				assist4.setCurrentMp(assist4.getMaxMp());

				if (assist4.isInObserverMode())
				{
					assist4.setLastCords(x, y + 50, z);
					assist4.leaveOlympiadObserverMode();
				}
				else
					assist4.teleportTo(x, y + 50, z, 0);
				assist4.broadcastUserInfo();
			}
			if (assist5 != null && assist5.isOnline())
			{
				assist5.getAppearance().isVisible();
				assist5.setCurrentCp(assist5.getMaxCp());
				assist5.setCurrentHp(assist5.getMaxHp());
				assist5.setCurrentMp(assist5.getMaxMp());

				if (assist5.isInObserverMode())
				{
					assist5.setLastCords(x, y - 200, z);
					assist5.leaveOlympiadObserverMode();
				}
				else
					assist5.teleportTo(x, y - 200, z, 0);
				assist5.broadcastUserInfo();
			}
			if (assist6 != null && assist6.isOnline())
			{
				assist6.getAppearance().isVisible();
				assist6.setCurrentCp(assist6.getMaxCp());
				assist6.setCurrentHp(assist6.getMaxHp());
				assist6.setCurrentMp(assist6.getMaxMp());

				if (assist6.isInObserverMode())
				{
					assist6.setLastCords(x, y - 150, z);
					assist6.leaveOlympiadObserverMode();
				}
				else
					assist6.teleportTo(x, y - 150, z, 0);
				assist6.broadcastUserInfo();
			}
			if (assist7 != null && assist7.isOnline())
			{
				assist7.getAppearance().isVisible();
				assist7.setCurrentCp(assist7.getMaxCp());
				assist7.setCurrentHp(assist7.getMaxHp());
				assist7.setCurrentMp(assist7.getMaxMp());

				if (assist7.isInObserverMode())
				{
					assist7.setLastCords(x, y - 100, z);
					assist7.leaveOlympiadObserverMode();
				}
				else
					assist7.teleportTo(x, y - 100, z, 0);
				assist7.broadcastUserInfo();
			}
			if (assist8 != null && assist8.isOnline())
			{
				assist8.getAppearance().isVisible();
				assist8.setCurrentCp(assist8.getMaxCp());
				assist8.setCurrentHp(assist8.getMaxHp());
				assist8.setCurrentMp(assist8.getMaxMp());

				if (assist8.isInObserverMode())
				{
					assist8.setLastCords(x, y - 50, z);
					assist8.leaveOlympiadObserverMode();
				}
				else
					assist8.teleportTo(x, y - 50, z, 0);
				assist8.broadcastUserInfo();
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
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setTitle(title);
				assist2.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist2.broadcastUserInfo();
				assist2.broadcastTitleInfo();
			}
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setTitle(title);
				assist3.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist3.broadcastUserInfo();
				assist3.broadcastTitleInfo();
			}
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setTitle(title);
				assist4.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist4.broadcastUserInfo();
				assist4.broadcastTitleInfo();
			}
			if (assist5 != null && assist5.isOnline())
			{
				assist5.setTitle(title);
				assist5.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist5.broadcastUserInfo();
				assist5.broadcastTitleInfo();
			}
			if (assist6 != null && assist6.isOnline())
			{
				assist6.setTitle(title);
				assist6.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist6.broadcastUserInfo();
				assist6.broadcastTitleInfo();
			}
			if (assist7 != null && assist7.isOnline())
			{
				assist7.setTitle(title);
				assist7.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist7.broadcastUserInfo();
				assist7.broadcastTitleInfo();
			}
			if (assist8 != null && assist8.isOnline())
			{
				assist8.setTitle(title);
				assist8.getAppearance().setTitleColor(Integer.decode("0x" + color).intValue());
				assist8.broadcastUserInfo();
				assist8.broadcastTitleInfo();
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

			if (assist2 != null && assist2.isOnline())
			{
				assist2._originalTitleColorTournament = assist2.getAppearance().getTitleColor();
				assist2._originalTitleTournament = assist2.getTitle();
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3._originalTitleColorTournament = assist3.getAppearance().getTitleColor();
				assist3._originalTitleTournament = assist3.getTitle();
			}

			if (assist4 != null && assist4.isOnline())
			{
				assist4._originalTitleColorTournament = assist4.getAppearance().getTitleColor();
				assist4._originalTitleTournament = assist4.getTitle();
			}

			if (assist5 != null && assist5.isOnline())
			{
				assist5._originalTitleColorTournament = assist5.getAppearance().getTitleColor();
				assist5._originalTitleTournament = assist5.getTitle();
			}

			if (assist6 != null && assist6.isOnline())
			{
				assist6._originalTitleColorTournament = assist6.getAppearance().getTitleColor();
				assist6._originalTitleTournament = assist6.getTitle();
				assist6._originalTitleTournament = assist6.getTitle();
			}

			if (assist7 != null && assist7.isOnline())
			{
				assist7._originalTitleColorTournament = assist7.getAppearance().getTitleColor();
				assist7._originalTitleTournament = assist7.getTitle();
			}

			if (assist8 != null && assist8.isOnline())
			{
				assist8._originalTitleColorTournament = assist8.getAppearance().getTitleColor();
				assist8._originalTitleTournament = assist8.getTitle();
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

			if (assist2 != null && assist2.isOnline())
			{
				assist2.setTitle(assist2._originalTitleTournament);
				assist2.getAppearance().setTitleColor(assist2._originalTitleColorTournament);
				assist2.broadcastUserInfo();
				assist2.broadcastTitleInfo();
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.setTitle(assist3._originalTitleTournament);
				assist3.getAppearance().setTitleColor(assist3._originalTitleColorTournament);
				assist3.broadcastUserInfo();
				assist3.broadcastTitleInfo();
			}

			if (assist4 != null && assist4.isOnline())
			{
				assist4.setTitle(assist4._originalTitleTournament);
				assist4.getAppearance().setTitleColor(assist4._originalTitleColorTournament);
				assist4.broadcastUserInfo();
				assist4.broadcastTitleInfo();
			}

			if (assist5 != null && assist5.isOnline())
			{
				assist5.setTitle(assist5._originalTitleTournament);
				assist5.getAppearance().setTitleColor(assist5._originalTitleColorTournament);
				assist5.broadcastUserInfo();
				assist5.broadcastTitleInfo();
			}

			if (assist6 != null && assist6.isOnline())
			{
				assist6.setTitle(assist6._originalTitleTournament);
				assist6.getAppearance().setTitleColor(assist6._originalTitleColorTournament);
				assist6.broadcastUserInfo();
				assist6.broadcastTitleInfo();
			}

			if (assist7 != null && assist7.isOnline())
			{
				assist7.setTitle(assist7._originalTitleTournament);
				assist7.getAppearance().setTitleColor(assist7._originalTitleColorTournament);
				assist7.broadcastUserInfo();
				assist7.broadcastTitleInfo();
			}

			if (assist8 != null && assist8.isOnline())
			{
				assist8.setTitle(assist8._originalTitleTournament);
				assist8.getAppearance().setTitleColor(assist8._originalTitleColorTournament);
				assist8.broadcastUserInfo();
				assist8.broadcastTitleInfo();
			}
		}

		private final Player assist4;
		private final Player assist5;
		private final Player assist6;
		private final Player assist7;
		private final Player assist8;

		public void rewards()
		{
			if (leader != null && leader.isOnline())
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, leader, true);
			
			if (assist != null && assist.isOnline())
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist, true);
			
			if (assist2 != null && assist2.isOnline())
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist2, true);
			
			if (assist3 != null && assist3.isOnline())
					assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist3, true);
			
			if (assist4 != null && assist4.isOnline())
					assist4.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist4, true);
			
			if (assist5 != null && assist5.isOnline())
					assist5.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist5, true);
			
			if (assist6 != null && assist6.isOnline())
					assist6.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist6, true);
			
			if (assist7 != null && assist7.isOnline())
					assist7.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist7, true);
			
			if (assist8 != null && assist8.isOnline())
					assist8.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_9X9, assist8, true);
			
			
								sendPacket("Congratulations, your team won the event!", 5);
		}

		public void rewardsLost()
		{
			if (leader != null && leader.isOnline())
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, leader, true);
			
			if (assist != null && assist.isOnline())
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist, true);
			
			if (assist2 != null && assist2.isOnline())
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist2, true);
			
			if (assist3 != null && assist3.isOnline())
					assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist3, true);
			
			if (assist4 != null && assist4.isOnline())
					assist4.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist4, true);
			
			if (assist5 != null && assist5.isOnline())
					assist5.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist5, true);
			
			if (assist6 != null && assist6.isOnline())
					assist6.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist6, true);
			
			if (assist7 != null && assist7.isOnline())
					assist7.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist7, true);
			
			if (assist8 != null && assist8.isOnline())
					assist8.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_9X9, assist8, true);
			sendPacket("your team lost the event! =(", 5);
		}

		public void setInTournamentEvent(boolean val)
		{
			if (leader != null && leader.isOnline())
				leader.setInArenaEvent(val);
			if (assist != null && assist.isOnline())
				assist.setInArenaEvent(val);
			if (assist2 != null && assist2.isOnline())
				assist2.setInArenaEvent(val);
			if (assist3 != null && assist3.isOnline())
				assist3.setInArenaEvent(val);
			if (assist4 != null && assist4.isOnline())
				assist4.setInArenaEvent(val);
			if (assist5 != null && assist5.isOnline())
				assist5.setInArenaEvent(val);
			if (assist6 != null && assist6.isOnline())
				assist6.setInArenaEvent(val);
			if (assist7 != null && assist7.isOnline())
				assist7.setInArenaEvent(val);
			if (assist8 != null && assist8.isOnline())
				assist8.setInArenaEvent(val);
		}

		public void removeMessage()
		{
			if (leader != null && leader.isOnline())
			{
				leader.sendMessage("Tournament: Your participation has been removed.");
				leader.setArenaProtection(false);
				leader.setArena9x9(false);
			}

			if (assist != null && assist.isOnline())
			{
				assist.sendMessage("Tournament: Your participation has been removed.");
				assist.setArenaProtection(false);
				assist.setArena9x9(false);
			}

			if (assist2 != null && assist2.isOnline())
			{
				assist2.sendMessage("Tournament: Your participation has been removed.");
				assist2.setArenaProtection(false);
				assist2.setArena9x9(false);
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.sendMessage("Tournament: Your participation has been removed.");
				assist3.setArenaProtection(false);
				assist3.setArena9x9(false);
			}

			if (assist4 != null && assist4.isOnline())
			{
				assist4.sendMessage("Tournament: Your participation has been removed.");
				assist4.setArenaProtection(false);
				assist4.setArena9x9(false);
			}

			if (assist5 != null && assist5.isOnline())
			{
				assist5.sendMessage("Tournament: Your participation has been removed.");
				assist5.setArenaProtection(false);
				assist5.setArena9x9(false);
			}

			if (assist6 != null && assist6.isOnline())
			{
				assist6.sendMessage("Tournament: Your participation has been removed.");
				assist6.setArenaProtection(false);
				assist6.setArena9x9(false);
			}

			if (assist7 != null && assist7.isOnline())
			{
				assist7.sendMessage("Tournament: Your participation has been removed.");
				assist7.setArenaProtection(false);
				assist7.setArena9x9(false);
			}

			if (assist8 != null && assist8.isOnline())
			{
				assist8.sendMessage("Tournament: Your participation has been removed.");
				assist8.setArenaProtection(false);
				assist8.setArena9x9(false);
			}
		}

		public void setArenaProtection(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setArenaProtection(val);
				leader.setArena9x9(val);
			}

			if (assist != null && assist.isOnline())
			{
				assist.setArenaProtection(val);
				assist.setArena9x9(val);
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setArenaProtection(val);
				assist2.setArena9x9(val);
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.setArenaProtection(val);
				assist3.setArena9x9(val);
			}

			if (assist4 != null && assist4.isOnline())
			{
				assist4.setArenaProtection(val);
				assist4.setArena9x9(val);
			}

			if (assist5 != null && assist5.isOnline())
			{
				assist5.setArenaProtection(val);
				assist5.setArena9x9(val);
			}

			if (assist6 != null && assist6.isOnline())
			{
				assist6.setArenaProtection(val);
				assist6.setArena9x9(val);
			}

			if (assist7 != null && assist7.isOnline())
			{
				assist7.setArenaProtection(val);
				assist7.setArena9x9(val);
			}

			if (assist8 != null && assist8.isOnline())
			{
				assist8.setArenaProtection(val);
				assist8.setArena9x9(val);
			}
		}

		public void revive()
		{
			if (leader != null && leader.isOnline() && leader.isDead())
				leader.doRevive();
			if (assist != null && assist.isOnline() && assist.isDead())
				assist.doRevive();
			if (assist2 != null && assist2.isOnline() && assist2.isDead())
				assist2.doRevive();
			if (assist3 != null && assist3.isOnline() && assist3.isDead())
				assist3.doRevive();
			if (assist4 != null && assist4.isOnline() && assist4.isDead())
				assist4.doRevive();
			if (assist5 != null && assist5.isOnline() && assist5.isDead())
				assist5.doRevive();
			if (assist6 != null && assist6.isOnline() && assist6.isDead())
				assist6.doRevive();
			if (assist7 != null && assist7.isOnline() && assist7.isDead())
				assist7.doRevive();
			if (assist8 != null && assist8.isOnline() && assist8.isDead())
				assist8.doRevive();
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
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setIsInvul(val);
				assist2.setStopArena(val);
			}
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setIsInvul(val);
				assist3.setStopArena(val);
			}
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setIsInvul(val);
				assist4.setStopArena(val);
			}
			if (assist5 != null && assist5.isOnline())
			{
				assist5.setIsInvul(val);
				assist5.setStopArena(val);
			}
			if (assist6 != null && assist6.isOnline())
			{
				assist6.setIsInvul(val);
				assist6.setStopArena(val);
			}
			if (assist7 != null && assist7.isOnline())
			{
				assist7.setIsInvul(val);
				assist7.setStopArena(val);
			}
			if (assist8 != null && assist8.isOnline())
			{
				assist8.setIsInvul(val);
				assist8.setStopArena(val);
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

			if (assist2 != null && assist2.isOnline())
			{
				assist2.setArenaAttack(val);
				assist2.broadcastUserInfo();
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.setArenaAttack(val);
				assist3.broadcastUserInfo();
			}

			if (assist4 != null && assist4.isOnline())
			{
				assist4.setArenaAttack(val);
				assist4.broadcastUserInfo();
			}

			if (assist5 != null && assist5.isOnline())
			{
				assist5.setArenaAttack(val);
				assist5.broadcastUserInfo();
			}

			if (assist6 != null && assist6.isOnline())
			{
				assist6.setArenaAttack(val);
				assist6.broadcastUserInfo();
			}

			if (assist7 != null && assist7.isOnline())
			{
				assist7.setArenaAttack(val);
				assist7.broadcastUserInfo();
			}

			if (assist8 != null && assist8.isOnline())
			{
				assist8.setArenaAttack(val);
				assist8.broadcastUserInfo();
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

			if (assist2 != null && assist2.isOnline())
			{

				if (assist2.getSummon() != null)
				{
					Summon summon = assist2.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist2);
				}

				if (assist2.getMountType() == 1 || assist2.getMountType() == 2)
					assist2.dismount();
			}

			if (assist3 != null && assist3.isOnline())
			{

				if (assist3.getSummon() != null)
				{
					Summon summon = assist3.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist3);
				}
				if (assist3.getMountType() == 1 || assist3.getMountType() == 2)
					assist3.dismount();
			}

			if (assist4 != null && assist4.isOnline())
			{

				if (assist4.getSummon() != null)
				{
					Summon summon = assist4.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist4);
				}

				if (assist4.getMountType() == 1 || assist4.getMountType() == 2)
					assist4.dismount();
			}

			if (assist5 != null && assist5.isOnline())
			{

				if (assist5.getSummon() != null)
				{
					Summon summon = assist5.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist5);
				}

				if (assist5.getMountType() == 1 || assist5.getMountType() == 2)
					assist5.dismount();
			}

			if (assist6 != null && assist6.isOnline())
			{

				if (assist6.getSummon() != null)
				{
					Summon summon = assist6.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist6);
				}
				if (assist6.getMountType() == 1 || assist6.getMountType() == 2)
					assist6.dismount();
			}

			if (assist7 != null && assist7.isOnline())
			{

				if (assist7.getSummon() != null)
				{
					Summon summon = assist7.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist7);
				}

				if (assist7.getMountType() == 1 || assist7.getMountType() == 2)
					assist7.dismount();
			}

			if (assist8 != null && assist8.isOnline())
			{

				if (assist8.getSummon() != null)
				{
					Summon summon = assist8.getSummon();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					if (summon instanceof Pet)
						summon.unSummon(assist8);
				}

				if (assist8.getMountType() == 1 || assist8.getMountType() == 2)
					assist8.dismount();
			}
		}

		public void removeSkills()
		{
			if (leader.getClassId() != ClassId.SHILLIEN_ELDER && leader.getClassId() != ClassId.SHILLIEN_SAINT && leader.getClassId() != ClassId.BISHOP && leader.getClassId() != ClassId.CARDINAL && leader.getClassId() != ClassId.ELVEN_ELDER && leader.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : leader.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						leader.stopSkillEffects(effect.getSkill().getId());
			if (assist.getClassId() != ClassId.SHILLIEN_ELDER && assist.getClassId() != ClassId.SHILLIEN_SAINT && assist.getClassId() != ClassId.BISHOP && assist.getClassId() != ClassId.CARDINAL && assist.getClassId() != ClassId.ELVEN_ELDER && assist.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist.stopSkillEffects(effect.getSkill().getId());
			if (assist2.getClassId() != ClassId.SHILLIEN_ELDER && assist2.getClassId() != ClassId.SHILLIEN_SAINT && assist2.getClassId() != ClassId.BISHOP && assist2.getClassId() != ClassId.CARDINAL && assist2.getClassId() != ClassId.ELVEN_ELDER && assist2.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist2.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist2.stopSkillEffects(effect.getSkill().getId());
			if (assist3.getClassId() != ClassId.SHILLIEN_ELDER && assist3.getClassId() != ClassId.SHILLIEN_SAINT && assist3.getClassId() != ClassId.BISHOP && assist3.getClassId() != ClassId.CARDINAL && assist3.getClassId() != ClassId.ELVEN_ELDER && assist3.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist3.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist3.stopSkillEffects(effect.getSkill().getId());
			if (assist4.getClassId() != ClassId.SHILLIEN_ELDER && assist4.getClassId() != ClassId.SHILLIEN_SAINT && assist4.getClassId() != ClassId.BISHOP && assist4.getClassId() != ClassId.CARDINAL && assist4.getClassId() != ClassId.ELVEN_ELDER && assist4.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist4.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist4.stopSkillEffects(effect.getSkill().getId());
			if (assist5.getClassId() != ClassId.SHILLIEN_ELDER && assist5.getClassId() != ClassId.SHILLIEN_SAINT && assist5.getClassId() != ClassId.BISHOP && assist5.getClassId() != ClassId.CARDINAL && assist5.getClassId() != ClassId.ELVEN_ELDER && assist5.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist5.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist5.stopSkillEffects(effect.getSkill().getId());
			if (assist6.getClassId() != ClassId.SHILLIEN_ELDER && assist6.getClassId() != ClassId.SHILLIEN_SAINT && assist6.getClassId() != ClassId.BISHOP && assist6.getClassId() != ClassId.CARDINAL && assist6.getClassId() != ClassId.ELVEN_ELDER && assist6.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist6.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist6.stopSkillEffects(effect.getSkill().getId());
			if (assist7.getClassId() != ClassId.SHILLIEN_ELDER && assist7.getClassId() != ClassId.SHILLIEN_SAINT && assist7.getClassId() != ClassId.BISHOP && assist7.getClassId() != ClassId.CARDINAL && assist7.getClassId() != ClassId.ELVEN_ELDER && assist7.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist7.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist7.stopSkillEffects(effect.getSkill().getId());
			if (assist8.getClassId() != ClassId.SHILLIEN_ELDER && assist8.getClassId() != ClassId.SHILLIEN_SAINT && assist8.getClassId() != ClassId.BISHOP && assist8.getClassId() != ClassId.CARDINAL && assist8.getClassId() != ClassId.ELVEN_ELDER && assist8.getClassId() != ClassId.EVAS_SAINT)
				for (L2Effect effect : assist8.getAllEffects())
					if (Config.ARENA_STOP_SKILL_LIST.contains(Integer.valueOf(effect.getSkill().getId())))
						assist8.stopSkillEffects(effect.getSkill().getId());
		}

		public void sendPacket(String message, int duration)
		{
			if (leader != null && leader.isOnline())
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist != null && assist.isOnline())
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist2 != null && assist2.isOnline())
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist3 != null && assist3.isOnline())
				assist3.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist4 != null && assist4.isOnline())
				assist4.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist5 != null && assist5.isOnline())
				assist5.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist6 != null && assist6.isOnline())
				assist6.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist7 != null && assist7.isOnline())
				assist7.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			if (assist8 != null && assist8.isOnline())
				assist8.sendPacket(new ExShowScreenMessage(message, duration * 1000));
		}

		public void inicarContagem(int duration)
		{
			if (leader != null && leader.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(leader, duration), 0L);
			if (assist != null && assist.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist, duration), 0L);
			if (assist2 != null && assist2.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist2, duration), 0L);
			if (assist3 != null && assist3.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist3, duration), 0L);
			if (assist4 != null && assist4.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist4, duration), 0L);
			if (assist5 != null && assist5.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist5, duration), 0L);
			if (assist6 != null && assist6.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist6, duration), 0L);
			if (assist7 != null && assist7.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist7, duration), 0L);
			if (assist8 != null && assist8.isOnline())
				ThreadPool.schedule(new Arena9x9.countdown(assist8, duration), 0L);
		}

		public void sendPacketinit(String message, int duration)
		{
			if (leader != null && leader.isOnline())
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist != null && assist.isOnline())
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist2 != null && assist2.isOnline())
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist3 != null && assist3.isOnline())
				assist3.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist4 != null && assist4.isOnline())
				assist4.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist5 != null && assist5.isOnline())
				assist5.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist6 != null && assist6.isOnline())
				assist6.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist7 != null && assist7.isOnline())
				assist7.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
			if (assist8 != null && assist8.isOnline())
				assist8.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
		}
	}

	private class EvtArenaTask implements Runnable
	{
		private final Arena9x9.Pair pairOne;
		private final Arena9x9.Pair pairTwo;
		private final int pOneX;
		private final int pOneY;
		private final int pOneZ;
		private final int pTwoX;
		private final int pTwoY;
		private final int pTwoZ;
		private Arena9x9.Arena arena;

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
			pairOne.inicarContagem(Config.ARENA_WAIT_INTERVAL_9X9);
			pairTwo.inicarContagem(Config.ARENA_WAIT_INTERVAL_9X9);
			try
			{
				Thread.sleep(Config.ARENA_WAIT_INTERVAL_9X9 * 1000);
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
			}
			catch (InterruptedException e)
			{
			}

	        this.finishDuel();
            final Arena9x9 this$2 = Arena9x9.this;
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
				Player leader1 = pairOne.getLeader();
				Player leader2 = pairTwo.getLeader();

				if (leader1.getClan() != null && leader2.getClan() != null && Config.TOURNAMENT_EVENT_ANNOUNCE)
			
				World.announceToOnlinePlayers("[9x9]: (" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!");
				
				
				pairOne.rewards();
				pairTwo.rewardsLost();
			}
			else if (pairTwo.isAlive() && !pairOne.isAlive())
			{
				Player leader1 = pairTwo.getLeader();
				Player leader2 = pairOne.getLeader();

				if (leader1.getClan() != null && leader2.getClan() != null && Config.TOURNAMENT_EVENT_ANNOUNCE)
					
					
					World.announceToOnlinePlayers("[9x9]: (" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!");
				
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
			for (Arena9x9.Arena arena : arenas)
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
		protected static final Arena9x9 INSTANCE = new Arena9x9();
	}
}
