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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link Plugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public abstract class PluginXMLConfig {
	
	@Element(name = "isActive")
	private boolean isActive = true;
	
	@Element(name = "isVisible", required = false)
	private boolean isVisible = true;
	
	@Element(name = "name")
	private String name = "default";
	
	@Element(name = "timeout", required = false)
	private int timeout = -1;
	
	@ElementArray(name = "semanticTypes", required = false)
	private int[] semanticTypes;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public PluginXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param isVisible
	 *            the isVisible to set
	 */
	public void setVisible(final boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	// //
	// --------------------------------------------------------------------------------
	// /**
	// * @return the priority
	// */
	// public int getPriority() {
	// return priority;
	// }
	//	
	// //
	// --------------------------------------------------------------------------------
	// /**
	// * @param priority
	// * the priority to set
	// */
	// public void setPriority(final int priority) {
	// this.priority = priority;
	// }
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the timeout value
	 * 
	 * @return the timeout value
	 */
	public int getTimeout() {
		return timeout;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the timeout value
	 * 
	 * @param timeout
	 *            the timeout value to set
	 */
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the semanticTypes
	 */
	public int[] getSemanticTypes() {
		return semanticTypes;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param semanticTypes
	 *            the semanticTypes to set
	 */
	public void setSemanticTypes(final int[] semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
	
}