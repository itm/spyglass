/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.layer;

import java.util.List;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * Convenience class as a base for all Layer classes.
 */
public interface Layer {
	
	/**
	 * 
	 * @param d
	 */
	public void addOrUpdate(DrawingObject d);
	
	/**
	 * 
	 * @param do
	 */
	public void bringToFront(DrawingObject dob);
	
	public void clear();
	
	/**
	 * 
	 * @param rect
	 */
	public List<DrawingObject> getDrawingObjects(AbsoluteRectangle rect);
	
	public List<DrawingObject> getDrawingObjects();
	
	/**
	 * 
	 * @param dob
	 * @param x
	 *            absolute coordinate
	 * @param y
	 *            absolute coordinate
	 */
	public void move(DrawingObject dob, int x, int y);
	
	/**
	 * 
	 * @param d
	 */
	public void remove(DrawingObject d);
}
