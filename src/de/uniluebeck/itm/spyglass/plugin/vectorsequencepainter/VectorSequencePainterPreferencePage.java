package de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class VectorSequencePainterPreferencePage extends PluginPreferencePage<VectorSequencePainterPlugin, VectorSequencePainterXMLConfig> {

	public VectorSequencePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public VectorSequencePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final VectorSequencePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		final VectorSequencePainterOptionsComposite optionsComposite = new VectorSequencePainterOptionsComposite(composite);

		optionsComposite.setDatabinding(dbc, config, this);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return VectorSequencePainterPlugin.class;
	}

}