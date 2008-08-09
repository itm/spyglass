/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * Spyglass is an application for visualizing network packets coming from an
 * arbitrary source, defined by a gateway instance. This class is the core of
 * the Spyglass program. It reads the XML configuration, instantiate objects,
 * injects dependencies and handles the PacketReader and Visualization threads.
 * After instantiating of this class, call the <code>start()</code> method to
 * start the visualization. An arbitrary GUI can be used with Spyglass, since
 * the visualization is handled by a SpyglassCanvas, which can be easily adopted
 * to other GUI libraries. Use the <code>addSpyglassListener(...)</code>
 * method to get informed when a redraw of the scene is needed.
 */
public class Spyglass {
	private static Category log = SpyglassLogger.get(Spyglass.class);
	
	// private static final String CONFIG_FILE = "config.xml";
	
	private final boolean isIShellPlugin;
	
	private PacketDispatcher packetDispatcher = null;
	
	private ArrayList<Packet> packetCache = new ArrayList<Packet>(250);
	
	private PluginManager pluginManager = null;
	
	private PacketReader packetReader = null;
	
	private PacketProducerTask packetProducerTask = null;
	
	private VisualizationTask visualizationTask = null;
	
	private final ExecutorService executor = Executors.newFixedThreadPool(2);
	
	private final EventListenerList listeners = new EventListenerList();
	
	private boolean visualizationRunning = true;
	
	private DrawingArea drawingArea;
	
	private final ConfigStore configStore;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor. Invokes the XML configuration reading from the default
	 * configuration files. Which file is used depends on the context (if
	 * spyglass is used as stand alone application or iShell plug-in).
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell
	 *            plug-in
	 */
	public Spyglass(final boolean isIShellPlugin) {
		this.isIShellPlugin = isIShellPlugin;
		configStore = new ConfigStore(isIShellPlugin);
		init(configStore.getSpyglassConfig());
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor. Invokes the XML configuration reading.
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell
	 *            plug-in
	 * @param config
	 *            the configuration parameters
	 */
	public Spyglass(final boolean isIShellPlugin, final SpyglassConfiguration config) {
		this.isIShellPlugin = isIShellPlugin;
		configStore = new ConfigStore(isIShellPlugin, config);
		init(configStore.getSpyglassConfig());
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor. Invokes the XML configuration reading.
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell
	 *            plug-in
	 * @param configFile
	 *            the file which contains the configuration parameters
	 */
	public Spyglass(final boolean isIShellPlugin, final File configFile) {
		this.isIShellPlugin = isIShellPlugin;
		configStore = new ConfigStore(isIShellPlugin, configFile);
		init(configStore.getSpyglassConfig());
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	private void init(final SpyglassConfiguration config) {
		// Create and inject objects
		// configFilePath = config.get
		pluginManager = config.getPluginManager();
		pluginManager.setNodePositioner(config.getNodePositioner());
		pluginManager.init();
		
		for (final de.uniluebeck.itm.spyglass.plugin.Plugin p : pluginManager.getPlugins()) {
			System.out.println(p.getInstanceName());
			System.out.println(p.isActive());
			System.out.println(p.isThreadRunning());
			System.out.println("");
		}
		
		drawingArea = config.getDrawingArea();
		
		packetReader = config.getPacketReader();
		
		packetDispatcher = new PacketDispatcher(pluginManager);
		packetProducerTask = new PacketProducerTask(this, config.getPacketDeliveryInitialDelay());
		visualizationTask = new VisualizationTask(config.getFps(), this);
		
		log.debug("Init done");
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Starts the visualization by starting a packet producer and a
	 * visualization thread.
	 */
	public void start() {
		log.debug("Starting visualization and packetProducer Task");
		executor.execute(packetProducerTask);
		executor.execute(visualizationTask);
		executor.shutdown();
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Adds a SpyglassListener object that gets notified of a Spyglass event
	 * (e.g. to know when a redraw must be done).
	 * 
	 * @param listener
	 *            The SpyglassListener object to add.
	 */
	public void addSpyglassListener(final SpyglassListener listener) {
		if (listener == null) {
			return;
		}
		
		log.debug("Added new listener: " + listener);
		listeners.add(SpyglassListener.class, listener);
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public void fireRedrawEvent(final EventObject e) {
		// Get listeners
		final EventListener[] list = listeners.getListeners(SpyglassListener.class);
		
		// if (log.isDebugEnabled()) {
		// log.debug("Fire redraw event");
		// }
		
		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			((SpyglassListener) list[i]).redraw(e);
		}
	}
	
	// --------------------------------------------------------------------------
	// -------
	public PacketDispatcher getPacketDispatcher() {
		return packetDispatcher;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public void setPacketDispatcher(final PacketDispatcher packetDispatcher) {
		this.packetDispatcher = packetDispatcher;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public ArrayList<Packet> getPacketCache() {
		return packetCache;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public void setPacketCache(final ArrayList<Packet> packetCache) {
		this.packetCache = packetCache;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public void setPluginManager(final PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public boolean isVisualizationRunning() {
		return visualizationRunning;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public void setVisualizationRunning(final boolean visualizationRunning) {
		this.visualizationRunning = visualizationRunning;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public PacketReader getPacketReader() {
		return packetReader;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public void setPacketReader(final PacketReader packetReader) {
		this.packetReader = packetReader;
	}
	
	// --------------------------------------------------------------------------
	// ------
	private void createPluginInstancesFromConfig() {
		// TODO: nothing to do here since the plugin manager is persistent
		// itself?!?
	}
	
	// --------------------------------------------------------------------------
	// ------
	public ConfigStore getConfigStore() {
		return configStore;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the drawingArea
	 */
	public DrawingArea getDrawingArea() {
		return drawingArea;
	}
	
}
