package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.handler.usercommandhandlers.ChangeTime;
import net.sf.l2j.gameserver.handler.usercommandhandlers.ChannelDelete;
import net.sf.l2j.gameserver.handler.usercommandhandlers.ChannelLeave;
import net.sf.l2j.gameserver.handler.usercommandhandlers.ChannelListUpdate;
import net.sf.l2j.gameserver.handler.usercommandhandlers.ClanPenalty;
import net.sf.l2j.gameserver.handler.usercommandhandlers.ClanWarsList;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Dismount;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Dressme;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Escape;
import net.sf.l2j.gameserver.handler.usercommandhandlers.GoldCoin;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Loc;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Menu;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Vote;

import Dev.AioMenu.VipMenu;

import net.sf.l2j.gameserver.handler.usercommandhandlers.Mount;
import net.sf.l2j.gameserver.handler.usercommandhandlers.OlympiadStat;
import net.sf.l2j.gameserver.handler.usercommandhandlers.PartyInfo;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Report;
import net.sf.l2j.gameserver.handler.usercommandhandlers.SiegeStatus;
import net.sf.l2j.gameserver.handler.usercommandhandlers.Time;

public class UserCommandHandler
{
	private final Map<Integer, IUserCommandHandler> _entries = new HashMap<>();
	
	protected UserCommandHandler()
	{
		registerHandler(new Dressme());
		registerHandler(new ChangeTime());
		registerHandler(new ChannelDelete());
		registerHandler(new ChannelLeave());
		registerHandler(new ChannelListUpdate());
		registerHandler(new ClanPenalty());
		registerHandler(new ClanWarsList());
		registerHandler(new Dismount());
		registerHandler(new Escape());
		registerHandler(new GoldCoin());
		registerHandler(new Loc());
		registerHandler(new Mount());
		registerHandler(new OlympiadStat());
		registerHandler(new PartyInfo());
		registerHandler(new Report());
		registerHandler(new SiegeStatus());
		registerHandler(new Time());
		registerHandler(new Vote());
		registerHandler(new Menu());
		registerHandler(new VipMenu());
	}
	
	private void registerHandler(IUserCommandHandler handler)
	{
		for (int id : handler.getUserCommandList())
			_entries.put(id, handler);
	}
	
	public IUserCommandHandler getHandler(int userCommand)
	{
		return _entries.get(userCommand);
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static UserCommandHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final UserCommandHandler INSTANCE = new UserCommandHandler();
	}
}