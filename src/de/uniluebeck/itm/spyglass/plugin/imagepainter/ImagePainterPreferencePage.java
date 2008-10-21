package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class ImagePainterPreferencePage extends
		PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> {
	
	public ImagePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	public ImagePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final ImagePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}
	
	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		final ImagePainterOptionsComposite optionsComposite = new ImagePainterOptionsComposite(composite);
		
		optionsComposite.setDatabinding(dbc, config);
		
		return composite;
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return ImagePainterPlugin.class;
	}
	
}