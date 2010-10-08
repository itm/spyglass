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

import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Attribute;

// --------------------------------------------------------------------------------
/**
 * This class represents an abstract position
 * 
 * @author Dariush Forouher, Sebastian Ebers
 * 
 */
public abstract class AbstractPosition implements Cloneable {
	/** The x-coordinate */
	@Attribute
	public int x = 0;

	/** The y-coordinate */
	@Attribute
	public int y = 0;

	/** The z-coordinate */
	@Attribute
	public int z = 0;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public AbstractPosition() {
		// nothing to do here
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
	public AbstractPosition(final Point2D point) {
		x = (int) Math.floor(point.getX());
		y = (int) Math.floor(point.getY());
		z = 0;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            the position's x-coordinate
	 * @param y
	 *            the position's y-coordinate
	 * @param z
	 *            the position's z-coordinate
	 * 
	 */
	public AbstractPosition(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// --------------------------------------------------------------------------------
	@Override
	public AbstractPosition clone() {
		return new AbsolutePosition(x, y, z);
	}

	// --------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	// --------------------------------------------------------------------------------
	/**
	 * Multiplies a value to this position
	 * 
	 * @param value
	 *            the value to be multiplied to this position
	 */
	public abstract void mult(final double value);

	// --------------------------------------------------------------------------------
	/**
	 * Adds an abstract position to this one
	 * 
	 * @param pos
	 *            the abstract position to be added to this one
	 */
	public abstract void add(final AbstractPosition pos);

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
		if (obj instanceof AbstractPosition) {
			final AbstractPosition other = (AbstractPosition) obj;
			return ((other.x == this.x) && (other.y == this.y) && (other.z == this.z));
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	@Override
	public int hashCode() {
		// TODO better hash method for fewer collisions
		return x + y + z;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Converts the object to a {@link Point2D} object
	 * 
	 * @return a {@link Point2D} object
	 */
	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Converts the object to a {@link Point}
	 * 
	 * @return a {@link Point}
	 */
	public Point toPoint() {
		return new Point(x, y);
	}

}
