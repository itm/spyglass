/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;

/**
 * Plugins which implement this interface want to draw on the drawingArea. They
 * will be called on redrawEvents to return their current List of drawingObjects
 * via the method defined below.
 * 
 * 
 * @author dariush
 * 
 */
public interface Drawable {
	
	/**
	 * TODO: really needed??
	 */
	public int getTimeout();
	
	/**
	 * Return if this plugin is currently visible.
	 */
	public boolean isVisible();
	
	/**
	 * 
	 * @param drawingArea
	 */
	public abstract List<DrawingObject> getDrawingObjects(DrawingArea drawingArea);
	
}