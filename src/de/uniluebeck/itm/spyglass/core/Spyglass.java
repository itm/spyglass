/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.core;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Category;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.drawing.SpyglassCanvas;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.Logging;

// --------------------------------------------------------------------------------
/**
 * Spyglass is an application for visualizing network packets coming from an arbitrary source, defined by a gateway
 * instance. This class is the core of the Spyglass program. It reads the XML configuration, instantiate objects,
 * injects dependencies and handles the PacketReader and Visualization threads. After instantiating of this class, call
 * the <code>start()</code> method to start the visualization. An arbitrary GUI can be used with Spyglass, since the
 * visualization is handled by a SpyglassCanvas, which can be easily adopted to other GUI libraries. Use the
 * <code>addSpyglassListener(...)</code> method to get informed when a redraw of the scene is needed.
 */
public class Spyglass {
	private static Category log = Logging.get(Spyglass.class);

	private static final String CONFIG_FILE = "config.xml";

	private InformationDispatcher infoDispatcher = null;

	private Deque<Packet> packetCache = new ArrayDeque<Packet>();

	private PluginManager pluginManager = null;

	private SpyglassCanvas canvas = null;

	private PacketReader packetReader = null;

	private PacketProducerTask packetProducerTask = null;

	private VisualizationTask visualizationTask = null;

	private ExecutorService executor = Executors.newFixedThreadPool(2);

	private EventListenerList listeners = new EventListenerList();

	private boolean visualizationRunning = true;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor. Invokes the XML configuration reading.
	 */
	public Spyglass() {
		init();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Starts the visualization by starting a packet producer and a visualization thread.
	 */
	public void start() {
		log.debug("Starting visualization and packetProducer Task");
		executor.execute(packetProducerTask);
		executor.execute(visualizationTask);
		executor.shutdown();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a SpyglassListener object that gets notified of a Spyglass event (e.g. to know when a redraw must be done).
	 * 
	 * @param listener
	 *            The SpyglassListener object to add.
	 */
	public void addSpyglassListener(SpyglassListener listener) {
		if (listener == null)
			return;

		log.debug("Added new listener: " + listener);
		listeners.add(SpyglassListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void fireRedrawEvent(EventObject e) {
		// Get listeners
		EventListener[] list = listeners.getListeners(SpyglassListener.class);

		if (log.isDebugEnabled())
			log.debug("Fire redraw event");

		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1)
			((SpyglassListener) list[i]).redraw(e);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Load the settings
	 */
	private void init() {
		log.debug("Initializing. Reading config from file: " + CONFIG_FILE);
		SpyglassConfiguration config = null;

		File cfgFile = new File(CONFIG_FILE);
		if (!cfgFile.isFile())
			throw new RuntimeException("Can't find config file '" + CONFIG_FILE + "'");

		try {
			Serializer serializer = new Persister();
			config = serializer.read(SpyglassConfiguration.class, cfgFile);
			if (config == null)
				throw new RuntimeException("Can't load configuration.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create and inject objects
		pluginManager = config.getPluginManager();
		canvas = config.getCanvas();
		packetReader = config.getPacketReader();

		infoDispatcher = new InformationDispatcher(pluginManager);
		packetProducerTask = new PacketProducerTask(this);
		visualizationTask = new VisualizationTask(config.getVisualizationDelay(), config.getVisualizationInitialDelay(), config.getFps(), this);

		log.debug("Init done");
	}

	// --------------------------------------------------------------------------------
	public InformationDispatcher getInfoDispatcher() {
		return infoDispatcher;
	}

	// --------------------------------------------------------------------------------
	public void setInfoDispatcher(InformationDispatcher infoDispatcher) {
		this.infoDispatcher = infoDispatcher;
	}

	// --------------------------------------------------------------------------------
	public Deque<Packet> getPacketCache() {
		return packetCache;
	}

	// --------------------------------------------------------------------------------
	public void setPacketCache(Deque<Packet> packetCache) {
		this.packetCache = packetCache;
	}

	// --------------------------------------------------------------------------------
	public SpyglassCanvas getCanvas() {
		return canvas;
	}

	// --------------------------------------------------------------------------------
	public void setCanvas(SpyglassCanvas canvas) {
		this.canvas = canvas;
	}

	// --------------------------------------------------------------------------------
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	// --------------------------------------------------------------------------------
	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	// --------------------------------------------------------------------------------
	public boolean isVisualizationRunning() {
		return visualizationRunning;
	}

	// --------------------------------------------------------------------------------
	public void setVisualizationRunning(boolean visualizationRunning) {
		this.visualizationRunning = visualizationRunning;
	}

	// --------------------------------------------------------------------------------
	public PacketReader getPacketReader() {
		return packetReader;
	}

	// --------------------------------------------------------------------------------
	public void setPacketReader(PacketReader packetReader) {
		this.packetReader = packetReader;
	}

}
