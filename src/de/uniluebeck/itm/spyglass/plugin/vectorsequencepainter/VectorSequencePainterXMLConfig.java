/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
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

	public static final String PROPERTYNAME_LINE_WIDTH = "lineWidth";

	public static final String PROPERTYNAME_LINE_COLOR_R_G_B = "lineColorRGB";

	public static final String PROPERTYNAME_DIMENSION = "dimension";

	@Element(name = PROPERTYNAME_DIMENSION, required = false)
	private int dimension = 2;

	@ElementArray(name = PROPERTYNAME_LINE_COLOR_R_G_B, required = false)
	private int[] lineColorRGB = { 0, 0, 0 };

	@Element(name = PROPERTYNAME_LINE_WIDTH, required = false)
	private int lineWidth = 1;

	// --------------------------------------------------------------------------------
	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param dimension
	 *            the dimension to set
	 */
	public void setDimension(final int dimension) {

		firePropertyChange(PROPERTYNAME_DIMENSION, this.dimension, this.dimension = dimension);

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

		firePropertyChange(PROPERTYNAME_LINE_COLOR_R_G_B, this.lineColorRGB, this.lineColorRGB = lineColorRGB);

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

		firePropertyChange(PROPERTYNAME_LINE_WIDTH, this.lineWidth, this.lineWidth = lineWidth);
	}

	public boolean equals(final VectorSequencePainterXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		return (dimension == o.dimension) && equalsRGB(lineColorRGB, o.lineColorRGB) && (lineWidth == o.lineWidth);
	}

}