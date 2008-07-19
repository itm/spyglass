package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencesWidget.PrefType;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public abstract class BackgroundPainterPluginPreferences extends PluginPreferencesWidget {

	public BackgroundPainterPluginPreferences(Widget parent, Plugin plugin,
			ConfigStore cs, PrefType type) {
		super(parent, plugin, cs, type);
		// TODO Auto-generated constructor stub
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}