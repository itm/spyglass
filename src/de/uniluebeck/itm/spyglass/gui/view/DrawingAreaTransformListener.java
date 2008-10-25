/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.util.EventListener;

// --------------------------------------------------------------------------------
/**
 * Listener for transform change events from the drawing area.
 */
public interface DrawingAreaTransformListener extends EventListener {
	
	// --------------------------------------------------------------------------------
	/**
	 * this method is evoked when the drawing area has modified its internal transform (which
	 * implies that either a movement of the drawing area or a zoom has occured).
	 * 
	 * @param e
	 */
	public void handleEvent(DrawingAreaTransformEvent e);
}
