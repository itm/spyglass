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

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link MapPainterPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class MapPainterXMLConfig extends PluginXMLConfig {
	
	@Element
	private float framePointDefaultValue = 3;
	
	@Element
	private int gridElementHeight = 1;
	
	@Element
	private int gridElementWidth = 1;
	
	@Element
	private AbsolutePosition gridLowerLeftPoint = new AbsolutePosition();
	
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
	private int numCols = 1;
	
	@Element
	private int numFramePointsHorizontal = 3;
	
	@Element
	private int numFramePointsVertical = 3;
	
	@Element
	private int numRows = 1;
	
	@Element
	private int refreshInterval = 10;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MapPainterXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the framePointDefaultValue
	 */
	public float getFramePointDefaultValue() {
		return framePointDefaultValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param framePointDefaultValue
	 *            the framePointDefaultValue to set
	 */
	public void setFramePointDefaultValue(final float framePointDefaultValue) {
		this.framePointDefaultValue = framePointDefaultValue;
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
		this.gridElementHeight = gridElementHeight;
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
		this.gridElementWidth = gridElementWidth;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the gridLowerLeftPoint
	 */
	public AbsolutePosition getGridLowerLeftPoint() {
		return gridLowerLeftPoint;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param gridLowerLeftPoint
	 *            the gridLowerLeftPoint to set
	 */
	public void setGridLowerLeftPoint(final AbsolutePosition gridLowerLeftPoint) {
		this.gridLowerLeftPoint = gridLowerLeftPoint;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lockGridElementSquare
	 */
	public boolean isLockGridElementSquare() {
		return lockGridElementSquare;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockGridElementSquare
	 *            the lockGridElementSquare to set
	 */
	public void setLockGridElementSquare(final boolean lockGridElementSquare) {
		this.lockGridElementSquare = lockGridElementSquare;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lockNumberOfRowsNCols
	 */
	public boolean isLockNumberOfRowsNCols() {
		return lockNumberOfRowsNCols;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockNumberOfRowsNCols
	 *            the lockNumberOfRowsNCols to set
	 */
	public void setLockNumberOfRowsNCols(final boolean lockNumberOfRowsNCols) {
		this.lockNumberOfRowsNCols = lockNumberOfRowsNCols;
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
		this.maxColorRGB = maxColorRGB;
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
		this.maxValue = maxValue;
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
		this.minColorRGB = minColorRGB;
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
		this.minValue = minValue;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the numCols
	 */
	public int getNumCols() {
		return numCols;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param numCols
	 *            the numCols to set
	 */
	public void setNumCols(final int numCols) {
		this.numCols = numCols;
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
		this.numFramePointsHorizontal = numFramePointsHorizontal;
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
		this.numFramePointsVertical = numFramePointsVertical;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the numRows
	 */
	public int getNumRows() {
		return numRows;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param numRows
	 *            the numRows to set
	 */
	public void setNumRows(final int numRows) {
		this.numRows = numRows;
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
		this.refreshInterval = refreshInterval;
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public boolean equals(final PluginXMLConfig other) {
		if (!(other instanceof MapPainterXMLConfig)) {
			return false;
		}
		final MapPainterXMLConfig o = (MapPainterXMLConfig) other;
		return (framePointDefaultValue == o.framePointDefaultValue) && (gridElementHeight == o.gridElementHeight)
				&& (gridElementWidth == o.gridElementWidth) && gridLowerLeftPoint.equals(o.gridLowerLeftPoint)
				&& (lockGridElementSquare == o.lockGridElementSquare) && (lockNumberOfRowsNCols == o.lockNumberOfRowsNCols)
				&& equalsRGB(maxColorRGB, o.maxColorRGB) && (maxValue == o.maxValue) && equalsRGB(minColorRGB, o.minColorRGB)
				&& (minValue == o.minValue) && (numCols == o.numCols) && (numFramePointsHorizontal == o.numFramePointsHorizontal)
				&& (numFramePointsVertical == o.numFramePointsVertical) && (numRows == o.numRows) && (refreshInterval == o.refreshInterval);
	}
	
}