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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SpringEmbedderPositionerPlugin}
 * 
 * @author Sebastian Ebers, Oliver Kleine
 * 
 */
public class SpringEmbedderPositionerXMLConfig extends NodePositionerXMLConfig {

	public static final String PROPERTYNAME_EDGE_SEMANTIC_TYPES = "edgeSemanticTypes";

	public static final String PROPERTYNAME_OPTIMUM_SPRING_LENGTH = "optimumSpringLength";

	public static final String PROPERTYNAME_SPRING_STIFFNESS = "springStiffness";

	public static final String PROPERTYNAME_REPULSION_FACTOR = "repulsionFactor";

	public static final String PROPERTYNAME_EFFICIENCY_FACTOR = "efficiencyFactor";

	@ElementList(required = false)
	private volatile List<Integer> edgeSemanticTypes = new LinkedList<Integer>();

	@Element(required = false)
	private volatile int optimumSpringLength = 200;

	@Element(required = false)
	private volatile double springStiffness = 0.1;

	@Element(required = false)
	private volatile int repulsionFactor = 2000000;

	@Element(required = false)
	private volatile double efficiencyFactor = 0.1;

	public SpringEmbedderPositionerXMLConfig() {
		this.edgeSemanticTypes.add(9);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the optimumSpringLength
	 */
	public int getOptimumSpringLength() {
		return optimumSpringLength;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param optimumSpringLength
	 *            the optimumSpringLength to set
	 */
	public void setOptimumSpringLength(final int optimumSpringLength) {
		this.optimumSpringLength = optimumSpringLength;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the springStiffness
	 */
	public double getSpringStiffness() {
		return springStiffness;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param springStiffness
	 *            the springStiffness to set
	 */
	public void setSpringStiffness(final double springStiffness) {
		this.springStiffness = springStiffness;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the repulsionFactor
	 */
	public int getRepulsionFactor() {
		return repulsionFactor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param repulsionFactor
	 *            the repulsionFactor to set
	 */
	public void setRepulsionFactor(final int repulsionFactor) {
		this.repulsionFactor = repulsionFactor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the efficiencyFactor
	 */
	public double getEfficiencyFactor() {
		return efficiencyFactor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param efficiencyFactor
	 *            the efficiencyFactor to set
	 */
	public void setEfficiencyFactor(final double efficiencyFactor) {
		this.efficiencyFactor = efficiencyFactor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the edgeSemanticTypes
	 */
	public List<Integer> getEdgeSemanticTypes() {
		return this.edgeSemanticTypes;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param edgeSemanticTypes
	 *            the edgeSemanticTypes to set
	 */
	public void setEdgeSemanticTypes(final List<Integer> edgeSemanticTypes) {
		firePropertyChange(PROPERTYNAME_EDGE_SEMANTIC_TYPES, this.edgeSemanticTypes, edgeSemanticTypes);
		this.edgeSemanticTypes = edgeSemanticTypes;
	}
}