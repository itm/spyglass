package de.uniluebeck.itm.spyglass.gui.configuration;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * Used for the historical plugins.
 * 
 * @author Dariush Forouher
 * 
 */
public class AbstractPluginTypePreferencePage extends PluginPreferencePage<Plugin, PluginXMLConfig> {
	
	public AbstractPluginTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass, final String pluginName) {
		super(dialog, spyglass, BasicOptions.ALL);
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return Plugin.class;
	}
	
}