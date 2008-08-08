package de.uniluebeck.itm.spyglass.plugin;

public interface PluginListChangeListener {
	
	public enum ListChangeEvent {
		PRIORITY_CHANGED, NEW_PLUGIN, PLUGIN_REMOVED, PLUGIN_STATE_CHANGED
	}
	
	public void pluginListChanged(Plugin p, ListChangeEvent what);
	
}
