package net.sf.l2j.gameserver.model.clanhall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;

import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.SiegeStatus;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.entity.ClanHallSiege;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.zone.type.SiegableHallZone;
import net.sf.l2j.gameserver.model.zone.type.SiegeZone;

public final class SiegableHall extends ClanHall
{
	private static final String UPDATE_CLANHALL = "UPDATE clanhall SET ownerId=?, endDate=? WHERE id=?";
	
	private final long _siegeLength;
	private final int[] _scheduleConfig;
	
	private Calendar _nextSiege;
	
	private SiegeStatus _status = SiegeStatus.REGISTRATION_OPENED;
	private SiegeZone _siegeZone;
	
	private ClanHallSiege _siege;
	
	public SiegableHall(StatsSet set)
	{
		super(set);
		
		_siegeLength = set.getLong("siegeLength");
		_scheduleConfig = set.getIntegerArray("scheduleConfig");
		
		// Feed _siegeZone.
		for (SiegeZone zone : ZoneManager.getInstance().getAllZones(SiegeZone.class))
		{
			if (zone.getSiegableId() == getId())
			{
				_siegeZone = zone;
				break;
			}
		}
	}
	
	@Override
	public final void updateDb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CLANHALL))
		{
			ps.setInt(1, getOwnerId());
			ps.setLong(2, getNextSiegeTime());
			ps.setInt(3, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save SiegableHall.", e);
		}
	}
	
	@Override
	public SiegableHallZone getZone()
	{
		return (SiegableHallZone) super.getZone();
	}
	
	public final void setSiege(final ClanHallSiege siegable)
	{
		_siege = siegable;
	}
	
	public final ClanHallSiege getSiege()
	{
		return _siege;
	}
	
	public final Calendar getSiegeDate()
	{
		return _nextSiege;
	}
	
	public final long getSiegeLength()
	{
		return _siegeLength;
	}
	
	public final long getNextSiegeTime()
	{
		return _nextSiege.getTimeInMillis();
	}
	
	public final void setNextSiegeDate(long date)
	{
		_nextSiege.setTimeInMillis(date);
	}
	
	public final void setNextSiegeDate(final Calendar c)
	{
		_nextSiege = c;
	}
	
	public final void updateNextSiege()
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, _scheduleConfig[0]);
		c.add(Calendar.MONTH, _scheduleConfig[1]);
		c.add(Calendar.YEAR, _scheduleConfig[2]);
		c.set(Calendar.HOUR_OF_DAY, _scheduleConfig[3]);
		c.set(Calendar.MINUTE, _scheduleConfig[4]);
		c.set(Calendar.SECOND, 0);
		
		setNextSiegeDate(c);
		
		updateDb();
	}
	
	public final void addAttacker(final Clan clan)
	{
		if (getSiege() != null)
			getSiege().getAttackerClans().add(clan);
	}
	
	public final void removeAttacker(final Clan clan)
	{
		if (getSiege() != null)
			getSiege().getAttackerClans().remove(clan);
	}
	
	public final boolean isRegistered(Clan clan)
	{
		return getSiege() != null && getSiege().getAttackerClans().contains(clan);
	}
	
	public final SiegeStatus getSiegeStatus()
	{
		return _status;
	}
	
	public final boolean isRegistering()
	{
		return _status == SiegeStatus.REGISTRATION_OPENED;
	}
	
	public final boolean isInSiege()
	{
		return _status == SiegeStatus.IN_PROGRESS;
	}
	
	public final boolean isWaitingBattle()
	{
		return _status == SiegeStatus.REGISTRATION_OVER;
	}
	
	public final void updateSiegeStatus(SiegeStatus status)
	{
		_status = status;
	}
	
	public final SiegeZone getSiegeZone()
	{
		return _siegeZone;
	}
	
	public final void spawnDoor()
	{
		spawnDoor(false);
	}
	
	/**
	 * Respawn doors associated to that clan hall.
	 * @param isDoorWeak if true, spawn doors with 50% max HPs.
	 */
	public final void spawnDoor(boolean isDoorWeak)
	{
		for (Door door : getDoors())
		{
			if (door.isDead())
				door.doRevive();
			
			door.closeMe();
			door.setCurrentHp((isDoorWeak) ? door.getMaxHp() / 2 : door.getMaxHp());
			door.broadcastStatusUpdate();
		}
	}
}
