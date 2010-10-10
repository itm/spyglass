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

// --------------------------------------------------------------------------------
/**
 * A Store for DataPoints with a k-Nearest-Neighbor implementation
 * 
 * @author Dariush Forouher
 * 
 */
public class DataStore extends AbstractDataStore<DataPoint> {

	/**
	 *
	 */
	private static final long serialVersionUID = -8466264495244878677L;

	@Override
	public DataStore clone() {
		final DataStore store = new DataStore();

		// this destroys the last hope of any half-ass acceptable performance.
		// but who wants that would have to reimplement the dataStore anyway with
		// a datastructure more suited for k-NN (e.g. a kd-tree)
		for (final DataPoint p : this) {
			store.add(p.clone());
		}

		return store;
	}

}
