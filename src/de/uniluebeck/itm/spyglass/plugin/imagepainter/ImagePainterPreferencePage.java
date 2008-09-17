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
		return super.createMS2Warning(parent); // TODO: plugin-specific options
		/*
		 * String msg = "ImagePainterPlugin Preference Page\n"; msg += (type == PrefType.INSTANCE ?
		 * "Instance Name: " + plugin.getInstanceName() + "\n" + "IsActive: " + plugin.isActive() +
		 * "\n" + "IsVisible: " + plugin.isVisible() : ""); final Label label = new Label(parent,
		 * SWT.NONE); label.setText(msg); return null;
		 */
	}
	
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return ImagePainterPlugin.class;
	}
	
}