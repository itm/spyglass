package de.uniluebeck.itm.spyglass.plugin;

public interface PluginListChangeListener {
	
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
	
	public void pluginListChanged(Plugin p, ListChangeEvent what);
	
}
