/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.util.Vector;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.SpyglassCanvas;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXmlConfig;

// --------------------------------------------------------------------------------
/**
 * Spyglass configuration file, mainly used by the deserialization process of
 * the Spyglass class. The persistence framework to utilize the serialization
 * and deserialization to/from an XML file is "SimpleXML"
 * (http://simple.sourceforge.net/).
 */
@Root
public class SpyglassConfiguration {
	
	@Element
	private PacketReader packetReader = null;
	
	@Element
	private SpyglassCanvas canvas = null;
	
	@Element(name = "instances")
	private PluginManager pluginManager = null;
	
	@Element
	private NodePositionerPlugin nodePositioner;
	
	@Element
	private GeneralSettingsXmlConfig generalSettings;
	
	@ElementList
	private Vector<Plugin> defaults;
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the generalSettings
	 */
	public GeneralSettingsXmlConfig getGeneralSettings() {
		return generalSettings;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param generalSettings
	 *            the generalSettings to set
	 */
	public void setGeneralSettings(final GeneralSettingsXmlConfig generalSettings) {
		this.generalSettings = generalSettings;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns a collection of plug-ins which are configured by default
	 * 
	 * @return the defaults a collection of plug-ins which are configured by
	 *         default
	 */
	public Vector<Plugin> getDefaults() {
		return defaults;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets a collection of plug-ins which are configured by default
	 * 
	 * @param defaults
	 *            a collection of plug-ins which are configured by default
	 */
	public void setDefaults(final Vector<Plugin> defaults) {
		this.defaults = defaults;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Replaces a plug-in in the list of default plug-ins
	 * 
	 * @param plugin
	 *            the new plug-in
	 * @return the previous plug-in of the same class or <code>null</code> if
	 *         no plug-in of the same class existet previously
	 */
	public Plugin replaceInDefaults(final Plugin plugin) {
		final Class<? extends Plugin> clazz = plugin.getClass();
		for (final Plugin p : defaults) {
			if (p.getClass().equals(clazz)) {
				defaults.remove(p);
				defaults.add(plugin);
				return p;
			}
		}
		defaults.add(plugin);
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the frames per second
	 * 
	 * @return the frames per second
	 */
	public long getFps() {
		return generalSettings.getFps();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the frames per second
	 * 
	 * @param the
	 *            frames per second
	 */
	public void setFps(final long fps) {
		generalSettings.setFps(fps);
	}
	
	// --------------------------------------------------------------------------------
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	// --------------------------------------------------------------------------------
	public void setPluginManager(final PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	// --------------------------------------------------------------------------------
	public SpyglassCanvas getCanvas() {
		return canvas;
	}
	
	// --------------------------------------------------------------------------------
	public void setCanvas(final SpyglassCanvas canvas) {
		this.canvas = canvas;
	}
	
	// --------------------------------------------------------------------------------
	public PacketReader getPacketReader() {
		return packetReader;
	}
	
	// --------------------------------------------------------------------------------
	public void setPacketReader(final PacketReader packetReader) {
		this.packetReader = packetReader;
	}
	
	// --------------------------------------------------------------------------------
	public void setNodePositioner(final NodePositionerPlugin randomNodePositioner) {
		this.nodePositioner = randomNodePositioner;
	}
	
	// --------------------------------------------------------------------------------
	public NodePositionerPlugin getNodePositioner() {
		return nodePositioner;
		
	}
	
	// --------------------------------------------------------------------------------
	public long getPacketDeliveryDelay() {
		return generalSettings.getPacketDeliveryDelay();
	}
	
	// --------------------------------------------------------------------------------
	public void setPacketDeliveryDelay(final long packetDeliveryDelay) {
		generalSettings.setPacketDeliveryDelay(packetDeliveryDelay);
	}
	
	// --------------------------------------------------------------------------------
	public long getPacketDeliveryInitialDelay() {
		return generalSettings.getPacketDeliveryInitialDelay();
	}
	
	// --------------------------------------------------------------------------------
	public void setPacketDeliveryInitialDelay(final long packetDeliveryInitialDelay) {
		generalSettings.setPacketDeliveryInitialDelay(packetDeliveryInitialDelay);
	}
	
}
