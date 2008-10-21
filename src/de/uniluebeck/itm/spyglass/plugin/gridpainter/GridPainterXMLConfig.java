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
	
	@Element
	private int gridElementHeight = 1;
	
	@Element
	private int gridElementWidth = 1;
	
	@Element
	private int lowerLeftX = 0;
	
	@Element
	private int lowerLeftY = 0;
	
	@ElementArray
	private int[] lineColorRGB = { 0, 0, 0 };
	
	@Element
	private float lineWidth = 1;
	
	@Element
	private boolean lockGridElementsSquare = true;
	
	@Element
	private boolean lockNumberOfRowsNCols = true;
	
	@Element
	private int numCols = 1;
	
	@Element
	private int numRows = 1;
	
	public boolean equals(final GridPainterXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		
		return (gridElementHeight == o.gridElementHeight)
				&& (gridElementWidth == o.gridElementWidth) && (lowerLeftX == o.lowerLeftX)
				&& (lowerLeftY == o.lowerLeftY) && equalsRGB(lineColorRGB, o.lineColorRGB)
				&& (lineWidth == o.lineWidth)
				&& (lockGridElementsSquare == o.lockGridElementsSquare)
				&& (lockNumberOfRowsNCols == o.lockNumberOfRowsNCols) && (numCols == o.numCols)
				&& (numRows == o.numRows);
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
	 * @return the gridElementWidth
	 */
	public int getGridElementWidth() {
		return gridElementWidth;
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
	 * @return
	 */
	public int getLowerLeftY() {
		return lowerLeftY;
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
		firePropertyChange("gridElementHeight", oldValue, gridElementHeight);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param gridElementWidth
	 *            the gridElementWidth to set
	 */
	public void setGridElementWidth(final int gridElementWidth) {
		final int oldValue = this.gridElementWidth;
		this.gridElementWidth = gridElementWidth;
		firePropertyChange("gridElementWidth", oldValue, gridElementWidth);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param gridLowerLeftPointX
	 */
	public void setLowerLeftX(final int gridLowerLeftPointX) {
		final int oldValue = this.lowerLeftX;
		this.lowerLeftX = gridLowerLeftPointX;
		firePropertyChange("gridLowerLeftPointX", oldValue, gridLowerLeftPointX);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param gridLowerLeftPointY
	 */
	public void setLowerLeftY(final int gridLowerLeftPointY) {
		final int oldValue = this.lowerLeftY;
		this.lowerLeftY = gridLowerLeftPointY;
		firePropertyChange("gridLowerLeftPointY", oldValue, gridLowerLeftPointY);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineColorRGB
	 *            the lineColorRGB to set
	 */
	public void setLineColorRGB(final int[] lineColor) {
		final int[] oldValue = this.lineColorRGB.clone();
		this.lineColorRGB = lineColor;
		firePropertyChange("lineColor", oldValue, lineColorRGB);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final float lineWidth) {
		final float oldValue = this.lineWidth;
		this.lineWidth = lineWidth;
		firePropertyChange("lineWidth", oldValue, lineWidth);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockGridElementsSquare
	 *            the lockGridElementsSquare to set
	 */
	public void setLockGridElementsSquare(final boolean lockGridElementsSquare) {
		final boolean oldValue = this.lockGridElementsSquare;
		this.lockGridElementsSquare = lockGridElementsSquare;
		firePropertyChange("lockGridElementsSquare", oldValue, lockGridElementsSquare);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockNumberOfRowsNCols
	 *            the lockNumberOfRowsNCols to set
	 */
	public void setLockNumberOfRowsNCols(final boolean lockNumberOfRowsNCols) {
		final boolean oldValue = this.lockNumberOfRowsNCols;
		this.lockNumberOfRowsNCols = lockNumberOfRowsNCols;
		firePropertyChange("lockNumberOfRowsNCols", oldValue, lockNumberOfRowsNCols);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param numCols
	 *            the numCols to set
	 */
	public void setNumCols(final int numCols) {
		final int oldValue = this.numCols;
		this.numCols = numCols;
		firePropertyChange("numCols", oldValue, numCols);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param numRows
	 *            the numRows to set
	 */
	public void setNumRows(final int numRows) {
		final int oldValue = this.numRows;
		this.numRows = numRows;
		firePropertyChange("numRows", oldValue, numRows);
	}
	
}