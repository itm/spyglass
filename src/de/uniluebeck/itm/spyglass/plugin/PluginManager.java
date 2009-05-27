/*
 * ---------------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007
 * by the SwarmNet (www.swarmnet.de) project SpyGlass is free software; you can redistribute
 * it and/or modify it under the terms of the BSD License. Refer to spyglass-licence.txt
 * file in the root of the SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Validate;

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
/**
 * The PluginManager holds all loaded plug-ins and is basically a wrapper for an internal list of
 * plug-ins.
 */
@Root
public class PluginManager {

	private static Logger log = SpyglassLoggerFactory.getLogger(PluginManager.class);

	// --------------------------------------------------------------------------
	/**
	 * The state of the plug-in manager
	 */
	public enum State {

		/**
		 * The plug-in manager has not been initialized yet.
		 */
		INFANT,

		/**
		 * The plug-in manager has been initialized and is running.
		 */
		ALIVE,

		/**
		 * The plug-in manager has been shut down.
		 */
		ZOMBIE
	}

	// --------------------------------------------------------------------------
	/**
	 * State of the manager
	 */
	private State state = State.INFANT;

	// --------------------------------------------------------------------------
	/**
	 * This List is serialized by the XMLSerializer. It must not be used by any code, since access
	 * to it is not synchronized! Use the variable <code>plugins</code> instead, which has a
	 * Collections.synchronizedList() wrapper around it.
	 */
	@ElementList(name = "plugins")
	private final List<Plugin> pluginsInternal = new ArrayList<Plugin>();

	/**
	 * The list of plug-ins. Has always to be a wrapper around pluginsInternal.
	 */
	private List<Plugin> plugins = Collections.synchronizedList(pluginsInternal);

	// --------------------------------------------------------------------------
	/**
	 * Registered listerers (of different types)
	 */
	private final EventListenerList listeners = new EventListenerList();

