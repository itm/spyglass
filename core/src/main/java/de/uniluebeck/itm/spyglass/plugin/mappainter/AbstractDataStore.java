/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * A Store for DataPoints with a k-Nearest-Neighbor implementation
 * 
 * @author Dariush Forouher
 * 
 */
public abstract class AbstractDataStore<T extends MetricPoint> extends HashSet<T> {

	/**
	 *
	 */
	private static final long serialVersionUID = -8466264495244878677L;

	/**
	 * Performs a k-nearest-neighbor search on the DataStore.
	 * 
	 * @param k
	 *            number of points to return (must be at least 1)
	 * @param pos
	 *            a position
	 * @return the k points nearest to the given position (or less points, if there are not enough
	 *         in the store)
	 */
	public List<T> kNN(final AbsolutePosition pos, final int k) {
		if (k < 1) {

			throw new IllegalArgumentException("k must be at least 1!");

		} else if (k > this.size()) {

			return new ArrayList<T>(this);

		} else {

			final ArrayList<T> list = new ArrayList<T>();

			// TODO: do some profiling and replace this with an O(log n) algorithm
			// if necessary.
			for (final T dataPoint : this) {
				final double distance = pos.getEuclideanDistance(dataPoint.position);
				for (int i = 0; i < k; i++) {
					if ((list.size() < i + 1) || (list.get(i).position.getEuclideanDistance(pos) > distance)) {
						list.add(i, dataPoint);
						break;
					}
				}
			}

			return k == 1 ? Collections.singletonList(list.get(0)) : list.subList(0, k);
		}
	}

}
