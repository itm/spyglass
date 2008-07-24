/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin.Position;

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
		clone.setColorR(getColorR());
		clone.setColorG(getColorG());
		clone.setColorB(getColorB());
		clone.setPosition(new Position(getPosition().x, getPosition().y));
		
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
	
}
