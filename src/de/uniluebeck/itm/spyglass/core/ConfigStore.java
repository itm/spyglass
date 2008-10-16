/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// ------------------------------------------------------------------------------
// --
/**
 * Instances of this class are used to load and store the spyglass configuration from and into the
 * local file system, respectively.<br>
 * Additionally, configuration parameters can be loaded and stored partially.
 * 
 * @author Sebastian Ebers
 * 
 */
public class ConfigStore {
	
	/** The path to the file where the default configuration is located */
	private static final String DEFAULT_CONFIG_FILE_STANDALONE = "config/DefaultSpyglassConfigStandalone.xml";
	
	private static final String DEFAULT_CONFIG_FILE_ISHELL_PLUGIN = "config/DefaultSpyglassConfigIShellPluin.xml";
	
	private String configFilePath = "config/DefaultSpyglassConfigStandalone.xml";
	
	/** An instance of a SpyGlass configuration object */
	private SpyglassConfiguration spyglassConfig;
	
	/** An object which is used for logging errors and other events */
	private static Logger log = SpyglassLogger.get(ConfigStore.class);
	
	private volatile Boolean isStoringInvoked = false;
	
	// private final boolean isIShellPlugin;
	
	@Override
	public void finalize() throws Throwable {
		spyglassConfig = null;
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the plug-in list
	 */
	private final PluginListChangeListener pluginManagerListener = new PluginListChangeListener() {
		
		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			switch (what) {
				case NEW_PLUGIN:
					p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
					store();
					break;
				case PLUGIN_REMOVED:
					p.getXMLConfig().removePropertyChangeListener(pluginPropertyListener);
					store();
					break;
				default:
					store();
			}
			
		}
		
	};
	
	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the configuration of an plug-in.<br>
	 */
	PropertyChangeListener pluginPropertyListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			store();
		}
	};
	
	// --------------------------------------------------------------------------
	/**
	 * Reads the configuration from an hard-coded standard-path (which is stored internally in this
	 * class)
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell plug-in
	 */
	public ConfigStore(final boolean isIShellPlugin) {
		this(isIShellPlugin, new File((isIShellPlugin) ? DEFAULT_CONFIG_FILE_ISHELL_PLUGIN
				: DEFAULT_CONFIG_FILE_STANDALONE));
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Reads the configuration from an hard-coded standard-path (which is stored internally in this
	 * class)
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell plug-in
	 * @param spyglassConfig
	 *            the configuration parameters
	 */
	public ConfigStore(final boolean isIShellPlugin, final SpyglassConfiguration spyglassConfig) {
		// this.isIShellPlugin = isIShellPlugin;
		this.configFilePath = (isIShellPlugin) ? DEFAULT_CONFIG_FILE_ISHELL_PLUGIN
				: DEFAULT_CONFIG_FILE_STANDALONE;
		this.spyglassConfig = spyglassConfig;
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Creates a new configStore for the given file.
	 * 
	 * @param isIShellPlugin
	 *            indicates whether or not the application is used as iShell plug-in
	 * @param configFile
	 *            the file which contains the configuration parameters
	 */
	public ConfigStore(final boolean isIShellPlugin, final File configFile) {
		// this.isIShellPlugin = isIShellPlugin;
		this.configFilePath = configFile.getPath();
		load(configFile);
	}
	
	// --------------------------------------------------------------------------
	/**
	 * @return the spyglassConfig
	 */
	public SpyglassConfiguration getSpyglassConfig() {
		return spyglassConfig;
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Returns the default configuration parameters of a plug-in
	 * 
	 * @param clazz
	 *            the plug-in's class
	 * @return the default configuration parameters of a plug-in
	 */
	public PluginXMLConfig readPluginTypeDefaults(final Class<? extends Plugin> clazz) {
		
		final Collection<Plugin> plugins = spyglassConfig.getDefaults();
		for (final Plugin plugin : plugins) {
			if (plugin.getClass().equals(clazz)) {
				return plugin.getXMLConfig();
			}
		}
		
		return null;
		
	}
	
	// --------------------------------------------------------------------------
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
			
			spyglassConfig.getPluginManager().addPluginListChangeListener(pluginManagerListener);
			spyglassConfig.getGeneralSettings().addPropertyChangeListener(pluginPropertyListener);
			for (final Plugin p : spyglassConfig.getDefaults()) {
				p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
			}
			for (final Plugin p : spyglassConfig.getPluginManager().getPlugins()) {
				p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
			}
			
			// TODO: for Milestone2: register for events from the PacketReader
			
			return true;
		}
		return false;
	}
	
	// --------------------------------------------------------------------------
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
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"Unable to load the SpyGlass configuration", "The configuration file '"
							+ configFile.getName() + "' is invalid!\r\n" + e.getLocalizedMessage());
			log.error("Unable to load configuration input: " + e, e);
			return null;
		}
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Stores the configuration persistently into the local file system using an extra
	 * {@link Thread}
	 */
	public void store() {
		store(false);
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Stores the configuration persistently into the local file system.
	 * 
	 * @param blocking
	 *            if <code>false</code> an extra {@link Thread} will be used
	 */
	public void store(final boolean blocking) {
		if (blocking) {
			store(new File(configFilePath));
		} else {
			storeInExtraThread(new File(configFilePath));
		}
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Stores the configuration persistently into the local file system.
	 * 
	 * The given configFile will not be used for subsequent updates. This method is therefore more
	 * suited for "Exports" of the configuration.
	 * 
	 * 
	 * @param configFile
	 *            the file which will contain the configuration data afterwards
	 * @return <code>true</code> if the configuration was stored successfully
	 */
	public synchronized boolean store(final File configFile) {
		
		log.debug("Initializing. Storing config to file: " + configFile);
		
		if (!configFile.isFile()) {
			throw new RuntimeException("Can't find config file '" + configFile + "'");
		}
		final SpyglassConfiguration backup = loadSpyglassConfig(configFile);
		try {
			purgeDefaults();
			new Persister().write(spyglassConfig, configFile);
			log.debug("Storing config to file: " + configFile + " completed");
			return true;
		} catch (final Exception e) {
			log.error("Unable to store configuration output: " + e, e);
			if (backup != null) {
				try {
					new Persister().write(backup, configFile);
					log.debug("The old content of the configuration file " + configFile
							+ " was recovered");
				} catch (final Exception e1) {
					log.error("Unable to store configuration output! The file" + configFile
							+ " may be corrupted!", e1);
				}
			}
			return false;
		}
	}
	
	/**
	 * Stores the configuration in an extra {@link Thread} to improve the performance.<br>
	 * The storing operation will be delayed for one second. Every call received within this second
	 * will be ignored.
	 */
	private void storeInExtraThread(final File configFile) {
		
		// check if there is already a storing operation in progress
		synchronized (isStoringInvoked) {
			// if so, return
			if (isStoringInvoked) {
				return;
			}
			// otherwise proceed
			isStoringInvoked = true;
		}
		
		// create the thread to store
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					// sleep for one second to wait for other calls which have not to be
					// processed for that reason
					sleep(1000);
				} catch (final InterruptedException e) {
					log.error(e, e);
				} finally {
					// the next successive call will no longer be ignored
					synchronized (isStoringInvoked) {
						isStoringInvoked = false;
					}
				}
				store(configFile);
			}
		};
		
		// this Thread must not be a Daemon! Otherwise a running storing process would be
		// interrupted when the program terminates.
		// This would result in a corrupted configuration file.
		t.setDaemon(false);
		t.start();
		
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Replaces all duplicated class instances from the list of plug-ins which are configured by
	 * default.
	 */
	private void purgeDefaults() {
		// TODO Auto-generated method stub
	}
}