/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A Node Position description.
 * 
 * coordinates can be made up of ranges, like
 * 
 * x: 10-20,40-56, y: 15,16,17 z: 0
 * 
 * @author dariush
 * 
 */
@Root
public class Position {

	/**
	 * x coordinate.
	 */
	@Attribute
	String x;

	/**
	 * y coordinate.
	 */
	@Attribute
	String y;

	/**
	 * z coordinate.
	 */
	@Attribute
	String z;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public Position() {
		// nothing to do
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param x
	 *            the position's x-coordinate
	 * @param y
	 *            the position's y-coordinate
	 * @param z
	 *            the position's z-coordinate
	 */
	public Position(final String x, final String y, final String z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
