package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public final class RequestPrivateStoreManageSell extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;

		if (player.isGM() && Config.GM_TRADE_RESTRICTED_ITEMS)
		{
			player.sendMessage("Gm private store disabled by config!");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.tryOpenPrivateSellStore(false);
	}
}