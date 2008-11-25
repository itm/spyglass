/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.EventListener;

// --------------------------------------------------------------------------------
/**
 * Listener for NodePosition events
 */
public interface NodePositionChangedListener extends EventListener {

	// --------------------------------------------------------------------------------
	/**
	 * Is called whenever a Node changes its position
	 * 
	 * @param e
	 */
	public void handleEvent(NodePositionChangedEvent e);
}
