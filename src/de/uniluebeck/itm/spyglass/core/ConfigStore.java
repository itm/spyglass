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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginFactory;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionPacketNodePositionerPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
// --
/**
 * Instances of this class are used to load and store the spyglass configuration from and into the
 * local file system, respectively.<br>
 * Additionally, configuration parameters can be loaded and stored partially.
 * 
 * @author Sebastian Ebers
 * @author Dariush Forouher
 * 
 */
public class ConfigStore extends PropertyBean {

	// --------------------------------------------------------------------------------
	/**
	 * Thread, which is responsible for storing into the default config file.
	 * 
	 * @author Dariush Forouher
	 * 
	 */
	private final class AsyncStoreRunnable implements Runnable {

		@Override
		public void run() {

			log.debug("Async-Store thread started.");

			while (!Thread.currentThread().isInterrupted()) {
				try {

					// wait until we're notified
					synchronized (storePendingMutex) {
						if (!storePending) {
							storePendingMutex.wait();
						}
					}
					log.debug("Woke up, waiting another second.");

					// sleep for one second to wait for other calls which have not to be
					// processed for that reason
					Thread.sleep(1000);

				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();

				} finally {
					if (!Thread.currentThread().isInterrupted()) {

						// allow new store requests to be made
						storePending = false;

						storeSync(SpyglassEnvironment.getConfigFilePath());
					}
				}
			}
		}
	}

	/** An object which is used for logging errors and other events */
	private static Logger log = SpyglassLoggerFactory.getLogger(ConfigStore.class);

	/** Current instance of a SpyGlass configuration object */
	private final SpyglassConfiguration spyglassConfig;

	/** true, if the config should be written to file. */
	private volatile boolean storePending = false;

	private final Object storePendingMutex = new Object();

	/** Thread for async stores */
	private final Thread storeThread = new Thread(new AsyncStoreRunnable(), "ConfigStore-Thread");

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
	 * @throws Exception
	 * 
	 */
	public ConfigStore() throws Exception {
		final File f = SpyglassEnvironment.getConfigFilePath();

		boolean newFile = false;
		// create the file if necessary
		if (!f.exists()) {
			if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) {
				throw new IOException("Could not create directory " + f.getParent());
			}
			f.createNewFile();
			newFile = true;
		}

		// no point in continuing here
		if (!f.canWrite()) {
			throw new IOException("Cannot open file " + f + " for writing!");
		}

		SpyglassConfiguration newConfig = null;

		if (!newFile) {

			try {
				newConfig = ConfigStore.load(f);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (final Exception e) {
				log.error("Unable to load configuration input:\r\nThe configuration file '" + f + "' is invalid! I'll move"
						+ " the broken one away and start with a fresh config", e);

				final boolean ret = f.renameTo(new File(f.getAbsolutePath() + ".save-" + System.currentTimeMillis()));
				if (!ret) {
					throw new IOException("Could not move config file out of the way. Stopping.", e);
				}
			}
		}

		// create a new config, if none exists
		if (newConfig == null) {

			newConfig = createDefaultSpyglassConfig();

			store();
		}

		spyglassConfig = newConfig;

		registerListener();

		storeThread.start();

		// TODO: for Milestone2: register for events from the PacketReader
	}

	private void registerListener() {
		spyglassConfig.getPluginManager().addPluginListChangeListener(pluginManagerListener);
		spyglassConfig.getGeneralSettings().addPropertyChangeListener(pluginPropertyListener);
		spyglassConfig.getGeneralSettings().getMetrics().addPropertyChangeListener(pluginPropertyListener);
		for (final Plugin p : spyglassConfig.getDefaultPlugins()) {
			p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
		}
		for (final Plugin p : spyglassConfig.getPluginManager().getPlugins()) {
			p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
		}
	}

	private void unregisterListener() {
		spyglassConfig.getPluginManager().removePluginListChangeListener(pluginManagerListener);
		spyglassConfig.getGeneralSettings().removePropertyChangeListener(pluginPropertyListener);
		spyglassConfig.getGeneralSettings().getMetrics().removePropertyChangeListener(pluginPropertyListener);
		for (final Plugin p : spyglassConfig.getDefaultPlugins()) {
			p.getXMLConfig().removePropertyChangeListener(pluginPropertyListener);
		}
		for (final Plugin p : spyglassConfig.getPluginManager().getPlugins()) {
			p.getXMLConfig().removePropertyChangeListener(pluginPropertyListener);
		}
	}

