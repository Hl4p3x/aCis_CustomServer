package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.logging.Logger;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import Dev.PartyFarm.PartyFarm;


public class AdminPartyFarm implements IAdminCommandHandler 
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ptfarm"

	};
	
	protected static final Logger _log = Logger.getLogger(AdminPartyFarm.class.getName());
	public static boolean _bestfarm_manual = false;
	public static boolean _arena_manual = false;

	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{


		if (command.equals("admin_ptfarm"))
			if (PartyFarm._started)
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info("[Party Farm]: Event Finished.");
				_log.info("----------------------------------------------------------------------------");
				PartyFarm._aborted = true;
				finishEventPartyFarm();

				activeChar.sendMessage("SYS: Voce Finalizou o Party Farm Manualmente..");
			}
			else
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info("[Party Farm]: Event Started.");
				_log.info("----------------------------------------------------------------------------");
				initEventPartyFarm();
				_bestfarm_manual = true;
				activeChar.sendMessage("SYS: Voce ativou o Best Farm Manualmente..");
			}
		return true;
	}




	private static void initEventPartyFarm()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{

				PartyFarm.bossSpawnMonster();
			}
		}, 1L);
	}

	private static void finishEventPartyFarm()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{

				PartyFarm.Finish_Event();

			}
		}, 1L);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
