package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

public final class RequestDropItem extends L2GameClientPacket
{
	private int _objectId;
	private int _count;
	private int _x;
	private int _y;
	private int _z;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
		_x = readD();
		_y = readD();
		_z = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.DROP_ITEM))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null || player.isDead())
			return;
		
		if (player.isGM() && Config.GM_TRADE_RESTRICTED_ITEMS)
		{
			player.sendMessage("Drop item disabled for GM by config!");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final ItemInstance item = player.validateItemManipulation(_objectId);
		if (item == null || _count == 0 || (!Config.ALLOW_DISCARDITEM && !player.isGM()) || !item.isDropable())
		{
			player.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if (item.isQuestItem())
			return;
		
		if (_count > item.getCount())
		{
			player.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if (_count < 0)
			return;
		
		if (!item.isStackable() && _count > 1)
			return;
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (player.isProcessingTransaction() || player.isOperating())
		{
			player.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_2);
			return;
		}
		
		if (item.isAugmented())
		{
			player.sendPacket(SystemMessageId.AUGMENTED_ITEM_CANNOT_BE_DISCARDED);
			return;
		}
		
		if (player.isCastingNow())
		{
			if (player.getCurrentSkill().getSkill() != null && player.getCurrentSkill().getSkill().getItemConsumeId() == item.getItemId())
			{
				player.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}
		
		if (player.isCastingSimultaneouslyNow())
		{
			if (player.getLastSimultaneousSkillCast() != null && player.getLastSimultaneousSkillCast().getItemConsumeId() == item.getItemId())
			{
				player.sendPacket(SystemMessageId.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}
		
		if (Item.TYPE2_QUEST == item.getItem().getType2() && !player.isGM())
		{
			player.sendPacket(SystemMessageId.CANNOT_DISCARD_EXCHANGE_ITEM);
			return;
		}
		
		if (!player.isInsideRadius(_x, _y, 150, false) || Math.abs(_z - player.getZ()) > 50)
		{
			player.sendPacket(SystemMessageId.CANNOT_DISCARD_DISTANCE_TOO_FAR);
			return;
		}
		
		if (item.isEquipped() && (!item.isStackable() || (item.isStackable() && _count >= item.getCount())))
		{
			ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(item);
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance itm : unequipped)
			{
				itm.unChargeAllShots();
				iu.addModifiedItem(itm);
			}
			
			player.sendPacket(iu);
			player.broadcastUserInfo();
			player.sendPacket(new ItemList(player, true));
		}
		
		player.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false);
	}
}