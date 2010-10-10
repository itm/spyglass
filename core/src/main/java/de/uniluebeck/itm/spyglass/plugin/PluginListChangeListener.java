/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.EventListener;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;

//--------------------------------------------------------------------------------
/**
 * Implementations of this interface listen to changes concerning a list of {@link DrawingObject}s
 * 
 */
public interface PluginListChangeListener extends EventListener {

	// --------------------------------------------------------------------------------
	/**
	 * Types of change events to be fired
	 */
	public enum ListChangeEvent {
		/**
		 * The priority of the plugin has changed.
		 */
		PRIORITY_CHANGED,

		/**
		 * A new plugin has been added.
		 */
		NEW_PLUGIN,

		/**
		 * A plugin has been removed.
		 */
		PLUGIN_REMOVED,

		/**
		 * The active node positioner has changed
		 */
		NEW_NODE_POSITIONER
	}

	// --------------------------------------------------------------------------------
	/**
	 * Called when the list of plugins changed
	 * 
	 * @param p
	 *            a plugin which state has changed in the watched list
	 * @param what
	 *            the type of change event
	 */
	public void pluginListChanged(Plugin p, ListChangeEvent what);

}
