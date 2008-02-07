/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing;

import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Abstract class that represents a drawing object. Every DrawingObject should have an unique id, a position and a
 * color, given by the RGB components in a R8G8B8 format.
 */
@Root
public abstract class DrawingObject {
	private int id = 0;

	private Point position = new Point(0, 0);

	@Element
	private int colorR = 200;

	@Element
	private int colorG = 0;

	@Element
	private int colorB = 0;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public DrawingObject() {
		this.id = -1;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public DrawingObject(int id) {
		this.id = id;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void update(DrawingObject other) {
		this.id = other.id;
		this.position.x = other.position.x;
		this.position.y = other.position.y;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Point getPosition() {
		return position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getId() {
		return id;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setId(int id) {
		this.id = id;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getColorR() {
		return colorR;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColorR(int colorR) {
		this.colorR = colorR;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getColorG() {
		return colorG;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColorG(int colorG) {
		this.colorG = colorG;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getColorB() {
		return colorB;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColorB(int colorB) {
		this.colorB = colorB;
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
