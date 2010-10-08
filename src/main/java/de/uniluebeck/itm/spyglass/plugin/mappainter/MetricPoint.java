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

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Element for the DataStore structure.
 * 
 * @author Dariush Forouher
 * 
 */
public abstract class MetricPoint implements Comparable<MetricPoint> {

	/**
	 * The position of this pata point
	 */
	public AbsolutePosition position = null;

	/**
	 * the value
	 */
	public double value = 0;

}
