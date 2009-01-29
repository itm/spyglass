// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

// --------------------------------------------------------------------------------
/**
 * This Class gives some information about the environment of Spyglass which are
 * static to the process.
 * 
 * It also servers as an abstract property store, providing some basic
 * file paths. 
 * 
 * @author Dariush Forouher
 *
 */
public class SpyglassEnvironment {

	/**
	 * Name of the property file
	 */
	private static final String PROPERTY_FILE = "spyglass.properties";

	/**
	 * property names
	 */
	private static final String PROPERTY_CONFIG_FILE_WORKING_DIR = "configfile_dir";
	private static final String PROPERTY_CONFIG_FILE_IMAGE_DIR = "image_dir";
	private static final String PROPERTY_CONFIG_ISHELL = "configfile_ishell";
	private static final String PROPERTY_CONFIG_STANDALONE = "configfile_standalone";

	/**
	 * our property store
	 */
	private static final Properties props = new Properties();
	
	/**
	 * Read the properies from file.
	 * 
	 * Fail early if the file is not readable
	 */
	static {
		final File f = new File(PROPERTY_FILE);
		
		try {
			if (!f.exists()) {
				createDefaultConfig(f);
			}
			
			final InputStream input = new FileInputStream(PROPERTY_FILE);
			props.load(input);

		// this is a fatal error for spyglass
		} catch (final IOException e) {
			throw new RuntimeException("Could not read or create properties file.",e);
		}

	}
	
	/**
	 * Are we running as a plugin inside iShell?
	 */
	private static boolean isIShellPlugin = true;
	
	private static void storeProps(final Properties props) throws IOException {
	
		final OutputStream output = new FileOutputStream(PROPERTY_FILE);
		props.store(output, "this property file contains some basic parameters for Spyglass");
	}

	/**
	 * Create a default property file
	 */
	private static void createDefaultConfig(final File f) throws IOException {
		if (!f.createNewFile()) {
			throw new IOException("Could not create property file!");
		} else {
			final Properties props = new Properties();
			final InputStream input = new FileInputStream(f);
			
			props.load(input);
			props.setProperty(PROPERTY_CONFIG_ISHELL, "config/DefaultSpyglassConfigIShellPlugin.xml");
			props.setProperty(PROPERTY_CONFIG_STANDALONE, "config/DefaultSpyglassConfigStandalone.xml");
			props.setProperty(PROPERTY_CONFIG_FILE_WORKING_DIR, "config/");
			props.setProperty(PROPERTY_CONFIG_FILE_IMAGE_DIR, "image/");
			storeProps(props);
		}
	}

	/**
	 * is Spyglass is running as an iShell Plugin?
	 * 
	 * should be set before Spyglass is started!
	 */
	static void setIShellPlugin(final boolean isIShellPlugin) {
		SpyglassEnvironment.isIShellPlugin = isIShellPlugin;
	}

	/**
	 * Returns true, if Spyglass is running as an iShell Plugin
	 */
	static boolean isIshellPlugin() {
		return isIShellPlugin;
	}
	
	/**
	 * The path to the config file.
	 */
	public static File getConfigFilePath() {
		
		if (isIShellPlugin) {
			return new File(props.getProperty(PROPERTY_CONFIG_ISHELL));
		} else {
			return new File(props.getProperty(PROPERTY_CONFIG_STANDALONE));
		}
	}

	/**
	 * Set the path to the config file.
	 */
	public static void setConfigFilePath(final File file) throws IOException {

		if (isIShellPlugin) {
			props.setProperty(PROPERTY_CONFIG_ISHELL, file.getAbsolutePath());
		} else {
			props.setProperty(PROPERTY_CONFIG_STANDALONE, file.getAbsolutePath());
		}
		
		storeProps(props);
	}
	
	/**
	 * The current working directory, which is displayed on file open dialogs for
	 * config files.
	 */
	public static String getConfigFileWorkingDirectory() {
		
		return props.getProperty(PROPERTY_CONFIG_FILE_WORKING_DIR);

	}
	
	/**
	 * Set the current working directory, which is displayed on file open dialogs for
	 * config files.
	 */
	public static void setConfigFileWorkingDirectory(final String path) throws IOException {

		props.setProperty(PROPERTY_CONFIG_FILE_WORKING_DIR, path);

		storeProps(props);
	}
	
	/**
	 * The current working directory, which is displayed on file open dialogs
	 * for image files.
	 */
	public static String getImageWorkingDirectory() {
		
		return props.getProperty(PROPERTY_CONFIG_FILE_IMAGE_DIR);
	}
	
	/**
	 * Set the current working directory, which is displayed on file open dialogs
	 * for image files.
	 */
	public static void setImageWorkingDirectory(final String path) throws IOException {
		props.setProperty(PROPERTY_CONFIG_FILE_IMAGE_DIR, path);

		storeProps(props);
	}

	

}
