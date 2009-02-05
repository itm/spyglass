package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class LinePainterPreferencePage extends PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> {

	private LinePainterOptionsComposite optionsComposite;

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final LinePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected void loadFromModel() {
		super.loadFromModel();
		optionsComposite.stringFormatter.connectTableWithData(dbc, config.getStringFormatters());
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);

		optionsComposite = new LinePainterOptionsComposite(composite);
		optionsComposite.setDatabinding(dbc, config, this);
		optionsComposite.stringFormatter.connectTableWithData(dbc, config.getStringFormatters());

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return LinePainterPlugin.class;
	}

}