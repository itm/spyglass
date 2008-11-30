/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// ------------------------------------------------------------------------------
// --
/**
 * Instances of this class create spyglass plug-ins.<br>
 * When creating those plug-ins they initialize them with their default values unless their
 * configuration parameters are provided by the caller.
 * 
 * @author Sebastian Ebers
 * 
 */
public class PluginFactory {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(PluginFactory.class);
	
	private final ConfigStore configStore;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Constructor
	 * 
	 * @param configStore
	 *            the instance currently used to load and store the configuration parameters
	 */
	public PluginFactory(final ConfigStore configStore) {
		this.configStore = configStore;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Crates a plug-in instance of a certain class type and initializes the instances parameters
	 * using the provided ones
	 * 
	 * @param pluginManager
	 *            the plug-in managing facility
	 * @param config
	 *            the configuration parameters
	 * @param clazz
	 *            the plug-in's class
	 */
	public static Plugin createInstance(final PluginXMLConfig config,
			final Class<? extends Plugin> clazz) {
		
		try {
			final Plugin plugin = clazz.newInstance();
			plugin.getXMLConfig().overwriteWith(config);
			return plugin;
		} catch (final InstantiationException e) {
			log.error("The plug-in of class " + clazz + " could not be instantiated", e);
			return null;
		} catch (final IllegalAccessException e) {
			log.error("The plug-in of class " + clazz + " could not be instantiated", e);
			return null;
		}
		
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Crates a plug-in instance of a certain class type and initializes the instances parameters
	 * with the default config from the configuration file.
	 * 
	 * @param clazz
	 *            the plug-in's class
	 */
	public Plugin createInstance(final Class<? extends Plugin> clazz) {
		
		try {
			final Plugin plugin = clazz.newInstance();
			plugin.getXMLConfig().overwriteWith(configStore.getSpyglassConfig().getDefaultConfig(clazz));
			return plugin;
		} catch (final InstantiationException e) {
			log.error("The plug-in of class " + clazz + " could not be instantiated", e);
			return null;
		} catch (final IllegalAccessException e) {
			log.error("The plug-in of class " + clazz + " could not be instantiated", e);
			return null;
		}
		
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Crates a plug-in instance of a certain class type. Its config is left untouched.
	 * 
	 * @param clazz
	 *            the plug-in's class
	 */
	public static Plugin createDefaultInstance(final Class<? extends Plugin> clazz) {
		
		try {
			final Plugin plugin = clazz.newInstance();
			return plugin;
		} catch (final InstantiationException e) {
			log.error("The plug-in of class " + clazz + " could not be instantiated", e);
			return null;
		} catch (final IllegalAccessException e) {
			log.error("The plug-in of class " + clazz + " could not be instantiated", e);
			return null;
		}
		
	}

}