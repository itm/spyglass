/*
 * ------------------------------------------------------------------------------ --
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Category;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.bsi.flegsens.RandomNodePositioner;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener.ListChangeEvent;
import de.uniluebeck.itm.spyglass.plugin.gridpainter.GridPainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.imagepainter.ImagePainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.linepainter.LinePainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.mappainter.MapPainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangePlugin;
import de.uniluebeck.itm.spyglass.plugin.objectpainter.ObjectPainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionPacketNodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.SimpleGlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.plugin.simplenodepainter.SimpleNodePainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.springembedderpositioner.SpringEmbedderPositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter.VectorSequencePainterPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// ------------------------------------------------------------------------------
// --
/**
 * The PluginManager holds all loaded plugins and is basically a wrapper for an
 * internal list of plugins.
 */
@Root
public class PluginManager {
	
	private static Category log = SpyglassLogger.get(PluginManager.class);
	
	@ElementList
	private final List<Plugin> plugins = Collections.synchronizedList(new ArrayList<Plugin>());
	
	private final Set<PluginListChangeListener> pluginListChangeListeners = new HashSet<PluginListChangeListener>();
	
	private static final List<Class<? extends Plugin>> availablePluginsTypes = new ArrayList<Class<? extends Plugin>>();
	
	static {
		availablePluginsTypes.add(GridPainterPlugin.class);
		availablePluginsTypes.add(ImagePainterPlugin.class);
		availablePluginsTypes.add(MapPainterPlugin.class);
		availablePluginsTypes.add(NodeSensorRangePlugin.class);
		availablePluginsTypes.add(ObjectPainterPlugin.class);
		availablePluginsTypes.add(SimpleGlobalInformationPlugin.class);
		availablePluginsTypes.add(SimpleNodePainterPlugin.class);
		availablePluginsTypes.add(PositionPacketNodePositionerPlugin.class);
		availablePluginsTypes.add(RandomNodePositioner.class);
		availablePluginsTypes.add(SpringEmbedderPositionerPlugin.class);
		availablePluginsTypes.add(LinePainterPlugin.class);
		availablePluginsTypes.add(VectorSequencePainterPlugin.class);
	}
	
