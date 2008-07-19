package de.uniluebeck.itm.spyglass.core;

import ishell.util.Logging;

import java.io.File;
import java.util.List;

import org.apache.log4j.Category;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXmlConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ConfigStore {
	
	private static final String defaultFileName = "config/DefaultSpyglassConfig.xml";
	private SpyglassConfiguration spyglassConfig;
	private static Category log = Logging.get(ConfigStore.class);
	
	@Override
	public void finalize() throws Throwable {
		
	}
	
	/**
	 * Reads the config from an hardcoded standard-path (which is stored
	 * internally in this class)
	 */
	public ConfigStore() {
		this(new File(defaultFileName));
	}
	
	/**
	 * Reads the config from an hardcoded standard-path (which is stored
	 * internally in this class)
	 */
	public ConfigStore(final SpyglassConfiguration spyglassConfig) {
		this.spyglassConfig = spyglassConfig;
	}
	
	/**
	 * creates a new configStore for the given File.
	 * 
	 * @param file
	 */
	public ConfigStore(final File configFile) {
		load(configFile);
	}
	
	/**
	 * @return the spyglassConfig
	 */
	public SpyglassConfiguration getSpyglassConfig() {
		return spyglassConfig;
	}
	
	/**
	 * @param spyglassConfig
	 *            the spyglassConfig to set
	 */
	public void setSpyglassConfig(final SpyglassConfiguration spyglassConfig) {
		this.spyglassConfig = spyglassConfig;
	}
	
	public GeneralSettingsXmlConfig readGeneralSettingsConfig() {
		return null;
	}
	
	/**
	 * 
	 * @param instanceName
	 */
	public PluginXMLConfig readPluginInstanceConfig(final String instanceName) {
		return null;
	}
	
	/**
	 * 
	 * @param clazz
	 */
	public List<PluginXMLConfig> readPluginInstanceConfigs(final Class<Plugin> clazz) {
		return null;
	}
	
	/**
	 * 
	 * @param clazz
	 */
	public PluginXMLConfig readPluginTypeDefaults(final Class<Plugin> clazz) {
		return null;
	}
	
	/**
	 * 
	 * @param config
	 */
	public void storeGeneralSettingsConfig(final GeneralSettingsXmlConfig config) {
		
	}
	
	/**
	 * 
	 * @param config
	 */
	public void storePluginInstanceConfig(final PluginXMLConfig config) {
		
	}
	
	/**
	 * 
	 * @param config
	 */
	public void storePluginTypeDefaults(final PluginXMLConfig config) {
		
	}
	
	/**
	 * Loads the configuration from a file
	 * 
	 * @param configFile
	 *            the file containing the configuration data
	 * @return <code>true</code> if the configuration was loaded successfully
	 */
	private boolean load(final File configFile) {
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
			spyglassConfig = config;
			return true;
			
		} catch (final Exception e) {
			log.error("Unable to load configuration input: " + e, e);
			return false;
		}
	}
	
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
		try {
			new Persister().write(spyglassConfig, configFile);
			return true;
		} catch (final Exception e) {
			log.error("Unable to store configuration output: " + e, e);
			return false;
		}
	}
	
}