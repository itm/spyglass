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

import org.eclipse.swt.graphics.Rectangle;

//--------------------------------------------------------------------------------
/**
 * This class represents an rectangle, measured in pixel coordinates.
 * 
 * @author Dariush Forouher
 * 
 */
public class PixelRectangle extends AbstractRectangle {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public PixelRectangle() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param other
	 *            an abstract rectangle
	 */
	public PixelRectangle(final AbstractRectangle other) {
		super(other);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param other
	 *            a rectangle
	 */
	public PixelRectangle(final Rectangle other) {
		super(other);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            the rectangles origin x-coordinate
	 * @param y
	 *            the rectangles origin a-coordinate
	 * @param width
	 *            the rectangles with
	 * @param height
	 *            the rectangles height
	 */
	public PixelRectangle(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param upperLeft
	 *            the rectangle's upper left point
	 * @param width
	 *            the rectangles with
	 * @param height
	 *            the rectangles height
	 */
	public PixelRectangle(final PixelPosition upperLeft, final int width, final int height) {
		super(upperLeft.x, upperLeft.y, width, height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the upper left point
	 */
	public PixelPosition getUpperLeft() {
		return new PixelPosition(rectangle.x, rectangle.y);
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 * 
	 * @param upperLeft
	 */
	public void setUpperLeft(final PixelPosition upperLeft) {
		this.rectangle.x = upperLeft.x;
		this.rectangle.y = upperLeft.y;
	}

	/**
	 * Returns a new rectangle which represents the union of the receiver and the given rectangle.
	 * 
	 * The union of two rectangles is the smallest single rectangle that completely covers both of
	 * the areas covered by the two given rectangles.
	 * 
	 * @param other
	 *            the rectangle to perform union with
	 * @return the union of the receiver and the argument
	 */
	public PixelRectangle union(final PixelRectangle other) {
		final PixelRectangle ret = new PixelRectangle();
		ret.rectangle = this.rectangle.union(other.rectangle);
		return ret;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns an SWT rectangle
	 * 
	 * @return an SWT rectangle
	 */
	public Rectangle toSWTRectangle() {
		return rectangle;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a new rectangle which represents the intersection of the receiver and the given
	 * rectangle.
	 * 
	 * The intersection of two rectangles is the rectangle that covers the area which is contained
	 * within both rectangles.
	 * 
	 * @param other
	 *            the rectangle to perform the intersect with
	 * @return the intersection of the receiver and the argument
	 */
	public PixelRectangle intersection(final PixelRectangle other) {
		final PixelRectangle ret = new PixelRectangle();
		ret.rectangle = this.rectangle.intersection(other.rectangle);
		return ret;
	}
}