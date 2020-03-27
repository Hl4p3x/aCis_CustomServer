package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExConfirmVariationGemstone;

public final class RequestConfirmGemStone extends AbstractRefinePacket
{
	private int _targetItemObjId;
	private int _refinerItemObjId;
	private int _gemstoneItemObjId;
	private int _gemStoneCount;
	
	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
		_gemstoneItemObjId = readD();
		_gemStoneCount = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final ItemInstance targetItem = player.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
			return;
		
		final ItemInstance refinerItem = player.getInventory().getItemByObjectId(_refinerItemObjId);
		if (refinerItem == null)
			return;
		
		final ItemInstance gemStoneItem = player.getInventory().getItemByObjectId(_gemstoneItemObjId);
		if (gemStoneItem == null)
			return;
		
		// Make sure the item is a gemstone
		if (!isValid(player, targetItem, refinerItem, gemStoneItem))
		{
			player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		// Check for gemstone count
		final LifeStone ls = getLifeStone(refinerItem.getItemId());
		if (ls == null)
			return;
		
		if (_gemStoneCount != getGemStoneCount(targetItem.getItem().getCrystalType()))
		{
			player.sendPacket(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT);
			return;
		}
		
		player.sendPacket(new ExConfirmVariationGemstone(_gemstoneItemObjId, _gemStoneCount));
	}
}