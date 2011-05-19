/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionstaticnodepositioner;

import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerXMLConfig;
import org.simpleframework.xml.Element;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link PositionStaticNodePositionerPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class PositionStaticNodePositionerXMLConfig extends NodePositionerXMLConfig {
    
    	public static final String PROPERTYNAME_TOPOLOGY_FILE_NAME = "topologyFileName";
		@Element(required = false)
	private volatile String topologyFileName = "";
                	// --------------------------------------------------------------------------------
	/**
	 * @return the topologyFileName
	 */
	public String getTopologyFileName() {
		return topologyFileName;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param topologyFileName
	 *            the topologyFileName to set
	 */
	public void setTopologyFileName(final String topologyFileName) {
		final String oldValue = this.topologyFileName;
		this.topologyFileName = topologyFileName;
		firePropertyChange(PROPERTYNAME_TOPOLOGY_FILE_NAME, oldValue, this.topologyFileName);
	}
}