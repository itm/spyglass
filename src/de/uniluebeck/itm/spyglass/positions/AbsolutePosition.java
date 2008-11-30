package de.uniluebeck.itm.spyglass.positions;

import java.awt.geom.Point2D;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the coordinate's of a single point when using absolute
 * coordinates
 * 
 * @author Sebastian Ebers
 * 
 */
public class AbsolutePosition extends AbstractPosition {

	public AbsolutePosition() {

	}

	/**
	 * Create a new AbsolutePosition object based on a Point2D instance.
	 * 
	 * The z-coordinate is implicitly set to 0.
	 */
	public AbsolutePosition(final Point2D point) {
		super(point);
	}

	public AbsolutePosition(final int x, final int y, final int z) {
		super(x, y, z);
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

	/**
	 * 
	 */
	public double getDistance(final AbsolutePosition other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
	}

}