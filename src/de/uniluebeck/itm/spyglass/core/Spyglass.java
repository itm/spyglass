/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * Spyglass is an application for visualizing network packets coming from an arbitrary source,
 * defined by a gateway instance. This class is the core of the Spyglass program. It reads the XML
 * configuration, instantiate objects, injects dependencies and handles the PacketReader and
 * Visualization threads. After instantiating of this class, call the <code>start()</code> method to
 * start the visualization. An arbitrary GUI can be used with Spyglass, since the visualization is
 * handled by a SpyglassCanvas, which can be easily adopted to other GUI libraries. Use the
 * <code>addSpyglassListener(...)</code> method to get informed when a redraw of the scene is
 * needed.
 */
public class Spyglass {
	private static Logger log = SpyglassLogger.get(Spyglass.class);
	
	// private static final String CONFIG_FILE = "config.xml";
	
	private PacketDispatcher packetDispatcher = null;
	
	private PluginManager pluginManager = null;
	
	private PacketReader packetReader = null;
	
	private PacketProducerTask packetProducerTask = null;
	
	private ExecutorService executor = Executors.newFixedThreadPool(2);
	
	/**
	 * TODO: Define exactly what this should do
	 */
	private boolean visualizationRunning = true;
	
	private final ConfigStore configStore;
	
	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the plug-in list
	 */
	private final PropertyChangeListener configStoreListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			try {
				if (evt.getPropertyName().equals("replaceConfiguration")) {
					init((SpyglassConfiguration) evt.getNewValue());
					executor = Executors.newFixedThreadPool(2);
					start();
				}
			} catch (final Exception e) {
				log.error(e, e);
			}
			
		}
		
	};
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor. Invokes the XML configuration reading from the default configuration files.
	 * Which file is used depends on the context (if spyglass is used as stand alone application or
	 * iShell plug-in).
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell plug-in
	 */
	public Spyglass(final boolean isIShellPlugin) {
		configStore = new ConfigStore(isIShellPlugin);
		configStore.addPropertyChangeListener(configStoreListener);
		init(configStore.getSpyglassConfig());
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor. Invokes the XML configuration reading.
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell plug-in
	 * @param config
	 *            the configuration parameters
	 */
	public Spyglass(final boolean isIShellPlugin, final SpyglassConfiguration config) {
		configStore = new ConfigStore(isIShellPlugin, config);
		configStore.addPropertyChangeListener(configStoreListener);
		init(configStore.getSpyglassConfig());
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor. Invokes the XML configuration reading.
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell plug-in
	 * @param configFile
	 *            the file which contains the configuration parameters
	 */
	public Spyglass(final boolean isIShellPlugin, final File configFile) {
		configStore = new ConfigStore(isIShellPlugin, configFile);
		configStore.addPropertyChangeListener(configStoreListener);
		init(configStore.getSpyglassConfig());
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	private void init(final SpyglassConfiguration config) {
		// Create and inject objects
		
		pluginManager = config.getPluginManager();
		pluginManager.init();
		
		packetReader = config.getPacketReader();
		
		packetDispatcher = new PacketDispatcher(pluginManager);
		packetProducerTask = new PacketProducerTask(this, config.getGeneralSettings()
				.getPacketDeliveryInitialDelay());
		
		log.debug("Init done");
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Starts the visualization by starting a packet producer and a visualization thread.
	 */
	public void start() {
		log.debug("Starting visualization and packetProducer Task");
		executor.execute(packetProducerTask);
		executor.shutdown();
	}
	
	public void shutdown() {
		setVisualizationRunning(false);
		configStore.store(true);
		log.info("All plugin-threads stopped");
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
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the currently active packet recorder or <code>null</code> if no recorder is active
	 * 
	 * @return the packetRecorder
	 */
	public PacketRecorder getPacketRecorder() {
		return packetReader instanceof PacketRecorder ? ((PacketRecorder) packetReader) : null;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public void setPacketReader(final PacketReader packetReader) {
		this.packetReader = packetReader;
	}
	
	// --------------------------------------------------------------------------
	// ------
	public ConfigStore getConfigStore() {
		return configStore;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the packetProducerTask
	 */
	public PacketProducerTask getPacketProducerTask() {
		return packetProducerTask;
	}
	
	/**
	 * This method gets the bounding boxes of all visible drawingObjects (which are applicable for
	 * AutoZoom and scrollbars) and merges them.
	 */
	public AbsoluteRectangle getBoundingBox() {
		final List<Plugin> list = getPluginManager().getVisibleActivePlugins();
		
		final List<DrawingObject> dobs = new ArrayList<DrawingObject>();
		
		for (final Plugin plugin : list) {
			if (plugin instanceof Drawable) {
				final Drawable plugin2 = (Drawable) plugin;
				
				dobs.addAll(plugin2.getAutoZoomDrawingObjects());
			}
		}
		
		AbsoluteRectangle maxRect = new AbsoluteRectangle();
		
		for (final DrawingObject drawingObject : dobs) {
			final AbsoluteRectangle nextRect = drawingObject.getBoundingBox();
			if (nextRect == null) {
				continue;
			}
			
			if (maxRect == null) {
				maxRect = nextRect;
			} else {
				maxRect = maxRect.union(nextRect);
			}
		}
		return maxRect;
	};
	
}
