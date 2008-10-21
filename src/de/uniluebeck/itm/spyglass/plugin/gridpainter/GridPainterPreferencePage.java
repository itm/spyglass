package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class GridPainterPreferencePage extends
		PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> {
	
	public GridPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	public GridPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final GridPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		final GridPainterOptionsComposite optionsComposite = new GridPainterOptionsComposite(
				composite);
		
		optionsComposite.setDatabinding(dbc, config, this);
		
		return composite;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return GridPainterPlugin.class;
	}
	
}