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
	public Line(final AbsolutePosition p, final AbsolutePosition d) {
		this();
		setPosition(p);
		setLineWidth(1);
		setEnd(d);
	}
	
	@Override
	public void update(final DrawingObject other) {
		if (other instanceof Line) {
			super.update(other);
			final Line l = (Line) other;
			setEnd(l.getEnd());
		}
	}
	
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
	public Line(final int id) {
		super(id);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setEnd(final AbsolutePosition end) {
		lineEnd = end;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AbsolutePosition getEnd() {
		return lineEnd;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		final Line clone = new Line();
		clone.setId(getId());
		clone.setColorR(getColorR());
		clone.setColorG(getColorG());
		clone.setColorB(getColorB());
		clone.setPosition(new AbsolutePosition(getPosition().x, getPosition().y, 0));
		clone.setLineWidth(getLineWidth());
		return clone;
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
	
	public int getLineWidth() {
		return width;
	}
	
	public void setLineWidth(final int width) {
		this.width = width;
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		final Color color = new Color(null, this.getColorR(), this.getColorG(), this.getColorB());
		gc.setForeground(color);
		gc.setLineWidth(this.getLineWidth());
		
		final PixelPosition start = drawingArea.absPoint2PixelPoint(this.getPosition());
		final PixelPosition end = drawingArea.absPoint2PixelPoint(this.getEnd());
		
		gc.drawLine((start.x), (start.y), (end.x), (end.y));
		// TODO: Implement the drawing of the line primitive
		color.dispose();
	}
	
	@Override
	public AbsoluteRectangle getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