	private NodePositionerPlugin nodePositioner = null;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all plugins which are currently administered by this instance and
	 * which are marked as 'active'
	 * 
	 * @return all plugins which are currently administered by this instance and
	 *         which are marked as 'active'
	 */
	public List<Plugin> getActivePlugins() {
		final List<Plugin> activePlugIns = new LinkedList<Plugin>();
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				if (p.isActive()) {
					activePlugIns.add(p);
				}
			}
		}
		return activePlugIns;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Adds a <code>PluginListChangeListener</code> instance to the list of
	 * listeners. Every listener will be informed of changes when
	 * plugin-instances are added or removed.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addPluginListChangeListener(final PluginListChangeListener listener) {
		pluginListChangeListeners.add(listener);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Removes a <code>PluginListChangeListener</code> instance from the list
	 * of listeners.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void removePluginListChangeListener(final PluginListChangeListener listener) {
		pluginListChangeListeners.remove(listener);
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all plugins which are currently administered by this instance
	 * 
	 * @return all plugins which are currently administered by this instance
	 */
	public List<Plugin> getPlugins() {
		synchronized (plugins) {
			return plugins;
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Initializes the instance by setting it as administration instance for all
	 * currently available plug-ins
	 */
	public void init() {
		// This is a workaround, since simple-xml does not call the setPlugins()
		// method
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				p.initializePacketConsumerThread();
				p.setPluginManager(this);
			}
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Adds a plug-in. The plug-in is put at the end of the list which means
	 * that the new plug-in has the lowest priority
	 * 
	 * @param plugin
	 *            The plugin object to be added.
	 */
	public void addPlugin(final Plugin plugin) {
		plugin.setPluginManager(this);
		plugin.initializePacketConsumerThread();
		synchronized (plugins) {
			plugins.add(plugin);
		}
		log.debug("Added plug-in: " + plugin);
		firePluginListChangedEvent(plugin, ListChangeEvent.NEW_PLUGIN);
		
		// TODO: fix!!!
		// TEAMQUESTION: where should the new plugin be placed? At the beginning
		// or the end?
		
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Puts a plug-in at the top of the list which means that its priority is
	 * the highest one<br>
	 * The others plug-ins' priorities are decreased by one which means that
	 * their order stays intact.
	 * 
	 * @param plugin
	 *            The plugin object to be added.
	 */
	public void increasePluginPriorityToTop(final Plugin plugin) {
		plugin.setPluginManager(this);
		synchronized (plugins) {
			plugins.remove(plugin);
			plugins.add(0, plugin);
		}
		log.debug("The plug-in: " + plugin + " is now the one with the highest priority");
		firePluginListChangedEvent(plugin, ListChangeEvent.PRIORITY_CHANGED);
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Removes a plug-in
	 */
	public void removePlugin(final Plugin plugin) {
		plugin.setActive(false);
		synchronized (plugins) {
			plugins.remove(plugin);
		}
		log.debug("Removed plug-in: " + plugin);
		firePluginListChangedEvent(plugin, ListChangeEvent.PLUGIN_REMOVED);
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Replaces the list of plug-ins
	 * 
	 * @param plugins
	 *            the new list of plug-ins
	 */
	public void setPlugins(final List<Plugin> plugins) {
		// deactivate all plug-ins which are currently available in the list (by
		// deactivating a plug-in, its thread is stopped)
		for (final Plugin plugin : plugins) {
			plugin.setActive(false);
		}
		synchronized (plugins) {
			this.plugins.clear();
			for (final Plugin p : plugins) {
				p.initializePacketConsumerThread();
				addPlugin(p);
			}
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Sets the state of a plug-in
	 * 
	 * @param plugin
	 *            the plug-in which state is to be set
	 * @param isActive
	 *            indicates if the provided plug-in is active or not
	 */
	public void setPluginStatus(final Plugin plugin, final boolean isActive) {
		
		plugin.setActive(isActive);
		synchronized (plugins) {
			if (!plugins.contains(plugin)) {
				addPlugin(plugin);
			}
		}
		firePluginListChangedEvent(plugin, ListChangeEvent.PLUGIN_STATE_CHANGED);
		
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Sets the instances which holds information about the nodes' positions.<br>
	 * Note that only one node positioner instance can be active at a time
	 * 
	 * @param np
	 *            a node positioner instance
	 */
	public void setNodePositioner(final NodePositionerPlugin np) {
		if (nodePositioner == null) {
			this.nodePositioner = np;
		} else {
			synchronized (nodePositioner) {
				this.nodePositioner = np;
			}
		}
		addPlugin(np);
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns the instances which holds information about the nodes' positions
	 * 
	 * @return the instances which holds information about the nodes' positions
	 */
	public NodePositionerPlugin getNodePositioner() {
		synchronized (nodePositioner) {
			assert (nodePositioner != null);
			return nodePositioner;
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Creates a new instance of a plug-in and entails it in the list
	 * 
	 * @param clazz
	 *            the plug-in's class
	 * @param config
	 *            the plug-in's configuration parameters
	 */
	public Plugin createNewPlugin(final Class<? extends Plugin> clazz, final PluginXMLConfig config) {
		final Plugin plugin = PluginFactory.createInstance(config, clazz);
		addPlugin(plugin);
		return plugin;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Fires an event which informs the listener about changes concerning a
	 * certain plug-in
	 * 
	 * @param p
	 *            the plug-in
	 * @param what
	 *            the reason
	 */
	private void firePluginListChangedEvent(final Plugin p, final ListChangeEvent what) {
		for (final PluginListChangeListener listener : pluginListChangeListeners) {
			listener.pluginListChanged(p, what);
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns a list of all types of plug-ins which are currently administered
	 * by this instance
	 * 
	 * @return a list of all types of plug-ins which are currently administered
	 *         by this instance
	 */
	public List<Class<? extends Plugin>> getAvailablePluginTypes() {
		return availablePluginsTypes;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all instances of a certain kind of plug-ins which are currently
	 * administered by this instance
	 * 
	 * @param clazz
	 *            the plug-in instances' class
	 */
	public List<Plugin> getPluginInstances(final Class<? extends Plugin> clazz) {
		
		final List<Plugin> instances = new LinkedList<Plugin>();
		synchronized (plugins) {
			for (final Plugin plugin : plugins) {
				if (plugin.getClass().equals(clazz)) {
					instances.add(plugin);
				}
			}
		}
		return instances;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all plug-ins which are currently visible
	 * 
	 * @return all plug-ins which are currently visible
	 */
	public List<Plugin> getVisiblePlugins() {
		final List<Plugin> visiblePlugins = new LinkedList<Plugin>();
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				if (p.isVisible()) {
					visiblePlugins.add(p);
				}
			}
		}
		return visiblePlugins;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 * @param plugin
	 */
	public void shutdownPlugin(final Plugin plugin) {
		synchronized (plugins) {
			plugin.reset();
			plugins.remove(plugin);
		}
	}
	
}
