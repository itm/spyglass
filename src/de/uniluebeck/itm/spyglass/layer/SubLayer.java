/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.layer;

import ishell.util.Logging;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;

// --------------------------------------------------------------------------------
/**
 * A Sublayer is usually used by a plugin to store all drawing objects, that
 * must be rendered to visualize the scene.
 * 
 * @deprecated Use QuadTree instead.
 */
public class SubLayer implements Layer {
	private static Category log = Logging.get(SubLayer.class);

	private HashMap<Integer, DrawingObject> drawingObjects = new HashMap<Integer, DrawingObject>();

	private Comparator<DrawingObject> paintOrderComp = new Comparator<DrawingObject>() {

		@Override
		public int compare(DrawingObject o1, DrawingObject o2) {
			return (int) (o1.getPaintOrderId() - o2.getPaintOrderId());
		}
	};

	private List<DrawingObject> paintOrder = new LinkedList<DrawingObject>();
	private long maxPaintOrder = 0;
	private boolean sortRequired = false;

	// --------------------------------------------------------------------------------

	public void clear() {
		paintOrder.clear();
		drawingObjects.clear();
	}

	public List<DrawingObject> getDrawingObjects() {
		if (sortRequired) {
			synchronized (paintOrder) {
				Collections.sort(paintOrder, paintOrderComp);
				sortRequired = false;
			}
		}
		return paintOrder;
	}

	public DrawingObject getDrawingObject(int id) {
		return drawingObjects.get(id);
	}
	
	public void reset()
	{
		clear();
	}

	/**
	 * Adds a DrawingObject to the internal hashmap. If there is already a
	 * DrawingObject with the same id, that objects gets updated.
	 * 
	 * @param object
	 *            The DrawingObject to add or update.
	 */
	@Override
	public void addOrUpdate(DrawingObject object) {
		//log.debug(object.toString() + " " + this.drawingObjects.size() + ", " + this.paintOrder.size());
		DrawingObject obj = getDrawingObject(object.getId());
		// If the Drawing Object with the ID is not already in the map, add it.
		if (obj == null) {
			log.debug("add object "+object);
			object.setPaintOrderId(maxPaintOrder++);
			drawingObjects.put(object.getId(), object);
			synchronized (paintOrder) {
				paintOrder.add(object);
				sortRequired = true;
			}
			return;
		}

		// Otherwise, update the object
		obj.update(object);
		//sortRequired = true; // TODO WEg spaeter
		//obj.setPaintOrderId(maxPaintOrder++);
		//log.debug("updating object " + obj);
	}

	@Override
	public void bringToFront(DrawingObject dob) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<DrawingObject> getDrawingObjects(Rectangle rect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(DrawingObject dob, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(DrawingObject d) {
		drawingObjects.remove(d.getId());
		paintOrder.remove(d);
	}

}
