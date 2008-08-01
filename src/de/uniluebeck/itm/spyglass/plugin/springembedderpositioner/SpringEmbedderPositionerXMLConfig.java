/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.ElementList;

import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SpringEmbedderPositionerPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SpringEmbedderPositionerXMLConfig extends NodePositionerXMLConfig {
	
	@ElementList
	private List<Integer> edgeSemanticTypes = new LinkedList<Integer>();
	
	// --------------------------------------------------------------------------------
	/**
	 * Cosntructor
	 */
	public SpringEmbedderPositionerXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the edgeSemanticTypes
	 */
	public List<Integer> getEdgeSemanticTypes() {
		return edgeSemanticTypes;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param edgeSemanticTypes
	 *            the edgeSemanticTypes to set
	 */
	public void setEdgeSemanticTypes(final List<Integer> edgeSemanticTypes) {
		this.edgeSemanticTypes = edgeSemanticTypes;
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public boolean equals(final PluginXMLConfig other) {
		if (!(other instanceof SpringEmbedderPositionerXMLConfig)) {
			return false;
		}
		final SpringEmbedderPositionerXMLConfig o = (SpringEmbedderPositionerXMLConfig) other;
		return edgeSemanticTypes.equals(o.edgeSemanticTypes);
	}
	
}