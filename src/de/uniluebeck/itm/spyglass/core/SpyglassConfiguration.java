/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.XMLConfig;

// --------------------------------------------------------------------------------
/**
 * Spyglass configuration file, mainly used by the deserialization process of the Spyglass class.
 * The persistence framework to utilize the serialization and deserialization to/from an XML file is
 * "SimpleXML" (http://simple.sourceforge.net/).
 * 
 * @author Sebastian Ebers, Daniel Bimschas, Dariush Forouher
 */
@Root(strict=true)
public class SpyglassConfiguration extends XMLConfig {
	
	@Element(name = "packetReader")
	private PacketReader packetReader = new PacketRecorder();;
	
	@Element(name = "instances")
	private PluginManager pluginManager = new PluginManager();
	
	@Element(name = "generalSettings")
	private GeneralSettingsXMLConfig generalSettings = new GeneralSettingsXMLConfig();
	
	@ElementList(name = "defaults")
	private Collection<Plugin> defaults = new LinkedList<Plugin>();
	
	// --------------------------------------------------------------------------------
	/**
	 * @param generalSettings
	 *            the generalSettings to set
	 */
	public void setGeneralSettings(final GeneralSettingsXMLConfig generalSettings) {
		final GeneralSettingsXMLConfig old = this.generalSettings;
		this.generalSettings = generalSettings;
		firePropertyChange("generalSettings", old, this.generalSettings);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the generalSettings
	 */
	public GeneralSettingsXMLConfig getGeneralSettings() {
		return generalSettings;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param defaults
	 *            the defaults to set
	 */
	public void setDefaultPlugins(final Collection<Plugin> defaults) {
		final Collection<Plugin> oldDefaults = this.defaults;
		// this is necessary in case of getting an unmodifiable list from a plug-in which is to be
		// copied
		this.defaults = new LinkedList<Plugin>(defaults);
		firePropertyChange("defaults", oldDefaults, this.defaults);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns a read-only collection of plug-ins which are configured by default
	 * 
	 * Note that this collection is immutable, entries cannot be added or removed.
	 * 
	 * @return the defaults a collection of plug-ins which are configured by default
	 */
	public Collection<Plugin> getDefaultPlugins() {
		return Collections.unmodifiableCollection(defaults);
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Returns the default configuration parameters of a plug-in
	 * 
	 * @param clazz
	 *            the plug-in's class
	 * @return the default configuration parameters of a plug-in
	 */
	public PluginXMLConfig getDefaultConfig(final Class<? extends Plugin> clazz) {
		
		final Collection<Plugin> plugins = getDefaultPlugins();
		for (final Plugin plugin : plugins) {
			if (plugin.getClass().equals(clazz)) {
				return plugin.getXMLConfig();
			}
		}
		
		return null;
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the instance which manages the plug-ins
	 * 
	 * @param pluginManager
	 *            the instance which manages the plug-ins
	 */
	public void setPluginManager(final PluginManager pluginManager) {
		final PluginManager oldPM = this.pluginManager;
		this.pluginManager = pluginManager;
		firePropertyChange("pluginManager", oldPM, this.pluginManager);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the instance which manages the plug-ins
	 * 
	 * @return the instance which manages the plug-ins
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	// --------------------------------------------------------------------------------
	public PacketReader getPacketReader() {
		return packetReader;
	}
	
	// --------------------------------------------------------------------------------
	public void setPacketReader(final PacketReader packetReader) {
		final PacketReader oldReader = this.packetReader;
		this.packetReader = packetReader;
		firePropertyChange("packetReader", oldReader, packetReader);
	}
	
}
