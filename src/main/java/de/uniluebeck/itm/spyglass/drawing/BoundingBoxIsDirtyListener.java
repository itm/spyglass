/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing;

import java.util.EventListener;

// --------------------------------------------------------------------------------
/**
 * Listen to events which indicate that a drawing object's bounding box is not up-to-date any more
 * 
 * @author Dariush Forouher
 */
public interface BoundingBoxIsDirtyListener extends EventListener {

	// --------------------------------------------------------------------------------
	/**
	 * Called when a drawing object has an outdated boundingBox and is in need of an SWT display
	 * thread to get it back up-to-date
	 * 
	 * @param dob
	 *            a drawing object which has an outdated bounding box
	 */
	public void syncNeeded(DrawingObject dob);
}
