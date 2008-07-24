/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
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
	
	@ElementArray
	private int[] lineColorRGB = { 0, 0, 0 };
	
	@Element
	private int lineWidth = 1;
	
	@ElementMap(entry = "stringFormatter", key = "nodeID", attribute = true, valueType = StringFormatter.class, required = false)
	private HashMap<Integer, StringFormatter> stringFormatters = null;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleNodePainterXMLConfig() {
		
	}
	
	/**
	 * @return the isExtendenInformationActive
	 */
	public HashMap<Integer, Boolean> getIsExtendenInformationActive() {
		return isExtendenInformationActive;
	}
	
	/**
	 * @param isExtendenInformationActive
	 *            the isExtendenInformationActive to set
	 */
	public void setIsExtendenInformationActive(final HashMap<Integer, Boolean> isExtendenInformationActive) {
		this.isExtendenInformationActive = isExtendenInformationActive;
	}
	
	/**
	 * @return the lineColorRGB
	 */
	public int[] getLineColorRGB() {
		return lineColorRGB;
		
	}
	
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public void setLineColor(final Color lineColor) {
		this.lineColorRGB = new int[] { lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue() };
	}
	
	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}
	
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	/**
	 * @return the stringFormatters
	 */
	public HashMap<Integer, StringFormatter> getStringFormatters() {
		return stringFormatters;
	}
	
	/**
	 * @param stringFormatters
	 *            the stringFormatters to set
	 */
	public void setStringFormatters(final HashMap<Integer, StringFormatter> stringFormatters) {
		this.stringFormatters = stringFormatters;
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
}