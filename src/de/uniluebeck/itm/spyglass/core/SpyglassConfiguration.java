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
import java.util.Vector;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.XMLConfig;

// --------------------------------------------------------------------------------
/**
 * Spyglass configuration file, mainly used by the deserialization process of the Spyglass class.
 * The persistence framework to utilize the serialization and deserialization to/from an XML file is
 * "SimpleXML" (http://simple.sourceforge.net/).
 * 
 * @author Sebastian Ebers, Daniel Bimschas, Dariush Forouher
 */
@Root
public class SpyglassConfiguration extends XMLConfig {
	
	@Element
	private PacketReader packetReader = null;
	
	@Element(name = "instances")
	private final PluginManager pluginManager = new PluginManager();
	
	@Element
	private final GeneralSettingsXMLConfig generalSettings = new GeneralSettingsXMLConfig();
	
	@ElementList
	private final Collection<Plugin> defaults = new Vector<Plugin>();
	
	@Element
	private final DrawingArea drawingArea = new DrawingArea();
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the generalSettings
	 */
	public GeneralSettingsXMLConfig getGeneralSettings() {
		return generalSettings;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns a read-only collection of plug-ins which are configured by default
	 * 
	 * Note that this collection is immutable, entries cannot be added or removed.
	 * 
	 * @return the defaults a collection of plug-ins which are configured by default
	 */
	public Collection<Plugin> getDefaults() {
		return Collections.unmodifiableCollection(defaults);
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
		final PacketReader oldReader = this.packetReader;
		this.packetReader = packetReader;
		firePropertyChange("packetReader", oldReader, packetReader);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the drawingArea
	 */
	public DrawingArea getDrawingArea() {
		return drawingArea;
	}
	
}
