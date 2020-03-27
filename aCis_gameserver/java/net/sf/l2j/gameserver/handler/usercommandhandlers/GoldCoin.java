package net.sf.l2j.gameserver.handler.usercommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;

/**
 * @author Williams
 *
 */
public class GoldCoin implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		115, 116
	};
	
	@Override
	public void useUserCommand(int id, Player activeChar)
	{
		if (id == 118)
		{
			for (IntIntHolder gold : Config.BANKING_SYSTEM_GOLDCOIN)
			{
				if (activeChar.getInventory().getInventoryItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA)
				{
					activeChar.reduceAdena("Gold Coin", Config.BANKING_SYSTEM_ADENA, activeChar, true);
					activeChar.addItem("Gold Coin", gold.getId(), gold.getValue(), activeChar, true);
				}
				else
					activeChar.sendMessage("Você não tem Adena suficiente para converter para "+  ItemData.getInstance().getTemplate(gold.getId()).getName() +"(s), você precisa de " + Config.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		else if (id == 119)
		{
			// If player hasn't enough space for adena
			final long a = activeChar.getInventory().getInventoryItemCount(57, 0);
			final long b = Config.BANKING_SYSTEM_ADENA;
			if (a + b > Integer.MAX_VALUE)
			{
				activeChar.sendMessage("Você não tem espaço suficiente para toda a adena no inventário!");
				return;
			}
						
			for (IntIntHolder gold : Config.BANKING_SYSTEM_GOLDCOIN)
			{
				if (activeChar.getInventory().getInventoryItemCount(gold.getId(), 0) >= gold.getValue())
				{
					activeChar.destroyItemByItemId("Adena", gold.getId(), gold.getValue(), activeChar, true);
					activeChar.addAdena("Adena", Config.BANKING_SYSTEM_ADENA, activeChar, true);
				}
				else
					activeChar.sendMessage("Você não tem nenhum "+ ItemData.getInstance().getTemplate(gold.getId()).getName() +" para trocar por " + Config.BANKING_SYSTEM_ADENA + " de Adena.");
			}
		}
		
		return;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}