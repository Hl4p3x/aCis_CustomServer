package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.trade.ItemRequest;
import net.sf.l2j.gameserver.model.trade.TradeList;
import net.sf.l2j.gameserver.network.SystemMessageId;

public final class RequestPrivateStoreSell extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 20; // length of one item
	
	private int _storePlayerId;
	private ItemRequest[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_storePlayerId = readD();
		int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new ItemRequest[count];
		
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			int itemId = readD();
			int ench = readH();
			readH(); // TODO analyse this
			int cnt = readD();
			int price = readD();
			
			if (objectId < 1 || itemId < 1 || cnt < 1 || price < 0 || ench < 0 || ench > 65535)
			{
				_items = null;
				return;
			}
			_items[i] = new ItemRequest(objectId, itemId, cnt, price, ench);
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isCursedWeaponEquipped())
			return;
		
		final Player storePlayer = World.getInstance().getPlayer(_storePlayerId);
		if (storePlayer == null)
			return;
		
		if (!player.isInsideRadius(storePlayer, Npc.INTERACTION_DISTANCE, true, false))
			return;
		
		if (storePlayer.getOperateType() != OperateType.BUY)
			return;
		
		final TradeList storeList = storePlayer.getBuyList();
		if (storeList == null)
			return;
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (!storeList.privateStoreSell(player, _items))
			return;
		
		if (storeList.getItems().isEmpty())
		{
			storePlayer.setOperateType(OperateType.NONE);
			storePlayer.broadcastUserInfo();
		}
	}
}