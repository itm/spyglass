/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;

//--------------------------------------------------------------------------------
/**
 * Abstract class that represents a drawing object. Every DrawingObject should have an unique id, a position and a
 * color, given by the RGB components in a R8G8B8 format.
 */
@Root
public abstract class DrawingObject {
	private int id = 0;

	private Position position = new Position(0, 0);

	@Element
	private int colorR = 200;

	@Element
	private int colorG = 0;

	@Element
	private int colorB = 0;

	@Element
	private int bgColorR = 255;

	@Element
	private int bgColorG = 255;

	@Element
	private int bgColorB = 255;

	private long paintOrderId;

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
		this.paintOrderId = other.paintOrderId;
		this.position.x = other.position.x;
		this.position.y = other.position.y;
		setColor(other.getColorR(), other.getColorG(), other.getColorB());
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
	public Position getPosition() {
		return position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPosition(Position position) {
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
	public void setColor(int R, int G, int B ) {
		this.colorR = R;
		this.colorG = G;
		this.colorB = B;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "paintOrderId: " + paintOrderId + ", id: " + id + ", " + super.toString();
	}

	public long getPaintOrderId() {
		return paintOrderId;
	}

	public void setPaintOrderId(long paintOrderId) {
		this.paintOrderId = paintOrderId;
	}

	public int getBgColorR() {
		return bgColorR;
	}

	public void setBgColorR(int bgColorR) {
		this.bgColorR = bgColorR;
	}

	public int getBgColorG() {
		return bgColorG;
	}

	public void setBgColor(int bgColorG) {
		this.bgColorG = bgColorG;
	}

	public int getBgColorB() {
		return bgColorB;
	}

	public void setBgColorB(int bgColorB) {
		this.bgColorB = bgColorB;
	}
	
	public void setBgColor(int r, int b, int g) {
		this.bgColorR = r;
		this.bgColorB = b;
		this.bgColorG = g;
	}
}
