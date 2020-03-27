package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.actor.Player;

public class RecipeShopMsg extends L2GameServerPacket
{
	private final Player _player;
	
	public RecipeShopMsg(Player player)
	{
		_player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xdb);
		
		writeD(_player.getObjectId());
		writeS(_player.getManufactureList().getStoreName());
	}
}