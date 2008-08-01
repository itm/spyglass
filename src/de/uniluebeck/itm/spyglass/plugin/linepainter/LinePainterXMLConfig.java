/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link LinePainterPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class LinePainterXMLConfig extends PluginXMLConfig {
	
	@ElementArray
	private int[] lineColorRGB = { 0, 0, 0 };
	
	@Element
	private int lineWidth = 1;
	
	@ElementMap(entry = "stringFormatter", key = "nodeID", attribute = true, valueType = StringFormatter.class)
	private HashMap<Integer, StringFormatter> stringFormatters = new HashMap<Integer, StringFormatter>();
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public LinePainterXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lineColorRGB
	 */
	public int[] getLineColorRGB() {
		return lineColorRGB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public void setLineColorRGB(final int[] lineColorRGB) {
		this.lineColorRGB = lineColorRGB;
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
		this.lineWidth = lineWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the stringFormatters
	 */
	public HashMap<Integer, StringFormatter> getStringFormatters() {
		return stringFormatters;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param stringFormatters
	 *            the stringFormatters to set
	 */
	public void setStringFormatters(final HashMap<Integer, StringFormatter> stringFormatters) {
		this.stringFormatters = stringFormatters;
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public boolean equals(final PluginXMLConfig other) {
		if (!(other instanceof LinePainterXMLConfig)) {
			return false;
		}
		final LinePainterXMLConfig o = (LinePainterXMLConfig) other;
		return equalsRGB(lineColorRGB, o.lineColorRGB) && (lineWidth == o.lineWidth) && stringFormatters.equals(o.stringFormatters);
	}
	
}