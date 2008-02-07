/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.core;

import java.util.List;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.util.Logging;
import de.uniluebeck.itm.spyglass.util.Tools;

// --------------------------------------------------------------------------------
/**
 * The InformationDispatch class is responsible for distributing Packet objects to all loaded Spyglass plugins. The
 * distribution process includes iteration over all plugins that the Plugin Manager handles. Please note that the
 * iteration process is not synchronized. That is, if another thread is accessing the plugins list of the Plugin
 * Manager, the distribution of Packets may cause a concurrent modification exception.
 */
public class InformationDispatcher {
	private static Category log = Logging.get(InformationDispatcher.class);

	private PluginManager pluginManager = null;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor.
	 * 
	 * @param pluginManager
	 *            The plugin manager object that is being used to get all loaded plugins.
	 */
	public InformationDispatcher(PluginManager pluginManager) {
		if (pluginManager == null) {
			log.error("Supplied plug-in manager is null");
			throw new IllegalArgumentException("pluginManager must not be null.");
		}

		this.pluginManager = pluginManager;
	}

	// --------------------------------------------------------------------------------
	/**
	 * This method distributes the given Packet object to all loaded plugins by invoking the <code>handlePacket</code>
	 * method of a plugin.
	 * 
	 * @param packet
	 *            The packet object to be distributed.
	 */
	public void dispatchPacket(Packet packet) {
		if (packet == null)
			return;

		List<Plugin> plugins = pluginManager.getPlugins();
		if (plugins == null)
			return;

		if (log.isDebugEnabled())
			log.debug("Dispatching packet[" + packet + "] to plugins: " + Tools.toString(plugins));

		for (Plugin plugin : plugins)
			plugin.handlePacket(packet);
	}

}
