/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionData;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * The PositionDataSE is used by the SpringEmbedderPositioner to provide the nodes positions with
 * respect to the calculated attraction and repulsion forces.
 * 
 * Additionally it keeps the real positions in the background.
 * 
 * @author Oliver Kleine
 * 
 */
public class PositionDataSE extends PositionData {

	public AbsolutePosition sePosition;

	// --------------------------------------------------------------------------------
	/**
	 * @param position
	 * @param lastSeen
	 */
	public PositionDataSE(final AbsolutePosition position, final AbsolutePosition sePosition, final long lastSeen) {
		super(position, lastSeen);
		this.sePosition = sePosition;
	}

	@Override
	public PositionDataSE clone() {
		return new PositionDataSE(position, sePosition, lastSeen);

	}

}
