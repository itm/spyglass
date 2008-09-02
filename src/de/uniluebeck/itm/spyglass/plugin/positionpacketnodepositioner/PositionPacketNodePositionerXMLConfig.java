/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link PositionPacketNodePositionerPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class PositionPacketNodePositionerXMLConfig extends NodePositionerXMLConfig {
	
	@Override
	public boolean equals(final PluginXMLConfig other) {
		return other instanceof PositionPacketNodePositionerXMLConfig ? getTimeToLive() == ((NodePositionerXMLConfig) other).getTimeToLive() : false;
	}
	
	@Override
	public void overwriteWith(final PluginXMLConfig newConfig) {
		super.overwriteWith(newConfig);
		final PositionPacketNodePositionerXMLConfig newConfig2 = (PositionPacketNodePositionerXMLConfig) newConfig;
	}
	
}