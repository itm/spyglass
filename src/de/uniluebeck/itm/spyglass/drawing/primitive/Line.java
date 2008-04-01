/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a line.
 */
@Root
public class Line extends DrawingObject {

	private Position lineEnd;
	private int width;
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Line(Position p, Position d) {
		this();
		setPosition(p);
		setLineWidth(1);
		setEnd(d);
	}

	public void update(DrawingObject other)
	{
		if (other instanceof Line)
		{
			super.update(other);
			Line l = (Line)other;
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
	public Line(int id) {
		super(id);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setEnd(Position end) {
		lineEnd = end;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Position getEnd() {
		return lineEnd;
	}
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		Line clone = new Line();
		clone.setId(getId());
		clone.setColorR(getColorR());
		clone.setColorG(getColorG());
		clone.setColorB(getColorB());
		clone.setPosition(new Position(getPosition().x, getPosition().y));
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

	public void setLineWidth(int width) {
		this.width = width;
	}

}
