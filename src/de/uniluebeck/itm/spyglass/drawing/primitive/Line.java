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
	
	private AbsolutePosition lineEnd;
	
	private int width;
	
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
		clone.setId(getId());
		clone.setColor(getColorR(), getColorG(), getColorB());
		clone.setPosition(new AbsolutePosition(getPosition().x, getPosition().y, 0));
		clone.setLineWidth(getLineWidth());
		return clone;
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		
		final Color color = new Color(gc.getDevice(), this.getColorR(), this.getColorG(), this
				.getColorB());
		gc.setForeground(color);
		gc.setLineWidth(this.getLineWidth());
		
		final PixelPosition start = drawingArea.absPoint2PixelPoint(this.getPosition());
		final PixelPosition end = drawingArea.absPoint2PixelPoint(this.getEnd());
		
		gc.drawLine(start.x, start.y, end.x, end.y);
		color.dispose();
		
		drawBoundingBox(drawingArea, gc);
	}
	
	@Override
	public AbsoluteRectangle getBoundingBox() {
		final AbsolutePosition pos = getPosition();
		final int lowerLeftX = lineEnd.x < pos.x ? lineEnd.x : pos.x;
		final int lowerLeftY = lineEnd.y < pos.y ? lineEnd.y : pos.y;
		final int upperRightX = lineEnd.x > pos.x ? lineEnd.x : pos.x;
		final int upperRightY = lineEnd.y > pos.y ? lineEnd.x : pos.y;
		final int width = Math.abs(upperRightX - lowerLeftX);
		final int height = Math.abs(upperRightY - lowerLeftY);
		return new AbsoluteRectangle(new AbsolutePosition(lowerLeftX, lowerLeftY, 0), width, height);
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
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setEnd(final AbsolutePosition end) {
		lineEnd = end;
	}
	
	public void setLineWidth(final int width) {
		this.width = width;
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
	public void update(final DrawingObject other) {
		if (other instanceof Line) {
			super.update(other);
			final Line l = (Line) other;
			setEnd(l.getEnd());
		}
	}
	
}
