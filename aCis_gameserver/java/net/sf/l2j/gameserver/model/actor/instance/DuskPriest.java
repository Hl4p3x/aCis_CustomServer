package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.enums.CabalType;
import net.sf.l2j.gameserver.enums.SealType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class DuskPriest extends SignsPriest
{
	public DuskPriest(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("Chat"))
			showChatWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		String filename = SevenSignsManager.SEVEN_SIGNS_HTML_PATH;
		
		final CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();
		
		switch (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()))
		{
			case DUSK:
				if (SevenSignsManager.getInstance().isCompResultsPeriod())
					filename += "dusk_priest_5.htm";
				else if (SevenSignsManager.getInstance().isRecruitingPeriod())
					filename += "dusk_priest_6.htm";
				else if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DUSK)
					{
						if (winningCabal != SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS))
							filename += "dusk_priest_2c.htm";
						else
							filename += "dusk_priest_2a.htm";
					}
					else if (winningCabal == CabalType.NORMAL)
						filename += "dusk_priest_2d.htm";
					else
						filename += "dusk_priest_2b.htm";
				}
				else
					filename += "dusk_priest_1b.htm";
				break;
			
			case DAWN:
				if (SevenSignsManager.getInstance().isSealValidationPeriod())
					filename += "dusk_priest_3a.htm";
				else
					filename += "dusk_priest_3b.htm";
				break;
			
			default:
				if (SevenSignsManager.getInstance().isCompResultsPeriod())
					filename += "dusk_priest_5.htm";
				else if (SevenSignsManager.getInstance().isRecruitingPeriod())
					filename += "dusk_priest_6.htm";
				else if (SevenSignsManager.getInstance().isSealValidationPeriod())
				{
					if (winningCabal == CabalType.DUSK)
						filename += "dusk_priest_4.htm";
					else if (winningCabal == CabalType.NORMAL)
						filename += "dusk_priest_2d.htm";
					else
						filename += "dusk_priest_2b.htm";
				}
				else
					filename += "dusk_priest_1a.htm";
				break;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
}