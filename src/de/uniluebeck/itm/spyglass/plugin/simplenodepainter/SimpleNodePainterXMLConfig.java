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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link SimpleNodePainterXMLConfig}
 * 
 * NOTE: the data in this class is protected by synchronized, rather than volatile.
 * the reason behind this is that making complicated methods like 
 * putExtendedInformationActive(int, boolean) thread-safe by volatile would be too 
 * error-prone. -- Dariush
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleNodePainterXMLConfig extends PluginWithStringFormatterXMLConfig {

	public static final String PROPERTYNAME_EXTENDED_DEFAULT_VALUE = "extendedDefaultValue";

	public static final String PROPERTYNAME_LINE_WIDTH = "lineWidth";

	public static final String PROPERTYNAME_LINE_COLOR_R_G_B = "lineColorRGB";

	@ElementMap(entry = "isActive", key = "nodeID", attribute = true, name = "extendedInformation", required = false)
	private HashMap<Integer, Boolean> isExtendenInformationActive = new HashMap<Integer, Boolean>();

	@Element(required = false)
	private boolean isExtendedDefaultValue = false;

	@ElementArray(required = false)
	private int[] lineColorRGB = { 255, 0, 0 };

	@Element(required = false)
	private int lineWidth = 1;

	// --------------------------------------------------------------------------------
	/**
	 * Returns if a node's the extended information is to be shown
	 * 
	 * @param nodeID
	 *            the node's identifier
	 * 
	 * @return <code>true</code> if a node's the extended information is to be shown
	 */
	public synchronized boolean isExtendedInformationActive(final int nodeID) {
		final Boolean isActive = isExtendenInformationActive.get(nodeID);

		// if there is no entry for a certain node in the map, yet, use the default behavior
		return (isActive != null) ? isActive : isExtendedDefaultValue;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets if a node's the extended information is to be shown
	 * 
	 * @param nodeID
	 *            the node's identifier
	 * @param isExtendendActive
	 *            if <code>true</code> the node's the extended information is to be shown
	 */
	public synchronized void putExtendedInformationActive(final int nodeID, final boolean isExtendendActive) {
		isExtendenInformationActive.put(nodeID, isExtendendActive);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the lineColorRGB
	 */
	public synchronized int[] getLineColorRGB() {
		return lineColorRGB.clone();

	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public synchronized void setLineColorRGB(final int[] lineColorRGB) {
		final int[] oldvalue = this.lineColorRGB.clone();
		this.lineColorRGB = lineColorRGB;
		firePropertyChange(PROPERTYNAME_LINE_COLOR_R_G_B, oldvalue, lineColorRGB);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the lineWidth
	 */
	public synchronized int getLineWidth() {
		return lineWidth;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public synchronized void setLineWidth(final int lineWidth) {
		final int oldvalue = this.lineWidth;
		this.lineWidth = lineWidth;
		firePropertyChange(PROPERTYNAME_LINE_WIDTH, oldvalue, lineWidth);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the getExtendedDefaultValue
	 */
	public synchronized boolean getExtendedDefaultValue() {
		return isExtendedDefaultValue;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param isExtendedDefaultValue
	 *            the isExtendedDefaultValue to set
	 */
	public synchronized void setExtendedDefaultValue(final boolean isExtendedDefaultValue) {
		final boolean oldValue = this.isExtendedDefaultValue;
		this.isExtendedDefaultValue = isExtendedDefaultValue;
		firePropertyChange(PROPERTYNAME_EXTENDED_DEFAULT_VALUE, oldValue, isExtendedDefaultValue);
	}

	@Override
	protected void finalize() throws Throwable {
		isExtendenInformationActive.clear();
		super.finalize();
	}

}