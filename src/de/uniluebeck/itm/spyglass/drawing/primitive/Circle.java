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

/**
 * A primitive drawing object, representing a circle.
 */
@Root
public class Circle extends DrawingObject {
	@Element
	private int diameter = 10;
	private short lineWidth = 1;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Circle() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Circle(int id) {
		super(id);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		Circle clone = new Circle();
		clone.setId(getId());
		clone.setDiameter(diameter);
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
	public int getDiameter() {
		return diameter;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setDiameter(int diameter) {
		this.diameter = diameter;
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

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void update(DrawingObject other)
	{
		if (other instanceof Circle)
		{
			super.update(other);
			Circle c = (Circle)other;
			setDiameter(c.getDiameter());
		}
	}

	public short getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(short lineWidth) {
		this.lineWidth = lineWidth;
	}

}
