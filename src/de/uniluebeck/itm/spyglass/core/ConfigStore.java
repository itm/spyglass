package de.uniluebeck.itm.spyglass.core;

import java.io.File;
import java.util.List;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXmlConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ConfigStore {



	public void finalize() throws Throwable {

	}

	/**
	 * Reads the config from an hardcoded standard-path (which is stored internally in
	 * this class)
	 */
	public void ConfigStore(){

	}

	/**
	 * creates a new configStore for the given File.
	 * 
	 * @param file
	 */
	public void ConfigStore(File file){

	}

	public GeneralSettingsXmlConfig readGeneralSettingsConfig(){
		return null;
	}

	/**
	 * 
	 * @param instanceName
	 */
	public PluginXMLConfig readPluginInstanceConfig(String instanceName){
		return null;
	}

	/**
	 * 
	 * @param clazz
	 */
	public List<PluginXMLConfig> readPluginInstanceConfigs(Class<Plugin> clazz){
		return null;
	}

	/**
	 * 
	 * @param clazz
	 */
	public PluginXMLConfig readPluginTypeDefaults(Class<Plugin> clazz){
		return null;
	}

	/**
	 * 
	 * @param config
	 */
	public void storeGeneralSettingsConfig(GeneralSettingsXmlConfig config){

	}

	/**
	 * 
	 * @param config
	 */
	public void storePluginInstanceConfig(PluginXMLConfig config){

	}

	/**
	 * 
	 * @param config
	 */
	public void storePluginTypeDefaults(PluginXMLConfig config){

	}

}