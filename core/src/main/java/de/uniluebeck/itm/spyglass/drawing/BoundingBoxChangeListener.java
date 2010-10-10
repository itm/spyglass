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

import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * Instances of this class listen to changes of a drawing object's bounding box
 */
public interface BoundingBoxChangeListener extends EventListener {

	// --------------------------------------------------------------------------------
	/**
	 * Called when a drawing object's bounding box actually changed
	 * 
	 * @param updatedDrawingObject
	 *            the drawing object which bounding box changed
	 * @param oldBox
	 *            the drawing object's old bounding box
	 */
	public void onBoundingBoxChanged(DrawingObject updatedDrawingObject, AbsoluteRectangle oldBox);

}
