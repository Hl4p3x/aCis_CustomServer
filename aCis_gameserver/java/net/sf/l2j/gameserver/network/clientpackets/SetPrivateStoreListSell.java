package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.enums.actors.RestrictionType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.trade.TradeList;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreManageListSell;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;

public final class SetPrivateStoreListSell extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 12; // length of the one item
	
	private boolean _packageSale;
	private Item[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_packageSale = (readD() == 1);
		int count = readD();
		if (count < 1 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new Item[count];
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			int cnt = readD();
			int price = readD();
			
			if (itemId < 1 || cnt < 1 || price < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new Item(itemId, cnt, price);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final TradeList tradeList = player.getSellList();
		tradeList.clear();

		if (Config.STORE_RESTRICTION_TYPE == RestrictionType.PVP && player.getPvpKills() < Config.MIN_PVP_TO_USE_STORE)
		{
			player.sendMessage("Você deve ter pelo menos " + Config.MIN_PVP_TO_USE_STORE + " (PVP) para uma abrir loja particular.");
			return;
		}
		
		if (Config.STORE_RESTRICTION_TYPE == RestrictionType.PK && player.getPkKills() < Config.MIN_PK_TO_USE_STORE)
		{
			player.sendMessage("Você deve ter pelo menos " + Config.MIN_PK_TO_USE_STORE + " (PK) para uma abrir loja particular.");
			return;
		}
		
		if (Config.STORE_RESTRICTION_TYPE == RestrictionType.LEVEL && player.getLevel() < Config.MIN_LEVEL_TO_USE_STORE)
		{
			player.sendMessage("Você deve ter pelo menos " + Config.MIN_LEVEL_TO_USE_STORE + " (LEVEL) para uma abrir loja particular.");
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		// Check multiple conditions. Message is sent directly from the method.
		if (!player.canOpenPrivateStore(false))
			return;
		
		// Integrity check.
		if (_items == null)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
			player.sendPacket(new PrivateStoreManageListSell(player, _packageSale));
			return;
		}
		
		// Check maximum number of allowed slots for pvt shops
		if (_items.length > player.getPrivateSellStoreLimit())
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
			player.sendPacket(new PrivateStoreManageListSell(player, _packageSale));
			return;
		}
		
		tradeList.setPackaged(_packageSale);
		
		int totalCost = player.getAdena();
		for (Item i : _items)
		{
			if (!i.addToTradeList(tradeList))
			{
				player.sendPacket(SystemMessageId.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListSell(player, _packageSale));
				return;
			}
			
			totalCost += i.getPrice();
			if (totalCost > Integer.MAX_VALUE)
			{
				player.sendPacket(SystemMessageId.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListSell(player, _packageSale));
				return;
			}
		}
		
		player.sitDown(true);
		player.setOperateType((_packageSale) ? OperateType.PACKAGE_SELL : OperateType.SELL);
		player.broadcastUserInfo();
		player.broadcastPacket(new PrivateStoreMsgSell(player));
	}
	
	private static class Item
	{
		private final int _itemId, _count, _price;
		
		public Item(int id, int num, int pri)
		{
			_itemId = id;
			_count = num;
			_price = pri;
		}
		
		public boolean addToTradeList(TradeList list)
		{
			if ((Integer.MAX_VALUE / _count) < _price)
				return false;
			
			list.addItem(_itemId, _count, _price);
			return true;
		}
		
		public long getPrice()
		{
			return _count * _price;
		}
	}
}