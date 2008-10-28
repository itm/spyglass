package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class MapPainterPreferencePage extends
		PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> {
	
	private MapPainterPrefComposite optionsComposite;
	
	public MapPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}
	
	public MapPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final MapPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		
		final Composite composite = super.createContents(parent);
		
		optionsComposite = new MapPainterPrefComposite(composite, SWT.NONE);
		
		optionsComposite.setDatabinding(dbc, config, spyglass, this);
		
		return composite;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return MapPainterPlugin.class;
	}
	
}