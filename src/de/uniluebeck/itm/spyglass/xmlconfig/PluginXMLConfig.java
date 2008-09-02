/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link Plugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public abstract class PluginXMLConfig extends PropertyBean {
	
	public static final int[] ALL_SEMANTIC_TYPES = new int[256];
	{
		for (int i = 0; i < 256; i++) {
			ALL_SEMANTIC_TYPES[i] = i;
		}
	}
	
	@Element(name = "isActive")
	private boolean active = true;
	
	@Element(name = "isVisible", required = false)
	private boolean visible = true;
	
	@Element(name = "name")
	private String name = "default";
	
	@Element(name = "timeout", required = false)
	private int timeout = -1;
	
	@ElementArray(name = "semanticTypes", required = false)
	private int[] semanticTypes = ALL_SEMANTIC_TYPES.clone();
	
	// --------------------------------------------------------------------------------
	/*
	 * @return the isActive
	 */
	public boolean getActive() {
		return active;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param active
	 *            the isActive to set
	 */
	public void setActive(final boolean active) {
		final boolean oldvalue = this.active;
		this.active = active;
		firePropertyChange("active", oldvalue, active);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the isVisible
	 */
	public boolean getVisible() {
		return visible;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param visible
	 *            the isVisible to set
	 */
	public void setVisible(final boolean visible) {
		final boolean oldvalue = this.visible;
		this.visible = visible;
		firePropertyChange("visible", oldvalue, visible);
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
		final String oldValue = this.name;
		this.name = name;
		firePropertyChange("name", oldValue, name);
	}
	
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
		final int oldvalue = this.timeout;
		this.timeout = timeout;
		firePropertyChange("timeout", oldvalue, timeout);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the semanticTypes
	 */
	public int[] getSemanticTypes() {
		return semanticTypes.clone();
	}
	
	public boolean getAllSemanticTypes() {
		
		// look for every possible semanticType 0..255 in semanticTypes
		A: for (int i = 0; i < 256; i++) {
			for (int j = 0; j < semanticTypes.length; j++) {
				if (semanticTypes[j] == i) {
					continue A;
				}
			}
			return false;
		}
		return true;
	}
	
	public void setAllSemanticTypes(final boolean value) {
		final boolean oldValueAllSemTypes = this.getAllSemanticTypes();
		if (value) {
			this.setSemanticTypes(PluginXMLConfig.ALL_SEMANTIC_TYPES);
		} else {
			this.setSemanticTypes(new int[0]);
		}
		firePropertyChange("allSemanticTypes", oldValueAllSemTypes, this.getAllSemanticTypes());
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param semanticTypes
	 *            the semanticTypes to set
	 */
	public void setSemanticTypes(final int[] semanticTypes) {
		final int[] oldvalue = this.semanticTypes;
		final boolean oldValueAllSemTypes = this.getAllSemanticTypes();
		this.semanticTypes = semanticTypes.clone();
		firePropertyChange("semanticTypes", oldvalue, semanticTypes);
		firePropertyChange("allSemanticTypes", oldValueAllSemTypes, this.getAllSemanticTypes());
	}
	
	public abstract boolean equals(final PluginXMLConfig other);
	
	protected boolean equalsRGB(final int[] lineColorRGB, final int[] otherLineColorRGB) {
		return (lineColorRGB[0] == otherLineColorRGB[0]) && (lineColorRGB[1] == otherLineColorRGB[1]) && (lineColorRGB[2] == otherLineColorRGB[2]);
	}
	
	/**
	 * Copy the data from newConfig into this object.
	 */
	public void overwriteWith(final PluginXMLConfig newConfig) {
		this.setName(newConfig.getName());
		this.setActive(newConfig.getActive());
		this.setVisible(newConfig.getVisible());
		this.setSemanticTypes(newConfig.getSemanticTypes());
		this.setTimeout(newConfig.getTimeout());
	}
	
	@Override
	public PluginXMLConfig clone() {
		
		try {
			final PluginXMLConfig clone = this.getClass().newInstance();
			clone.overwriteWith(this);
			return clone;
		} catch (final InstantiationException e) {
			throw new RuntimeException("Error during cloning", e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Error during cloning", e);
		}
	}
}