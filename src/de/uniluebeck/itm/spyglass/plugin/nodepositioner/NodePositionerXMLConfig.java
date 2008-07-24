/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepositioner;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link NodePositionerPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class NodePositionerXMLConfig extends PluginXMLConfig {
	
	@Element
	private int timeToLive = 10;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public NodePositionerXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the timeToLive
	 */
	public int getTimeToLive() {
		return timeToLive;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param timeToLive
	 *            the timeToLive to set
	 */
	public void setTimeToLive(final int timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
}