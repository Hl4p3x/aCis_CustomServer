package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.CharSelectSlot;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GameClient.GameClientState;
import net.sf.l2j.gameserver.network.serverpackets.CharSelected;
import net.sf.l2j.gameserver.network.serverpackets.SSQInfo;

public class RequestGameStart extends L2GameClientPacket
{
	private int _slot;
	
	@Override
	protected void readImpl()
	{
		_slot = readD();
		readH(); // Not used.
		readD(); // Not used.
		readD(); // Not used.
		readD(); // Not used.
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		if (!FloodProtectors.performAction(client, Action.CHARACTER_SELECT))
			return;
		
		// we should always be able to acquire the lock but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getActiveCharLock().tryLock())
		{
			try
			{
				// should always be null but if not then this is repeated packet and nothing should be done here
				if (client.getPlayer() == null)
				{
					final CharSelectSlot info = client.getCharSelectSlot(_slot);
					if (info == null || info.getAccessLevel() < 0)
						return;
					
					// Load up character from disk
					final Player player = client.loadCharFromDisk(_slot);
					if (player == null)
						return;
					
					player.setClient(client);
					client.setPlayer(player);
					player.setOnlineStatus(true, true);
					
					sendPacket(SSQInfo.sendSky());
					
					client.setState(GameClientState.ENTERING);
					
					sendPacket(new CharSelected(player, client.getSessionId().playOkID1));
				}
			}
			finally
			{
				client.getActiveCharLock().unlock();
			}
		}
	}
}