/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a rectangle.
 */
public class Rectangle extends DrawingObject {

	/**
	 *
	 */
	private int height = 10;

	/**
	 *
	 */
	private int lineWidth = 1;

	/**
	 *
	 */
	private int width = 10;

	// --------------------------------------------------------------------------------
	/**
	 */
	public Rectangle() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Rectangle(final int x, final int y, final int width, final int height) {
		setPosition(new AbsolutePosition(x, y));
		setWidth(width);
		setHeight(height);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return new AbsoluteRectangle(getPosition(), width, height);
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		final Color color = new Color(null, this.getColor());
		final Color bg = new Color(null, this.getBgColor());

		final PixelPosition px = drawingArea.absPoint2PixelPoint(this.getPosition());

		gc.setForeground(color);
		gc.setBackground(bg);
		gc.setLineWidth(this.getLineWidth());
		gc.fillRectangle((px.x - (this.getWidth() / 2)), (px.y - (this.getHeight() / 2)), this.getWidth(), this.getHeight());
		gc.drawRectangle((px.x - (this.getWidth() / 2)), (px.y - (this.getHeight() / 2)), this.getWidth(), this.getHeight());

		color.dispose();
		bg.dispose();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public synchronized int getHeight() {
		return height;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public synchronized int getLineWidth() {
		return lineWidth;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public synchronized int getWidth() {
		return width;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param height
	 * @param fireBoundingBoxChangeEvent
	 */
	public synchronized void setHeight(final int height) {
		this.height = height;
		markBoundingBoxDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 */
	public synchronized void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
		markContentDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param width
	 * @param fireBoundingBoxChangeEvent
	 */
	public void setWidth(final int width) {
		this.width = width;
		markBoundingBoxDirty();
	}

	@Override
	public String toString() {
		return getBoundingBox().rectangle.toString();
	}

}