	/**
	 * Create a Spyglass Configuration with default values and one PostionPacketNodePositioner as
	 * the only plugin.
	 * 
	 * @throws Exception
	 */
	private static SpyglassConfiguration createDefaultSpyglassConfig() throws Exception {
		SpyglassConfiguration newConfig;
		newConfig = new SpyglassConfiguration();
		final Collection<Plugin> defaultPlugins = new HashSet<Plugin>();
		newConfig.getPluginManager();
		for (final Class<? extends Plugin> p : PluginManager.getAvailablePluginTypes()) {
			defaultPlugins.add(PluginFactory.createDefaultInstance(p));
		}
		newConfig.setDefaultPlugins(defaultPlugins);

		// Create node positioner
		newConfig.getPluginManager().createNewPlugin(PositionPacketNodePositionerPlugin.class, null);
		return newConfig;
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
	 * Overwrites the current config with the one inside the given file.
	 * 
	 * @throws Exception
	 * 
	 */
	public void importConfig(final File file) throws Exception {
		if (spyglassConfig == null) {
			throw new IllegalStateException("Cannot import a configuration when we have" + "no working base configuration yet.");
		}

		final SpyglassConfiguration sgc = ConfigStore.load(file);

		/* get rid of the current configuration, listeners and plug-ins */

		log.debug("Unregistering old listener.");
		// unregister the old ones
		unregisterListener();

		log.debug("Stopping packet reader.");
		final PacketReader pr = spyglassConfig.getPacketReader();
		if (pr instanceof PacketRecorder) {
			((PacketRecorder) pr).setReadFromFile(false);
			((PacketRecorder) pr).setRecording(false);
		}

		log.debug("Stopping plugins.");
		// destroy the currently active plug-ins since they will be no longer needed
		for (final Plugin p : spyglassConfig.getPluginManager().getPlugins()) {
			p.shutdown();
		}

		/* now activate the new configuration */

		log.debug("Overwriting config.");
		spyglassConfig.overwriteWith(sgc);

		log.debug("Registering new listener.");
		// register the new ones
		registerListener();

		log.debug("Finished with importing config. Yeah!");

		// TODO: for Milestone2: register for events from the PacketReader

	}

	/**
	 * Exports the config into the given file
	 * 
	 * Note: Do this synchronized, since users generally don't mind waiting on this occasions (lots
	 * of programs hang when saving their files after explicitly been commanded by the user).
	 */
	public void exportConfig(final File file) {
		storeSync(file);
	}

	// --------------------------------------------------------------------------
	/**
	 * Loads and returns the configuration from a file
	 * 
	 * @param configFile
	 *            the file containing the configuration data
	 * @returns a valid SpyglassConfiguration
	 * @throws an
	 *             Exception if anything goes wrong
	 */
	private static SpyglassConfiguration load(final File configFile) throws Exception {
		log.debug("Initializing. Reading config from file: " + configFile);

		if (!configFile.isFile()) {
			throw new IOException("Can't find config file '" + configFile + "'");
		}

		final Serializer serializer = new Persister();
		final SpyglassConfiguration config = serializer.read(SpyglassConfiguration.class, configFile);

		config.getPluginManager();
		// if there is a default config missing, add it
		for (final Class<? extends Plugin> clazz : PluginManager.getAvailablePluginTypes()) {
			if (config.getDefaultConfig(clazz) == null) {
				final ArrayList<Plugin> set = new ArrayList<Plugin>(config.getDefaultPlugins());
				set.add(PluginFactory.createDefaultInstance(clazz));
				config.setDefaultPlugins(set);
			}
		}

		return config;
	}

	/**
	 * Store the config into the system-given file.
	 * 
	 * Stores the configuration in an extra {@link Thread} to improve the performance.<br>
	 * The storing operation will be delayed for one second. Every call received within this second
	 * will be ignored.
	 */
	public void store() {
		synchronized (storePendingMutex) {

			// log.debug("Waking up async-store thread.");
			this.storePending = true;
			this.storePendingMutex.notifyAll();
		}
	}

	/**
	 * Stores the configuration persistently into the given file.
	 * 
	 * This method is synchronized to avoid storing into the same file concurrently.
	 * 
	 * @param configFile
	 *            the file which will contain the configuration data afterwards
	 */
	private synchronized void storeSync(final File configFile) {

		log.debug("Storing config to file: " + configFile);

		final StringWriter buf = new StringWriter();

		try {

			// If something happens here, the file will never have been opened.
			new Persister().write(spyglassConfig, buf);

			final FileWriter writer = new FileWriter(configFile);
			writer.write(buf.toString());
			writer.close();

		} catch (final IOException e) {
			log.error("Unable to store configuration output: Error while writing the file!", e);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (final Exception e) {
			log.error("Unable to store configuration output: " + e.getMessage(), e);
		}

		log.debug("Stored config to file: " + configFile + ".");

	}

	/**
	 * Notify the ConfigStore that Spyglass is shutting down and thus we should not accept any mre
	 * store requests.
	 * 
	 * This method blocks until async-store thread has died.
	 * 
	 * @throws InterruptedException
	 *             if the excecuting thread is interrupted while this method is called.
	 */
	public void shutdown() throws InterruptedException {

		storeThread.interrupt();
		storeThread.join();
	}

}