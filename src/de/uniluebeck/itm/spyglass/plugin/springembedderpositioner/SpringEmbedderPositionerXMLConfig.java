/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import java.util.Arrays;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

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

	@ElementArray(required = false)
	private volatile int[] edgeSemanticTypes = new int[] { -1 };

	@Element(required = false)
	private volatile int optimumSpringLength = 200;

	@Element(required = false)
	private volatile double springStiffness = 0.1;

	@Element(required = false)
	private volatile int repulsionFactor = 2000000;

	@Element(required = false)
	private volatile double efficiencyFactor = 0.1;

	// public SpringEmbedderPositionerXMLConfig() {
	// this.edgeSemanticTypes.add(9);
	// }

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
	public int[] getEdgeSemanticTypes() {
		return this.edgeSemanticTypes;
	}

	public void setEdgeSemanticTypes(final int[] edgeSemanticTypes) {
		final int[] oldvalue = this.edgeSemanticTypes;

		// If "-1" is in the list, reduce it to that number
		Arrays.sort(edgeSemanticTypes);
		if (Arrays.binarySearch(edgeSemanticTypes, -1) >= 0) {
			this.edgeSemanticTypes = new int[] { -1 };
		} else {
			this.edgeSemanticTypes = edgeSemanticTypes.clone();
		}
		firePropertyChange(PROPERTYNAME_EDGE_SEMANTIC_TYPES, oldvalue, this.edgeSemanticTypes);
	}

	public boolean containsEdgeSemanticType(final int type) {
		if (this.isAllEdgeSemanticTypes()) {
			return true;
		}

		for (int i = 0; i < edgeSemanticTypes.length; i++) {
			if (edgeSemanticTypes[i] == type) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean isAllEdgeSemanticTypes() {
		return (this.edgeSemanticTypes.length == 1) && (this.edgeSemanticTypes[0] == -1);
	}
}