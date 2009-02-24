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
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
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
	private static Logger log = SpyglassLoggerFactory.getLogger(Spyglass.class);

	private PacketDispatcher packetDispatcher = null;

	private PluginManager pluginManager = null;

	private PacketReader packetReader = null;

	private PacketProducerTask packetProducerTask = null;

	/**
	 * Contains the packetProducerTask
	 */
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	private final ConfigStore configStore;

	// --------------------------------------------------------------------------
	/**
	 * Constructor. Invokes the XML configuration reading from the default configuration files.
	 * Which file is used depends on the context (if Spyglass is used as stand alone application or
	 * iShell plug-in).
	 * 
	 * @throws Exception
	 * 
	 */
	public Spyglass() throws Exception {
		configStore = new ConfigStore();
		init();
	}

	// --------------------------------------------------------------------------
	/**
	 * Initializes the Spyglass application
	 */
	private void init() {
		// Create and inject objects
		setPluginManager(configStore.getSpyglassConfig().getPluginManager());

		setPacketReader(configStore.getSpyglassConfig().getPacketReader());

		packetDispatcher = new PacketDispatcher(this);

		packetProducerTask = new PacketProducerTask(this, configStore.getSpyglassConfig().getGeneralSettings().getPacketDeliveryInitialDelay());

		configStore.getSpyglassConfig().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("packetReader")) {
					setPacketReader((PacketReader) evt.getNewValue());
				} else if (evt.getPropertyName().equals("pluginManager")) {
					setPluginManager((PluginManager) evt.getNewValue());
				}
			}
		});

		log.debug("Init done");
	}

	// --------------------------------------------------------------------------
	/**
	 * Starts the packet dispatching
	 */
	public void start() {
		log.debug("Starting packetProducer Task");
		executor.execute(packetProducerTask);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shuts down the Spyglass application.<br>
	 * All Threads and plug-ins will be stopped.
	 */
	public void shutdown() {
		// Shutdown the packetProducerTask
		executor.shutdownNow();

		// TODO: not sure if this will even be executed, since were shutting down the configStore
		// right after
		configStore.store();
		try {
			configStore.shutdown();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// shutdown all plug-ins
		for (final Plugin p : pluginManager.getPlugins()) {
			try {
				p.shutdown();
			} catch (final Exception e) {
				log.warn("The plugin could not be shut down properly. Continuing anyway.", e);
			}
		}

		log.debug("All plugin-threads stopped");
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the facility which is responsible for dispatching incoming packets to all active
	 * plug-ins.
	 * 
	 * @return the facility which is responsible for dispatching incoming packets to all active
	 *         plug-ins.
	 */
	public PacketDispatcher getPacketDispatcher() {
		return packetDispatcher;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the facility which is responsible for dispatching incoming packets to all active
	 * plug-ins.
	 * 
	 * @param packetDispatcher
	 *            the facility which is responsible for dispatching incoming packets to all active
	 *            plug-ins.
	 */
	public void setPacketDispatcher(final PacketDispatcher packetDispatcher) {
		this.packetDispatcher = packetDispatcher;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the facility which manages the currently loaded plug-ins
	 * 
	 * @return the facility which manages the currently loaded plug-ins
	 */
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the facility which manages the currently loaded plug-ins
	 * 
	 * @param pluginManager
	 *            the facility which manages the currently loaded plug-ins
	 */
	public void setPluginManager(final PluginManager pluginManager) {
		this.pluginManager = pluginManager;
		pluginManager.init();
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the facility which reads packets to be evaluated from a configurable source (e.g. a
	 * file)
	 * 
	 * @return the facility which reads packets to be evaluated from a configurable source (e.g. a
	 *         file)
	 */
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
	/**
	 * Sets the facility which reads packets to be evaluated from a configurable source (e.g. a
	 * file)
	 * 
	 * @param packetReader
	 *            the facility which reads packets to be evaluated from a configurable source (e.g.
	 *            a file)
	 */
	public void setPacketReader(final PacketReader packetReader) {
		packetReader.init(this);
		this.packetReader = packetReader;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns the facility responsible for loading and storing the application's configuration
	 * 
	 * @return the facility responsible for loading and storing the application's configuration
	 */
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

	// --------------------------------------------------------------------------------
	/**
	 * Resets the application
	 */
	public void reset() {
		try {
			log.debug("The PacketReader will be resetted now");
			packetReader.reset();
			log.debug("The PacketReader was sucessfully resetted");
		} catch (final IOException e) {
			log.error("Error while trying to reset the packet reader", e);
		}
		// reset the plug-ins. The active NodePositionerPlugin has to be reseted at last because
		// otherwise plug-ins which are not reseted, yet might miss needed positioning information
		final List<Plugin> plugins = pluginManager.getPlugins();
		for (final Plugin plugin : plugins) {
			if (!((plugin instanceof NodePositionerPlugin) && plugin.isActive())) {
				log.debug("The plug-in named '" + plugin.getInstanceName() + "' will be resetted now");
				plugin.reset();
				log.debug("The plug-in named '" + plugin.getInstanceName() + "' was sucessfully resetted");
			}
		}
		log.debug("The node positioner '" + pluginManager.getNodePositioner().getInstanceName() + "' will be resetted now");
		pluginManager.getNodePositioner().reset();
		log.debug("The node positioner '" + pluginManager.getNodePositioner().getInstanceName() + "' was sucessfully resetted");
	}

}
