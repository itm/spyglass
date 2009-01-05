/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.util.Set;
import java.util.TreeSet;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.StatisticalInformationEvaluator;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SimpleGlobalInformationPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationXMLConfig extends PluginXMLConfig {

	public static final String PROPERTYNAME_STATISTICAL_INFORMATION_EVALUATORS = "statisticalInformationEvaluators";

	public static final String PROPERTYNAME_SHOW_NUM_NODES = "showNumNodes";

	public static final String PROPERTYNAME_SHOW_NODE_DEGREE = "showNodeDegree";

	public static final String PROPERTYNAME_SEMANTIC_TYPES4_NEIGHBORHOODS = "semanticTypes4Neighborhoods";

	@ElementArray(required = false)
	private int[] semanticTypes4Neighborhoods;

	@Element(required = false)
	private boolean showNodeDegree = true;

	@Element(required = false)
	private boolean showNumNodes = true;

	@ElementList(required = false)
	private Set<StatisticalInformationEvaluator> statisticalInformationEvaluators = new TreeSet<StatisticalInformationEvaluator>();

	// --------------------------------------------------------------------------------
	/**
	 * @return the semanticTypes4Neighborhoods
	 */
	public synchronized int[] getSemanticTypes4Neighborhoods() {
		return semanticTypes4Neighborhoods;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the semantic types of packages which provide information about neighborhood relations.
	 * 
	 * @param semanticTypes4Neighborhoods
	 *            tSets the semantic types of packages which provide information about neighborhood
	 *            relations.
	 */
	public synchronized void setSemanticTypes4Neighborhoods(final int[] semanticTypes4Neighborhoods) {
		firePropertyChange(PROPERTYNAME_SEMANTIC_TYPES4_NEIGHBORHOODS, this.semanticTypes4Neighborhoods,
				this.semanticTypes4Neighborhoods = semanticTypes4Neighborhoods);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the showNodeDegree
	 */
	public synchronized boolean isShowNodeDegree() {
		return showNodeDegree;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param showNodeDegree
	 *            the showNodeDegree to set
	 */
	public synchronized void setShowNodeDegree(final boolean showNodeDegree) {

		firePropertyChange(PROPERTYNAME_SHOW_NODE_DEGREE, this.showNodeDegree, this.showNodeDegree = showNodeDegree);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the showNumNodes
	 */
	public synchronized boolean isShowNumNodes() {
		return showNumNodes;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param showNumNodes
	 *            the showNumNodes to set
	 */
	public synchronized void setShowNumNodes(final boolean showNumNodes) {
		firePropertyChange(PROPERTYNAME_SHOW_NUM_NODES, this.showNumNodes, this.showNumNodes = showNumNodes);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param semanticType
	 * @return the statisticalInformationEvaluators
	 */
	public synchronized StatisticalInformationEvaluator getStatisticalInformationEvaluators4Type(final int semanticType) {
		for (final StatisticalInformationEvaluator sfs : statisticalInformationEvaluators) {
			if (sfs.getSemanticType() == semanticType) {
				return sfs;
			}
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the statisticalInformationEvaluators
	 */
	public synchronized Set<StatisticalInformationEvaluator> getStatisticalInformationEvaluators() {
		return statisticalInformationEvaluators;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param statisticalInformationEvaluators
	 *            the statisticalInformationEvaluators to set
	 */
	public synchronized void setStatisticalInformationEvaluators(final Set<StatisticalInformationEvaluator> statisticalInformationEvaluators) {
		// these extensive operations are necessary to prevent dependencies when creating new
		// instances of the associated plug-in
		final Set<StatisticalInformationEvaluator> oldValue = new TreeSet<StatisticalInformationEvaluator>(this.statisticalInformationEvaluators);
		this.statisticalInformationEvaluators.clear();
		for (final StatisticalInformationEvaluator e : statisticalInformationEvaluators) {
			this.statisticalInformationEvaluators.add(e.clone());
		}
		firePropertyChange(PROPERTYNAME_STATISTICAL_INFORMATION_EVALUATORS, oldValue, this.statisticalInformationEvaluators);
	}

}