package net.sf.l2j.gameserver.model.location;

import java.util.Objects;

import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.model.WorldObject;

/**
 * A datatype extending {@link Location}, wildly used as character position, since it also stores heading of the character.
 */
public class SpawnLocation extends Location
{
	public static final SpawnLocation DUMMY_SPAWNLOC = new SpawnLocation(0, 0, 0, 0);
	
	protected volatile int _heading;
	
	public SpawnLocation(int x, int y, int z, int heading)
	{
		super(x, y, z);
		
		_heading = heading;
	}
	
	public SpawnLocation(SpawnLocation loc)
	{
		super(loc.getX(), loc.getY(), loc.getZ());
		
		_heading = loc.getHeading();
	}
	
	@Override
	public String toString()
	{
		return _x + ", " + _y + ", " + _z + ", " + _heading;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(_heading);
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (!super.equals(obj))
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final SpawnLocation other = (SpawnLocation) obj;
		return _heading == other._heading;
	}
	
	@Override
	public void clean()
	{
		super.set(0, 0, 0);
		
		_heading = 0;
	}
	
	public int getHeading()
	{
		return _heading;
	}
	
	public void setHeading(int heading)
	{
		_heading = heading;
	}
	
	/**
	 * Set the heading of this {@link SpawnLocation} to face a 2D point.
	 * @param targetX : The X target to face.
	 * @param targetY : The Y target to face.
	 */
	public void setHeadingTo(int targetX, int targetY)
	{
		_heading = MathUtil.calculateHeadingFrom(_x, _y, targetX, targetY);
	}
	
	/**
	 * Set the heading of this {@link SpawnLocation} to face a {@link WorldObject}.
	 * @param object : The WorldObject to face.
	 * @see #setHeadingTo(int, int)
	 */
	public void setHeadingTo(WorldObject object)
	{
		setHeadingTo(object.getX(), object.getY());
	}
	
	/**
	 * Set the heading of this {@link SpawnLocation} to face a {@link Location}.
	 * @param loc : The Location to face.
	 * @see #setHeadingTo(int, int)
	 */
	public void setHeadingTo(Location loc)
	{
		setHeadingTo(loc.getX(), loc.getY());
	}
	
	public void set(int x, int y, int z, int heading)
	{
		super.set(x, y, z);
		
		_heading = heading;
	}
	
	public void set(SpawnLocation loc)
	{
		super.set(loc.getX(), loc.getY(), loc.getZ());
		
		_heading = loc.getHeading();
	}
	
	/**
	 * @param offset : The offset to add.
	 * @return a new {@link Location} object based on current {@link SpawnLocation} and a given offset.
	 */
	public Location getLocationWithOffset(int offset)
	{
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(_heading));
		
		final int x = _x + (int) (Math.cos(radian) * offset);
		final int y = _y + (int) (Math.sin(radian) * offset);
		
		return new Location(x, y, _z);
	}
}