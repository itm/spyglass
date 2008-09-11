/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SimpleNodePainterXMLConfig}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleNodePainterXMLConfig extends PluginXMLConfig {
	
	@ElementMap(entry = "isActive", key = "nodeID", attribute = true, name = "extendedInformation")
	private HashMap<Integer, Boolean> isExtendenInformationActive = new HashMap<Integer, Boolean>();
	
	@Element(name = "isExtendedDefaultValue")
	private boolean isExtendedDefaultValue = false;
	
	@ElementArray
	private int[] lineColorRGB = { 255, 0, 0 };
	
	@Element
	private int lineWidth = 1;
	
	@ElementMap(entry = "stringFormatter", key = "nodeID", attribute = true, valueType = String.class, required = true)
	private HashMap<Integer, String> stringFormatters = new HashMap<Integer, String>();
	
	@Element(name = "defaultStringFormatter", required = false)
	private String defaultStringFormatter = null;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleNodePainterXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns if a node's the extended information is to be shown
	 * 
	 * @return <code>true</code> if a node's the extended information is to be shown
	 */
	public boolean isExtendedInformationActive(final int nodeID) {
		final Boolean isActive = isExtendenInformationActive.get(nodeID);
		
		// if there is no entry for a certain node in the map, yet, use the default behavior
		return (isActive != null) ? isActive : isExtendedDefaultValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param isExtendenInformationActive
	 *            the isExtendenInformationActive to set
	 */
	public void setIsExtendenInformationActive(
			final HashMap<Integer, Boolean> isExtendenInformationActive) {
		this.isExtendenInformationActive = isExtendenInformationActive;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lineColorRGB
	 */
	public int[] getLineColorRGB() {
		return lineColorRGB.clone();
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public void setLineColorRGB(final int[] lineColorRGB) {
		final int[] oldvalue = this.lineColorRGB.clone();
		this.lineColorRGB = lineColorRGB;
		firePropertyChange("lineColor", oldvalue, lineColorRGB);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final int lineWidth) {
		final int oldvalue = this.lineWidth;
		this.lineWidth = lineWidth;
		firePropertyChange("lineWidth", oldvalue, lineWidth);
	}
	
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
	 * matching object was created previously and the default expression is undefined.
	 * 
	 * @return a string formatting object in respect to a syntax type or <code>null</code> if no
	 *         matching object was created previously and the default expression is undefined.
	 */
	public StringFormatter getStringFormatter(final int semanticType) {
		// TODO: SE - get all string formatters for complete information ????
		// or better save HashMap of semantic types and strings
		if (stringFormatters.containsKey(semanticType)) {
			return new StringFormatter(stringFormatters.get(semanticType));
		} else if (defaultStringFormatter != null) {
			return new StringFormatter(defaultStringFormatter);
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
		firePropertyChange("stringFormatters", oldValue, stringFormatters);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the getExtendedDefaultValue
	 */
	public boolean getExtendedDefaultValue() {
		return isExtendedDefaultValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param isExtendedDefaultValue
	 *            the isExtendedDefaultValue to set
	 */
	public void setExtendedDefaultValue(final boolean isExtendedDefaultValue) {
		final boolean oldValue = this.isExtendedDefaultValue;
		this.isExtendedDefaultValue = isExtendedDefaultValue;
		firePropertyChange("extendedDefaultValue", oldValue, isExtendedDefaultValue);
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
		firePropertyChange("defaultStringFormatter", oldValue, defaultStringFormatter);
	}
	
	/**
	 * Copy the data from newConfig into this object.
	 */
	@Override
	public void overwriteWith(final PluginXMLConfig newConfig) {
		super.overwriteWith(newConfig);
		final SimpleNodePainterXMLConfig newConfig2 = (SimpleNodePainterXMLConfig) newConfig;
		
		this.setIsExtendenInformationActive(newConfig2.isExtendenInformationActive);
		this.setExtendedDefaultValue(newConfig2.getExtendedDefaultValue());
		this.setLineColorRGB(newConfig2.getLineColorRGB());
		this.setLineWidth(newConfig2.getLineWidth());
		this.setStringFormatters(newConfig2.getStringFormatters());
		this.setDefaultStringFormatter(newConfig2.getDefaultStringFormatter());
		
	}
	
	@Override
	public boolean equals(final PluginXMLConfig other) {
		if (!(other instanceof SimpleNodePainterXMLConfig)) {
			return false;
		}
		final SimpleNodePainterXMLConfig o = (SimpleNodePainterXMLConfig) other;
		
		return (defaultStringFormatter != null)
				&& defaultStringFormatter.equals(o.defaultStringFormatter)
				&& (isExtendedDefaultValue == o.isExtendedDefaultValue)
				&& (isExtendenInformationActive == o.isExtendenInformationActive)
				&& equalsRGB(lineColorRGB, o.lineColorRGB) && (lineWidth == o.lineWidth)
				&& stringFormatters.equals(o.stringFormatters);
	}
	
}