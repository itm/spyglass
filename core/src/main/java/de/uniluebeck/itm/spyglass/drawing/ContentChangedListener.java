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
 * Listen to property changes of drawing objects
 * 
 * @author Dariush Forouher
 * 
 */
public interface ContentChangedListener extends EventListener {

	// --------------------------------------------------------------------------------
	/**
	 * Called when some properties of a drawing object have changed
	 * 
	 * @param updatedDrawingObject
	 *            a drawing object
	 */
	public void onContentChanged(DrawingObject updatedDrawingObject);

}
