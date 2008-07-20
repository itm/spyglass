/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import ishell.util.Logging;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Category;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * The PluginManager holds all loaded plugins and is basically a wrapper for an
 * internal list of plugins.
 */
@Root
public class PluginManager {
	
	public enum ListChangeEvent {
		
	}
	
	private static Category log = Logging.get(PluginManager.class);
	
	@ElementList
	private final List<Plugin> plugins = new ArrayList<Plugin>();
	
	private NodePositionerPlugin nodePositioner = null;
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns all plugins which are currently administered by this instance and
	 * which are marked as 'active'
	 * 
	 * @return all plugins which are currently administered by this instance and
	 *         which are marked as 'active'
	 */
	public List<Plugin> getActivePlugins() {
		return plugins;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns all plugins which are currently administered by this instance
	 * 
	 * @return all plugins which are currently administered by this instance
	 */
	public List<Plugin> getPlugins() {
		return plugins;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void init() {
		// This is a workaround, since simple-xml does not call the setPlugins()
		// method
		for (final Plugin p : plugins) {
			p.setPluginManager(this);
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Adds a plugin. Afters adding a new plugin, the internal plugin list gets
	 * sorted by the natural sorting order of the plugins (that is, by
	 * priority).
	 * 
	 * @param plugin
	 *            The plugin object to be added.
	 */
	public void addPlugin(final Plugin plugin) {
		log.debug("Added plug-in: " + plugin);
		plugin.setPluginManager(this);
		if (plugin.isActive()) {
			plugins.add(plugin);
			// Collections.sort(plugins);
			
			// TODO: fix!!!
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void removePlugin(final Plugin plugin) {
		plugins.remove(plugin);
		log.debug("Removed plug-in: " + plugin);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPlugins(final List<Plugin> plugins) {
		this.plugins.clear();
		for (final Plugin p : plugins) {
			addPlugin(p);
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPluginStatus(final Plugin plugin, final boolean isActive) {
		
		if (isActive) {
			if (!plugins.contains(plugin)) {
				addPlugin(plugin);
			}
			
		} else {
			removePlugin(plugin);
		}
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setNodePositioner(final NodePositionerPlugin np) {
		this.nodePositioner = np;
		addPlugin(np);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public NodePositionerPlugin getNodePositioner() {
		assert (nodePositioner != null);
		return nodePositioner;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @param clazz
	 * @param config
	 */
	public Plugin createNewPlugin(final Class<Plugin> clazz, final PluginXMLConfig config) {
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @param p
	 * @param what
	 */
	private void firePluginListChangedEvent(final Plugin p, final ListChangeEvent what) {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public List<Class<Plugin>> getAvailablePluginTypes() {
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns all instances of a certain kind of plug-ins which are currently
	 * administered by this instance
	 * 
	 * @param clazz
	 *            the plug-in instances' class
	 */
	public List<Plugin> getPluginInstances(final Class<? extends Plugin> clazz) {
		
		final List<Plugin> instances = new LinkedList<Plugin>();
		for (final Plugin plugin : plugins) {
			if (plugin.getClass().equals(clazz)) {
				instances.add(plugin);
			}
		}
		return instances;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns all plugins which are currently visible
	 * 
	 * @return all plugins which are currently visible
	 */
	public List<Plugin> getVisiblePlugins() {
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @param plugin
	 */
	public void shutdownPlugin(final Plugin plugin) {
		
	}
	
}
