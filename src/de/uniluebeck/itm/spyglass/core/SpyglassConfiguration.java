/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.util.Vector;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXmlConfig;

// --------------------------------------------------------------------------------
/**
 * Spyglass configuration file, mainly used by the deserialization process of the Spyglass class.
 * The persistence framework to utilize the serialization and deserialization to/from an XML file is
 * "SimpleXML" (http://simple.sourceforge.net/).
 * 
 * @author Sebastian Ebers, Daniel Bimschas, Dariush Forouher
 */
@Root
public class SpyglassConfiguration {
	
	@Element
	private PacketReader packetReader = null;
	
	@Element(name = "instances")
	private final PluginManager pluginManager = new PluginManager();
	
	@Element
	private final GeneralSettingsXmlConfig generalSettings = new GeneralSettingsXmlConfig();
	
	@ElementList
	private final Vector<Plugin> defaults = new Vector<Plugin>();
	
	@Element
	private final DrawingArea drawingArea = new DrawingArea();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the generalSettings
	 */
	public GeneralSettingsXmlConfig getGeneralSettings() {
		return generalSettings;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns a collection of plug-ins which are configured by default
	 * 
	 * @return the defaults a collection of plug-ins which are configured by default
	 */
	public Vector<Plugin> getDefaults() {
		return defaults;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Replaces a plug-in in the list of default plug-ins
	 * 
	 * @param plugin
	 *            the new plug-in
	 * @return the previous plug-in of the same class or <code>null</code> if no plug-in of the same
	 *         class existet previously
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
	public PluginManager getPluginManager() {
		return pluginManager;
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
	/**
	 * @return the drawingArea
	 */
	public DrawingArea getDrawingArea() {
		return drawingArea;
	}
	
}
