package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class ArenaEventZone extends SpawnZoneType
{
	
	
	public ArenaEventZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.ARENA_EVENT, true);
		character.setInsideZone(ZoneId.PVP, true);
		if (character instanceof Player)
		{
			Player player = (Player) character;
			if (player.isArenaProtection())
			{
				if (player.getPvpFlag() > 0)
					PvpFlagTaskManager.getInstance().remove(player);
				player.updatePvPFlag(1);
				player.broadcastUserInfo();
			}
			player.sendPacket(new SystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
		}
	}


	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.ARENA_EVENT, false);
		character.setInsideZone(ZoneId.PVP, false);
		if (character instanceof Player)
		{
			Player player = (Player) character;

			player.updatePvPFlag(0);
			player.broadcastUserInfo();

			player.sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
		}
	}
	
	
}
