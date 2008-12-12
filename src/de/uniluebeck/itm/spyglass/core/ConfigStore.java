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

	/** An object which is used for logging errors and other events */
	private static Logger log = SpyglassLoggerFactory.getLogger(ConfigStore.class);

	/** Current instance of a SpyGlass configuration object */
	private final SpyglassConfiguration spyglassConfig;

	private volatile Boolean storingPending = false;

	private Object storeMutex = new Object();

	private volatile Boolean shutdownInProgress = false;

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
	 */
	public ConfigStore() throws IOException {
		final File f = SpyglassEnvironment.getConfigFilePath();

		// create the file if necessary
		if (!f.exists()) {
			f.createNewFile();
		}

		// no point in continuing here
		if (!f.canWrite()) {
			throw new IOException("Cannot open file " + f + " for writing!");
		}

		SpyglassConfiguration newConfig = null;
		try {
			newConfig = ConfigStore.load(f);
		} catch (final Exception e) {
			log.error("Unable to load configuration input:\r\nThe configuration file '" + f + "' is invalid! I'll move"
					+ " the broken one away and start with a fresh config", e);

			final boolean ret = f.renameTo(new File(f.getAbsolutePath() + ".save-" + System.currentTimeMillis()));
			if (!ret) {
				throw new IOException("Could not move config file out of the way. Stopping.", e);
			}
		}

		// create a new config, if none exists
		if (newConfig == null) {

			newConfig = createDefaultSpyglassConfig();

			store();
		}

		spyglassConfig = newConfig;

		registerListener();

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
	 */
	private static SpyglassConfiguration createDefaultSpyglassConfig() {
		SpyglassConfiguration newConfig;
		newConfig = new SpyglassConfiguration();
		final Collection<Plugin> defaultPlugins = new HashSet<Plugin>();
		for (final Class<? extends Plugin> p : newConfig.getPluginManager().getAvailablePluginTypes()) {
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

		// unregister the old ones
		unregisterListener();

		// destroy the currently active plug-ins since they will be no longer needed
		for (final Plugin p : spyglassConfig.getPluginManager().getPlugins()) {
			p.reset();
			p.shutdown();
		}
		final PacketReader pr = spyglassConfig.getPacketReader();
		if (pr instanceof PacketRecorder) {
			((PacketRecorder) pr).setReadFromFile(false);
			((PacketRecorder) pr).enableRecording(false);
		}

		spyglassConfig.overwriteWith(sgc);

		// register the new ones
		registerListener();

		firePropertyChange("replaceConfiguration", null, spyglassConfig);

		// TODO: for Milestone2: register for events from the PacketReader

	}

	/**
	 * Exports the config into the given file
	 */
	public void exportConfig(final File file) {
		this.store(file);
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

		// if there is a default config missing, add it
		for (final Class<? extends Plugin> clazz : config.getPluginManager().getAvailablePluginTypes()) {
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
	 */
	public void store() {
		this.store(SpyglassEnvironment.getConfigFilePath());
	}

	/**
	 * Stores the configuration persistently into the given file.<br>
	 * <br>
	 * 
	 * Stores the configuration in an extra {@link Thread} to improve the performance.<br>
	 * The storing operation will be delayed for one second. Every call received within this second
	 * will be ignored.
	 * 
	 * @param configFile
	 *            the file which will contain the configuration data afterwards
	 */
	private void store(final File configFile) {

		// don't start new writes when we are leaving the building
		if (shutdownInProgress) {
			//TODO racy zu storePending!!
			return;
		}

		// if configFile is not our main configfile, then save regardless
		// of any other pending storing.
		final boolean foreignFile = !configFile.equals(SpyglassEnvironment.getConfigFilePath());

		// check if there is already a storing operation in progress
		if (!foreignFile) {
			synchronized (storingPending) {
				// if so, return
				if (storingPending) {
					return;
				}
				// otherwise proceed
				storingPending = true;
			}
		}

		// create the thread to store
		final Thread t = new Thread() {
			@Override
			public void run() {

				// storing concurrently may have unpleasant results (file corruption etc).
				synchronized (storeMutex) {

					try {
						// sleep for one second to wait for other calls which have not to be
						// processed for that reason
						sleep(1000);
					} catch (final InterruptedException e) {
						log.error(e, e);
					} finally {
						// the next successive call will no longer be ignored
						if (!foreignFile) {
							synchronized (storingPending) {
								storingPending = false;
							}
						}
					}

					log.debug("Storing config to file: " + configFile);

					try {

						final StringWriter buf = new StringWriter();

						// If something happens here, the file will never have been opened.
						new Persister().write(spyglassConfig, buf);

						final FileWriter writer = new FileWriter(configFile);
						writer.write(buf.toString());
						writer.close();

						log.debug("Stored config to file: " + configFile + ".");

					} catch (final Exception e) {
						log.error("Unable to store configuration output: " + e, e);
					}

					// notify waitForRemainingWrites() that maybe it can return
					synchronized (storingPending) {
						storingPending.notifyAll();
					}
				}

			}
		};

		// this Thread must not be a Daemon! Otherwise a running storing process would be
		// interrupted when the program terminates.
		// This would result in a corrupted configuration file.
		t.setDaemon(false);
		t.start();

	}

	/**
	 * Notify the ConfigStore that Spyglass is shutting down and thus we should not accept any mre
	 * store requests.
	 */
	public void signalShutdown() {
		shutdownInProgress = true;
	}

	/**
	 * This method blocks until the config has been written. If there is currently no write in
	 * progress, this method returns instantly.
	 */
	public void waitForRemainingWrites() {
		return;
//		synchronized (storingPending) {
//			while (storingPending) {
//				log.debug("Waiting for remaining configuration writes...");
//				try {
//					storingPending.wait();
//				} catch (final InterruptedException e) {
//					log.error("Interrupted", e);
//				}
//			}
//		}
	}
}