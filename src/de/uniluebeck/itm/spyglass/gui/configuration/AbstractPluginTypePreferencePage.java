package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * Used for the abstract plugin types in the inheritance hierarchy.
 */
public class AbstractPluginTypePreferencePage extends PluginPreferencePage<Plugin, PluginXMLConfig> {
	
	private String pluginName;
	
	public AbstractPluginTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass, final String pluginName) {
		super(dialog, spyglass, BasicOptions.ALL);
		this.pluginName = pluginName;
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		final Label test = new Label(parent, SWT.NONE);
		test.setText("The plugin type \"" + pluginName + "\" is not instantiable.\n"
				+ "Please select an instantiable plugin type from the left.");
		return parent;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return Plugin.class;
	}
	
}