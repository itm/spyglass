/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

//--------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link Plugin} which makes use
 * of {@link StringFormatter} objects
 * 
 */
public abstract class PluginWithStringFormatterXMLConfig extends PluginXMLConfig {

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginWithStringFormatterXMLConfig#setDefaultStringFormatter(String)} yields a change
	 */
	public static final String PROPERTYNAME_DEFAULT_STRING_FORMATTER = "defaultStringFormatter";

	/**
	 * The name of a {@link PropertyChangeEvent} which is fired whenever the call of
	 * {@link PluginWithStringFormatterXMLConfig#setStringFormatters(HashMap)} yields a change
	 */
	public static final String PROPERTYNAME_STRING_FORMATTERS = "stringFormatters";

	@ElementMap(entry = "stringFormatter", key = "nodeID", attribute = true, valueType = String.class, required = false)
	private volatile HashMap<Integer, String> stringFormatters = new HashMap<Integer, String>();

	@Element(required = false)
	private volatile String defaultStringFormatter = "";

	// --------------------------------------------------------------------------------
	/**
	 * @return the stringFormatters
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer, String> getStringFormatters() {
		return (HashMap<Integer, String>) stringFormatters.clone();
	}

	/**
	 * Returns a string formatting object in respect to a syntax type or <code>null</code> if no
	 * matching object was created previously.
	 * 
	 * @param semanticType
	 *            a sementic type
	 * 
	 * @return a string formatting object in respect to a syntax type or <code>null</code> if no
	 *         matching object was created previously.
	 */
	public StringFormatter getStringFormatter(final int semanticType) {
		final HashMap<Integer, String> copy = this.stringFormatters; // since it may be replaced
		// while this method runs
		if (copy.containsKey(semanticType)) {
			return new StringFormatter(copy.get(semanticType));
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param stringFormatters
	 *            the stringFormatters to set
	 */
	@SuppressWarnings("unchecked")
	public void setStringFormatters(final HashMap<Integer, String> stringFormatters) {
		final Map<Integer, String> oldValue = this.stringFormatters;
		this.stringFormatters = (HashMap<Integer, String>) stringFormatters.clone();
		firePropertyChange(PROPERTYNAME_STRING_FORMATTERS, oldValue, this.stringFormatters);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the defaultStringFormatter
	 */
	public String getDefaultStringFormatter() {
		return defaultStringFormatter;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param defaultStringFormatter
	 *            the defaultStringFormatter to set
	 */
	public void setDefaultStringFormatter(final String defaultStringFormatter) {
		final String oldValue = this.defaultStringFormatter;
		this.defaultStringFormatter = defaultStringFormatter;
		firePropertyChange(PROPERTYNAME_DEFAULT_STRING_FORMATTER, oldValue, defaultStringFormatter);
	}
}
