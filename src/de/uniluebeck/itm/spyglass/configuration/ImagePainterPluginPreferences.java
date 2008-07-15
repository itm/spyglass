package de.uniluebeck.itm.spyglass.configuration;

import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.configuration.PluginPreferencesWidget.PrefType;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class ImagePainterPluginPreferences extends BackgroundPainterPluginPreferences {

	public ImagePainterPluginPreferences(Widget parent, Plugin plugin,
			ConfigStore cs, PrefType type) {
		super(parent, plugin, cs, type);
		// TODO Auto-generated constructor stub
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}