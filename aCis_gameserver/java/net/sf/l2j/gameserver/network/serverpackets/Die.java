package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.zone.type.MultiZone;

public class Die extends L2GameServerPacket
{
	private final Creature _creature;
	private final int _objectId;
	private final boolean _fake;
	private boolean _inZone;
	private boolean _sweepable;
	private boolean _allowFixedRes;
	private Clan _clan;
	private boolean _funEvent;
	
	public Die(Creature creature)
	{
		_creature = creature;
		_objectId = creature.getObjectId();
		_fake = !creature.isDead();
		
		if (creature instanceof Player)
		{
			Player player = (Player) creature;
			_allowFixedRes = player.getAccessLevel().allowFixedRes();
			_clan = player.getClan();
			_funEvent = !player.isInFunEvent();

			// multi zonas
			final MultiZone zone = ZoneManager.getInstance().getZone(player, MultiZone.class);
			_inZone = zone != null && zone.getRevive() > 0;
			
		}
		else if (creature instanceof Monster)
			_sweepable = ((Monster) creature).getSpoilState().isSweepable();
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_fake || _inZone)
			return;
		
		writeC(0x06);
		writeD(_objectId);
		writeD(_funEvent ? 0x01 : 0); // to nearest village
		
		if (_funEvent && _clan != null)
		{
			SiegeSide side = null;
			
			final Siege siege = CastleManager.getInstance().getActiveSiege(_creature);
			if (siege != null)
				side = siege.getSide(_clan);
			
			writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
			writeD((_clan.hasCastle() || side == SiegeSide.OWNER || side == SiegeSide.DEFENDER) ? 0x01 : 0x00); // to castle
			writeD((side == SiegeSide.ATTACKER && _clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
		}
		else
		{
			writeD(0x00); // to clanhall
			writeD(0x00); // to castle
			writeD(0x00); // to siege HQ
		}
		
		writeD((_sweepable) ? 0x01 : 0x00); // sweepable (blue glow)
		writeD((_allowFixedRes) ? 0x01 : 0x00); // FIXED
	}
}