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

	public AbsolutePosition(final int x, final int y) {
		super(x, y, 0);
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
	public void mult(final double d) {
		x = this.mult(x, d);
		y = this.mult(y, d);
		z = this.mult(z, d);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void add(final AbstractPosition p) {
		x = this.add(x, p.x);
		y = this.add(y, p.y);
		z = this.add(z, p.z);
	}

	/**
	 * 
	 */
	public double getEuclideanDistance(final AbsolutePosition other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
	}

	public AbsolutePosition getDistanceVector(final AbsolutePosition other) {
		return new AbsolutePosition(other.x - this.x, other.y - this.y, other.z - this.z);
	}

	private int add(final int a, final int b) {
		int result = a + b;

		result = Math.min(result, (int) Math.pow(2, 15));
		result = Math.max(result, (int) -Math.pow(2, 15));

		return result;
	}

	private int mult(final int a, final double b) {
		int result = (int) Math.round(a * b);

		result = Math.min(result, (int) Math.pow(2, 15));
		result = Math.max(result, (int) -Math.pow(2, 15));

		return result;
	}

}