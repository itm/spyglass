package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class MapPainterPreferencePage extends
		PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> {
	
	public MapPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}
	
	public MapPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final MapPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		return super.createMS2Warning(parent); // TODO: plugin-specific options
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return MapPainterPlugin.class;
	}
	
}