	// --------------------------------------------------------------------------
	/**
	 * List of plug-ins that are currently being removed
	 * 
	 * (this is currently only used to make sure, that such a plug-in wont be elected as new active
	 * node positioner)
	 */
	private final List<Plugin> removePending = new ArrayList<Plugin>();

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
		// availablePluginsTypes.add(TemplatePlugin.class);
	}

	// --------------------------------------------------------------------------
	/**
	 * Constructor for SimpleXML
	 */
	public PluginManager() {

		// Create default node positioner (we must have one at all times!)
		try {
			// since the state is still INFANT, it won't get connected.
			createNewPlugin(PositionPacketNodePositionerPlugin.class, null);
		} catch (final Exception e) {
			throw new RuntimeException("Could not create default node positioner plug-in. Smells like a bug...");
		}

	}

	// --------------------------------------------------------------------------
	/**
	 * This method is called after the deserialization of this instance has finished.
	 * 
	 * First it wraps the deserialized plug-in list into a synchrozied collection. Then it
	 * initializes the pluginManager.
	 */
	@Commit
	protected void commit() {
		this.plugins = Collections.synchronizedList(pluginsInternal);
	}

	// --------------------------------------------------------------------------
	/**
	 * This method is called after the deserialization of this instance has finished.
	 * 
	 * - checks for duplicate plug-in names - checks for the NodePositioner
	 */
	@Validate
	protected void validate() {
		final HashSet<String> pluginNames = new HashSet<String>();

		boolean foundActiveNodePos = false;

		for (final Plugin p : pluginsInternal) {
			final String name = p.getInstanceName();
			if (pluginNames.contains(name)) {
				throw new IllegalStateException("Found two plugins with the name " + name + ". Cannot continue.");
			}
			if ((p instanceof NodePositionerPlugin) && p.isActive()) {
				if (foundActiveNodePos) {
					throw new IllegalStateException("There must always only be one NodePositioner active.");
				}
				foundActiveNodePos = true;
			}
			pluginNames.add(name);
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Initializes the plug-in manager.
	 * 
	 * Connects and initializes all existing plug-ins and sets the state to ALIVE.
	 */
	public void init() {
		if (state != State.INFANT) {
			throw new RuntimeException("Can only initialize a new PluginManager!");
		}

		state = State.ALIVE;

		// connect the list of existing plugins
		synchronized (plugins) {
			final Iterator<Plugin> it = plugins.iterator();
			while (it.hasNext()) {
				final Plugin p = it.next();
				try {
					connectPlugin(p);
				} catch (final Exception e) {
					log.error("Could not connect plug-in " + p + " with the plug-in manager. Dropping the plug-in.", e);
					it.remove();
				}
			}
		}

		log.info("PluginManager initialized.");
	}

	/**
	 * Shuts the plug-in manager and all containing plug-ins down.
	 */
	public void shutdown() {
		if (state != State.ALIVE) {
			throw new RuntimeException("Can shutdown a running PluginManager only!");
		}

		state = State.ZOMBIE;

		for (final Plugin p : plugins) {
			try {
				p.shutdown();
			} catch (final Exception e) {
				log.warn("The plug-in " + p + " could not be shut down properly. Continuing anyway.", e);
			}
		}

		log.info("PluginManager shut down.");
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns all plug-ins which are currently administered by this instance and which are marked
	 * as 'active'
	 * 
	 * @return all plug-ins which are currently administered by this instance and which are marked
	 *         as 'active'
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
	 * listener will be informed of changes when plug-in instances are added or removed.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addPluginListChangeListener(final PluginListChangeListener listener) {
		listeners.add(PluginListChangeListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a <code>PluginListChangeListener</code> instances to the list of listeners. Every
	 * listener will be informed of changes when plug-in-instances are added or removed.
	 * 
	 * @param listeners
	 *            the listeners to add
	 */
	public void addPluginListChangeListeners(final List<PluginListChangeListener> listeners) {
		for (final PluginListChangeListener listener : listeners) {
			this.listeners.add(PluginListChangeListener.class, listener);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a <code>PluginListChangeListener</code> instance from the list of listeners.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void removePluginListChangeListener(final PluginListChangeListener listener) {
		listeners.remove(PluginListChangeListener.class, listener);
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns a list-copy of all plug-ins which are currently administered by this instance<br>
	 * Note that the priorities of the plug-ins are decreasing.
	 * 
	 * @return a list-copy of all plug-ins which are currently administered by this instance
	 */
	public List<Plugin> getPlugins() {
		synchronized (plugins) {
			return new ArrayList<Plugin>(plugins);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a list-copy of all plug-ins which are currently administered by this instance, except
	 * of the plug-ins that are of a class (or extending a class) contained in the excludes list.<br>
	 * Note that the priorities of the plug-ins are decreasing.
	 * 
	 * @param checkHierarchy
	 *            <code>true</code> if the class hierarchy should be checked, such that even
	 *            plug-ins derived from a class included in the <code>excludes</code> list will be
	 *            excluded, <code>false</code> if only plug-ins of exactly the class contained in
	 *            <code>exclude</code> list shall be excluded
	 * @param excludes
	 *            plug-in class to exclude from the list
	 * @return a list-copy of plug-in instances in decreasing priority
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
	/**
	 * Connect the plug-in with the plug-in manager
	 * 
	 * @param plugin
	 *            the plug-in to be connected
	 * @throws Exception
	 */
	private void connectPlugin(final Plugin plugin) throws Exception {
		if (plugin instanceof NodePositionerPlugin) {

			// if there is already an active nodepositioner, don't enable the new one.
			if (plugin.isActive() && getNodePositioner().isActive() && (getNodePositioner() != plugin)) {
				plugin.setActive(false);
			}

			// If the plug-in is a NodePositioner, we have to act when the plug-in is (de)activated
			// (to en/disable any existing node positioner)
			plugin.getXMLConfig().addPropertyChangeListener(PluginXMLConfig.PROPERTYNAME_ACTIVE, new PropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					final Boolean activeOld = (Boolean) evt.getOldValue();
					final Boolean activeNew = (Boolean) evt.getNewValue();
					if (activeNew && !activeOld) {
						// the plug-in got activated

						newNodePositioner(plugin);

						firePluginListChangedEvent(plugin, ListChangeEvent.NEW_NODE_POSITIONER);

					} else if (activeOld && !activeNew) {
						// the plug-in got deactivated

						findNewNodePositioner();
					}

				}

			});
		}

		plugin.init(this);

	}

	// --------------------------------------------------------------------------
	/**
	 * Adds a plug-in if it is not contained yet. The plug-in is put at the end of the list which
	 * means that the new plug-in has the lowest priority
	 * 
	 * If we're not ALIVE yet, then just add the plug-in to the list. It will be connected later by
	 * {@link #init()}.
	 * 
	 * @param plugin
	 *            The plug-in object to be added.
	 * @throws Exception
	 *             when the plug-in could not be added.
	 */
	private void addPlugin(final Plugin plugin) throws Exception {

		if (state == State.ALIVE) {
			this.connectPlugin(plugin);
		}

		if (plugins.contains(plugin)) {
			throw new RuntimeException("Plug-in already there!");
		}

		plugins.add(plugin);
		log.debug("Added plug-in: " + plugin);

		if (state == State.ALIVE) {
			// Important: Only fire the event *after* it has been completely initialized
			// (the UIController and probably other Listeners depend on this assumption)
			firePluginListChangedEvent(plugin, ListChangeEvent.NEW_PLUGIN);
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Enable another NodePositioner, if the active one got disabled or deleted.
	 */
	protected void findNewNodePositioner() {
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

	// --------------------------------------------------------------------------
	/**
	 * Disable all NodePositioner plug-ins except the given one.
	 * 
	 * @param plugin
	 *            the new {@link NodePositionerPlugin}
	 */
	protected void newNodePositioner(final Plugin plugin) {
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
	/**
	 * Puts a plug-in at the top of the list which means that its priority is the highest one<br>
	 * The others plug-ins' priorities are decreased by one which means that their order stays
	 * intact.
	 * 
	 * @param plugin
	 *            The plug-in object whose priority is to be increased.
	 * @throws RuntimeException
	 *             if one tries to increase the priority of a plug-in that is not managed by the
	 *             <code>PluginManager</code>.
	 */
	public void increasePluginPriorityToTop(final Plugin plugin) {

		if (!plugins.remove(plugin)) {
			throw new RuntimeException("The plug-in was not yet managed.");
		}
		plugins.add(plugin);
		log.debug("The plug-in: " + plugin + " is now the one with the highest priority");
		firePluginListChangedEvent(plugin, ListChangeEvent.PRIORITY_CHANGED);
	}

	// --------------------------------------------------------------------------
	/**
	 * Decreases the priorities of a list of plug-ins by one each.<br>
	 * If two plug-ins draw objects at the same spot on the drawing area, the one with the higher
	 * priority will draw the object on top of the other. The same holds for events as the object
	 * with the higher priority will receive the event previously to one with a lower priority.
	 * 
	 * @param list
	 *            the list of plug-ins which priorities are to be decreased
	 * 
	 */
	public void decreasePluginPriorities(final List<Plugin> list) {

		final HashMap<Plugin, Integer> newPrios = new HashMap<Plugin, Integer>();

		synchronized (this.plugins) {

			int currentIndex;

			for (final Plugin p : list) {
				currentIndex = plugins.indexOf(p);
				if (currentIndex == -1) {
					throw new RuntimeException("The plug-in was not yet managed.");
				}
				newPrios.put(p, currentIndex == 0 ? 0 : currentIndex - 1);
			}

			for (final Plugin p : list) {
				plugins.remove(p);
				plugins.add(newPrios.get(p), p);
			}

		}

		for (final Plugin p : plugins) {
			log.debug("Gave plug-in " + p + " priority number " + plugins.indexOf(p));
			firePluginListChangedEvent(p, ListChangeEvent.PRIORITY_CHANGED);
		}

	}

	// --------------------------------------------------------------------------
	/**
	 * Increases the priorities of a list of plug-ins by one each.<br>
	 * If two plug-ins draw objects at the same spot on the drawing area, the one with the higher
	 * priority will draw the object on top of the other. The same holds for events as the object
	 * with the higher priority will receive the event previously to one with a lower priority.
	 * 
	 * @param list
	 *            the list of plug-ins which priorities are to be increased
	 * 
	 */
	public void increasePluginPriorities(final List<Plugin> list) {

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
			log.debug("Gave plug-in " + p + " priority number " + plugins.indexOf(p));
			firePluginListChangedEvent(p, ListChangeEvent.PRIORITY_CHANGED);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Toggles the priorities of the two plug-ins passed in as parameters.
	 * 
	 * @param onePlugin
	 * @param theOtherPlugin
	 * @throws RuntimeException
	 *             if one tries to toggle the priority of a plug-in that is not managed by the
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
	/**
	 * Removes a plug-in instance
	 * 
	 * @param plugin
	 *            the plug-in instance to be removed
	 * @return whether the plug-in was successfully removed
	 */
	public boolean removePlugin(final Plugin plugin) {

		log.debug("Removing plug-in " + plugin);

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
				log.debug("Cannot remove plug-in, it is the only node positioner.");
				return false;
			}
		}

		synchronized (removePending) {
			removePending.add(plugin);
		}

		// first disable the plug-in. if it is a NodePositioner, this would also
		// activate another one as replacement.
		plugin.setActive(false);

		try {
			plugin.shutdown();
		} catch (final Exception e) {
			log.warn("The plug-in could not be shut down properly. Continuing anyway.", e);
		}

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
	/**
	 * Returns the instance which holds information about the nodes' positions
	 * 
	 * @return the instance which holds information about the nodes' positions
	 */
	public NodePositionerPlugin getNodePositioner() {

		synchronized (plugins) {

			// first try to find the active nodepositioner
			for (final Plugin p : plugins) {
				if ((p instanceof NodePositionerPlugin) && p.isActive()) {
					return (NodePositionerPlugin) p;
				}
			}

			// if there none, then we're currently in the process of switching from one
			// to another. In this case just return an inactive one.
			// (as soon as the new one arrives, we will be back in business again)
			for (final Plugin p : plugins) {
				if ((p instanceof NodePositionerPlugin)) {
					return (NodePositionerPlugin) p;
				}
			}
		}

		throw new IllegalStateException("No NodePositioner found!");
	}

	// --------------------------------------------------------------------------
	/**
	 * Add a new Listener for changes in the position of nodes. Whenever a node moves around, an
	 * event is fired. Note that the listener will be added no matter if it already exists in the
	 * list.
	 * 
	 * @param listener
	 *            a listener to be added to the already existing ones
	 */
	public void addNodePositionListener(final NodePositionListener listener) {
		listeners.add(NodePositionListener.class, listener);
	}

	// --------------------------------------------------------------------------
	/**
	 * Remove the given listener.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeNodePositionListener(final NodePositionListener listener) {
		listeners.remove(NodePositionListener.class, listener);
	}

	// --------------------------------------------------------------------------
	/**
	 * Fires a NodePositionEvent.
	 * 
	 * Note: This method should only be used by NodePositioners. It is declared public so that it
	 * can be used across packages, but ordinary plug-ins must never call it!
	 * 
	 * @param event
	 *            the NodePositionEvent to be fired
	 * 
	 */
	public void fireNodePositionEvent(final NodePositionEvent event) {
		// Get listeners
		final EventListener[] list = listeners.getListeners(NodePositionListener.class);

		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			((NodePositionListener) list[i]).handleEvent(event);
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Creates a new instance of a plug-in and entails it in the list
	 * 
	 * @param clazz
	 *            the plug-in's class
	 * @param config
	 *            the plug-in's configuration parameters
	 * @return the new instance of a plug-in
	 * @throws Exception
	 *             when the plug-in could not be created.
	 */
	public Plugin createNewPlugin(final Class<? extends Plugin> clazz, final PluginXMLConfig config) throws Exception {
		final Plugin plugin;
		if (config != null) {
			plugin = PluginFactory.createInstance(config, clazz);
		} else {
			plugin = PluginFactory.createDefaultInstance(clazz);
		}

		final String baseName = plugin.getInstanceName();
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

	// --------------------------------------------------------------------------
	/**
	 * Returns true, if a plug-in with the given name already exists.
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
	/**
	 * Fires an event which informs the listener about changes concerning a certain plug-in
	 * 
	 * @param p
	 *            the plug-in
	 * @param what
	 *            the reason
	 */
	protected void firePluginListChangedEvent(final Plugin p, final ListChangeEvent what) {

		final PluginListChangeListener[] list = this.listeners.getListeners(PluginListChangeListener.class);

		for (final PluginListChangeListener listener : list) {
			listener.pluginListChanged(p, what);
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns a list of all types of plug-ins which are currently administered by this instance
	 * 
	 * @return a list of all types of plug-ins which are currently administered by this instance
	 */
	public static List<Class<? extends Plugin>> getAvailablePluginTypes() {
		return availablePluginsTypes;
	}

	// --------------------------------------------------------------------------
	/**
	 * Returns all instances of a certain kind of plug-ins which are currently administered by this
	 * instance
	 * 
	 * @param clazz
	 *            the plug-in instances' class
	 * @param checkHierarchy
	 *            <code>true</code> if also plug-ins that extend <code>clazz</code> should be
	 *            returned, <code>false</code> otherwise
	 * @return a list of plug-ins which are currently administered by this instance
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
	/**
	 * Returns all active plug-ins in decreasing priority which are currently visible
	 * 
	 * @return all active plug-ins in decreasing priority which are currently visible
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

}
