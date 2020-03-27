package net.sf.l2j.gameserver;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

/**
 * @author Gabia
 *
 */
public class Announcements
{
	private static Announcements _instance;
	

	
	
	public static Announcements getInstance()
	{
		if (_instance == null)
		{
			_instance = new Announcements();
		}
		return _instance;
	}
	
	public static void announceToPlayers(String message)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			player.sendMessage(message);
		}
	}
	
	public static boolean isSummoning = false;
	
	@SuppressWarnings("null")
	public static void ArenaAnnounce(String text)
	{
		isSummoning = true;
		
		
		
		for (Player player : World.getInstance().getPlayers())
		{
			if ((player != null) && (player.isOnline()))
			{
				new CreatureSay(SayType.CRITICAL_ANNOUNCE, null, "[Tournament]: " + text);
			}
			if (Config.ARENA_MESSAGE_ENABLED)
			{
				if ((!player.isInsideZone(ZoneId.ARENA_EVENT)) && (!player.isDead()) && (!player.isInArenaEvent()) && (!player.isInStoreMode()) && (!player.isRooted()) && (player.getKarma() <= 0) && (!player.isInOlympiadMode())  && (!player.isFestivalParticipant()) && (Config.TOURNAMENT_EVENT_SUMMON))
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							Announcements.isSummoning = false;
						}
					}, 31000L);
					
					SpawnLocation _position = new SpawnLocation(Config.Tournament_locx + Rnd.get(-100, 100), Config.Tournament_locy + Rnd.get(-100, 100), Config.Tournament_locz, 0);
					
					ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
					confirm.addString("=== [Tournament]: (4 x 4) / (9 x 9) ====");
					confirm.addZoneName(_position);
					confirm.addTime(30000);
					confirm.addRequesterId(player.getObjectId());
					player.sendPacket(confirm);
				}
			}
		}
	}
	
	class Restore implements Runnable
	{
		Restore()
		{
		}
		
		@Override
		public void run()
		{
			Announcements.isSummoning = false;
		}
	}
}
