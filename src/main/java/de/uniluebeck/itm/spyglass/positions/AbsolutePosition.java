/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
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

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public AbsolutePosition() {
		// nothing to do
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create a new AbsolutePosition object based on a Point2D instance.
	 * 
	 * The z-coordinate is implicitly set to 0.
	 * 
	 * @param point
	 *            a Point2D instance
	 */
	public AbsolutePosition(final Point2D point) {
		super(point);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param z
	 *            z-coordinate
	 */
	public AbsolutePosition(final int x, final int y, final int z) {
		super(x, y, z);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	public AbsolutePosition(final int x, final int y) {
		super(x, y, 0);
	}

	// --------------------------------------------------------------------------------
	@Override
	public AbsolutePosition clone() {
		return new AbsolutePosition(x, y, z);
	}

	// --------------------------------------------------------------------------------
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

	// --------------------------------------------------------------------------------
	/**
	 * Returns the Euclidean distance between this instance and another position
	 * 
	 * @param other
	 *            another position
	 * @return the Euclidean distance between this instance and another position
	 */
	public double getEuclideanDistance(final AbsolutePosition other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the distance between this instance and another position as a vector
	 * 
	 * @param other
	 *            another position
	 * @return the distance between this instance and another position as a vector
	 */
	public AbsolutePosition getDistanceVector(final AbsolutePosition other) {
		return new AbsolutePosition(other.x - this.x, other.y - this.y, other.z - this.z);
	}

	// --------------------------------------------------------------------------------
	private int add(final int a, final int b) {
		int result = a + b;

		result = Math.min(result, (int) Math.pow(2, 15));
		result = Math.max(result, (int) -Math.pow(2, 15));

		return result;
	}

	// --------------------------------------------------------------------------------
	private int mult(final int a, final double b) {
		int result = (int) Math.round(a * b);

		result = Math.min(result, (int) Math.pow(2, 15));
		result = Math.max(result, (int) -Math.pow(2, 15));

		return result;
	}

}