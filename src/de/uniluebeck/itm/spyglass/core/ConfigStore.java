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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
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
public class ConfigStore extends PropertyBean {
	
	/**
	 * Path to the configuration file to be loaded if the default one is corrupted and the
	 * application is started in stand-alone mode
	 */
	public static final String FAILSAVE_CONFIG_FILE_STANDALONE = "config/failsave/FailsaveSpyglassConfigStandalone.xml";
	
	/**
	 * Path to the configuration file to be loaded if the default one is corrupted and the
	 * application is started in plug-in mode
	 */
	public static final String FAILSAVE_CONFIG_FILE_ISHELL_PLUGIN = "config/failsave/FailsaveSpyglassConfigIShellPlugin.xml";
	
	private static final String pluginConfigDirectory = new File("./config/plugin")
			.getAbsoluteFile().toString();
	private static final String standaloneConfigDirectory = new File("./config/plugin")
			.getAbsoluteFile().toString();
	
	/** The path to the file where the default configuration is located */
	private static final String DEFAULT_CONFIG_FILE_STANDALONE = "config/DefaultSpyglassConfigStandalone.xml";
	
	private static final String DEFAULT_CONFIG_FILE_ISHELL_PLUGIN = "config/DefaultSpyglassConfigIShellPlugin.xml";
	
	private String configFilePath = "config/DefaultSpyglassConfigStandalone.xml";
	
	/** The path to the configuration file which was last loaded by the user (in this session) */
	private String userDefinedCurrentConfiguration = null;
	
	/** An instance of a SpyGlass configuration object */
	private SpyglassConfiguration spyglassConfig;
	
	/** An object which is used for logging errors and other events */
	private static Logger log = SpyglassLogger.get(ConfigStore.class);
	
	private volatile Boolean isStoringInvoked = false;
	
	private final boolean isIShellPlugin;
	
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
		this.isIShellPlugin = isIShellPlugin;
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
		this.isIShellPlugin = isIShellPlugin;
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
		
		final Collection<Plugin> plugins = spyglassConfig.getDefaultPlugins();
		for (final Plugin plugin : plugins) {
			if (plugin.getClass().equals(clazz)) {
				return plugin.getXMLConfig();
			}
		}
		
		return null;
		
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to load the configuration from the local file system.
	 * 
	 * @return <code>true</code> if the configuration was loaded successfully
	 */
	public boolean loadFromFileSystem() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.xml" });
		fd.setFilterPath(isIShellPlugin ? pluginConfigDirectory : standaloneConfigDirectory);
		final String path = fd.open();
		if (path != null) {
			final File file = new File(path);
			userDefinedCurrentConfiguration = file.toString();
			return load(file);
		}
		return false;
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
		boolean fireEvent = false;
		if (sgc != null) {
			
			if (spyglassConfig == null) {
				spyglassConfig = sgc;
			} else {
				spyglassConfig.overwriteWith(sgc);
				fireEvent = true;
			}
			
			spyglassConfig.getPluginManager().addPluginListChangeListener(pluginManagerListener);
			spyglassConfig.getGeneralSettings().addPropertyChangeListener(pluginPropertyListener);
			for (final Plugin p : spyglassConfig.getDefaultPlugins()) {
				p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
			}
			for (final Plugin p : spyglassConfig.getPluginManager().getPlugins()) {
				p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
			}
			
			if (fireEvent) {
				firePropertyChange("replaceConfiguration", null, spyglassConfig);
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
			log.error("Unable to load configuration input: " + e, e);
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"Unable to load the SpyGlass configuration", "The configuration file '"
							+ configFile.getName() + "' is invalid!\r\n" + e.getLocalizedMessage());
			return null;
		}
	}
	
	// --------------------------------------------------------------------------
	/**
	 * Opens a dialog to store the current configuration to the local file system.<br>
	 * After the file was selected, the configuration will be stored (in an extra {@link Thread}).
	 * If no file was selected, the storing operation will be aborted.
	 */
	public void storeFileSystem() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.xml" });
		fd.setFilterPath(isIShellPlugin ? pluginConfigDirectory : standaloneConfigDirectory);
		String path = fd.open();
		if (path != null) {
			if (!path.endsWith(".xml")) {
				path += ".xml";
			}
			final File file = new File(path);
			storeInExtraThread(file);
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
	 * Stores the configuration persistently into the local file system.<br>
	 * <br>
	 * 
	 * The given configFile will not be used for subsequent updates. This method is therefore more
	 * suited for "Exports" of the configuration.<br>
	 * <br>
	 * 
	 * Note that if a file was previously loaded by the user, this file will be updated, too.
	 * 
	 * @param configFile
	 *            the file which will contain the configuration data afterwards
	 * @return <code>true</code> if the configuration was stored successfully
	 */
	private synchronized boolean store(final File configFile) {
		
		// the fail-save files must not be overwritten
		if (configFile.toString().contains(FAILSAVE_CONFIG_FILE_ISHELL_PLUGIN)
				|| configFile.toString().contains(FAILSAVE_CONFIG_FILE_STANDALONE)) {
			return false;
		}
		
		log.debug("Initializing. Storing config to file: " + configFile);
		SpyglassConfiguration backup = null;
		
		// if the configuration file is not a file, yet, try to create it
		if (!configFile.isFile()) {
			try {
				configFile.createNewFile();
				
			} catch (final IOException e) {
				log.error("Error creating the file " + configFilePath, e);
			}
			if (!configFile.isFile()) {
				throw new RuntimeException("Can't find config file '" + configFile + "'");
			}
		}

		// otherwise load the file's configuration as backup
		else {
			backup = loadSpyglassConfig(configFile);
		}
		
		try {
			purgeDefaults();
			new Persister().write(spyglassConfig, configFile);
			log.debug("Storing config to file: " + configFile + " completed");
			
			// if the user loaded a configuration file, this file has to be updated, too.
			if (userDefinedCurrentConfiguration != null) {
				log.debug("Initializing. Storing config to file: "
						+ userDefinedCurrentConfiguration);
				new Persister().write(spyglassConfig, new File(userDefinedCurrentConfiguration));
				log.debug("Storing config to file: " + userDefinedCurrentConfiguration
						+ " completed");
			}
			
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
	
	public static void resetDefaultFile(final boolean isIShellPlugin) throws IOException {
		
		File in = null;
		File out = null;
		if (isIShellPlugin) {
			out = new File(DEFAULT_CONFIG_FILE_ISHELL_PLUGIN);
			in = new File(FAILSAVE_CONFIG_FILE_ISHELL_PLUGIN);
		} else {
			out = new File(DEFAULT_CONFIG_FILE_STANDALONE);
			in = new File(FAILSAVE_CONFIG_FILE_STANDALONE);
		}
		
		final FileChannel inChannel = new FileInputStream(in).getChannel();
		final FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (final IOException e) {
			throw e;
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Replaces all duplicated class instances from the list of plug-ins which are configured by
	 * default.
	 */
	private void purgeDefaults() {
		// spyglassConfig
		// .setDefaultPlugins(new LinkedList<Plugin>(spyglassConfig.getDefaultPlugins()));
	}
}