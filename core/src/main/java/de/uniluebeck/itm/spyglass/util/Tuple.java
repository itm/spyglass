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

import org.simpleframework.xml.Element;

// --------------------------------------------------------------------------------
/**
 * A simple type-safe helper class for methods returning or receiving tuples of data.
 * 
 * @author Daniel Bimschas
 * @param <V>
 * @param <W>
 */
public class Tuple<V, W> {

	@Element(required = true)
    private V first;

	@Element(required = true)
    private W second;

    public Tuple(){
       super(); 
    }

	// --------------------------------------------------------------------------------
	/**
	 * Constructs a new Tuple instance.
	 * 
	 * @param first
	 * @param second
	 */
	public Tuple(final V first, final W second) {
		this();
		this.first = first;
		this.second = second;
	}

    /**
     * The first element of the tuple.
     */
    public V getFirst() {
        return first;
    }

    public void setFirst(V first) {
        this.first = first;
    }

    /**
     * The second element of the tuple.
     */
    public W getSecond() {
        return second;
    }

    public void setSecond(W second) {
        this.second = second;
    }
}
