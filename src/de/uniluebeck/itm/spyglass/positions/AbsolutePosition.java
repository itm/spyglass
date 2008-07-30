package de.uniluebeck.itm.spyglass.positions;

import org.simpleframework.xml.Attribute;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the coordinate's of a single point when
 * using absolute coordinates
 * 
 * @author Sebastian Ebers
 * 
 */
public class AbsolutePosition implements Cloneable {
	
	@Attribute
	/* The x-coordinate */
	public int x = 0;
	
	@Attribute
	/* The y-coordinate */
	public int y = 0;
	
	@Attribute
	/* The z-coordinate */
	public int z = 0;
	
	public AbsolutePosition() {
		
	}
	
	public AbsolutePosition(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public AbsolutePosition clone() {
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
	public AbsolutePosition mult(final double d) {
		return new AbsolutePosition((int) (x * d), (int) (y * d), (int) (z * d));
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AbsolutePosition add(final AbsolutePosition p) {
		return new AbsolutePosition(x + p.x, y + p.y, z + p.z);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Indicates whether some other object is "equal to" this one according to
	 * their coordinate information.
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> if this object is the same as the obj
	 *         argument; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		obj.equals(new Object());
		if (obj instanceof AbsolutePosition) {
			final AbsolutePosition other = (AbsolutePosition) obj;
			return ((other.x == this.x) && (other.y == this.y) && (other.z == this.z));
		}
		return false;
	}
	
}