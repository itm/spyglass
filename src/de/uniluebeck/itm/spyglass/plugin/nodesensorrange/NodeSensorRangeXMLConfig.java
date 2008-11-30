/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.util.HashMap;

import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link NodeSensorRangePlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class NodeSensorRangeXMLConfig extends PluginXMLConfig {

	public static final String PROPERTYNAME_NODE_RANGES = "nodeRanges";

	//@Element(required=false)
	// TODO: solve cyclic dependencies here
	// private NodeSensorRange defalutNodeSensorRange = new NodeSensorRange();
	@ElementMap(entry = "ranges", key = "nodeID", attribute = true, valueType = NodeSensorRange.class, required=false)
	private HashMap<Integer, NodeSensorRange> nodeRanges = new HashMap<Integer, NodeSensorRange>();

	// //
	// --------------------------------------------------------------------------------
	// /**
	// * @return the defalutNodeSensorRange
	// */
	// public NodeSensorRange getDefalutNodeSensorRange() {
	// return defalutNodeSensorRange;
	// }
	//	
	// //
	// --------------------------------------------------------------------------------
	// /**
	// * @param defalutNodeSensorRange
	// * the defalutNodeSensorRange to set
	// */
	// public void setDefalutNodeSensorRange(final NodeSensorRange
	// defalutNodeSensorRange) {
	// this.defalutNodeSensorRange = defalutNodeSensorRange;
	// }

	// --------------------------------------------------------------------------------
	/**
	 * @return the nodeRanges
	 */
	public HashMap<Integer, NodeSensorRange> getNodeRanges() {
		return nodeRanges;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param nodeRanges
	 *            the nodeRanges to set
	 */
	public void setNodeRanges(final HashMap<Integer, NodeSensorRange> nodeRanges) {

		firePropertyChange(PROPERTYNAME_NODE_RANGES, this.nodeRanges, this.nodeRanges = nodeRanges);

	}

	public boolean equals(final NodeSensorRangeXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		return nodeRanges.equals((o).nodeRanges);
	}

}