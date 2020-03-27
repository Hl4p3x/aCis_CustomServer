package net.sf.l2j.gameserver.handler.itemhandlers;


import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.items.ShotType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class BeastSoulShots implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (playable == null)
			return;
		
		final Player player = playable.getActingPlayer();
		if (player == null)
			return;
		
		if (playable instanceof Summon)
		{
			player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
			return;
		}
		
		final Summon summon = player.getSummon();
		if (summon == null)
		{
			player.sendPacket(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return;
		}
		
		if (summon.isDead())
		{
			player.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
			return;
		}
		
		// SoulShots are already active.
		if (summon.isChargedShot(ShotType.SOULSHOT))
			return;
		
		   // If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
		   if (!Config.INFINITY_SS && player.destroyItemWithoutTrace("Consume", item.getObjectId(), summon.getSoulShotsPerHit(), null, false))
		   {
		       if (!player.disableAutoShot(item.getItemId()))
		    	   player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
		       return;
		   }
		   
		   if (!player.isSSDisabled())
			   summon.setChargedShot(ShotType.SOULSHOT, true);
		
		
		
		
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addItemName(item.getItemId()));
		summon.setChargedShot(ShotType.SOULSHOT, true);
		player.broadcastPacketInRadius(new MagicSkillUse(summon, summon, 2033, 1, 0, 0), 600);
	}
}