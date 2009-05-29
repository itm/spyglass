package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class LinePainterPreferencePage extends PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> {

	private LinePainterOptionsComposite optionsComposite;
	private HashMap<Integer, String> tempStringFormatters;

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final LinePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected void loadFromModel() {
		super.loadFromModel();
		tempStringFormatters = config.getStringFormatters();
		optionsComposite.stringFormatter.connectTableWithData(dbc, tempStringFormatters);
	}

	@Override
	protected void storeToModel() {
		super.storeToModel();
		config.setStringFormatters(tempStringFormatters);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);

		optionsComposite = new LinePainterOptionsComposite(composite);
		optionsComposite.setDatabinding(dbc, config, this);
		tempStringFormatters = config.getStringFormatters();
		optionsComposite.stringFormatter.connectTableWithData(dbc, tempStringFormatters);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return LinePainterPlugin.class;
	}

}