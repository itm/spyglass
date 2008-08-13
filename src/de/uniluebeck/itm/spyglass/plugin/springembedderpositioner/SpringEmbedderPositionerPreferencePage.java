package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class SpringEmbedderPositionerPreferencePage extends PluginPreferencePage<SpringEmbedderPositionerPlugin, SpringEmbedderPositionerXMLConfig> {
	
	public SpringEmbedderPositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
	}
	
	public SpringEmbedderPositionerPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final SpringEmbedderPositionerPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_VISIBLE_AND_SEMANTIC_TYPES);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createContents(parent); // TODO: plugin-specific options
	}
	
	@Override
	public SpringEmbedderPositionerXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final SpringEmbedderPositionerXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
}