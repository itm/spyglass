/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.layer;

import java.util.HashMap;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;

/**
 * A Sublayer is usually used by a plugin to store all drawing objects, that must be rendered to visualize the scene.
 */
public class SubLayer extends Layer {
	private HashMap<Integer, DrawingObject> drawingObjects = new HashMap<Integer, DrawingObject>();

	// --------------------------------------------------------------------------------
	/**
	 * Adds a DrawingObject to the internal hashmap. If there is already a DrawingObject with the same id, that objects
	 * gets updated.
	 * 
	 * @param object
	 *            The DrawingObject to add or update.
	 */
	public void addOrUpdateDrawingObject(DrawingObject object) {
		DrawingObject obj = getDrawingObject(object.getId());

		// If the Drawing Object with the ID is not already in the map, add it.
		if (obj == null) {
			drawingObjects.put(object.getId(), object);
			return;
		}

		// Otherwise, update the object
		obj.update(object);
	}

	public HashMap<Integer, DrawingObject> getDrawingObjects() {
		return drawingObjects;
	}

	public void setDrawingObjects(HashMap<Integer, DrawingObject> drawingObjects) {
		this.drawingObjects = drawingObjects;
	}

	public void removeDrawingObject(DrawingObject object) {
		drawingObjects.remove(object);
	}

	public DrawingObject getDrawingObject(int id) {
		return drawingObjects.get(id);
	}

}
