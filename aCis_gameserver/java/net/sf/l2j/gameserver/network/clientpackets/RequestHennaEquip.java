package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.data.xml.HennaData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.Henna;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

public final class RequestHennaEquip extends L2GameClientPacket
{
	private int _symbolId;
	
	@Override
	protected void readImpl()
	{
		_symbolId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Henna henna = HennaData.getInstance().getHenna(_symbolId);
		if (henna == null)
			return;
		
		if (!henna.canBeUsedBy(player))
		{
			player.sendPacket(SystemMessageId.CANT_DRAW_SYMBOL);
			return;
		}
		
		if (player.getHennaList().isFull())
		{
			player.sendPacket(SystemMessageId.SYMBOLS_FULL);
			return;
		}
		
		final ItemInstance ownedDyes = player.getInventory().getItemByItemId(henna.getDyeId());
		final int count = (ownedDyes == null) ? 0 : ownedDyes.getCount();
		
		if (count < Henna.DRAW_AMOUNT)
		{
			player.sendPacket(SystemMessageId.CANT_DRAW_SYMBOL);
			return;
		}
		
		// reduceAdena sends a message.
		if (!player.reduceAdena("Henna", henna.getDrawPrice(), player.getCurrentFolk(), true))
			return;
		
		// destroyItemByItemId sends a message.
		if (!player.destroyItemByItemId("Henna", henna.getDyeId(), Henna.DRAW_AMOUNT, player, true))
			return;
		
		final boolean success = player.getHennaList().add(henna);
		if (success)
		{
			player.sendPacket(new HennaInfo(player));
			player.sendPacket(new UserInfo(player));
			player.sendPacket(SystemMessageId.SYMBOL_ADDED);
		}
	}
}