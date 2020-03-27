package net.sf.l2j.gameserver.network.clientpackets;


import net.sf.l2j.gameserver.model.CharSelectInfoPackage;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.serverpackets.CharSelectInfo;
import net.sf.l2j.gameserver.network.serverpackets.SSQInfo;

import hwid.Hwid;

public final class CharacterRestore extends L2GameClientPacket
{
	private int _slot;
	
	@SuppressWarnings("unused")
	private int _unk1; // new in C4
	@SuppressWarnings("unused")
	private int _unk2; // new in C4
	@SuppressWarnings("unused")
	private int _unk3; // new in C4
	@SuppressWarnings("unused")
	private int _unk4; // new in C4
	
	@Override
	protected void readImpl()
	{
		_slot = readD();
		_unk1 = readH();
		_unk2 = readD();
		_unk3 = readD();
		_unk4 = readD();
	}
	

	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		if (!FloodProtectors.performAction(client, Action.CHARACTER_SELECT))
			return;
		
		// we should always be able to acquire the lock but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getActiveCharLock().tryLock())
			try
		{
				// should always be null but if not then this is repeated packet and nothing should be done here
				if (client.getActiveChar() == null)
				{
					final CharSelectInfoPackage info = client.getCharSelection(_slot);
					if (info == null || info.getAccessLevel() < 0)
						return;
					
					// Load up character from disk
					final Player cha = client.loadCharFromDisk(_slot);
					if (cha == null)
						return;
					
					cha.setClient(client);
					client.setActiveChar(cha);
					cha.setOnlineStatus(true, true);
					
					sendPacket(SSQInfo.sendSky());
					
					if (!Hwid.checkPlayerWithHWID(client, cha.getObjectId(), cha.getName()))
						return;
					
					getClient().markRestoredChar(_slot);
					
					final CharSelectInfo csi = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1, 0);
					sendPacket(csi);
					getClient().setCharSelectSlot(csi.getCharacterSlots());
				}
		}
			
			finally
		{
			client.getActiveCharLock().unlock();
		}
	}
}