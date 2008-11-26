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
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a line.
 */
@Root
public class Line extends DrawingObject {

	private AbsolutePosition lineEnd = new AbsolutePosition();

	private int width = 1;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Line() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		final Line clone = new Line();
		clone.setColor(getColorR(), getColorG(), getColorB());
		clone.setPosition(new AbsolutePosition(getPosition().x, getPosition().y, 0));
		clone.setLineWidth(getLineWidth());
		return clone;
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		final Color color = new Color(gc.getDevice(), this.getColorR(), this.getColorG(), this.getColorB());
		gc.setForeground(color);
		gc.setLineWidth(this.getLineWidth());

		final PixelPosition start = drawingArea.absPoint2PixelPoint(this.getPosition());
		final PixelPosition end = drawingArea.absPoint2PixelPoint(this.getEnd());

		gc.drawLine(start.x, start.y, end.x, end.y);
		color.dispose();

		// drawBoundingBox(drawingArea, gc);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AbsolutePosition getEnd() {
		return lineEnd;
	}

	public int getLineWidth() {
		return width;
	}

	public void setEnd(final AbsolutePosition end) {
		setEnd(end, true);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setEnd(final AbsolutePosition end, final boolean fireBoundingBoxChangeEvent) {
		lineEnd = end;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}

	public void setLineWidth(final int width) {
		setLineWidth(width, true);
	}

	public void setLineWidth(final int width, final boolean fireBoundingBoxChangeEvent) {
		this.width = width;
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
	protected AbsoluteRectangle calculateBoundingBox() {
		final AbsolutePosition pos = getPosition();
		final int upperLeftX = Math.min(lineEnd.x, pos.x);
		final int upperLeftY = Math.min(lineEnd.y, pos.y);
		// final int lowerRightX = Math.max(lineEnd.x, pos.x);
		// final int lowerRightY = Math.max(lineEnd.x, pos.y);

		// System.out.println("(" + upperLeftX + "," + upperLeftY + "," + lowerRightX + ","
		// + lowerRightY + ")");

		final int width = Math.abs(lineEnd.x - pos.x);
		final int height = Math.abs(lineEnd.y - pos.y);
		return new AbsoluteRectangle(new AbsolutePosition(upperLeftX, upperLeftY, 0), width, height);
	}

}
