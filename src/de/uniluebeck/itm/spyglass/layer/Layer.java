/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.layer;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.danbim.swtquadtree.ISWTQuadTree;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * Convenience class as a base for all Layer classes.
 * 
 * @author Daniel Bimschas
 */
public interface Layer {

	// --------------------------------------------------------------------------------
	/**
	 * Factory for creation of all types of Layers.
	 * 
	 * @author Daniel Bimschas
	 */
	public static class Factory {

		// --------------------------------------------------------------------------------
		/**
		 * Same as calling {@link Factory#createQuadTreeLayer(boolean)} with <code>threadSafe</code>
		 * set to <code>true</code>.
		 * 
		 * @return a new Layer instance based on a QuadTree
		 */
		public static Layer createQuadTreeLayer() {
			return createQuadTreeLayer(true);
		}

		// --------------------------------------------------------------------------------
		/**
		 * Constructs a new QuadTree instance. It uses the factory method in {@link ISWTQuadTree}
		 * with a totalSideLength of 2^16, originX=originY=-((2^16)/2), minSideLength=2^6,
		 * capacity=5.
		 * 
		 * @param threadSafe
		 *            if the layer should be thread-safe, i.e. all methods should run with
		 *            synchronized semantics
		 * @return a new Layer instance based on a QuadTree
		 */
		public static Layer createQuadTreeLayer(final boolean threadSafe) {

			final int totalSideLength = (int) Math.pow(2, 16);
			final int originX = -(totalSideLength / 2);
			final int originY = -(totalSideLength / 2);
			final int minSideLength = 32; // 2^6
			final int capacity = 5;

			return new QuadTree(originX, originY, totalSideLength, minSideLength, capacity, threadSafe);

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a DrawingObject to the layer.
	 * 
	 * @param d
	 */
	public void add(final DrawingObject d);

	// --------------------------------------------------------------------------------
	/**
	 * Sets the point order parameter of a drawing object to make it the last one in the set to be
	 * painted. This way, the drawing object will be in front of all other ones.
	 * 
	 * @param object
	 *            the drawing object to be brought to the front
	 */
	public void bringToFront(final DrawingObject dob);

	/**
	 * Sets the point order parameter of a drawing object to make it the first one in the set to be
	 * painted. This way, the drawing object will be behind all other ones.
	 * 
	 * @param object
	 *            the drawing object to be brought to the front
	 */
	public void pushBack(final DrawingObject object);

	// --------------------------------------------------------------------------------
	/**
	 * Removes all objects from the Layer.
	 */
	public void clear();

	// --------------------------------------------------------------------------------
	/**
	 * Same as calling {@link Layer#getDrawingObjects(AbsoluteRectangle, boolean)} with
	 * <code>sorted</code> set to <code>true</code>.
	 * 
	 * @see Layer#getDrawingObjects(AbsoluteRectangle, boolean)
	 */
	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect);

	// --------------------------------------------------------------------------------
	/**
	 * Retrieves all drawing objects whose bounding box intersects with <code>rect</code>.
	 * 
	 * @param rect
	 *            the bounding box inside which the objects should be
	 * @param sorted
	 *            if the objects should be sorted according to {@link Layer#pushBack(DrawingObject)}
	 *            and {@link Layer#bringToFront(DrawingObject)}.<br/>
	 *            If set to <code>false</code> the operation runs faster, but there is no guarantee
	 *            in which order the elements are contained in the set. <br/>
	 *            If set to <code>true</code> the order reflects the order of insertion and the
	 *            {@link Layer#pushBack(DrawingObject)} and
	 *            {@link Layer#bringToFront(DrawingObject)} operations performed.
	 * @return all drawing objects that are in this layer and whose bounding boxes intersect with
	 *         <code>rect</code> as a <code>Set&lt;DrawingObject&gt;</code> when setting
	 *         <code>sorted</code> to <code>false</code>, or as a
	 *         <code>SortedSet&lt;DrawingObject&gt;</code> when setting <code>sorted</code> to
	 *         <code>true</code>.
	 */
	public Set<DrawingObject> getDrawingObjects(final AbsoluteRectangle rect, final boolean sorted);

	// --------------------------------------------------------------------------------
	/**
	 * Same as calling {@link Layer#getDrawingObjects(boolean)} with <code>sorted</code> set to
	 * <code>false</code>.
	 * 
	 * Since the only use-case at the current time is the auto zoom method there's no need to sort
	 * the elements which costs additional time (n*log(n) in the QuadTree implementation where n
	 * denotes the number of items in the tree).
	 * 
	 * @see Layer#getDrawingObjects(boolean)
	 */
	public Set<DrawingObject> getDrawingObjects();

	// --------------------------------------------------------------------------------
	/**
	 * Returns all drawing objects, sorted or not according to <code>sorted</code>.<br/>
	 * <br/>
	 * In general sorting costs time and should only be made when necessary. It costs n*log(n) in
	 * the current QuadTree implementation, since sorting is done by using a {@link TreeSet}.
	 * 
	 * @param sorted
	 *            if the objects should be sorted according to {@link Layer#pushBack(DrawingObject)}
	 *            and {@link Layer#bringToFront(DrawingObject)}.<br/>
	 *            If set to <code>false</code> the operation runs faster, but there is no guarantee
	 *            in which order the elements are contained in the set. <br/>
	 *            If set to <code>true</code> the order reflects the order of insertion and the
	 *            {@link Layer#pushBack(DrawingObject)} and
	 *            {@link Layer#bringToFront(DrawingObject)} operations performed.
	 * @return all drawing objects that are in this layer as a <code>Set&lt;DrawingObject&gt;</code>
	 *         when setting <code>sorted</code> to <code>false</code> or as a
	 *         <code>SortedSet&lt;DrawingObject&gt;</code> when setting <code>sorted</code> to
	 *         <code>true</code>.
	 */
	public Set<DrawingObject> getDrawingObjects(final boolean sorted);

	// --------------------------------------------------------------------------------
	/**
	 * Removes the drawing object <code>d</code> from the layer.
	 * 
	 * @param d
	 *            the drawing object to remove
	 */
	public void remove(final DrawingObject d);

	// --------------------------------------------------------------------------------
	/**
	 * Removes all elements from <code>dos</code> from the layer.
	 * 
	 * @param dos
	 *            the drawing objects to remove
	 */
	public void removeAll(final Set<DrawingObject> dos);

}
