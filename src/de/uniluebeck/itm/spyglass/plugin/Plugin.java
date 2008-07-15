/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;
import java.util.Queue;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Widget;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.SubLayer;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Base class for all Spyglass plugins. A plugin has at least a priority, that determines the drawing order, a state
 * (active or not) and a default drawing object that is being used for drawing jobs.
 */
@Root
public abstract class Plugin implements Runnable {

	@Attribute
	private boolean isActive = true;

	private PluginManager pluginManager;
	private Queue<Packet> packetQueue;
	private SubLayer quadTree;

	// --------------------------------------------------------------------------------
	/**
	 * This method handles a Packet object. Usually, a plugin creates a new DrawingObject for each packet it handles.
	 * 
	 * @param packet
	 *            The packet object to handle.
	 */
	public abstract void handlePacket(Packet packet);

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public abstract String name();


	// --------------------------------------------------------------------------------
	/**
	 * holt sich die information aus dem PluginXMLConfig-Objekt
	 */
	public final boolean isActive() {
		return isActive;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Löscht den Zustand des Plugins, z.B. QuadTree leeren, Instanzvariablen auf
	 * Default stellen
	 */
	public abstract void reset();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public final void setActive(boolean isActive) {
		this.isActive = isActive;

		if (pluginManager != null)
			pluginManager.setPluginStatus(this, isActive);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public final void setPluginManager(PluginManager pluginManager) {
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
	 * @param pluginManager
	 * @param drawingArea
	 */
	public void Plugin(PluginManager pluginManager, DrawingArea drawingArea){

	}

	/**
	 * 
	 * @param parent
	 * @param cs
	 */
	public abstract PreferencesConfigurationWidget  createPreferencesWidget(Widget parent, ConfigStore cs);

	/**
	 * should be implemented static
	 * @param parent
	 * @param cs
	 */
	public abstract PreferencesConfigurationWidget createTypePreferencesWidget(Widget parent, ConfigStore cs);

	/**
	 * 
	 * @param drawingArea
	 */
	public abstract List<DrawingObject> getDrawingObjects(DrawingArea drawingArea);

	public abstract String getHumanReadableName();

	public abstract PluginXMLConfig getXMLConfig();

	/**
	 * 
	 * @param e
	 * @param drawingArea
	 */
	public boolean handleEvent(MouseEvent e, DrawingArea drawingArea){
		return false;
	}

	/**
	 * holt sich die Information aus dem PluginXMLConfig-Objekt
	 */
	public boolean isVisible(){
		return false;
	}

	/**
	 * must not write in the quadtree
	 * for expensive calculations
	 * 
	 * @param packet
	 */
	protected abstract void processPacket(Packet packet);

	/**
	 * Should never be overridden!!!
	 */
	public void run(){

	}

	/**
	 * 
	 * @param setVisible
	 */
	public void setVisible(boolean setVisible){

	}

	/**
	 * 
	 * @param xmlConfig
	 */
	public abstract void setXMLConfig(PluginXMLConfig xmlConfig);

	/**
	 * must only be called from run()
	 * may change the quadtree, but it should be fast, because it blocks the GUI
	 */
	protected abstract void updateQuadTree();

}
