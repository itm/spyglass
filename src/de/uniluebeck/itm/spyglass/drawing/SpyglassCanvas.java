/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing;

import org.eclipse.swt.graphics.GC;
import org.simpleframework.xml.Root;

// --------------------------------------------------------------------------------
/**
 * 
 *
 */
@Root
public interface SpyglassCanvas {

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void draw(DrawingObject drawingObj, GC gc);
}
