package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.model.actor.Player;

public final class RequestPrivateStoreQuitBuy extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.setOperateType(OperateType.NONE);
		player.broadcastUserInfo();
	}
}