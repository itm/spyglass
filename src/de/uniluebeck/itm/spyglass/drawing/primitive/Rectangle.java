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
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a rectangle.
 */
@Root
public class Rectangle extends DrawingObject {
	
	@Attribute
	private int width = 10;
	
	@Attribute
	private int height = 10;
	
	@Attribute
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
	public Rectangle(final int id) {
		super(id);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		final Rectangle clone = new Rectangle();
		clone.setId(getId());
		clone.setWidth(width);
		clone.setHeight(height);
		clone.setColor(getColorR(), getColorG(), getColorB());
		clone.setPosition(new AbsolutePosition(getPosition().x, getPosition().y, 0));
		
		return clone;
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
		this.width = width;
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
		this.height = height;
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
		if (other instanceof Rectangle) {
			super.update(other);
			final Rectangle r = (Rectangle) other;
			setWidth(r.getWidth());
			setWidth(r.getWidth());
		}
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		final Color color = new Color(null, this.getColorR(), this.getColorG(), this.getColorB());
		final Color bg = new Color(null, this.getBgColorR(), this.getBgColorG(), this.getBgColorB());
		
		final PixelPosition px = drawingArea.absPoint2PixelPoint(this.getPosition());
		
		gc.setForeground(color);
		gc.setBackground(bg);
		gc.setLineWidth(this.getLineWidth());
		gc.fillRectangle((px.x - (this.getWidth() / 2)), (px.y - (this.getHeight() / 2)), this
				.getWidth(), this.getHeight());
		gc.drawRectangle((px.x - (this.getWidth() / 2)), (px.y - (this.getHeight() / 2)), this
				.getWidth(), this.getHeight());
		
		color.dispose();
		bg.dispose();
	}
	
	@Override
	public AbsoluteRectangle getBoundingBox() {
		return new AbsoluteRectangle(getPosition(), width, height);
	}
	
}
