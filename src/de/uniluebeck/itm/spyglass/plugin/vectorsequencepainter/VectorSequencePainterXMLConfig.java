/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link VectorSequencePainterPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class VectorSequencePainterXMLConfig extends PluginXMLConfig {
	
	@Element
	private byte dimension = 2;
	
	@ElementArray
	private int[] lineColorRGB = { 0, 0, 0 };
	
	@Element
	private int lineWidth = 1;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public VectorSequencePainterXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the dimension
	 */
	public byte getDimension() {
		return dimension;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param dimension
	 *            the dimension to set
	 */
	public void setDimension(final byte dimension) {
		this.dimension = dimension;
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
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
}