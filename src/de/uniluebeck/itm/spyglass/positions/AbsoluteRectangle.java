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
 * This class represents an rectangle, measured in absolute coordinates.
 * 
 * @author Dariush Forouher
 * 
 */
public class AbsoluteRectangle extends AbstractRectangle {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public AbsoluteRectangle() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param other
	 *            another abstract rectangle
	 */
	public AbsoluteRectangle(final AbstractRectangle other) {
		super(other);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param other
	 *            another rectangle
	 */
	public AbsoluteRectangle(final Rectangle other) {
		super(other);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            the rectangle's lower left x-coordinate
	 * @param y
	 *            the rectangle's lower left y-coordinate
	 * @param width
	 *            the rectangle's width
	 * @param height
	 *            the rectangles origin x-coordinate
	 * 
	 */
	public AbsoluteRectangle(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param lowerLeft
	 *            the rectangle's lower left coordinate
	 * @param width
	 *            the rectangle's width
	 * @param height
	 *            the rectangles origin x-coordinate
	 */
	public AbsoluteRectangle(final AbsolutePosition lowerLeft, final int width, final int height) {
		super(lowerLeft.x, lowerLeft.y, width, height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the lower left point
	 */
	public AbsolutePosition getLowerLeft() {
		return new AbsolutePosition(rectangle.x, rectangle.y, 0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 * 
	 * @param upperLeft
	 */
	public void setLowerLeft(final AbsolutePosition upperLeft) {
		this.rectangle.x = upperLeft.x;
		this.rectangle.y = upperLeft.y;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the coordinates of the center point
	 */
	public AbsolutePosition getCenter() {
		return new AbsolutePosition(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2, 0);
	}

	// --------------------------------------------------------------------------------
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
	public AbsoluteRectangle union(final AbsoluteRectangle other) {
		final AbsoluteRectangle ret = new AbsoluteRectangle();
		ret.rectangle = this.rectangle.union(other.rectangle);
		return ret;
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
	public AbsoluteRectangle intersection(final AbsoluteRectangle other) {
		final AbsoluteRectangle ret = new AbsoluteRectangle();
		ret.rectangle = this.rectangle.intersection(other.rectangle);
		return ret;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Inherits the values of another rectangle
	 * 
	 * @param other
	 *            the rectangle which values are to be inherited
	 */
	public void inherit(final AbsoluteRectangle other) {
		this.rectangle.x = other.rectangle.x;
		this.rectangle.y = other.rectangle.y;
		this.rectangle.height = other.rectangle.height;
		this.rectangle.width = other.rectangle.width;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns if this instance of an absolute rectangle equals another one
	 * 
	 * @param other
	 *            another absolute rectangle
	 * @return <code>true</code>, if this instance of an absolute rectangle equals another one,
	 *         <code>false</code> otherwise
	 * 
	 */
	public boolean equals(final AbsoluteRectangle other) {
		return other == null ? false : rectangle.equals(other.rectangle);
	}

}