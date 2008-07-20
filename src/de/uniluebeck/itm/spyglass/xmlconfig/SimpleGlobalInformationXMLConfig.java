/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import de.uniluebeck.itm.spyglass.plugin.SimpleGlobalInformationPlugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SimpleGlobalInformationPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationXMLConfig extends PluginXMLConfig {
	
	@ElementList
	private List<Integer> semanticTypes4Neighborhoods = new LinkedList<Integer>();
	
	@Element
	private boolean showNodeDegree = true;
	
	@Element
	private boolean showNumNodes = true;
	
	@ElementList(required = false)
	private List<StringFormatterSettings> stringFormatterSettings = null;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleGlobalInformationXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the semanticTypes4Neighborhoods
	 */
	public List<Integer> getSemanticTypes4Neighborhoods() {
		return semanticTypes4Neighborhoods;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param semanticTypes4Neighborhoods
	 *            the semanticTypes4Neighborhoods to set
	 */
	public void setSemanticTypes4Neighborhoods(final List<Integer> semanticTypes4Neighborhoods) {
		this.semanticTypes4Neighborhoods = semanticTypes4Neighborhoods;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the showNodeDegree
	 */
	public boolean isShowNodeDegree() {
		return showNodeDegree;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param showNodeDegree
	 *            the showNodeDegree to set
	 */
	public void setShowNodeDegree(final boolean showNodeDegree) {
		this.showNodeDegree = showNodeDegree;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the showNumNodes
	 */
	public boolean isShowNumNodes() {
		return showNumNodes;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param showNumNodes
	 *            the showNumNodes to set
	 */
	public void setShowNumNodes(final boolean showNumNodes) {
		this.showNumNodes = showNumNodes;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the stringFormatterSettings
	 */
	public List<StringFormatterSettings> getStringFormatterSettings() {
		return stringFormatterSettings;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param stringFormatterSettings
	 *            the stringFormatterSettings to set
	 */
	public void setStringFormatterSettings(final List<StringFormatterSettings> stringFormatterSettings) {
		this.stringFormatterSettings = stringFormatterSettings;
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
}