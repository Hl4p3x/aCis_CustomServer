package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.trade.TradeList;

public class TradeStart extends L2GameServerPacket
{
	private final ItemInstance[] _items;
	private final TradeList _tradeList;
	
	public TradeStart(Player player)
	{
		_items = player.getInventory().getAvailableItems(true, false, false);
		_tradeList = player.getActiveTradeList();
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_tradeList == null || _tradeList.getPartner() == null)
			return;
		
		writeC(0x1E);
		writeD(_tradeList.getPartner().getObjectId());
		writeH(_items.length);
		
		for (ItemInstance temp : _items)
		{
			final Item item = temp.getItem();
			
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(temp.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
			writeD(item.getBodyPart());
			writeH(temp.getEnchantLevel());
			writeH(temp.getCustomType2());
			writeH(0x00);
		}
	}
}