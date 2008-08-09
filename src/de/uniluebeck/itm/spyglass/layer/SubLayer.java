/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.layer;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * A Sublayer is usually used by a plugin to store all drawing objects, that
 * must be rendered to visualize the scene.
 * 
 * @deprecated Use QuadTree instead.
 */
@Deprecated
public class SubLayer implements Layer {
	private static Category log = SpyglassLogger.get(SubLayer.class);
	
	private final HashMap<Integer, DrawingObject> drawingObjects = new HashMap<Integer, DrawingObject>();
	
	private final Comparator<DrawingObject> paintOrderComp = new Comparator<DrawingObject>() {
		public int compare(final DrawingObject o1, final DrawingObject o2) {
			return (int) (o1.getPaintOrderId() - o2.getPaintOrderId());
		}
	};
	
	private final List<DrawingObject> paintOrder = new LinkedList<DrawingObject>();
	private long maxPaintOrder = 0;
	private boolean sortRequired = false;
	
	// --------------------------------------------------------------------------
	// ------
	
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
	
	public DrawingObject getDrawingObject(final int id) {
		return drawingObjects.get(id);
	}
	
	public void reset() {
		clear();
	}
	
	/**
	 * Adds a DrawingObject to the internal hashmap. If there is already a
	 * DrawingObject with the same id, that objects gets updated.
	 * 
	 * @param object
	 *            The DrawingObject to add or update.
	 */
	public void addOrUpdate(final DrawingObject object) {
		// log.debug(object.toString() + " " + this.drawingObjects.size() + ", "
		// + this.paintOrder.size());
		final DrawingObject obj = getDrawingObject(object.getId());
		// If the Drawing Object with the ID is not already in the map, add it.
		if (obj == null) {
			log.debug("add object " + object);
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
		// sortRequired = true; // TODO WEg spaeter
		// obj.setPaintOrderId(maxPaintOrder++);
		// log.debug("updating object " + obj);
	}
	
	/**
	 * Sets the point order parameter of a drawing object to make it the last
	 * one in the set to be painted. This way, the drawing object will be in
	 * front of all other ones.
	 * 
	 * @param object
	 *            the drawing object to be brought to the front
	 */
	public void bringToFront(final DrawingObject object) {
		final List<DrawingObject> dos = getDrawingObjects();
		final long size = dos.size();
		int pos = 0;
		for (final DrawingObject drawingObject : dos) {
			
			// if the current drawing object is not the one which is to be
			// brought to the front ...
			if (!drawingObject.equals(object)) {
				// ... "normalize" it's position parameter
				drawingObject.setPaintOrderId(pos++);
			} else {
				// ... otherwise set it's position parameter to the maximum
				// possible one
				object.setPaintOrderId(size - 1);
			}
		}
	}
	
	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect) {
		// TODO Auto-generated method stub
		return getDrawingObjects();
	}
	
	public void move(final DrawingObject dob, final int x, final int y) {
		// TODO Auto-generated method stub
		
	}
	
	public void remove(final DrawingObject d) {
		drawingObjects.remove(d.getId());
		paintOrder.remove(d);
	}
	
}
