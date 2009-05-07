/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.TreeSet;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SimpleGlobalInformationPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationXMLConfig extends PluginXMLConfig {

	/**
	 * Name of a {@link PropertyChangeEvent} which is fired whenever any statistical information
	 * evaluator is added, deleted or changed.<br>
	 * The event is fired whenever the call of
	 * {@link SimpleGlobalInformationXMLConfig#setStatisticalInformationEvaluators(Set)} yields a
	 * change
	 */
	public static final String PROPERTYNAME_STATISTICAL_INFORMATION_EVALUATORS = "statisticalInformationEvaluators";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link SimpleGlobalInformationXMLConfig#setShowNumNodes(boolean)} yields a change
	 */
	public static final String PROPERTYNAME_SHOW_NUM_NODES = "showNumNodes";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link SimpleGlobalInformationXMLConfig#setShowNodeDegree(boolean)} yields a change
	 */
	public static final String PROPERTYNAME_SHOW_NODE_DEGREE = "showNodeDegree";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link SimpleGlobalInformationXMLConfig#setSemanticTypes4Neighborhoods(int[])} yields a
	 * change
	 */
	public static final String PROPERTYNAME_SEMANTIC_TYPES4_NEIGHBORHOODS = "semanticTypes4Neighborhoods";

	@ElementArray(required = false)
	private int[] semanticTypes4Neighborhoods = { -1 };

	@Element(required = false)
	private boolean showNodeDegree = false;

	@Element(required = false)
	private boolean showNumNodes = true;

	@ElementList(required = false)
	private Set<StatisticalInformationEvaluator> statisticalInformationEvaluators = new TreeSet<StatisticalInformationEvaluator>();

	// --------------------------------------------------------------------------------
	/**
	 * Returns the semantic types of packages which provide information about neighborhood relations
	 * 
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
	 *            the semantic types of packages which provide information about neighborhood
	 *            relations.
	 */
	public synchronized void setSemanticTypes4Neighborhoods(final int[] semanticTypes4Neighborhoods) {
		firePropertyChange(PROPERTYNAME_SEMANTIC_TYPES4_NEIGHBORHOODS, this.semanticTypes4Neighborhoods,
				this.semanticTypes4Neighborhoods = semanticTypes4Neighborhoods);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the average node degree is to be shown
	 * 
	 * @return <code>true</code>if the average node degree is to be shown, <code>false</code>
	 *         otherwise
	 */
	public synchronized boolean isShowNodeDegree() {
		return showNodeDegree;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets whether the average node degree is to be shown
	 * 
	 * @param showNodeDegree
	 *            indicates whether the average node degree is to be shown
	 */
	public synchronized void setShowNodeDegree(final boolean showNodeDegree) {
		firePropertyChange(PROPERTYNAME_SHOW_NODE_DEGREE, this.showNodeDegree, this.showNodeDegree = showNodeDegree);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the number of nodes is to be shown
	 * 
	 * @return <code>true</code>if the number of nodes is to be shown, <code>false</code> otherwise
	 */
	public synchronized boolean isShowNumNodes() {
		return showNumNodes;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets whether the number of nodes is to be shown
	 * 
	 * @param showNumNodes
	 *            <code>true</code>if the number of nodes is to be shown, <code>false</code>
	 *            otherwise
	 */
	public synchronized void setShowNumNodes(final boolean showNumNodes) {
		firePropertyChange(PROPERTYNAME_SHOW_NUM_NODES, this.showNumNodes, this.showNumNodes = showNumNodes);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns an object which evaluates statistical information related to a certain semantic type
	 * 
	 * @param semanticType
	 *            the semantic type related to the statistical information
	 * @return the object which evaluates statistical information related to a certain semantic type
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
	 * Returns a set of objects which are configured to evaluate statistical information
	 * 
	 * @return a set of objects which are configured to evaluate statistical information
	 */
	public synchronized Set<StatisticalInformationEvaluator> getStatisticalInformationEvaluators() {
		return statisticalInformationEvaluators;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets a set of objects which are configured to evaluate statistical information
	 * 
	 * @param statisticalInformationEvaluators
	 *            a set of objects which are configured to evaluate statistical information
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