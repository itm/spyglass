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
 * @author Sebastian Ebers, Daniel Bimschas
 * 
 */
public abstract class PluginXMLConfig extends XMLConfig {

	public static final String PROPERTYNAME_SEMANTIC_TYPES = "semanticTypes";

	public static final String PROPERTYNAME_TIMEOUT = "timeout";

	public static final String PROPERTYNAME_NAME = "name";

	public static final String PROPERTYNAME_VISIBLE = "visible";

	public static final String PROPERTYNAME_ACTIVE = "active";

	public static final String PROPERTYNAME_ALL_SEMANTIC_TYPES = "allSemanticTypes";

	@Element(name = PROPERTYNAME_ACTIVE, required = false)
	private boolean active = true;

	@Element(name = PROPERTYNAME_VISIBLE, required = false)
	private boolean visible = true;

	@Element(name = PROPERTYNAME_NAME, required = false)
	private String name = "default";

	@Element(name = PROPERTYNAME_TIMEOUT, required = false)
	private int timeout = -1;

	@ElementArray(name = PROPERTYNAME_SEMANTIC_TYPES, required = false)
	private int[] semanticTypes = new int[] { -1 };

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
		firePropertyChange(PROPERTYNAME_ACTIVE, oldvalue, active);
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
		firePropertyChange(PROPERTYNAME_VISIBLE, oldvalue, visible);
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
		firePropertyChange(PROPERTYNAME_NAME, oldValue, name);
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
		firePropertyChange(PROPERTYNAME_TIMEOUT, oldvalue, timeout);
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
		firePropertyChange(PROPERTYNAME_SEMANTIC_TYPES, oldvalue, semanticTypes);
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

		return (this.getActive() == other.getActive()) && (this.getVisible() == other.getVisible()) && this.getName().equals(other.getName())
				&& Arrays.equals(this.getSemanticTypes(), other.getSemanticTypes()) && (this.getTimeout() == other.getTimeout());
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
	protected static boolean equalsRGB(final int[] lineColorRGB, final int[] otherLineColorRGB) {
		return (lineColorRGB[0] == otherLineColorRGB[0]) && (lineColorRGB[1] == otherLineColorRGB[1]) && (lineColorRGB[2] == otherLineColorRGB[2]);
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