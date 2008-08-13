package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// TEAMQUESTION: Warum kann diese klasse nicht einfach abstract deklariert werden? -- Dariush
public class AbstractPluginTypePreferencePage extends PluginPreferencePage<Plugin, PluginXMLConfig> {
	
	private final String pluginName;
	
	public AbstractPluginTypePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final String pluginName) {
		super(dialog, spyglass, Plugin.class, BasicOptions.ALL);
		this.pluginName = pluginName;
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createContents(parent);
	}
	
	@Override
	public PluginXMLConfig getFormValues() {
		// nothing to do, since this is abstract (!)
		throw new RuntimeException("This method should never be called");
	}
	
	@Override
	public void setFormValues(final PluginXMLConfig config) {
		// nothing to do, since this is abstract (!)
	}
	
}
