/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Data representing one node
 * 
 * (the nodeID itself is not part of this object)
 * 
 * @author Dariush Forouher
 * 
 */
public class PositionData {

	/**
	 * Current position of the node
	 */
	public AbsolutePosition position;

	/**
	 * Timestamp (in millies) when the node was last seen.
	 */
	public long lastSeen;

	public PositionData(final AbsolutePosition position, final long lastSeen) {
		super();
		this.position = position;
		this.lastSeen = lastSeen;
	}

	@Override
	public PositionData clone() {
		return new PositionData(position, lastSeen);

	}
}