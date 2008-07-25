/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import org.eclipse.swt.events.MouseEvent;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Base class for all Spyglass plugins. A plugin has at least a priority, that determines the
 * drawing order, a state (active or not) and a default drawing object that is being used for
 * drawing jobs.
 */
@Root
public abstract class Plugin implements Runnable {
	
	@Attribute
	private boolean isActive = true;
	
	private PluginManager pluginManager;
	
	// --------------------------------------------------------------------------------
	/**
	 * This method handles a Packet object. Usually, a plugin creates a new DrawingObject for each
	 * packet it handles.
	 * 
	 * @param packet
	 *            The packet object to handle.
	 */
	public abstract void handlePacket(SpyglassPacket packet);
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public abstract String getName();
	
	// --------------------------------------------------------------------------------
	/**
	 * holt sich die information aus dem PluginXMLConfig-Objekt
	 */
	public final boolean isActive() {
		return isActive;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Löscht den Zustand des Plugins, z.B. QuadTree leeren, Instanzvariablen auf Default stellen
	 */
	public abstract void reset();
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public final void setActive(final boolean isActive) {
		this.isActive = isActive;
		
		if (pluginManager != null) {
			pluginManager.setPluginStatus(this, isActive);
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public final void setPluginManager(final PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public final PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/**
	 * 
	 * @param parent
	 * @param cs
	 */
	public abstract PluginPreferencePage<? extends Plugin> createPreferencePage(final ConfigStore cs);
	
	/**
	 * should be implemented static
	 * 
	 * @param parent
	 * @param cs
	 */
	public static PluginPreferencePage<? extends Plugin> createTypePreferencePage(final ConfigStore cs) {
		throw new RuntimeException("This method must only be called on subclasses and must be implemented in every instantiable subclass.");
	}
	
	public static String getHumanReadableName() {
		throw new RuntimeException("This method must only be called on subclasses and must be implemented by every abstract or instantiable suclass.");
	}
	
	public abstract PluginXMLConfig getXMLConfig();
	
	/**
	 * 
	 * @param e
	 * @param drawingArea
	 */
	public boolean handleEvent(final MouseEvent e, final DrawingArea drawingArea) {
		return false;
	}
	
	/**
	 * holt sich die Information aus dem PluginXMLConfig-Objekt
	 */
	public boolean isVisible() {
		return false;
	}
	
	/**
	 * must not write in the quadtree for expensive calculations
	 * 
	 * @param packet
	 */
	protected abstract void processPacket(Packet packet);
	
	/**
	 * Should never be overridden!!!
	 */
	public void run() {
		
	}
	
	/**
	 * 
	 * @param setVisible
	 */
	public void setVisible(final boolean setVisible) {
		
	}
	
	/**
	 * 
	 * @param xmlConfig
	 */
	public abstract void setXMLConfig(PluginXMLConfig xmlConfig);
	
	/**
	 * must only be called from run() may change the quadtree, but it should be fast, because it
	 * blocks the GUI
	 */
	protected abstract void updateQuadTree();
	
	/**
	 * This method returns an identification string representing the plugin. it is primarily used
	 * for identifiing plugins (and which classes they are instanciated of) in log messages.
	 * 
	 */
	@Override
	public abstract String toString();
	
}
