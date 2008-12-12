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
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link MapPainterPlugin}
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class MapPainterXMLConfig extends PluginXMLConfig {

	public static final String PROPERTYNAME_REFRESH_INTERVAL = "refreshInterval";

	public static final String PROPERTYNAME_HEIGHT = "height";

	public static final String PROPERTYNAME_NUM_FRAME_POINTS_VERTICAL = "numFramePointsVertical";

	public static final String PROPERTYNAME_NUM_FRAME_POINTS_HORIZONTAL = "numFramePointsHorizontal";

	public static final String PROPERTYNAME_WIDTH = "width";

	public static final String PROPERTYNAME_MIN_VALUE = "minValue";

	public static final String PROPERTYNAME_MIN_COLOR_R_G_B = "minColorRGB";

	public static final String PROPERTYNAME_MAX_VALUE = "maxValue";

	public static final String PROPERTYNAME_MAX_COLOR_R_G_B = "maxColorRGB";

	public static final String PROPERTYNAME_LOCK_NUMBER_OF_ROWS_N_COLS = "lockNumberOfRowsNCols";

	public static final String PROPERTYNAME_LOCK_GRID_ELEMENT_SQUARE = "lockGridElementSquare";

	public static final String PROPERTYNAME_LOWER_LEFT_Y = "lowerLeftY";

	public static final String PROPERTYNAME_LOWER_LEFT_X = "lowerLeftX";

	public static final String PROPERTYNAME_GRID_ELEMENT_WIDTH = "gridElementWidth";

	public static final String PROPERTYNAME_GRID_ELEMENT_HEIGHT = "gridElementHeight";

	public static final String PROPERTYNAME_DEFAULT_VALUE = "defaultValue";

	@Element(required=false)
	private float defaultValue = 3;

	@Element(required=false)
	private int gridElementHeight = 1;

	@Element(required=false)
	private int gridElementWidth = 1;

	@Element(required=false)
	private int lowerLeftX = 0;

	@Element(required=false)
	private int lowerLeftY = 0;

	@Element(required=false)
	private boolean lockGridElementSquare = true;

	@Element(required=false)
	private boolean lockNumberOfRowsNCols = true;

	@ElementArray(required=false)
	private int[] maxColorRGB = { 0, 0, 0 };

	@Element(required=false)
	private float maxValue = 1;

	@ElementArray(required=false)
	private int[] minColorRGB = { 255, 255, 255 };

	@Element(required=false)
	private float minValue = 0;

	@Element(required=false)
	private int width = 1;

	@Element(required=false)
	private int numFramePointsHorizontal = 3;

	@Element(required=false)
	private int numFramePointsVertical = 3;

	@Element(required=false)
	private int height = 1;

	@Element(required=false)
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
	public void setDefaultValue(final float defaultValue) {
		firePropertyChange(PROPERTYNAME_DEFAULT_VALUE, this.defaultValue, this.defaultValue = defaultValue);
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
		firePropertyChange(PROPERTYNAME_GRID_ELEMENT_HEIGHT, this.gridElementHeight, this.gridElementHeight = gridElementHeight);
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
		firePropertyChange(PROPERTYNAME_GRID_ELEMENT_WIDTH, this.gridElementWidth, this.gridElementWidth = gridElementWidth);
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
		firePropertyChange(PROPERTYNAME_LOWER_LEFT_X, this.lowerLeftX, this.lowerLeftX = lowerLeftX);
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
		firePropertyChange(PROPERTYNAME_LOWER_LEFT_Y, this.lowerLeftY, this.lowerLeftY = lowerLeftY);
	}

	/**
	 * Return the lower left point
	 */
	public AbsolutePosition getLowerLeft() {
		final AbsolutePosition pos = new AbsolutePosition();
		pos.x = getLowerLeftX();
		pos.y = getLowerLeftY();
		return pos;
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

		firePropertyChange(PROPERTYNAME_LOCK_GRID_ELEMENT_SQUARE, this.lockGridElementSquare, this.lockGridElementSquare = lockGridElementSquare);
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

		firePropertyChange(PROPERTYNAME_LOCK_NUMBER_OF_ROWS_N_COLS, this.lockNumberOfRowsNCols, this.lockNumberOfRowsNCols = lockNumberOfRowsNCols);
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

		firePropertyChange(PROPERTYNAME_MAX_COLOR_R_G_B, this.maxColorRGB, this.maxColorRGB = maxColorRGB.clone());
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

		firePropertyChange(PROPERTYNAME_MAX_VALUE, this.maxValue, this.maxValue = maxValue);
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

		firePropertyChange(PROPERTYNAME_MIN_COLOR_R_G_B, this.minColorRGB, this.minColorRGB = minColorRGB.clone());
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
		firePropertyChange(PROPERTYNAME_MIN_VALUE, this.minValue, this.minValue = minValue);
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

		firePropertyChange(PROPERTYNAME_WIDTH, this.width, this.width = width);
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

		firePropertyChange(PROPERTYNAME_NUM_FRAME_POINTS_HORIZONTAL, this.numFramePointsHorizontal,
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

		firePropertyChange(PROPERTYNAME_NUM_FRAME_POINTS_VERTICAL, this.numFramePointsVertical, this.numFramePointsVertical = numFramePointsVertical);
	}

	/**
	 * Return the dimensions of the grid.
	 */
	public AbsoluteRectangle getBoundingBox() {
		return new AbsoluteRectangle(this.getLowerLeft(), getWidth(), getHeight());
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

		firePropertyChange(PROPERTYNAME_HEIGHT, this.height, this.height = height);
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

		firePropertyChange(PROPERTYNAME_REFRESH_INTERVAL, this.refreshInterval, this.refreshInterval = refreshInterval);
	}

}