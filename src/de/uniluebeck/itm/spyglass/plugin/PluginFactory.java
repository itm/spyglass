package de.uniluebeck.itm.spyglass.plugin;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class PluginFactory {

	public PluginFactory(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 
	 * @param config
	 * @param clazz
	 */
	public static Plugin createInstance(PluginXMLConfig config, Class<Plugin> clazz){
		return null;
	}

}