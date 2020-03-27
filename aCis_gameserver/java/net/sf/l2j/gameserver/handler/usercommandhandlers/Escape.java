package net.sf.l2j.gameserver.handler.usercommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		if (player.isCastingNow() || player.isSitting() || player.isMovementDisabled() || player.isOutOfControl() || player.isInOlympiadMode() || player.isInObserverMode() || player.isFestivalParticipant() || player.isInJail() || player.isInsideZone(ZoneId.BOSS))
		{
			player.sendPacket(SystemMessageId.NO_UNSTUCK_PLEASE_SEND_PETITION);
			return;
		}
		
		// Official timer 5 minutes, for GM 1 second
		if (player.isGM())
			player.doCast(SkillTable.getInstance().getInfo(2100, 1));
		else
		{
		    player.sendPacket(new PlaySound("systemmsg_e.809"));
		    int unstuckTimer = Config.UNSTUCK_TIME * 1000;
		   
		    L2Skill skill = SkillTable.getInstance().getInfo(2099, 1);
		    skill.setHitTime(unstuckTimer);
		    player.doCast(skill);

		    if (unstuckTimer < 60000)
		        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addString("You will unstuck in " + unstuckTimer / 1000 + " seconds."));
		    else
		        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2).addString("You will unstuck i " + unstuckTimer / 60000 + " minutes."));

		}
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}