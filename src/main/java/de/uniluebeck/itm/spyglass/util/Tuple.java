/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.util;

// --------------------------------------------------------------------------------
/**
 * A simple type-safe helper class for methods returning or receiving tuples of data.
 * 
 * @author Daniel Bimschas
 * @param <V>
 * @param <W>
 */
public class Tuple<V, W> {

	/**
	 * The first element of the tuple.
	 */
	public V first;

	/**
	 * The second element of the tuple.
	 */
	public W second;

	// --------------------------------------------------------------------------------
	/**
	 * Constructs a new Tuple instance.
	 * 
	 * @param first
	 * @param second
	 */
	public Tuple(final V first, final W second) {
		super();
		this.first = first;
		this.second = second;
	}

}
