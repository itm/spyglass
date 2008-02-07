/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a rectangle.
 */
@Root
public class Rectangle extends DrawingObject {
	@Element
	private int width = 10;

	@Element
	private int height = 10;

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
	public Rectangle(int id) {
		super(id);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		Rectangle clone = new Rectangle();
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
	public void setWidth(int width) {
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
	public void setHeight(int height) {
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

}
