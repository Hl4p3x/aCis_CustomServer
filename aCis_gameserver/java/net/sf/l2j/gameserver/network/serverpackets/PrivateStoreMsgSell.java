package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.actor.Player;

public class PrivateStoreMsgSell extends L2GameServerPacket
{
	private final Player _player;
	private String _message;
	
	public PrivateStoreMsgSell(Player player)
	{
		_player = player;
		
		if (_player.getSellList() != null)
			_message = _player.getSellList().getTitle();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x9c);
		
		writeD(_player.getObjectId());
		writeS(_message);
	}
}