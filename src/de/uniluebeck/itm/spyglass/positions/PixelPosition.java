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

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the (2D) coordinate's of a single point when using pixels
 * 
 * @author Dariush Forouher
 */
public class PixelPosition extends AbstractPosition {

	// --------------------------------------------------------------------------------
	/**
	 * Create a new instance based on an SWT point.
	 * 
	 * @param point
	 */
	public PixelPosition(final Point point) {
		super(point.x, point.y, 0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            the position's x-coordinate
	 * @param y
	 *            the position's y-coordinate
	 */
	public PixelPosition(final int x, final int y) {
		super(x, y, 0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param point
	 *            the position's point
	 */
	public PixelPosition(final Point2D point) {
		super(point);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public PixelPosition() {
		// nothing to do here
	}

	// --------------------------------------------------------------------------------
	@Override
	public void mult(final double d) {
		x = (int) (x * d);
		y = (int) (y * d);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void add(final AbstractPosition p) {
		x = x + p.x;
		y = y + p.y;
	}

}