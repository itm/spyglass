/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepositioner;

import java.util.List;
import java.util.Map;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionData;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Instances of this class provide information about the position of nodes. These positions may be
 * based on a metric but it cannot be granted.
 */
public abstract class NodePositionerPlugin extends Plugin {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public NodePositionerPlugin() {
		super(true);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a node's absolute position
	 * 
	 * @param nodeId
	 *            the node's identifier
	 * @return Position of the node with the supplied nodeID or <code>null</code> if the nodeId is
	 *         unknown
	 */
	public abstract AbsolutePosition getPosition(int nodeId);

	// --------------------------------------------------------------------------------
	/**
	 * Returns the number of nodes which are currently active
	 * 
	 * @return the number of nodes which are currently active
	 */
	public abstract int getNumNodes();

	// --------------------------------------------------------------------------------
	/**
	 * Returns a list of all nodes that this NodePositionPlugin currently has location information
	 * for
	 * 
	 * @return a list of node-IDs
	 */
	public abstract List<Integer> getNodeList();

	// --------------------------------------------------------------------------------
	/**
	 * Returns a list of all nodes and the appropriate PositionData objects that this
	 * NodePositionPlugin currently has location information for
	 * 
	 * @return a Map containg node-IDs and PositionData objects
	 */
	public abstract Map<Integer, PositionData> getNodeMap();

	// --------------------------------------------------------------------------------
	/**
	 * Add all nodes and its position information that the given Map contains into the nodeMap
	 */
	public abstract void addNodes(final Map<Integer, PositionData> oldNodeMap);

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the node positioner offers metrical information
	 * 
	 * @return <code>true</code> if the node positioner offers metrical information,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean offersMetric();

	public static String getHumanReadableName() {
		return "NodePositioner";
	}

	@Override
	public String toString() {
		return NodePositionerPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}

}
