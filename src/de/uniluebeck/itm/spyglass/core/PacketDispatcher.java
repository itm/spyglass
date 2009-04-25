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
import java.util.List;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
/**
 * Instances of this class are responsible for distributing Packet objects to all active Spyglass
 * plug-ins.<br>
 * The distribution process includes iteration over all plug-ins that the {@link PluginManager}
 * handles. Please note that the iteration process is not synchronized. That is, if another thread
 * is accessing the plug-ins list of the {@link PluginManager}, the distribution of Packets may
 * cause a concurrent modification exception.
 */
public class PacketDispatcher {
	private static Logger log = SpyglassLoggerFactory.getLogger(PacketDispatcher.class);

	private PluginManager pluginManager = null;

	// --------------------------------------------------------------------------
	/**
	 * Constructor.
	 *
	 * @param spyglass
	 *            the application's main class
	 */
	public PacketDispatcher(final Spyglass spyglass) {
		if (spyglass.getPluginManager() == null) {
			log.error("Supplied plug-in manager is null");
			throw new IllegalArgumentException("pluginManager must not be null.");
		}

		this.pluginManager = spyglass.getPluginManager();

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener("pluginManager", new PropertyChangeListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				pluginManager = (PluginManager) evt.getNewValue();
			}
		});
	}

	// --------------------------------------------------------------------------
	/**
	 * This method distributes the given Packet object to all loaded plugins by invoking the
	 * <code>handlePacket</code> method of a plugin.
	 *
	 * @param packet
	 *            The packet object to be distributed.
	 * @throws InterruptedException
	 */
	public void dispatchPacket(final SpyglassPacket packet) throws InterruptedException {
		if (packet == null) {
			return;
		}

		// get the active node positioner which has to handle the packet
		// synchronously.
		// Note that no exception is caught here since an error
		// here affects the whole application (since there has to be an active
		// note positionen which can handle packets all the time!)
		// An exception like this has to be handled differently
		final NodePositionerPlugin np = pluginManager.getNodePositioner();

		if (log.isDebugEnabled()) {
			log.debug("Dispatching packet[" + packet + "] to NodePositioner " + np);
		}

		try {
			np.handlePacket(packet);
		} catch (final Exception e1) {
			log.error("Plugin " + np + " threw an exception while handling the packet " + packet, e1);
		}

		final List<Plugin> plugins = pluginManager.getActivePlugins();
		if (plugins == null) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Dispatching packet[" + packet + "] to plugins: " + plugins);
		}

		for (final Plugin plugin : plugins) {
			// We handled the active NodePositioner already
			if (plugin instanceof NodePositionerPlugin) {
				continue;
			}
			try {
				plugin.handlePacket(packet);
			} catch (final InterruptedException e) {
				throw e; // we don't handle this
			} catch (final Exception e) {
				log.error("Plugin " + plugin + " threw an exception while handling the packet " + packet, e);
			}
		}
	}
}
