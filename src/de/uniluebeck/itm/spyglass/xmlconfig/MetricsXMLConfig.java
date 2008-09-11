/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import org.simpleframework.xml.Element;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters concerning plug-ins which support
 * metrics
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class MetricsXMLConfig extends XMLConfig {
	
	/**
	 * a string for the unit
	 */
	@Element
	private String unit = "m";
	
	/**
	 * multiplying an absolute coordinate's x value with this factor will result in the
	 * corresponding metric value
	 */
	@Element
	private float abs2metricFactorX = 1;
	
	/**
	 * multiplying an absolute coordinate's y value with this factor will result in the
	 * corresponding metric value
	 */
	@Element
	private float abs2metricFactorY = 1;
	
	/**
	 * this is an offset value which allows the user to let the metric coordinates starting position
	 * be anywhere in the absolute coordinate system
	 */
	@Element
	private float abs2metricOffsetX = 0;
	
	/**
	 * this is an offset value which allows the user to let the metric coordinates starting position
	 * be anywhere in the absolute coordinate system
	 */
	@Element
	private float abs2metricOffsetY = 0;
	
	/**
	 * TODO (SE) - what is this?
	 * 
	 * TODO does this belong to the model? (DFO)
	 */
	@Element
	private boolean lockAbs2metricFactor = false;
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(final String unit) {
		final String oldValue = this.unit;
		this.unit = unit;
		firePropertyChange("unit", oldValue, unit);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the abs2metricFactorX
	 */
	public float getAbs2metricFactorX() {
		return abs2metricFactorX;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param abs2metricFactorX
	 *            the abs2metricFactorX to set
	 */
	public void setAbs2metricFactorX(final float abs2metricFactorX) {
		final float oldValue = this.abs2metricFactorX;
		this.abs2metricFactorX = abs2metricFactorX;
		firePropertyChange("abs2metricFactorX", oldValue, abs2metricFactorX);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the abs2metricFactorY
	 */
	public float getAbs2metricFactorY() {
		return abs2metricFactorY;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param abs2metricFactorY
	 *            the abs2metricFactorY to set
	 */
	public void setAbs2metricFactorY(final float abs2metricFactorY) {
		final float oldValue = this.abs2metricFactorY;
		this.abs2metricFactorY = abs2metricFactorY;
		firePropertyChange("abs2metricFactorY", oldValue, abs2metricFactorY);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the abs2metricOffsetX
	 */
	public float getAbs2metricOffsetX() {
		return abs2metricOffsetX;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param abs2metricOffsetX
	 *            the abs2metricOffsetX to set
	 */
	public void setAbs2metricOffsetX(final float abs2metricOffsetX) {
		final float oldValue = this.abs2metricOffsetX;
		this.abs2metricOffsetX = abs2metricOffsetX;
		firePropertyChange("abs2metricOffsetX", oldValue, abs2metricOffsetX);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the abs2metricOffsetY
	 */
	public float getAbs2metricOffsetY() {
		return abs2metricOffsetY;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param abs2metricOffsetY
	 *            the abs2metricOffsetY to set
	 */
	public void setAbs2metricOffsetY(final float abs2metricOffsetY) {
		final float oldValue = this.abs2metricOffsetY;
		this.abs2metricOffsetY = abs2metricOffsetY;
		firePropertyChange("abs2metricOffsetY", oldValue, abs2metricOffsetY);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the lockAbs2metricFactor
	 */
	public boolean isLockAbs2metricFactor() {
		return lockAbs2metricFactor;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lockAbs2metricFactor
	 *            the lockAbs2metricFactor to set
	 */
	public void setLockAbs2metricFactor(final boolean lockAbs2metricFactor) {
		final boolean oldValue = this.lockAbs2metricFactor;
		this.lockAbs2metricFactor = lockAbs2metricFactor;
		firePropertyChange("lockAbs2metricFactor", oldValue, lockAbs2metricFactor);
	}
	
}
