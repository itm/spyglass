/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * A naive layer implementation which can be utilized as a baseline when doing performance tests
 * against the QuadTree.
 * 
 * Internally the SimpleLayer uses an ArrayList which guarantees fixed and known runtime constrains.
 * 
 * All methods of this class have a O(n) runtime (except add, which has constant runtime), where n
 * denotes the number of items currently residing in the layer.
 * 
 * @author Dariush Forouher, refactored to returning {@link Set} by Daniel Bimschas
 * 
 */
public class SimpleLayer implements Layer, BoundingBoxChangeListener {

	private ArrayList<DrawingObject> list = new ArrayList<DrawingObject>();

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uniluebeck.itm.spyglass.layer.Layer#addOrUpdate(de.uniluebeck.itm.spyglass.drawing.
	 * DrawingObject)
	 */
	@Override
	public synchronized void add(final DrawingObject d) {
		this.remove(d);
		this.addInternal(d);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a drawing object internally
	 * 
	 * @param d
	 *            a drawing object
	 */
	protected void addInternal(final DrawingObject d) {
		list.add(d);
		d.addBoundingBoxChangedListener(this);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uniluebeck.itm.spyglass.layer.Layer#bringToFront(de.uniluebeck.itm.spyglass.drawing.
	 * DrawingObject)
	 */
	@Override
	public synchronized void bringToFront(final DrawingObject dob) {
		this.remove(dob);
		list.add(0, dob);
		dob.addBoundingBoxChangedListener(this);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#clear()
	 */
	@Override
	public synchronized void clear() {
		for (final DrawingObject d : this.list) {
			d.removeBoundingBoxChangeListener(this);
			list.remove(d);
		}
	}

	private class DummyComparator implements Comparator<DrawingObject> {

		@Override
		public int compare(final DrawingObject o1, final DrawingObject o2) {
			return -1; // generates a list instead of a tree
		}

	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#getDrawingObjects()
	 */
	@SuppressWarnings("synthetic-access")
	@Override
	public synchronized SortedSet<DrawingObject> getDrawingObjects() {
		final TreeSet<DrawingObject> set = new TreeSet<DrawingObject>(new DummyComparator());
		for (final DrawingObject dob : list) { // O(n)
			set.add(dob); // O(1)
		}
		return set;
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.spyglass.layer.Layer#getDrawingObjects(de.uniluebeck.itm.spyglass.positions
	 * .AbsoluteRectangle)
	 */
	@SuppressWarnings("synthetic-access")
	@Override
	public synchronized SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect) {
		final TreeSet<DrawingObject> ret = new TreeSet<DrawingObject>(new DummyComparator());
		for (final DrawingObject d : list) {
			if (rect.intersects(d.getBoundingBox())) {
				ret.add(d);
			}
		}
		return ret;
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.spyglass.layer.Layer#getDrawingObjects(de.uniluebeck.itm.spyglass.positions
	 * .AbsoluteRectangle, boolean)
	 */
	@Override
	public Set<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect, final boolean sorted) {
		return getDrawingObjects(rect);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#getDrawingObjects(boolean)
	 */
	@Override
	public Set<DrawingObject> getDrawingObjects(final boolean sorted) {
		return getDrawingObjects();
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener#onBoundingBoxChanged(de.uniluebeck
	 * .itm.spyglass.drawing.DrawingObject)
	 */
	@Override
	public void onBoundingBoxChanged(final DrawingObject updatedDrawingObject, final AbsoluteRectangle oldBox) {
		this.add(updatedDrawingObject);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.spyglass.layer.Layer#pushBack(de.uniluebeck.itm.spyglass.drawing.DrawingObject
	 * )
	 */
	@Override
	public synchronized void pushBack(final DrawingObject object) {
		this.remove(object);
		this.add(object);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.spyglass.layer.Layer#remove(de.uniluebeck.itm.spyglass.drawing.DrawingObject
	 * )
	 */
	@Override
	public synchronized void remove(final DrawingObject d) {
		d.removeBoundingBoxChangeListener(this);
		list.remove(d); // O(n)
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.layer.Layer#removeAll(java.util.Set)
	 */
	@Override
	public void removeAll(final Collection<DrawingObject> dos) {
		for (final DrawingObject dob : dos) {
			remove(dob);
		}
	}

}
