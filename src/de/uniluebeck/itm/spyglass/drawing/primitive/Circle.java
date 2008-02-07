/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;

/**
 * A primitive drawing object, representing a circle.
 */
@Root
public class Circle extends DrawingObject {
	@Element
	private int diameter = 10;

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
		clone.setPosition(new Point(getPosition().x, getPosition().y));

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

}
