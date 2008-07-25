/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepositioner;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * 
 */
public abstract class NodePositionerPlugin extends Plugin {
	
	// --------------------------------------------------------------------------------
	/**
	 * @param nodeId
	 * @return Position of the node with the supplied nodeID
	 */
	public abstract AbsolutePosition getPosition(int nodeId);
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract int getNumNodes();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public abstract boolean offersMetric();
	
	public static String getHumanReadableName() {
		return "NodePositioner";
	}
	
	@Override
	public String toString() {
		return NodePositionerPlugin.getHumanReadableName() + "." + this.getName();
	}
	
}
