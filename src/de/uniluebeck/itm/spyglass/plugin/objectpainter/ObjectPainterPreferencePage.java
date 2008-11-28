package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class ObjectPainterPreferencePage extends PluginPreferencePage<ObjectPainterPlugin, ObjectPainterXMLConfig> {

	public ObjectPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public ObjectPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final ObjectPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = super.createContents(parent);

		final ObjectPainterOptionsComposite optionsComposite = new ObjectPainterOptionsComposite(composite);

		optionsComposite.setDatabinding(dbc, config, spyglass, this);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return ObjectPainterPlugin.class;
	}

}