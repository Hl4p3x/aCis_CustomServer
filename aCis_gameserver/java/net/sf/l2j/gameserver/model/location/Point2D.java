package net.sf.l2j.gameserver.model.location;

import java.util.Objects;

import net.sf.l2j.gameserver.model.actor.Creature;

/**
 * A datatype used to retain a 2D (x/y) point. It got the capability to be set and cleaned.
 */
public class Point2D
{
	protected volatile int _x;
	protected volatile int _y;
	
	public Point2D(int x, int y)
	{
		_x = x;
		_y = y;
	}
	
	@Override
	public String toString()
	{
		return _x + ", " + _y;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(_x, _y);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final Point2D other = (Point2D) obj;
		return _x == other._x && _y == other._y;
	}
	
	/**
	 * @param x : The X coord to test.
	 * @param y : The Y coord to test.
	 * @return true if all coordinates equals this {@link Point2D} coordinates.
	 */
	public boolean equals(int x, int y)
	{
		return _x == x && _y == y;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public void setX(int x)
	{
		_x = x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public void setY(int y)
	{
		_y = y;
	}
	
	public void set(int x, int y)
	{
		_x = x;
		_y = y;
	}
	
	/**
	 * Refresh the current {@link Point2D} using two {@link Point2D}s and an offset. The new destination is calculated to go in opposite side of the Point2D reference.<br>
	 * <br>
	 * This method is perfect to calculate fleeing characters location.
	 * @param fugitive : The Creature used as initial Location.
	 * @param attacker : The Creature used as Point2D reference.
	 * @param offset : The offset used to impact X and Y.
	 * @see #setFleeing(Point2D, Point2D, int)
	 */
	public void setFleeing(Creature fugitive, Creature attacker, int offset)
	{
		setFleeing(fugitive.getPosition(), attacker.getPosition(), offset);
	}
	
	/**
	 * Refresh the current {@link Point2D} using two {@link Point2D}s and an offset. The new destination is calculated to go in opposite side of the Point2D reference.<br>
	 * <br>
	 * This method is perfect to calculate fleeing characters location.
	 * @param initialLoc : The initial Point2D position.
	 * @param referenceLoc : The Point2D used as reference.
	 * @param offset : The offset used to impact X and Y.
	 */
	public void setFleeing(Point2D initialLoc, Point2D referenceLoc, int offset)
	{
		_x = initialLoc.getX() + ((referenceLoc.getX() < _x) ? offset : -offset);
		_y = initialLoc.getY() + ((referenceLoc.getY() < _y) ? offset : -offset);
	}
	
	public void clean()
	{
		_x = 0;
		_y = 0;
	}
	
	/**
	 * @param x : The X position to test.
	 * @param y : The Y position to test.
	 * @return the distance between this {@Point2D} and some given coordinates.
	 */
	public double distance(int x, int y)
	{
		double dx = (double) _x - x;
		double dy = (double) _y - y;
		
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * @param point : The Point2D to test.
	 * @return the distance between this {@Point2D} and the Point2D set as parameter.
	 */
	public double distance(Point2D point)
	{
		return distance(point.getX(), point.getY());
	}
	
	/**
	 * @param x : The X position to test.
	 * @param y : The Y position to test.
	 * @param radius : The radius to check.
	 * @return true if this {@link Point2D} is in the radius of some given coordinates.
	 */
	public boolean isInRadius(int x, int y, int radius)
	{
		return distance(x, y) < radius;
	}
	
	/**
	 * @param point : The Point2D to test.
	 * @param radius : The radius to check.
	 * @return true if this {@link Point2D} is in the radius of the Point2D set as parameter.
	 */
	public boolean isInRadius(Point2D point, int radius)
	{
		return distance(point) < radius;
	}
}