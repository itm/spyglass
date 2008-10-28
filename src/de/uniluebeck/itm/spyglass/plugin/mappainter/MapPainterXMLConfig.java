/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link MapPainterPlugin}
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class MapPainterXMLConfig extends PluginXMLConfig {
	
	@Element
	private float defaultValue = 3;
	
	@Element
	private int gridElementHeight = 1;
	
	@Element
	private int gridElementWidth = 1;
	
	@Element
	private int lowerLeftX = 0;
	
	@Element
	private int lowerLeftY = 0;
	
	@Element
	private boolean lockGridElementSquare = true;
	
	@Element
	private boolean lockNumberOfRowsNCols = true;
	
	@ElementArray
	private int[] maxColorRGB = { 0, 0, 0 };
	
	@Element
	private float maxValue = 1;
	
	@ElementArray
	private int[] minColorRGB = { 255, 255, 255 };
	
	@Element
	private float minValue = 0;
	
	@Element
	private int width = 1;
	
	@Element
	private int numFramePointsHorizontal = 3;
	
	@Element
	private int numFramePointsVertical = 3;
	
	@Element
	private int height = 1;
	
	@Element
	private int refreshInterval = 10;
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the defaultValue
	 */
	public float getDefaultValue() {
		return defaultValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(final float framePointDefaultValue) {
		firePropertyChange("defaultValue", this.defaultValue,
				this.defaultValue = framePointDefaultValue);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the gridElementHeight
	 */
	public int getGridElementHeight() {
		return gridElementHeight;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param gridElementHeight
	 *            the gridElementHeight to set
	 */
	public void setGridElementHeight(final int gridElementHeight) {
		firePropertyChange("gridElementHeight", this.gridElementHeight,
				this.gridElementHeight = gridElementHeight);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the gridElementWidth
	 */
	public int getGridElementWidth() {
		return gridElementWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param gridElementWidth
	 *            the gridElementWidth to set
	 */
	public void setGridElementWidth(final int gridElementWidth) {
		firePropertyChange("gridElementWidth", this.gridElementWidth,
				this.gridElementWidth = gridElementWidth);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getLowerLeftX() {
		return lowerLeftX;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lowerLeftX
	 */
	public void setLowerLeftX(final int lowerLeftX) {
		firePropertyChange("lowerLeftX", this.lowerLeftX, this.lowerLeftX = lowerLeftX);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getLowerLeftY() {
		return lowerLeftY;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lowerLeftY
	 */
	public void setLowerLeftY(final int lowerLeftY) {
		firePropertyChange("lowerLeftY", this.lowerLeftY, this.lowerLeftY = lowerLeftY);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lockGridElementSquare
	 */
	public boolean getLockGridElementSquare() {
		return lockGridElementSquare;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockGridElementSquare
	 *            the lockGridElementSquare to set
	 */
	public void setLockGridElementSquare(final boolean lockGridElementSquare) {
		
		firePropertyChange("lockGridElementSquare", this.lockGridElementSquare,
				this.lockGridElementSquare = lockGridElementSquare);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lockNumberOfRowsNCols
	 */
	public boolean getLockNumberOfRowsNCols() {
		return lockNumberOfRowsNCols;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockNumberOfRowsNCols
	 *            the lockNumberOfRowsNCols to set
	 */
	public void setLockNumberOfRowsNCols(final boolean lockNumberOfRowsNCols) {
		
		firePropertyChange("lockNumberOfRowsNCols", this.lockNumberOfRowsNCols,
				this.lockNumberOfRowsNCols = lockNumberOfRowsNCols);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the maxColorRGB
	 */
	public int[] getMaxColorRGB() {
		return maxColorRGB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param maxColorRGB
	 *            the maxColorRGB to set
	 */
	public void setMaxColorRGB(final int[] maxColorRGB) {
		
		firePropertyChange("maxColorRGB", this.maxColorRGB, this.maxColorRGB = maxColorRGB);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the maxValue
	 */
	public float getMaxValue() {
		return maxValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param maxValue
	 *            the maxValue to set
	 */
	public void setMaxValue(final float maxValue) {
		
		firePropertyChange("maxValue", this.maxValue, this.maxValue = maxValue);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the minColorRGB
	 */
	public int[] getMinColorRGB() {
		return minColorRGB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param minColorRGB
	 *            the minColorRGB to set
	 */
	public void setMinColorRGB(final int[] minColorRGB) {
		
		firePropertyChange("minColorRGB", this.minColorRGB, this.minColorRGB = minColorRGB);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the minValue
	 */
	public float getMinValue() {
		return minValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param minValue
	 *            the minValue to set
	 */
	public void setMinValue(final float minValue) {
		;
		firePropertyChange("minValue", this.minValue, this.minValue = minValue);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the numCols
	 */
	public int getWidth() {
		return width;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param width
	 *            the numCols to set
	 */
	public void setWidth(final int width) {
		
		firePropertyChange("width", this.width, this.width = width);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the numFramePointsHorizontal
	 */
	public int getNumFramePointsHorizontal() {
		return numFramePointsHorizontal;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param numFramePointsHorizontal
	 *            the numFramePointsHorizontal to set
	 */
	public void setNumFramePointsHorizontal(final int numFramePointsHorizontal) {
		
		firePropertyChange("numFramePointsHorizontal", this.numFramePointsHorizontal,
				this.numFramePointsHorizontal = numFramePointsHorizontal);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the numFramePointsVertical
	 */
	public int getNumFramePointsVertical() {
		return numFramePointsVertical;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param numFramePointsVertical
	 *            the numFramePointsVertical to set
	 */
	public void setNumFramePointsVertical(final int numFramePointsVertical) {
		
		firePropertyChange("numFramePointsVertical", this.numFramePointsVertical,
				this.numFramePointsVertical = numFramePointsVertical);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the numRows
	 */
	public int getHeight() {
		return height;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param height
	 *            the numRows to set
	 */
	public void setHeight(final int height) {
		
		firePropertyChange("height", this.height, this.height = height);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the refreshInterval
	 */
	public int getRefreshInterval() {
		return refreshInterval;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param refreshInterval
	 *            the refreshInterval to set
	 */
	public void setRefreshInterval(final int refreshInterval) {
		
		firePropertyChange("refreshInterval", this.refreshInterval,
				this.refreshInterval = refreshInterval);
	}
	
	public boolean equals(final MapPainterXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		
		return (defaultValue == o.defaultValue) && (gridElementHeight == o.gridElementHeight)
				&& (gridElementWidth == o.gridElementWidth) && (lowerLeftX == o.lowerLeftX)
				&& (lowerLeftY == o.lowerLeftY)
				&& (lockGridElementSquare == o.lockGridElementSquare)
				&& (lockNumberOfRowsNCols == o.lockNumberOfRowsNCols)
				&& equalsRGB(maxColorRGB, o.maxColorRGB) && (maxValue == o.maxValue)
				&& equalsRGB(minColorRGB, o.minColorRGB) && (minValue == o.minValue)
				&& (width == o.width) && (numFramePointsHorizontal == o.numFramePointsHorizontal)
				&& (numFramePointsVertical == o.numFramePointsVertical) && (height == o.height)
				&& (refreshInterval == o.refreshInterval);
	}
	
}