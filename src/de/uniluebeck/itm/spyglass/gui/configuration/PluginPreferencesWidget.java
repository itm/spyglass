package de.uniluebeck.itm.spyglass.gui.configuration;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PreferencesWidget;

public abstract class PluginPreferencesWidget<T extends Plugin> extends PreferencesWidget {


	public enum PrefType {

	}

	private Widget parent;
	private T plugin;
	/**
	 * Enumeration. Decides, if the surrounding PluginPreferencesWidget represents
	 * an Type or an Instance.
	 */
	private PrefType type;


	/**
	 * 
	 * @param parent
	 * @param plugin
	 * @param cs
	 * @param type
	 */
	public PluginPreferencesWidget(Widget parent, T plugin, ConfigStore cs, PrefType type){
		super(parent, 0); // TODO 
	}
	
	public void finalize() throws Throwable {
		super.finalize();
	}

	public void draw(){

	}

	public PluginXMLConfig getCurrentPluginConfig(){
		return null;
	}

	public T getPluginClass(){
		return null;
	}


}