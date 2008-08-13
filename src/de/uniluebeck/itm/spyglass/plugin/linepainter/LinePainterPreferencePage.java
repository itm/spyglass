package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class LinePainterPreferencePage extends PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> {
	
	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}
	
	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final LinePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createContents(parent); // TODO: plugin-specific options
		
	}
	
	@Override
	public LinePainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final LinePainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}