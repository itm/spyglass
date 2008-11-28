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
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a rectangle.
 */
public class Rectangle extends DrawingObject {

	private int width = 10;

	private int height = 10;

	private int lineWidth = 1;

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Rectangle() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getWidth() {
		return width;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setWidth(final int width) {
		setWidth(width, true);
	}

	public void setWidth(final int width, final boolean fireBoundingBoxChangeEvent) {
		this.width = width;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getHeight() {
		return height;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setHeight(final int height) {
		setHeight(height, true);
	}

	public void setHeight(final int height, final boolean fireBoundingBoxChangeEvent) {
		this.height = height;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String toString() {
		// TODO Implement
		return super.toString();
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

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return new AbsoluteRectangle(getPosition(), width, height);
	}

}
