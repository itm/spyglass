/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import ishell.util.Logging;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Category;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXmlConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are used to load and store the spyglass configuration
 * from and into the local file system, respectively.<br>
 * Additionally, configuration parameters can be loaded and stored partially.
 * 
 * @author Sebastian Ebers
 * 
 */
public class ConfigStore {
	
	/** The path to the file where the default configuration is located */
	private static final String defaultFileName = "config/DefaultSpyglassConfig.xml";
	
	/** An instance of a spyglass configuration object */
	private SpyglassConfiguration spyglassConfig;
	
	/** An object which is used for logging errors and other events */
	private static Category log = Logging.get(ConfigStore.class);
	
	@Override
	public void finalize() throws Throwable {
		spyglassConfig = null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Reads the config from an hardcoded standard-path (which is stored
	 * internally in this class)
	 */
	public ConfigStore() {
		this(new File(defaultFileName));
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Reads the config from an hardcoded standard-path (which is stored
	 * internally in this class)
	 */
	public ConfigStore(final SpyglassConfiguration spyglassConfig) {
		this.spyglassConfig = spyglassConfig;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * creates a new configStore for the given File.
	 * 
	 * @param file
	 */
	public ConfigStore(final File configFile) {
		load(configFile);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the spyglassConfig
	 */
	public SpyglassConfiguration getSpyglassConfig() {
		return spyglassConfig;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param spyglassConfig
	 *            the spyglassConfig to set
	 */
	public void setSpyglassConfig(final SpyglassConfiguration spyglassConfig) {
		this.spyglassConfig = spyglassConfig;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the general settings configuration parameters
	 * 
	 * @return the general settings configuration parameters
	 */
	public GeneralSettingsXmlConfig readGeneralSettingsConfig() {
		return spyglassConfig.getGeneralSettings();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the configuration parameters of a plug-in
	 * 
	 * @param instanceName
	 *            the plug-in's human readable name
	 */
	public PluginXMLConfig readPluginInstanceConfig(final String instanceName) {
		final List<Plugin> plugins = spyglassConfig.getPluginManager().getPlugins();
		for (final Plugin plugin : plugins) {
			if (plugin.getHumanReadableName().equals(instanceName)) {
				return plugin.getXMLConfig();
			}
		}
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the configuration parameters of a certain kind of plug-ins
	 * 
	 * @param clazz
	 *            the plug-ins' class
	 */
	public List<PluginXMLConfig> readPluginInstanceConfigs(final Class<? extends Plugin> clazz) {
		final List<Plugin> plugins = spyglassConfig.getPluginManager().getPluginInstances(clazz);
		final List<PluginXMLConfig> configs = new LinkedList<PluginXMLConfig>();
		for (final Plugin plugin : plugins) {
			configs.add(plugin.getXMLConfig());
		}
		return configs;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the default configuration parameters of a plug-in
	 * 
	 * @param clazz
	 *            the plug-in's class
	 * @return the default configuration parameters of a plug-in
	 */
	public PluginXMLConfig readPluginTypeDefaults(final Class<? extends Plugin> clazz) {
		
		final List<Plugin> plugins = spyglassConfig.getDefaults();
		for (final Plugin plugin : plugins) {
			if (plugin.getClass().equals(clazz)) {
				return plugin.getXMLConfig();
			}
		}
		return null;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Stores the general settings configuration
	 * 
	 * @param config
	 *            the configuration parameters
	 */
	public void storeGeneralSettingsConfig(final GeneralSettingsXmlConfig config) {
		spyglassConfig.setGeneralSettings(config);
		store();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Stores the provided configuration parameters of a plug-in persistently.<br>
	 * The matching plug-in is determined by comparing the provided
	 * configuration instance with the configuration instance of each plug-in
	 * currently administered by the plug-in manager.
	 * 
	 * @param config
	 *            the configuration to be stored
	 */
	public void storePluginInstanceConfig(final PluginXMLConfig config) {
		final List<Plugin> plugins = spyglassConfig.getPluginManager().getPlugins();
		for (final Plugin plugin : plugins) {
			final PluginXMLConfig cfg = plugin.getXMLConfig();
			if ((cfg != null) && config.getClass().equals(cfg.getClass())) {
				plugin.setXMLConfig(config);
				store();
				return;
			}
		}
		throw new UnsupportedOperationException("No matching plugin was found! Create a new one first");
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Stores the provided configuration as default configuration for a plug-in.<br>
	 * The matching plug-in is determined by comparing the provided
	 * configuration's class with the configuration classes of each plug-in
	 * which configuration parameters are available within the configuration
	 * file
	 * 
	 * @param config
	 *            the configuration to be stored
	 */
	public void storePluginTypeDefaults(final PluginXMLConfig config) {
		final List<Plugin> plugins = spyglassConfig.getDefaults();
		for (final Plugin plugin : plugins) {
			final PluginXMLConfig cfg = plugin.getXMLConfig();
			if ((cfg != null) && config.getClass().equals(cfg.getClass())) {
				plugin.setXMLConfig(config);
				store();
				return;
			}
		}
		throw new UnsupportedOperationException("No matching configuration was found! Create a new one first");
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Loads the configuration from a file
	 * 
	 * @param configFile
	 *            the file containing the configuration data
	 * @return <code>true</code> if the configuration was loaded successfully
	 */
	private boolean load(final File configFile) {
		final SpyglassConfiguration sgc = ConfigStore.loadSpyglassConfig(configFile);
		if (sgc != null) {
			spyglassConfig = sgc;
			return true;
		}
		return false;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Loads and returns the configuration from a file
	 * 
	 * @param configFile
	 *            the file containing the configuration data
	 * @return <code>true</code> if the configuration was loaded successfully
	 */
	private static SpyglassConfiguration loadSpyglassConfig(final File configFile) {
		log.debug("Initializing. Reading config from file: " + configFile);
		SpyglassConfiguration config = null;
		
		if (!configFile.isFile()) {
			throw new RuntimeException("Can't find config file '" + configFile + "'");
		}
		
		try {
			final Serializer serializer = new Persister();
			config = serializer.read(SpyglassConfiguration.class, configFile);
			
			if (config == null) {
				throw new RuntimeException("Can't load configuration.");
			}
			return config;
			
		} catch (final Exception e) {
			log.error("Unable to load configuration input: " + e, e);
			return null;
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Stores the configuration persistently into the local file system.<br>
	 * Since no file is provided the file {@link ConfigStore#defaultFileName} is
	 * used.
	 * 
	 * @return <code>true</code> if the configuration was stored successfully
	 */
	public boolean store() {
		return store(new File(defaultFileName));
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Stores the configuration persistently into the local file system.
	 * 
	 * @param configFile
	 *            the file which will contain the configuration data afterwards
	 * @return <code>true</code> if the configuration was stored successfully
	 */
	public boolean store(final File configFile) {
		
		log.debug("Initializing. Storing config to file: " + configFile);
		
		if (!configFile.isFile()) {
			throw new RuntimeException("Can't find config file '" + configFile + "'");
		}
		final SpyglassConfiguration backup = loadSpyglassConfig(configFile);
		try {
			new Persister().write(spyglassConfig, configFile);
			return true;
		} catch (final Exception e) {
			log.error("Unable to store configuration output: " + e, e);
			if (backup != null) {
				try {
					new Persister().write(backup, configFile);
					log.debug("The old content of the configuration file " + configFile + " was recovered");
				} catch (final Exception e1) {
					log.error("Unable to store configuration output! The file" + configFile + " may be corrupted!", e1);
				}
			}
			return false;
		}
	}
}