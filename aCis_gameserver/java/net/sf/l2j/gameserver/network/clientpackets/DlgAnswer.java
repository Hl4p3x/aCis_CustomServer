package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.Announcements;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;

import Dev.Tournament.ArenaTask;

public final class DlgAnswer extends L2GameClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requesterId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
			player.reviveAnswer(_answer);
		else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
			
			if ((Announcements.isSummoning == true) && (_answer == 1))
				player.teleportTo(ArenaTask.loc1x(), ArenaTask.loc1y(), ArenaTask.loc1z(), 125);

		{   
			if (_answer == 1) 
			{
				for (Player allsplayer : World.getInstance().getPlayers())
					player.teleportTo(allsplayer.getX(), allsplayer.getY(), allsplayer.getZ(), 100);
			}
			else
				player.teleportAnswer(_answer, _requesterId);		         
		}
		if (_messageId == 1983 && Config.ALLOW_WEDDING)
			player.engageAnswer(_answer);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
			player.activateGate(_answer, 1);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
			player.activateGate(_answer, 0);
		else
			player.teleportAnswer(_answer, _requesterId);
	}
}