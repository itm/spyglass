/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
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
 * This Class gives some information about the environment of Spyglass which are static to the
 * process.
 * 
 * It also servers as an abstract property store, providing some basic file paths.
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
	private static final String PROPERTY_CONFIG_RECORD_DIR = "record_dir";
	private static final String PROPERTY_CONFIG_ISHELL = "configfile_ishell";
	private static final String PROPERTY_CONFIG_STANDALONE = "configfile_standalone";
	private static final String PROPERTY_CONFIG_STANDALONE_SIZE_X = "screensize_x";
	private static final String PROPERTY_CONFIG_STANDALONE_SIZE_Y = "screensize_y";
	private static final String PROPERTY_CONFIG_DRAWINGAREA_SIZE = "drawingarea_size";

	/**
	 * our property store
	 */
	private static final Properties props = new Properties();

	// --------------------------------------------------------------------------------
	/**
	 * Read the properties from file.
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
			throw new RuntimeException("Could not read or create properties file.", e);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Are we running as a plugin inside iShell?
	 */
	private static boolean isIShellPlugin = true;

	private static void storeProps(final Properties props) throws IOException {

		final OutputStream output = new FileOutputStream(PROPERTY_FILE);
		props.store(output, "this property file contains some basic parameters for Spyglass");
	}

	// --------------------------------------------------------------------------------
	/**
	 * Create a default property file
	 */
	private static void createDefaultConfig(final File f) throws IOException {
		if (!f.createNewFile()) {
			throw new IOException("Could not create property file!");
		}
		final Properties props = new Properties();
		final InputStream input = new FileInputStream(f);

		props.load(input);
		props.setProperty(PROPERTY_CONFIG_ISHELL, "config/DefaultSpyglassConfigIShellPlugin.xml");
		props.setProperty(PROPERTY_CONFIG_STANDALONE, "config/DefaultSpyglassConfigStandalone.xml");
		props.setProperty(PROPERTY_CONFIG_FILE_WORKING_DIR, "config/");
		props.setProperty(PROPERTY_CONFIG_FILE_IMAGE_DIR, "image/");
		props.setProperty(PROPERTY_CONFIG_RECORD_DIR, "record/");
		props.setProperty(PROPERTY_CONFIG_STANDALONE_SIZE_X, "800");
		props.setProperty(PROPERTY_CONFIG_STANDALONE_SIZE_Y, "600");
		props.setProperty(PROPERTY_CONFIG_DRAWINGAREA_SIZE, "83");
		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets whether Spyglass is running as an iShell plugin<br>
	 * This should be set before Spyglass is started!
	 * 
	 * @param isIShellPlugin
	 *            indicates whether Spyglass is running as an iShell plugin
	 */
	static void setIShellPlugin(final boolean isIShellPlugin) {
		SpyglassEnvironment.isIShellPlugin = isIShellPlugin;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns <code>true</code>, if Spyglass is running as an iShell Plugin
	 * 
	 * @return <code>true</code>, if Spyglass is running as an iShell Plugin
	 */
	public static boolean isIshellPlugin() {
		return isIShellPlugin;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the path to the configuration file.
	 * 
	 * @return the path to the configuration file.
	 */
	public static File getConfigFilePath() {

		if (isIShellPlugin) {
			return new File(props.getProperty(PROPERTY_CONFIG_ISHELL));
		}
		return new File(props.getProperty(PROPERTY_CONFIG_STANDALONE));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the horizontal size of the main window
	 * 
	 * @return the horizontal size of the main window
	 */
	public static int getWindowSizeX() {
		return Integer.parseInt(props.getProperty(PROPERTY_CONFIG_STANDALONE_SIZE_X));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the vertical size of the main window
	 * 
	 * @return the vertical size of the main window
	 */
	public static int getWindowSizeY() {
		return Integer.parseInt(props.getProperty(PROPERTY_CONFIG_STANDALONE_SIZE_Y));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the horizontal size of the main window
	 * 
	 * @param size
	 *            the horizontal size of the main window
	 * @throws IOException
	 */
	public static void setWindowSizeX(final int size) throws IOException {
		props.setProperty(PROPERTY_CONFIG_STANDALONE_SIZE_X, size + "");
		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the vertical size of the main window
	 * 
	 * @param size
	 *            the vertical size of the main window
	 * @throws IOException
	 */
	public static void setWindowSizeY(final int size) throws IOException {
		props.setProperty(PROPERTY_CONFIG_STANDALONE_SIZE_Y, size + "");
		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the path to the configuration file.
	 * 
	 * @param file
	 *            the path to the configuration file
	 * @throws IOException
	 */
	public static void setConfigFilePath(final File file) throws IOException {

		if (isIShellPlugin) {
			props.setProperty(PROPERTY_CONFIG_ISHELL, file.getAbsolutePath());
		} else {
			props.setProperty(PROPERTY_CONFIG_STANDALONE, file.getAbsolutePath());
		}

		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the current working directory, which is displayed on file open dialogs for
	 * configuration files.
	 * 
	 * @return the current working directory, which is displayed on file open dialogs for
	 *         configuration files
	 */
	public static String getConfigFileWorkingDirectory() {
		return props.getProperty(PROPERTY_CONFIG_FILE_WORKING_DIR);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the current working directory, which is displayed on file open dialogs for configuration
	 * files.
	 * 
	 * @param path
	 *            the current working directory, which is displayed on file open dialogs for
	 *            configuration files
	 * @throws IOException
	 */
	public static void setConfigFileWorkingDirectory(final String path) throws IOException {
		props.setProperty(PROPERTY_CONFIG_FILE_WORKING_DIR, path);
		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the current working directory, which is displayed on file open dialogs for image
	 * files.
	 * 
	 * @return the current working directory, which is displayed on file open dialogs for image
	 *         files
	 */
	public static String getImageWorkingDirectory() {
		return props.getProperty(PROPERTY_CONFIG_FILE_IMAGE_DIR);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the current working directory, which is displayed on file open dialogs for image files.
	 * 
	 * @param path
	 *            the current working directory, which is displayed on file open dialogs for image
	 *            files
	 * @throws IOException
	 */
	public static void setImageWorkingDirectory(final String path) throws IOException {
		props.setProperty(PROPERTY_CONFIG_FILE_IMAGE_DIR, path);
		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the current working directory, which is displayed on file open dialogs for record
	 * files.
	 * 
	 * @return the current working directory, which is displayed on file open dialogs for record
	 *         files
	 */
	public static String getDefalutRecordDirectory() {
		return props.getProperty(PROPERTY_CONFIG_RECORD_DIR);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the current working directory, which is displayed on file open dialogs for record files.
	 * 
	 * @param path
	 *            the current working directory, which is displayed on file open dialogs for record
	 *            files
	 * @throws IOException
	 */
	public static void setDefalutRecordDirectory(final String path) throws IOException {
		props.setProperty(PROPERTY_CONFIG_RECORD_DIR, path);
		storeProps(props);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the current drawing area's size
	 * 
	 * @return the current drawing area's size
	 */
	public static Integer getDrawingAreaSize() {
		return Integer.parseInt(props.getProperty(PROPERTY_CONFIG_DRAWINGAREA_SIZE));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the current drawing area's size
	 * 
	 * @param size
	 *            current drawing area's size
	 * @throws IOException
	 */
	public static void setDrawingAreaSize(final int size) throws IOException {
		props.setProperty(PROPERTY_CONFIG_DRAWINGAREA_SIZE, size + "");
		storeProps(props);
	}

}
