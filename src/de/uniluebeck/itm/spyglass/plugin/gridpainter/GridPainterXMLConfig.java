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

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
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
	private AbsolutePosition gridLowerLeftPoint = new AbsolutePosition();
	
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
		final int oldValue = this.gridElementHeight;
		this.gridElementHeight = gridElementHeight;
		firePropertyChange("gridElementHeight", oldValue, gridElementHeight);
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
		final int oldValue = this.gridElementWidth;
		this.gridElementWidth = gridElementWidth;
		firePropertyChange("gridElementWidth", oldValue, gridElementWidth);
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
		final AbsolutePosition oldValue = this.gridLowerLeftPoint;
		this.gridLowerLeftPoint = gridLowerLeftPoint;
		firePropertyChange("gridLowerLeftPoint", oldValue, gridLowerLeftPoint);
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
	 * @return the lineWidth
	 */
	public float getLineWidth() {
		return lineWidth;
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
	 * @return the lockGridElementsSquare
	 */
	public boolean getLockGridElementsSquare() {
		return lockGridElementsSquare;
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
		final boolean oldValue = this.lockNumberOfRowsNCols;
		this.lockNumberOfRowsNCols = lockNumberOfRowsNCols;
		firePropertyChange("lockNumberOfRowsNCols", oldValue, lockNumberOfRowsNCols);
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
		final int oldValue = this.numCols;
		this.numCols = numCols;
		firePropertyChange("numCols", oldValue, numCols);
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
		final int oldValue = this.numRows;
		this.numRows = numRows;
		firePropertyChange("numRows", oldValue, numRows);
	}
	
	public boolean equals(final GridPainterXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		
		return (gridElementHeight == o.gridElementHeight)
				&& (gridElementWidth == o.gridElementWidth)
				&& gridLowerLeftPoint.equals(o.gridLowerLeftPoint)
				&& equalsRGB(lineColorRGB, o.lineColorRGB) && (lineWidth == o.lineWidth)
				&& (lockGridElementsSquare == o.lockGridElementsSquare)
				&& (lockNumberOfRowsNCols == o.lockNumberOfRowsNCols) && (numCols == o.numCols)
				&& (numRows == o.numRows);
	}
	
}