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

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SpringEmbedderPositionerPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SpringEmbedderPositionerXMLConfig extends NodePositionerXMLConfig {

	public static final String PROPERTYNAME_EDGE_SEMANTIC_TYPES = "edgeSemanticTypes";

	@ElementList(required = false)
	private volatile List<Integer> edgeSemanticTypes = new LinkedList<Integer>();

	// --------------------------------------------------------------------------------
	/**
	 * @return the edgeSemanticTypes
	 */
	public List<Integer> getEdgeSemanticTypes() {
		return new LinkedList<Integer>(edgeSemanticTypes);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param edgeSemanticTypes
	 *            the edgeSemanticTypes to set
	 */
	public void setEdgeSemanticTypes(final LinkedList<Integer> edgeSemanticTypes) {
		firePropertyChange(PROPERTYNAME_EDGE_SEMANTIC_TYPES, this.edgeSemanticTypes, this.edgeSemanticTypes = new LinkedList<Integer>(
				edgeSemanticTypes));

	}

	@Override
	public int getTimeout() {
		return 5000;
	}

}