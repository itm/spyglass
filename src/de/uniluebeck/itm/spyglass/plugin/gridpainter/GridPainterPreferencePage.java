package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;

public class GridPainterPreferencePage extends PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> {
	
	public GridPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, GridPainterPlugin.class, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	public GridPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final GridPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createContents(parent); // TODO: plugin-specific options
	}
	
	@Override
	public GridPainterXMLConfig getFormValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setFormValues(final GridPainterXMLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
}