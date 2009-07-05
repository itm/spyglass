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

import java.util.EventListener;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;

// --------------------------------------------------------------------------------
/**
 * Implementations of this interface listen to changes concerning {@link DrawingObject}s
 * 
 */
public interface DrawingObjectListener extends EventListener {

	// --------------------------------------------------------------------------------
	/**
	 * Called when a {@link DrawingObject} was added
	 * 
	 * @param p
	 *            the plug-in which added the drawing object
	 * @param dob
	 *            the drawing object which was added
	 */
	public void drawingObjectAdded(Plugin p, final DrawingObject dob);

	// --------------------------------------------------------------------------------
	/**
	 * Called when a {@link DrawingObject} was removed
	 * 
	 * @param p
	 *            the plug-in which removed the drawing object
	 * @param dob
	 *            the drawing object which was removed
	 */
	public void drawingObjectRemoved(Plugin p, final DrawingObject dob);

}
