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
 * Instances of this class contain the configuration parameters of the general settings
 * 
 * @author Sebastian Ebers, Dariush Forouher
 * 
 */
public class GeneralSettingsXMLConfig extends XMLConfig {
	
	/**
	 * if <code>true</code> a ruler is shown in the graphical user interface
	 */
	@Element(required=false)
	private boolean showRuler = true;
	
	/**
	 * defines the metric settings
	 */
	@Element(required=false)
	final private MetricsXMLConfig metrics = new MetricsXMLConfig();
	
	/**
	 * the time's unit (important when showing the results of received packets on the graphical user
	 * interface). The unit is just for information purposes. It is not processed in any way.
	 */
	@Element(required=false)
	private String timeUnit = "s";
	
	/**
	 * the time's scale(important when showing the results of received packets on the graphical user
	 * interface)
	 */
	@Element(required=false)
	private float timeScale = 1;
	
	/**
	 * the delay used when processing recorded packets TODO: wrong!
	 */
	@Element(required=false)
	private long packetDeliveryInitialDelay = 1000;
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the showRuler
	 */
	public boolean getShowRuler() {
		return showRuler;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param showRuler
	 *            the showRuler to set
	 */
	public void setShowRuler(final boolean showRuler) {
		final boolean oldValue = this.showRuler;
		this.showRuler = showRuler;
		firePropertyChange("showRuler", oldValue, showRuler);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param metrics
	 *            the metrics to set
	 */
	public void setMetrics(final MetricsXMLConfig metrics) {
		this.metrics.overwriteWith(metrics);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the metrics
	 */
	public MetricsXMLConfig getMetrics() {
		return metrics;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the timeUnit
	 */
	public String getTimeUnit() {
		return timeUnit;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param timeUnit
	 *            the timeUnit to set
	 */
	public void setTimeUnit(final String timeUnit) {
		final String oldValue = this.timeUnit;
		this.timeUnit = timeUnit;
		firePropertyChange("timeUnit", oldValue, timeUnit);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the timeScale
	 */
	public float getTimeScale() {
		return timeScale;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param timeScale
	 *            the timeScale to set
	 */
	public void setTimeScale(final float timeScale) {
		final float oldValue = this.timeScale;
		this.timeScale = timeScale;
		firePropertyChange("timeScale", oldValue, timeScale);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the packetDeliveryInitialDelay
	 */
	public long getPacketDeliveryInitialDelay() {
		return packetDeliveryInitialDelay;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param packetDeliveryInitialDelay
	 *            the packetDeliveryInitialDelay to set
	 */
	public void setPacketDeliveryInitialDelay(final long packetDeliveryInitialDelay) {
		final long oldValue = this.packetDeliveryInitialDelay;
		this.packetDeliveryInitialDelay = packetDeliveryInitialDelay;
		firePropertyChange("packetDeliveryInitialDelay", oldValue, packetDeliveryInitialDelay);
	}
	
	public void overwriteWith(final GeneralSettingsXMLConfig o) {
		super.overwriteWith(o);
		this.metrics.overwriteWith(o.getMetrics());
	}
	
}
