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
 * @author Sebastian Ebers
 * 
 */
public class GeneralSettingsXmlConfig {
	
	@Element
	private boolean showRuler = false;
	
	@Element(required = false)
	private MetricsXMLConfig metrics = null;
	
	@Element
	private String timeUnit = "s";
	
	@Element
	private float timeScale = 1;
	
	@Element
	private long packetDeliveryInitialDelay = 1000;
	
	@Element(name = "framesPerSecond")
	private long fps = 25;
	
	// --------------------------------------------------------------------------------
	/** Constructor */
	public GeneralSettingsXmlConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param showRuler
	 *            if <code>true</code> a ruler is shown in the graphical user interface
	 * @param metrics
	 *            defines the metric settings
	 * @param timeUnit
	 *            the time's unit (important when showing the results of received packets on the
	 *            graphical user interface). The unit is just for information purposes. It is not
	 *            processed in any way.
	 * @param timeScale
	 *            the time's scale(important when showing the results of received packets on the
	 *            graphical user interface)
	 * @param packetDeliveryDelay
	 *            the delay used when processing recorded packets
	 * @param packetDeliveryInitialDelay
	 *            the delay used when processing recorded packets
	 * @param fps
	 *            the number of frames which drawn per second
	 */
	public GeneralSettingsXmlConfig(final boolean showRuler, final MetricsXMLConfig metrics, final String timeUnit, final float timeScale,
			final long packetDeliveryInitialDelay, final long fps) {
		super();
		this.showRuler = showRuler;
		this.metrics = metrics;
		this.timeUnit = timeUnit;
		this.timeScale = timeScale;
		this.packetDeliveryInitialDelay = packetDeliveryInitialDelay;
		this.fps = fps;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the showRuler
	 */
	public boolean isShowRuler() {
		return showRuler;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param showRuler
	 *            the showRuler to set
	 */
	public void setShowRuler(final boolean showRuler) {
		this.showRuler = showRuler;
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
	 * @param metrics
	 *            the metrics to set
	 */
	public void setMetrics(final MetricsXMLConfig metrics) {
		this.metrics = metrics;
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
		this.timeUnit = timeUnit;
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
		this.timeScale = timeScale;
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
		this.packetDeliveryInitialDelay = packetDeliveryInitialDelay;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the fps
	 */
	public long getFps() {
		return fps;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param fps
	 *            the fps to set
	 */
	public void setFps(final long fps) {
		this.fps = fps;
	}
	
}
