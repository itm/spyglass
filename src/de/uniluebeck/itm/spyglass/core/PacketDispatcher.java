/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.util.List;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.Tools;

// ------------------------------------------------------------------------------
// --
/**
 * The InformationDispatch class is responsible for distributing Packet objects to all loaded
 * Spyglass plugins. The distribution process includes iteration over all plugins that the Plugin
 * Manager handles. Please note that the iteration process is not synchronized. That is, if another
 * thread is accessing the plugins list of the Plugin Manager, the distribution of Packets may cause
 * a concurrent modification exception.
 */
public class PacketDispatcher {
	private static Logger log = SpyglassLoggerFactory.get(PacketDispatcher.class);
	
	private PluginManager pluginManager = null;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor.
	 * 
	 * @param pluginManager
	 *            The plug-in manager object that is being used to get all loaded plug-ins.
	 * @param packetRecorder
	 *            object which records incoming packets
	 */
	public PacketDispatcher(final PluginManager pluginManager) {
		if (pluginManager == null) {
			log.error("Supplied plug-in manager is null");
			throw new IllegalArgumentException("pluginManager must not be null.");
		}
		
		this.pluginManager = pluginManager;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * This method distributes the given Packet object to all loaded plugins by invoking the
	 * <code>handlePacket</code> method of a plugin.
	 * 
	 * @param packet
	 *            The packet object to be distributed.
	 */
	public void dispatchPacket(final SpyglassPacket packet) {
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
		log.debug("Dispatching packet[" + packet + "] to NodePositioner " + np);
		np.handlePacket(packet);
		
		final List<Plugin> plugins = pluginManager.getActivePlugins();
		if (plugins == null) {
			return;
		}
		
		log.debug("Dispatching packet[" + packet + "] to plugins: " + Tools.toString(plugins));
		
		for (final Plugin plugin : plugins) {
			// We handled the active NodePositioner already
			if (plugin instanceof NodePositionerPlugin) {
				continue;
			}
			try {
				plugin.handlePacket(packet);
			} catch (final Throwable t) {
				log.error("The plugin " + plugin + " could not handle a packet : " + t, t);
			}
		}
	}
}
