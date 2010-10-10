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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * This class represents an abstract rectangle
 * 
 * @author Dariush Forouher, Sebastian Ebers
 * 
 */
public abstract class AbstractRectangle {

	/** The {@link Rectangle} which is used as basis */
	public Rectangle rectangle;

	// --------------------------------------------------------------------------------
	/**
	 * @return the height of the rectangle
	 */
	public int getHeight() {
		return rectangle.height;
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the height
	 * 
	 * @param height
	 */
	public void setHeight(final int height) {
		this.rectangle.height = height;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the width
	 */
	public int getWidth() {
		return rectangle.width;
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the width
	 * 
	 * @param width
	 */
	public void setWidth(final int width) {
		this.rectangle.width = width;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public AbstractRectangle() {
		this.rectangle = new Rectangle(0, 0, 0, 0);
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
	public AbstractRectangle(final int x, final int y, final int width, final int height) {
		this.rectangle = new Rectangle(x, y, width, height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            another object of this class
	 */
	public AbstractRectangle(final AbstractRectangle other) {
		this.rectangle = new Rectangle(other.rectangle.x, other.rectangle.y, other.rectangle.width, other.rectangle.height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param other
	 *            a rectangle
	 */
	public AbstractRectangle(final Rectangle other) {
		this.rectangle = new Rectangle(other.x, other.y, other.width, other.height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether a point specified by its x- and y-coordinates is contained in this rectangle
	 * 
	 * @param x
	 *            a point's x-coordinate
	 * @param y
	 *            a point's y coordinate
	 * @return <code>true</code>, if a point specified by its x- and y-coordinates is contained in
	 *         this rectangle, <code>false</code> otherwise
	 */
	public boolean contains(final int x, final int y) {
		return this.rectangle.contains(x, y);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether a point specified by its x- and y-coordinates is contained in this rectangle
	 * 
	 * @param x
	 *            a point's x-coordinate
	 * @param y
	 *            a point's y coordinate
	 * @return <code>true</code>, if a point specified by its x- and y-coordinates is contained in
	 *         this rectangle, <code>false</code> otherwise
	 */
	public boolean contains(final double x, final double y) {
		return this.rectangle.contains((int) x, (int) y);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether a point is contained in this rectangle
	 * 
	 * @param p
	 *            a point
	 * @return <code>true</code>, if a point is contained in this rectangle, <code>false</code>
	 *         otherwise
	 */
	public boolean contains(final Point p) {
		return this.rectangle.contains(p);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether another rectangle is contained in this one
	 * 
	 * @param rect
	 *            another rectangle
	 * @return <code>true</code>, if another rectangle is contained in this one, <code>false</code>
	 *         otherwise
	 */
	public boolean contains(final AbstractRectangle rect) {
		final Rectangle intersection = this.rectangle.intersection(rect.rectangle);
		return (intersection.x == rect.rectangle.x) && (intersection.y == rect.rectangle.y) && (intersection.width == rect.rectangle.width)
				&& (intersection.height == rect.rectangle.height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether another rectangle intersects this one
	 * 
	 * @param rect
	 *            another rectangle
	 * @return <code>true</code>, if another rectangle intersects this one, <code>false</code>
	 *         otherwise
	 */
	public boolean intersects(final AbstractRectangle rect) {
		return this.rectangle.intersects(rect.rectangle);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether another rectangle intersects this one
	 * 
	 * @param rect
	 *            another rectangle
	 * @return <code>true</code>, if another rectangle intersects this one, <code>false</code>
	 *         otherwise
	 */
	public boolean intersects(final Rectangle rect) {
		return this.rectangle.intersects(rect);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Translates this rectangle
	 * 
	 * @param x
	 *            value added to the x-coordinate
	 * @param y
	 *            value added to the y-coordnate
	 */
	public void translate(final int x, final int y) {
		this.rectangle.x += x;
		this.rectangle.y += y;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Resizes the rectangle
	 * 
	 * @param width
	 *            the new width value
	 * @param height
	 *            the new height value
	 */
	public void setSize(final int width, final int height) {
		this.rectangle.width = width;
		this.rectangle.height = height;

	}

	// --------------------------------------------------------------------------------
	@Override
	public String toString() {
		return this.rectangle.toString();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Performs the given Transformation on this Rectangle.
	 * 
	 * @param at
	 *            a transform object
	 */
	public void transform(final AffineTransform at) {

		final Rectangle newRect = new Rectangle(0, 0, 0, 0);

		final Point2D upLeft = at.transform(new Point2D.Double(rectangle.x, rectangle.y), null);
		newRect.x = (int) Math.floor(upLeft.getX());
		newRect.y = (int) Math.floor(upLeft.getY());

		final Point2D lowerRightAbs = new Point2D.Double(rectangle.x + this.getWidth(), rectangle.y + this.getHeight());
		final Point2D lRight = at.transform(lowerRightAbs, null);

		final int lowerRightX = (int) Math.floor(lRight.getX() + 1);
		final int lowerRightY = (int) Math.floor(lRight.getY() + 1);

		newRect.width = Math.abs(lowerRightX - newRect.x);
		newRect.height = Math.abs(newRect.y - lowerRightY);

		this.rectangle = newRect;

	}

}