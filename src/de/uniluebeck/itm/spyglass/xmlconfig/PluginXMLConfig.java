/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import java.util.Arrays;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link Plugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public abstract class PluginXMLConfig extends XMLConfig {
	
	// private static final Logger log = SpyglassLoggerFactory.get(PluginXMLConfig.class);
	
	// public static final int[] ALL_SEMANTIC_TYPES = new int[256];
	// static {
	// for (int i = 0; i < 256; i++) {
	// ALL_SEMANTIC_TYPES[i] = i;
	// }
	// }
	
	@Element(name = "isActive")
	private boolean active = true;
	
	@Element(name = "isVisible", required = false)
	private boolean visible = true;
	
	@Element(name = "name")
	private String name = "default";
	
	@Element(name = "timeout", required = false)
	private int timeout = -1;
	
	@ElementArray(name = "semanticTypes", required = false)
	private int[] semanticTypes = new int[] { -1 };
	
	// private int[] semanticTypes = ALL_SEMANTIC_TYPES.clone();
	
	// --------------------------------------------------------------------------------
	/**
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
	 * Returns the list of semantic types.
	 * 
	 * Note that this method returns "-1" when "all semantic types" are selected.
	 * 
	 * @return the semanticTypes
	 */
	public int[] getSemanticTypes() {
		return semanticTypes.clone();
	}
	
	/**
	 * Returns true, if the given integer is in the list of semantic types.
	 */
	public boolean containsSemanticType(final int type) {
		if (this.isAllSemanticTypes()) {
			return true;
		}
		
		for (int i = 0; i < semanticTypes.length; i++) {
			if (semanticTypes[i] == type) {
				return true;
			}
		}
		return false;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param semanticTypes
	 *            the semanticTypes to set
	 */
	public void setSemanticTypes(final int[] semanticTypes) {
		final int[] oldvalue = this.semanticTypes;
		
		// If "-1" is in the list, reduce it to that number
		Arrays.sort(semanticTypes);
		if (Arrays.binarySearch(semanticTypes, -1) >= 0) {
			this.semanticTypes = new int[] { -1 };
		} else {
			this.semanticTypes = semanticTypes;
		}
		firePropertyChange("semanticTypes", oldvalue, semanticTypes);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns <code>true</code> if the plug-in is interested all packages independent of the
	 * packet's semantic type
	 * 
	 * @return <code>true</code> if the plug-in is interested all packages independent of the
	 *         packet's semantic type
	 */
	public boolean isAllSemanticTypes() {
		return (semanticTypes.length == 1) && (semanticTypes[0] == -1);
	}
	
	/**
	 * Returns <code>true</code> if the configuration is equal to certain other configuration
	 * 
	 * @param other
	 *            the configuration this one is compared to
	 * @return <code>true</code> if the configuration is equal to certain other configuration
	 */
	public boolean equals(final PluginXMLConfig other) {
		
		// needed, since invocations on implementing subclasses may fall back
		// to this method
		if (!this.getClass().equals(other.getClass())) {
			return false;
		}
		
		return (this.getActive() == other.getActive()) && (this.getVisible() == other.getVisible())
				&& this.getName().equals(other.getName())
				&& Arrays.equals(this.getSemanticTypes(), other.getSemanticTypes())
				&& (this.getTimeout() == other.getTimeout());
	}
	
	/**
	 * Returns if two color settings equal one another
	 * 
	 * @param lineColorRGB
	 *            the first color setting
	 * @param otherLineColorRGB
	 *            the second color setting
	 * @return <code>true</code> if two color settings equal one another
	 */
	protected boolean equalsRGB(final int[] lineColorRGB, final int[] otherLineColorRGB) {
		return (lineColorRGB[0] == otherLineColorRGB[0])
				&& (lineColorRGB[1] == otherLineColorRGB[1])
				&& (lineColorRGB[2] == otherLineColorRGB[2]);
	}
	
	@Override
	public final PluginXMLConfig clone() {
		
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