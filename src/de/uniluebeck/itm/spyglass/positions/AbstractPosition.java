package de.uniluebeck.itm.spyglass.positions;

import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Attribute;

// -------------------------------------------------------------------------------- /**
/**
 * This class represents an abstract position
 * 
 * @author Dariush Forouher, Sebastian Ebers
 * 
 */
public abstract class AbstractPosition implements Cloneable {
	@Attribute
	/* The x-coordinate */
	public int x = 0;

	@Attribute
	/* The y-coordinate */
	public int y = 0;

	@Attribute
	/* The z-coordinate */
	public int z = 0;

	public AbstractPosition() {

	}

	/**
	 * Create a new AbsolutePosition object based on a Point2D instance.
	 * 
	 * The z-coordinate is implicitly set to 0.
	 */
	public AbstractPosition(final Point2D point) {
		x = (int) Math.floor(point.getX());
		y = (int) Math.floor(point.getY());
		z = 0;
	}

	public AbstractPosition(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public AbstractPosition clone() {
		return new AbsolutePosition(x, y, z);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public abstract void mult(final double d);

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public abstract void add(final AbstractPosition p);

	// --------------------------------------------------------------------------------
	/**
	 * Indicates whether some other object is "equal to" this one according to their coordinate
	 * information.
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code>
	 *         otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		obj.equals(new Object());
		if (obj instanceof AbstractPosition) {
			final AbstractPosition other = (AbstractPosition) obj;
			return ((other.x == this.x) && (other.y == this.y) && (other.z == this.z));
		}
		return false;
	}

	@Override
	public int hashCode() {
		// TODO better hash method for fewer collisions
		return x + y + z;
	}

	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}

	public Point toPoint() {
		return new Point(x, y);
	}

}
