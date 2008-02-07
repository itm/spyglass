/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.plugin;

import java.util.*;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * The PluginManager holds all loaded plugins and is basically a wrapper for an internal list of plugins.
 */
@Root
public class PluginManager {
	@ElementList
	List<Plugin> plugins = new ArrayList<Plugin>();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public List<Plugin> getPlugins() {
		return plugins;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a plugin. Afters adding a new plugin, the internal plugin list gets sorted by the natural sorting order of
	 * the plugins (that is, by priority).
	 * 
	 * @param plugin
	 *            The plugin object to be added.
	 */
	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
		Collections.sort(plugins);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void removePlugin(Plugin plugin) {
		plugins.remove(plugin);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPlugins(List<Plugin> plugins) {
		this.plugins = plugins;
	}

}
