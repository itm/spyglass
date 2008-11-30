// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass;

import java.io.File;

// --------------------------------------------------------------------------------
/**
 * This Class gives some information about the environment of Spyglass which are
 * static to the process.
 * 
 * 
 * @author Dariush Forouher
 *
 */
public class SpyglassEnvironment {

	/**
	 * Are we running as a plugin inside iShell?
	 * 
	 * TODO: add a setter so that spyglassStandalone can tweak this variable
	 */
	private static boolean isIShellPlugin = true;
	
	/**
	 * Returns true, if Spyglass is running as an iShell Plugin
	 */
	public static boolean isIshellPlugin() {
		return isIShellPlugin;
	}
	
	/**
	 * The path to the config file.
	 * 
	 * TODO: store these paths permanently so they survive a restart of spyglass
	 */
	public static File getConfigFilePath() {
		if (isIShellPlugin) {
			return new File("config/DefaultSpyglassConfigIShellPlugin.xml");
		} else {
			return new File("config/DefaultSpyglassConfigStandalone.xml");
		}
	}
	
	public static void setConfigFilePath(final File file) {
		// TODO
	}
	
	/**
	 * The current working directory, which is displayed on file open dialogs for
	 * config files.
	 * 
	 * TODO: Store it somewhere permanently
	 */
	public static String getConfigFileWorkingDirectory() {
		return new File("./config/plugin").getAbsoluteFile().toString();
	}
	
	public static void setConfigFileWorkingDirectory(final String path) {
		// TODO
	}
	
	/**
	 * The current working directory, which is displayed on file open dialogs.
	 * 
	 * TODO: Store it somewhere permanently
	 */
	public static String getImageWorkingDirectory() {
		return new File("images/").getAbsoluteFile().toString();
	}
	
	public static void setImageWorkingDirectory(final String path) {
		// TODO
	}

	

}
