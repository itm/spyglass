package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class LinePainterPreferencePage extends PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> {

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final LinePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		final LinePainterOptionsComposite optionsComposite = new LinePainterOptionsComposite(composite);

		optionsComposite.setDatabinding(dbc, config, this);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return LinePainterPlugin.class;
	}

}