/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import java.beans.PropertyChangeEvent;
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

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginXMLConfig#setSemanticTypes(int[])} yields a change
	 */
	public static final String PROPERTYNAME_SEMANTIC_TYPES = "semanticTypes";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginXMLConfig#setTimeout(int)} yields a change
	 */
	public static final String PROPERTYNAME_TIMEOUT = "timeout";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginXMLConfig#setName(String)} yields a change
	 */
	public static final String PROPERTYNAME_NAME = "name";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginXMLConfig#setVisible(boolean)} yields a change
	 */
	public static final String PROPERTYNAME_VISIBLE = "visible";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginXMLConfig#setActive(boolean)} yields a change
	 */
	public static final String PROPERTYNAME_ACTIVE = "active";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired when the call of
	 * {@link PluginXMLConfig#setSemanticTypes(int[])} yields a change which makes the plug-in to
	 * listen to all semantic types
	 */
	public static final String PROPERTYNAME_ALL_SEMANTIC_TYPES = "allSemanticTypes";

	@Element(name = PROPERTYNAME_ACTIVE, required = false)
	private volatile boolean active = true;

	@Element(name = PROPERTYNAME_VISIBLE, required = false)
	private volatile boolean visible = true;

	@Element(name = PROPERTYNAME_NAME, required = false)
	private volatile String name = "default";

	/**
	 * Timeout (in seconds)
	 */
	@Element(name = PROPERTYNAME_TIMEOUT, required = false)
	private volatile int timeout = 0;

	@ElementArray(name = PROPERTYNAME_SEMANTIC_TYPES, required = false)
	private volatile int[] semanticTypes = new int[] { -1 };

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
	public synchronized int[] getSemanticTypes() {
		return semanticTypes.clone();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns <code>true</code>, if the given semantic type is in the list of semantic types.
	 * 
	 * @param type
	 *            the semantic type to be tested
	 * @return <code>true</code>, if the given integer is in the list of semantic types.
	 */
	public synchronized boolean containsSemanticType(final int type) {
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
	public synchronized void setSemanticTypes(final int[] semanticTypes) {
		final int[] oldvalue = this.semanticTypes;

		// If "-1" is in the list, reduce it to that number
		Arrays.sort(semanticTypes);
		if (Arrays.binarySearch(semanticTypes, -1) >= 0) {
			this.semanticTypes = new int[] { -1 };
			firePropertyChange(PROPERTYNAME_ALL_SEMANTIC_TYPES, oldvalue, this.semanticTypes);
		} else {
			this.semanticTypes = semanticTypes.clone();
		}
		firePropertyChange(PROPERTYNAME_SEMANTIC_TYPES, oldvalue, this.semanticTypes);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns <code>true</code> if the plug-in is interested all packages independent of the
	 * packet's semantic type
	 * 
	 * @return <code>true</code> if the plug-in is interested all packages independent of the
	 *         packet's semantic type
	 */
	public synchronized boolean isAllSemanticTypes() {
		return (semanticTypes.length == 1) && (semanticTypes[0] == -1);
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