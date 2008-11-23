/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

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
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// ------------------------------------------------------------------------------
// --
/**
 * The PluginManager holds all loaded plugins and is basically a wrapper for an internal list of
 * plugins.
 */
@Root
public class PluginManager {

	private static Logger log = SpyglassLoggerFactory.getLogger(PluginManager.class);

	@ElementList
	private final List<Plugin> plugins = Collections.synchronizedList(new ArrayList<Plugin>());

	private final Set<PluginListChangeListener> pluginListChangeListeners = Collections.synchronizedSet(new HashSet<PluginListChangeListener>());

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
		availablePluginsTypes.add(SpringEmbedderPositionerPlugin.class);
		availablePluginsTypes.add(LinePainterPlugin.class);
		availablePluginsTypes.add(VectorSequencePainterPlugin.class);
	}

	/**
	 * List of plugins that are currently being removed
	 * 
	 * (this is currently only used to make sure, that such a plugin wont be elected as new active
	 * node positioner)
	 */
	private final List<Plugin> removePending = new ArrayList<Plugin>();

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all plugins which are currently administered by this instance and which are marked as
	 * 'active'
	 * 
	 * @return all plugins which are currently administered by this instance and which are marked as
	 *         'active'
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
	 * Adds a <code>PluginListChangeListener</code> instance to the list of listeners. Every
	 * listener will be informed of changes when plugin-instances are added or removed.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addPluginListChangeListener(final PluginListChangeListener listener) {
		pluginListChangeListeners.add(listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a <code>PluginListChangeListener</code> instance from the list of listeners.
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
	 * Returns a list-copy of all plugins which are currently administered by this instance
	 * 
	 * @return a list-copy of all plugins which are currently administered by this instance
	 */
	public List<Plugin> getPlugins() {
		synchronized (plugins) {
			return new ArrayList<Plugin>(plugins);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a list-copy of all plugins which are currently administered by this instance, except
	 * of the plugins that are of a class (or extending a class) contained in the excludes list
	 * 
	 * @param checkHierarchy
	 *            <code>true</code> if the class hierarchy should be checked, such that even plugins
	 *            derived from a class included in the <code>excludes</code> list will be excluded,
	 *            <code>false</code> if only plugins of exactly the class contained in
	 *            <code>exclude</code> list shall be excluded
	 * @param excludes
	 *            plugin class to exclude from the list
	 * @return a list-copy of plugin instances
	 */
	@SuppressWarnings("unchecked")
	public List<Plugin> getPlugins(final boolean checkHierarchy, final Class<? extends Plugin>... excludes) {

		Class<? extends Plugin> currentClass;
		final List<Plugin> pluginList = new ArrayList<Plugin>();
		boolean containedInExcludes;

		synchronized (plugins) {
			for (final Plugin plugin : plugins) {

				containedInExcludes = false;
				currentClass = plugin.getClass();

				if (checkHierarchy) {

					while (!currentClass.equals(Plugin.class)) {

						currentClass = (Class<? extends Plugin>) currentClass.getSuperclass();

						for (final Class<? extends Plugin> exclude : excludes) {
							if (currentClass.equals(exclude)) {
								containedInExcludes = true;
								break;
							}
						}

					}

				} else {

					for (final Class<? extends Plugin> exclude : excludes) {
						if (currentClass.equals(exclude)) {
							containedInExcludes = true;
							break;
						}
					}

				}

				if (!containedInExcludes) {
					pluginList.add(plugin);
				}

			}
		}

		return pluginList;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Initializes the instance by setting it as administration instance for all currently available
	 * plug-ins
	 * 
	 * This method is called after the PluginManager got instanciated by the XML Deserializer
	 */
	public void init() {

		// This is a workaround, since simple-xml does not call the setPlugins()
		// method
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				connectPlugin(p);
				log.debug(p.getInstanceName());
			}
		}
		log.debug("All plug-ins loaded and connected");
	}

	/**
	 * Connect the Plugin with the plugin manager
	 */
	private void connectPlugin(final Plugin plugin) {
		// If the plugin is a NodePositioner, we have to act when the plugin is activated
		// (to disable all other plugins)
		if (plugin instanceof NodePositionerPlugin) {
			plugin.getXMLConfig().addPropertyChangeListener("active", new PropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					final Boolean activeOld = (Boolean) evt.getOldValue();
					final Boolean activeNew = (Boolean) evt.getNewValue();
					if (activeNew && !activeOld) {
						// the plugin got activated

						newNodePositioner(plugin);

						firePluginListChangedEvent(plugin, ListChangeEvent.NEW_NODE_POSITIONER);

					} else if (activeOld && !activeNew) {
						// the plugin got deactivated

						findNewNodePositioner();
					}

				}

			});
		}

		plugin.init(this);

	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Adds a plug-in if it is not contained yet. The plug-in is put at the end of the list which
	 * means that the new plug-in has the lowest priority
	 * 
	 * @param plugin
	 *            The plugin object to be added.
	 */
	private void addPlugin(final Plugin plugin) {
		this.connectPlugin(plugin);

		if (!plugins.contains(plugin)) {
			plugins.add(plugin);
		}
		log.debug("Added plug-in: " + plugin);

		firePluginListChangedEvent(plugin, ListChangeEvent.NEW_PLUGIN);

		// TODO: fix!!!
		// TEAMQUESTION: where should the new plugin be placed? At the beginning
		// or the end?

	}

	/**
	 * Enable another NodePositioner, if the active one got disabled or deleted.
	 */
	private void findNewNodePositioner() {
		log.debug("Finding a new node positioner");

		final ArrayList<Plugin> nodePositioner = new ArrayList<Plugin>();
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				if ((p instanceof NodePositionerPlugin) && !this.removePending.contains(p)) {
					nodePositioner.add(p);

					// If there is already one active, do nothing.
					if (p.isActive()) {
						return;
					}

				}
			}
		}

		assert (nodePositioner.size() > 0);

		// activate the first one in the list
		log.debug("Enabling " + nodePositioner.get(0));
		nodePositioner.get(0).setActive(true);

	}

	/**
	 * Disable all NodePositioner Plugins except the given one.
	 */
	private void newNodePositioner(final Plugin plugin) {
		log.debug("Disabling old node positioner; new one is " + plugin);

		synchronized (plugins) {
			for (final Plugin p : plugins) {
				if ((p instanceof NodePositionerPlugin) && (p != plugin) && p.isActive()) {
					p.getXMLConfig().setActive(false);
					log.debug("Disabled " + p);

				}
			}
		}

	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Puts a plug-in at the top of the list which means that its priority is the highest one<br>
	 * The others plug-ins' priorities are decreased by one which means that their order stays
	 * intact.
	 * 
	 * @param plugin
	 *            The plugin object whose priority is to be increased.
	 * @throws RuntimeException
	 *             if one tries to increase the priority of a plugin that is not managed by the
	 *             <code>PluginManager</code>.
	 */
	public void increasePluginPriorityToTop(final Plugin plugin) {

		if (!plugins.remove(plugin)) {
			throw new RuntimeException("The plugin was not yet managed.");
		}
		plugins.add(0, plugin);
		log.debug("The plug-in: " + plugin + " is now the one with the highest priority");
		firePluginListChangedEvent(plugin, ListChangeEvent.PRIORITY_CHANGED);
	}

	public void increasePluginPriorities(final List<Plugin> list) {

		final HashMap<Plugin, Integer> newPrios = new HashMap<Plugin, Integer>();

		synchronized (this.plugins) {

			int currentIndex;

			for (final Plugin p : list) {
				currentIndex = plugins.indexOf(p);
				if (currentIndex == -1) {
					throw new RuntimeException("The plugin was not yet managed.");
				}
				newPrios.put(p, currentIndex == 0 ? 0 : currentIndex - 1);
			}

			for (final Plugin p : list) {
				plugins.remove(p);
				plugins.add(newPrios.get(p), p);
			}

		}

		for (final Plugin p : plugins) {
			log.debug("Gave plugin " + p + " priority number " + plugins.indexOf(p));
			firePluginListChangedEvent(p, ListChangeEvent.PRIORITY_CHANGED);
		}

	}

	public void decreasePluginPriorities(final List<Plugin> list) {

		final HashMap<Integer, Plugin> newPrios = new HashMap<Integer, Plugin>();

		synchronized (this.plugins) {

			int currentIndex, moveCount = 0;
			boolean move;

			for (final Plugin p : plugins) {

				move = list.contains(p);
				currentIndex = plugins.indexOf(p);
				newPrios.put(move ? (currentIndex + 1) : (currentIndex + (-1 * moveCount)), p);
				moveCount = move ? (moveCount + 1) : 0;

			}

			for (final Plugin p : newPrios.values()) {
				plugins.remove(p);
			}

			for (int i = 0; i < newPrios.size(); i++) {
				plugins.add(i, newPrios.get(i));
			}

		}

		for (final Plugin p : plugins) {
			log.debug("Gave plugin " + p + " priority number " + plugins.indexOf(p));
			firePluginListChangedEvent(p, ListChangeEvent.PRIORITY_CHANGED);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Toggles the priorities of the two plugins passed in as parameters.
	 * 
	 * @param onePlugin
	 * @param theOtherPlugin
	 * @throws RuntimeException
	 *             if one tries to toggle the priority of a plugin that is not managed by the
	 *             <code>PluginManager</code>.
	 */
	public void togglePluginPriorities(final Plugin onePlugin, final Plugin theOtherPlugin) {

		int onePos = Integer.MIN_VALUE, otherPos = Integer.MIN_VALUE;
		int i = 0;

		synchronized (plugins) {

			for (final Plugin p : plugins) {
				if (p == onePlugin) {
					onePos = i;
				} else if (p == theOtherPlugin) {
					otherPos = i;
				}
				i++;
			}

			if ((onePos == Integer.MIN_VALUE) || (otherPos == Integer.MIN_VALUE)) {
				throw new RuntimeException("One of the plugins is not yet managed.");
			}

			final Plugin tmp = plugins.get(onePos);
			plugins.set(onePos, theOtherPlugin);
			plugins.set(otherPos, tmp);

			log.debug("Toggled the priorities of the plug-ins \"" + onePlugin + "\" and \"" + theOtherPlugin + "\"");

		}
		firePluginListChangedEvent(onePlugin, ListChangeEvent.PRIORITY_CHANGED);

	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Removes a plug-in
	 */
	public boolean removePlugin(final Plugin plugin) {

		log.debug("Removing plugin " + plugin);

		// We cannot remove the active NodePositioner, if there is no other to replace it.
		if ((plugin instanceof NodePositionerPlugin) && plugin.getXMLConfig().getActive()) {
			boolean foundAnotherNP = false;
			synchronized (plugins) {
				for (final Plugin p : plugins) {
					if ((p instanceof NodePositionerPlugin) && (p != plugin)) {
						foundAnotherNP = true;
						break;
					}
				}
			}
			if (!foundAnotherNP) {
				log.debug("Cannot remove plugin, it is the only node positioner.");
				return false;
			}
		}

		synchronized (removePending) {
			removePending.add(plugin);
		}

		// first disable the plugin. if it is a NodePositioner, this would also activate another one
		// as replacement.
		plugin.setActive(false);

		synchronized (plugins) {
			plugins.remove(plugin);
		}
		synchronized (removePending) {
			removePending.remove(plugin);
		}
		log.debug("Removed plug-in: " + plugin);
		firePluginListChangedEvent(plugin, ListChangeEvent.PLUGIN_REMOVED);

		return true;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns the instances which holds information about the nodes' positions
	 * 
	 * @return the instances which holds information about the nodes' positions
	 */
	public NodePositionerPlugin getNodePositioner() {
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				if ((p instanceof NodePositionerPlugin) && p.isActive()) {
					return (NodePositionerPlugin) p;
				}
			}
		}
		throw new IllegalStateException("No active NodePositioner found!");
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
	 * @return the new instance of a plug-in
	 */
	public Plugin createNewPlugin(final Class<? extends Plugin> clazz, final PluginXMLConfig config) {
		final Plugin plugin = PluginFactory.createInstance(config, clazz);

		final String baseName = config.getName();
		String instanceName = baseName;
		if (containsPlugin(instanceName)) {
			int i = 1;
			while (containsPlugin(instanceName)) {
				i++;
				instanceName = String.format("%s (%d)", baseName, i);
			}
		}

		plugin.getXMLConfig().setName(instanceName);

		addPlugin(plugin);
		return plugin;
	}

	/**
	 * Returns true, if a plugin with the given name already exists.
	 */
	private boolean containsPlugin(final String name) {
		for (final Plugin p : getPlugins()) {
			if (p.getInstanceName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Fires an event which informs the listener about changes concerning a certain plug-in
	 * 
	 * @param p
	 *            the plug-in
	 * @param what
	 *            the reason
	 */
	private void firePluginListChangedEvent(final Plugin p, final ListChangeEvent what) {

		// TODO: This is a workaround. The real solution is to make sure the
		// eventListerList ist multithreading safe.
		final Set<PluginListChangeListener> copySet;
		synchronized (pluginListChangeListeners) {
			copySet = new HashSet<PluginListChangeListener>(pluginListChangeListeners);
		}
		for (final PluginListChangeListener listener : copySet) {
			listener.pluginListChanged(p, what);
		}
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns a list of all types of plug-ins which are currently administered by this instance
	 * 
	 * @return a list of all types of plug-ins which are currently administered by this instance
	 */
	public List<Class<? extends Plugin>> getAvailablePluginTypes() {
		return availablePluginsTypes;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all instances of a certain kind of plug-ins which are currently administered by this
	 * instance
	 * 
	 * @param clazz
	 *            the plug-in instances' class
	 * @param checkHierarchy
	 *            <code>true</code> if also Plugins that extend <code>clazz</code> should be
	 *            returned, <code>false</code> otherwise
	 */
	@SuppressWarnings("unchecked")
	public List<Plugin> getPluginInstances(final Class<? extends Plugin> clazz, final boolean checkHierarchy) {

		final List<Plugin> instances = new LinkedList<Plugin>();
		Class<? extends Plugin> currentClass;

		synchronized (plugins) {

			for (final Plugin plugin : plugins) {

				if (checkHierarchy) {

					currentClass = plugin.getClass();

					while (!currentClass.equals(Plugin.class)) {

						currentClass = (Class<? extends Plugin>) currentClass.getSuperclass();

						if (currentClass.equals(clazz)) {
							instances.add(plugin);
							break;
						}

					}

				} else {
					if (plugin.getClass().equals(clazz)) {
						instances.add(plugin);
					}
				}

			}

		}

		return instances;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns all active plug-ins which are currently visible
	 * 
	 * @return all active plug-ins which are currently visible
	 */
	public List<Plugin> getVisibleActivePlugins() {
		final List<Plugin> visibleActivePlugins = new LinkedList<Plugin>();
		synchronized (plugins) {
			for (final Plugin p : plugins) {
				if (p.isActive() && p.isVisible()) {
					visibleActivePlugins.add(p);
				}
			}
		}
		return visibleActivePlugins;
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 * @param plugin
	 */
	public void shutdownPlugin(final Plugin plugin) {
		plugin.reset();
		plugins.remove(plugin);
	}

}
