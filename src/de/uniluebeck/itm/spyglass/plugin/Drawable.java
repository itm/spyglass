/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;

/**
 * Plugins which implement this interface want to draw on the drawingArea. They will be called on
 * redrawEvents to return their current List of drawingObjects via the method defined below.
 * 
 * 
 * @author dariush
 * 
 */
public interface Drawable {
	
	/**
	 * Returns the time the plug-in's drawing objects will be visible.
	 * 
	 * @return the time the plug-in's drawing objects will be visible.
	 */
	public int getTimeout();
	
	/**
	 * Returns if this plug-in is currently visible.
	 * 
	 * @return <tt>true</tt> if this plug-in is currently visible.
	 */
	public boolean isVisible();
	
	/**
	 * Returns the objects the plug-in wants to be drawn on the GUI
	 * 
	 * @param drawingArea
	 *            the description of the currently visible part of the area where the sensor nodes
	 *            are placed
	 */
	public abstract List<DrawingObject> getDrawingObjects(DrawingArea drawingArea);
	
	/**
	 * Returns all drawing objects, which shall be considered when doing auto-zoom.
	 * 
	 * The auto-zoom function adjusts zoom and positioning of the drawing area in a way that all
	 * drawing objects returned by all plugins via this method are visible.
	 * 
	 * Note, that a plugin doesn't necessarily has to return all or even any drawing objects. The
	 * GridPainter for instance would be advised not to return any drawing objects at all, otherwise
	 * the auto-zoom function would be rendered pretty useless.
	 * 
	 * @returns all DrawingObjects which shall be considered by the auto-zoom function.
	 */
	public abstract List<DrawingObject> getAutoZoomDrawingObjects();
	
}