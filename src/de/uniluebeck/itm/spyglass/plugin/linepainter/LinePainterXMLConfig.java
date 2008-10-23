/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link LinePainterPlugin}
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class LinePainterXMLConfig extends PluginWithStringFormatterXMLConfig {
	
	@ElementArray
	private int[] lineColorRGB = { 0, 0, 0 };
	
	@Element
	private int lineWidth = 1;
	
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
		final int[] oldValue = this.lineColorRGB;
		this.lineColorRGB = lineColorRGB;
		firePropertyChange("lineColorRGB", oldValue, lineColorRGB);
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
		final int oldValue = this.lineWidth;
		this.lineWidth = lineWidth;
		firePropertyChange("lineWidth", oldValue, lineWidth);
	}
	
	public boolean equals(final LinePainterXMLConfig o) {
		
		if (!super.equals(o)) {
			return false;
		}
		
		return equalsRGB(lineColorRGB, o.lineColorRGB) && (lineWidth == o.lineWidth);
	}
	
}