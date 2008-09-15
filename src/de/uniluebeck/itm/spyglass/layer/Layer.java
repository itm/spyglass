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
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @param d
	 */
	public void addOrUpdate(DrawingObject d);
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the point order parameter of a drawing object to make it the last one in the set to be
	 * painted. This way, the drawing object will be in front of all other ones.
	 * 
	 * @param object
	 *            the drawing object to be brought to the front
	 */
	public void bringToFront(DrawingObject dob);
	
	/**
	 * Sets the point order parameter of a drawing object to make it the first one in the set to be
	 * painted. This way, the drawing object will be behind all other ones.
	 * 
	 * @param object
	 *            the drawing object to be brought to the front
	 */
	public void pushBack(final DrawingObject object);
	
	public void clear();
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @param rect
	 */
	public List<DrawingObject> getDrawingObjects(AbsoluteRectangle rect);
	
	public List<DrawingObject> getDrawingObjects();
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @param d
	 * @return <code>true</code> if <code>d</code> was found (and removed), <code>false</code>
	 *         otherwise
	 */
	public boolean remove(DrawingObject d);
}
