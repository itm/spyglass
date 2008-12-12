/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link GridPainterPlugin}
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class GridPainterXMLConfig extends PluginXMLConfig {

	public static final String PROPERTYNAME_NUM_ROWS = "numRows";

	public static final String PROPERTYNAME_NUM_COLS = "numCols";

	public static final String PROPERTYNAME_LOCK_NUMBER_OF_ROWS_N_COLS = "lockNumberOfRowsNCols";

	public static final String PROPERTYNAME_LOCK_GRID_ELEMENTS_SQUARE = "lockGridElementsSquare";

	public static final String PROPERTYNAME_LINE_WIDTH = "lineWidth";

	public static final String PROPERTYNAME_LINE_COLOR_R_G_B = "lineColorRGB";

	public static final String PROPERTYNAME_GRID_LOWER_LEFT_POINT_Y = "gridLowerLeftPointY";

	public static final String PROPERTYNAME_GRID_LOWER_LEFT_POINT_X = "gridLowerLeftPointX";

	public static final String PROPERTYNAME_GRID_ELEMENT_WIDTH = "gridElementWidth";

	public static final String PROPERTYNAME_GRID_ELEMENT_HEIGHT = "gridElementHeight";

	@Element(required=false)
	private int gridElementHeight = 1;

	@Element(required=false)
	private int gridElementWidth = 1;

	@Element(required=false)
	private int gridLowerLeftPointX = 0;

	@Element(required=false)
	private int gridLowerLeftPointY = 0;

	@ElementArray(required=false)
	private int[] lineColorRGB = { 0, 0, 0 };

	@Element(required=false)
	private float lineWidth = 1;

	@Element(required=false)
	private boolean lockGridElementsSquare = true;

	@Element(required=false)
	private boolean lockNumberOfRowsNCols = true;

	@Element(required=false)
	private int numCols = 1;

	@Element(required=false)
	private int numRows = 1;

	// --------------------------------------------------------------------------------
	/**
	 * @return the gridElementHeight
	 */
	public int getGridElementHeight() {
		return gridElementHeight;
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
	 * @return
	 */
	public int getGridLowerLeftPointX() {
		return gridLowerLeftPointX;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getGridLowerLeftPointY() {
		return gridLowerLeftPointY;
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
	 * @return the lineWidth
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the lockGridElementsSquare
	 */
	public boolean getLockGridElementsSquare() {
		return lockGridElementsSquare;
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
	 * @return the numCols
	 */
	public int getNumCols() {
		return numCols;
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
	 * @param gridElementHeight
	 *            the gridElementHeight to set
	 */
	public void setGridElementHeight(final int gridElementHeight) {
		final int oldValue = this.gridElementHeight;
		this.gridElementHeight = gridElementHeight;
		firePropertyChange(PROPERTYNAME_GRID_ELEMENT_HEIGHT, oldValue, gridElementHeight);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param gridElementWidth
	 *            the gridElementWidth to set
	 */
	public void setGridElementWidth(final int gridElementWidth) {
		final int oldValue = this.gridElementWidth;
		this.gridElementWidth = gridElementWidth;
		firePropertyChange(PROPERTYNAME_GRID_ELEMENT_WIDTH, oldValue, gridElementWidth);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public void setLineColorRGB(final int[] lineColor) {
		final int[] oldValue = this.lineColorRGB.clone();
		this.lineColorRGB = lineColor;
		firePropertyChange(PROPERTYNAME_LINE_COLOR_R_G_B, oldValue, lineColorRGB);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final float lineWidth) {
		final float oldValue = this.lineWidth;
		this.lineWidth = lineWidth;
		firePropertyChange(PROPERTYNAME_LINE_WIDTH, oldValue, lineWidth);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lockGridElementsSquare
	 *            the lockGridElementsSquare to set
	 */
	public void setLockGridElementsSquare(final boolean lockGridElementsSquare) {
		final boolean oldValue = this.lockGridElementsSquare;
		this.lockGridElementsSquare = lockGridElementsSquare;
		firePropertyChange(PROPERTYNAME_LOCK_GRID_ELEMENTS_SQUARE, oldValue, lockGridElementsSquare);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lockNumberOfRowsNCols
	 *            the lockNumberOfRowsNCols to set
	 */
	public void setLockNumberOfRowsNCols(final boolean lockNumberOfRowsNCols) {
		final boolean oldValue = this.lockNumberOfRowsNCols;
		this.lockNumberOfRowsNCols = lockNumberOfRowsNCols;
		firePropertyChange(PROPERTYNAME_LOCK_NUMBER_OF_ROWS_N_COLS, oldValue, lockNumberOfRowsNCols);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param gridLowerLeftPointX
	 */
	public void setGridLowerLeftPointX(final int gridLowerLeftPointX) {
		final int oldValue = this.gridLowerLeftPointX;
		this.gridLowerLeftPointX = gridLowerLeftPointX;
		firePropertyChange(PROPERTYNAME_GRID_LOWER_LEFT_POINT_X, oldValue, gridLowerLeftPointX);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param gridLowerLeftPointY
	 */
	public void setGridLowerLeftPointY(final int gridLowerLeftPointY) {
		final int oldValue = this.gridLowerLeftPointY;
		this.gridLowerLeftPointY = gridLowerLeftPointY;
		firePropertyChange(PROPERTYNAME_GRID_LOWER_LEFT_POINT_Y, oldValue, gridLowerLeftPointY);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param numCols
	 *            the numCols to set
	 */
	public void setNumCols(final int numCols) {
		final int oldValue = this.numCols;
		this.numCols = numCols;
		firePropertyChange(PROPERTYNAME_NUM_COLS, oldValue, numCols);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param numRows
	 *            the numRows to set
	 */
	public void setNumRows(final int numRows) {
		final int oldValue = this.numRows;
		this.numRows = numRows;
		firePropertyChange(PROPERTYNAME_NUM_ROWS, oldValue, numRows);
	}

}