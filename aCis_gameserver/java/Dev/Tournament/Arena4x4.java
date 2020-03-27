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


public class Arena4x4 implements Runnable
{
	public static List<Pair> registered;
	int free = Config.ARENA_EVENT_COUNT_4X4;

	Arena[] arenas = new Arena[Config.ARENA_EVENT_COUNT_4X4];

	Map<Integer, String> fights = new HashMap<>(Config.ARENA_EVENT_COUNT_4X4);

	public Arena4x4()
	{
		registered = new ArrayList<>();

		for (int i = 0; i < Config.ARENA_EVENT_COUNT_4X4; i++)
		{
			int[] coord = Config.ARENA_EVENT_LOCS_4X4[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
	}

	public static Arena4x4 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	public boolean register(Player player, Player assist, Player assist2, Player assist3)
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
		}
		return registered.add(new Pair(player, assist, assist2, assist3));
	}

	public boolean isRegistered(Player player)
	{
		for (Pair p : registered)
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player || p.getAssist3() == player)
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
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player || p.getAssist3() == player)
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
		Player assist2;
		Player assist3;

		public Pair(Player leader, Player assist, Player assist2, Player assist3)
		{
			this.leader = leader;
			this.assist = assist;
			this.assist2 = assist2;
			this.assist3 = assist3;
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
				return false;
			}
			if ((assist == null || !assist.isOnline() || assist2 == null || !assist2.isOnline() || assist3 == null || !assist3.isOnline()) && leader != null && leader.isOnline())
			{
				leader.sendMessage("Tournament: You participation in Event was Canceled.");

				if (assist != null || assist.isOnline())
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist2 != null || assist2.isOnline())
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				if (assist3 != null || assist3.isOnline())
					assist3.sendMessage("Tournament: You participation in Event was Canceled.");
				return false;
			}
			return true;
		}

		public boolean isDead()
		{
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !assist2.isOnline() || !assist2.isInsideZone(ZoneId.ARENA_EVENT) || !assist2.isArenaAttack()) && (assist3 == null || assist3.isDead() || !assist3.isOnline() || !assist3.isInsideZone(ZoneId.ARENA_EVENT) || !assist3.isArenaAttack()))
				return false;
			return !leader.isDead() || !assist.isDead() || !assist2.isDead() || !assist3.isDead();
		}

		public boolean isAlive()
		{
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !assist2.isOnline() || !assist2.isInsideZone(ZoneId.ARENA_EVENT) || !assist2.isArenaAttack()) && (assist3 == null || assist3.isDead() || !assist3.isOnline() || !assist3.isInsideZone(ZoneId.ARENA_EVENT) || !assist3.isArenaAttack()))
				return false;
			return !leader.isDead() || !assist.isDead() || !assist2.isDead() || !assist3.isDead();
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
					assist.setLastCords(x, y + 50, z);
					assist.leaveOlympiadObserverMode();
				}
				else
					assist.teleportTo(x, y + 50, z, 0);
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
					assist2.setLastCords(x, y - 100, z);
					assist2.leaveOlympiadObserverMode();
				}
				else
					assist2.teleportTo(x, y - 100, z, 0);
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
					assist3.setLastCords(x, y - 50, z);
					assist3.leaveOlympiadObserverMode();
				}
				else
					assist3.teleportTo(x, y - 50, z, 0);
				assist3.broadcastUserInfo();
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
		}

		public void rewards()
		{
			if (leader != null && leader.isOnline())
				
		
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_4X4, leader, true);

			if (assist != null && assist.isOnline())
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_4X4, assist, true);
			
			if (assist2 != null && assist2.isOnline())
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_4X4, assist2, true);
			
			if (assist3 != null && assist3.isOnline())
				assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_4X4, assist3, true);
			sendPacket("Congratulations, your team won the event!", 5);
		}

		public void rewardsLost()
		{
			if (leader != null && leader.isOnline())

				leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_4X4, leader, true);
			if (assist != null && assist.isOnline())

				assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_4X4, assist, true);
			if (assist2 != null && assist2.isOnline())

				assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_4X4, assist2, true);
			if (assist3 != null && assist3.isOnline())
				assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_4X4, assist3, true);
			
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
		}

		public void removeMessage()
		{
			if (leader != null && leader.isOnline())
			{
				leader.sendMessage("Tournament: Your participation has been removed.");
				leader.setArenaProtection(false);
				leader.setArena4x4(false);
			}

			if (assist != null && assist.isOnline())
			{
				assist.sendMessage("Tournament: Your participation has been removed.");
				assist.setArenaProtection(false);
				assist.setArena4x4(false);
			}

			if (assist2 != null && assist2.isOnline())
			{
				assist2.sendMessage("Tournament: Your participation has been removed.");
				assist2.setArenaProtection(false);
				assist2.setArena4x4(false);
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.sendMessage("Tournament: Your participation has been removed.");
				assist3.setArenaProtection(false);
				assist3.setArena4x4(false);
			}
		}

		public void setArenaProtection(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setArenaProtection(val);
				leader.setArena4x4(val);
			}

			if (assist != null && assist.isOnline())
			{
				assist.setArenaProtection(val);
				assist.setArena4x4(val);
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setArenaProtection(val);
				assist2.setArena4x4(val);
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.setArenaProtection(val);
				assist3.setArena4x4(val);
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
		}

		public void inicarContagem(int duration)
		{
			if (leader != null && leader.isOnline())
				ThreadPool.schedule(new Arena4x4.countdown(leader, duration), 0L);
			if (assist != null && assist.isOnline())
				ThreadPool.schedule(new Arena4x4.countdown(assist, duration), 0L);
			if (assist2 != null && assist2.isOnline())
				ThreadPool.schedule(new Arena4x4.countdown(assist2, duration), 0L);
			if (assist3 != null && assist3.isOnline())
				ThreadPool.schedule(new Arena4x4.countdown(assist3, duration), 0L);
		}

		public void sendPacketinit(String message, int duration)
		{
			if (leader != null && leader.isOnline())
			{
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));

				if ((leader.getClassId() == ClassId.SHILLIEN_ELDER || leader.getClassId() == ClassId.SHILLIEN_SAINT || leader.getClassId() == ClassId.BISHOP || leader.getClassId() == ClassId.CARDINAL || leader.getClassId() == ClassId.ELVEN_ELDER || leader.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_4X4 == 0)
					ThreadPool.schedule(new Runnable()
					{

						@Override
						public void run()
						{
							leader.getClient().closeNow();
						}
					}, 100L);
			}

			if (assist != null && assist.isOnline())
			{
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));

				if ((assist.getClassId() == ClassId.SHILLIEN_ELDER || assist.getClassId() == ClassId.SHILLIEN_SAINT || assist.getClassId() == ClassId.BISHOP || assist.getClassId() == ClassId.CARDINAL || assist.getClassId() == ClassId.ELVEN_ELDER || assist.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_4X4 == 0)
					ThreadPool.schedule(new Runnable()
					{

						@Override
						public void run()
						{
							assist.getClient().closeNow();
						}
					}, 100L);
			}

			if (assist2 != null && assist2.isOnline())
			{
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				if ((assist2.getClassId() == ClassId.SHILLIEN_ELDER || assist2.getClassId() == ClassId.SHILLIEN_SAINT || assist2.getClassId() == ClassId.BISHOP || assist2.getClassId() == ClassId.CARDINAL || assist2.getClassId() == ClassId.ELVEN_ELDER || assist2.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_4X4 == 0)
					ThreadPool.schedule(new Runnable()
					{

						@Override
						public void run()
						{
							assist2.getClient().closeNow();
						}
					}, 100L);
			}

			if (assist3 != null && assist3.isOnline())
			{
				assist3.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				if ((assist3.getClassId() == ClassId.SHILLIEN_ELDER || assist3.getClassId() == ClassId.SHILLIEN_SAINT || assist3.getClassId() == ClassId.BISHOP || assist3.getClassId() == ClassId.CARDINAL || assist3.getClassId() == ClassId.ELVEN_ELDER || assist3.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_4X4 == 0)
					ThreadPool.schedule(new Runnable()
					{

						@Override
						public void run()
						{
							assist3.getClient().closeNow();
						}
					}, 100L);
			}
		}
	}

	private class EvtArenaTask implements Runnable
	{
		private final Arena4x4.Pair pairOne;
		private final Arena4x4.Pair pairTwo;
		private final int pOneX;
		private final int pOneY;
		private final int pOneZ;
		private final int pTwoX;
		private final int pTwoY;
		private final int pTwoZ;
		private Arena4x4.Arena arena;

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
			pairOne.inicarContagem(Config.ARENA_WAIT_INTERVAL_4X4);
			pairTwo.inicarContagem(Config.ARENA_WAIT_INTERVAL_4X4);
			try
			{
				Thread.sleep(Config.ARENA_WAIT_INTERVAL_4X4 * 1000);
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
            final Arena4x4 this$2 = Arena4x4.this;
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

				World.announceToOnlinePlayers("[4x4]: (" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!");
				pairOne.rewards();
				pairTwo.rewardsLost();
			}
			else if (pairTwo.isAlive() && !pairOne.isAlive())
			{
				Player leader1 = pairTwo.getLeader();
				Player leader2 = pairOne.getLeader();

				if (leader1.getClan() != null && leader2.getClan() != null && Config.TOURNAMENT_EVENT_ANNOUNCE)
	
				World.announceToOnlinePlayers("[4x4]: (" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!");
				
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
			for (Arena4x4.Arena arena : arenas)
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
		protected static final Arena4x4 INSTANCE = new Arena4x4();
	}
}
