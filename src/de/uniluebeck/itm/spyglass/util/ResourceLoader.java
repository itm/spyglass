/*******************************************************************************
 * 
 * This file has originally been developed for the JCarcassonne project.
 * 
 * Copyright (C) 2005-2006 by
 * 
 * Sebastian Ebers Janna Wagner Marco Wegner
 * 
 * (University of Luebeck)
 * 
 * *****************************************************************
 */

package de.uniluebeck.itm.spyglass.util;

import ishell.util.Logging;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Category;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class for loading resources correctly from within a JAR file or from
 * the local file system.
 * 
 * The implementation of this class uses the Singleton design pattern
 * implemented as Initialization on demand holder idiom (lazy-loaded singleton).
 * 
 * @author Sebastian Ebers
 * @author Janna Wagner
 * @author Marco Wegner
 */
public class ResourceLoader {
	
	private static Category log = Logging.get(ResourceLoader.class);
	
	// --------------------------------------------------------------------
	// Static nested class
	// --------------------------------------------------------------------
	
	/**
	 * 
	 */
	private static class LazyHolder {
		private static final ResourceLoader instance = new ResourceLoader();
	}
	
	// --------------------------------------------------------------------
	// Static methods for resource loading
	// --------------------------------------------------------------------
	
	/**
	 * Loads the icon with the specified file name. If the file name does not
	 * represent a JAR file URL, then the icon is searched in the local file
	 * system.
	 * 
	 * The file name is relative either to the root of the JAR file or the
	 * current directory (i.e. project root) in the local file system.
	 * 
	 * @param filename
	 *            The icon's location.
	 * @return icon The icon.
	 */
	public static Icon loadIcon(final String filename) {
		Icon icon;
		final URL url = getLoader().findResource(filename);
		if (url != null) {
			icon = new ImageIcon(url);
		} else {
			icon = new ImageIcon(filename);
		}
		return icon;
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * Loads the awt image with the specified file name. If the file name does
	 * not represent a JAR file URL, then the image is searched in the local
	 * file system.
	 * 
	 * The file name is relative either to the root of the JAR file or the
	 * current directory (i.e. project root) in the local file system.
	 * 
	 * @param filename
	 *            The image's location.
	 * @return java.awt.Image The image.
	 */
	public static java.awt.Image loadAWTImage(final String filename) {
		java.awt.Image image;
		final URL url = getLoader().findResource(filename);
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (url != null) {
			image = toolkit.getImage(url);
		} else {
			image = toolkit.getImage(filename);
		}
		return image;
	}
	
	/**
	 * Loads the SWT image with the specified file name. If the file name does
	 * not represent a JAR file URL, then the image is searched in the local
	 * file system.
	 * 
	 * The file name is relative either to the root of the JAR file or the
	 * current directory (i.e. project root) in the local file system.
	 * 
	 * @param filename
	 *            The image's location.
	 * @return org.eclipse.swt.graphics.Image The image.
	 */
	public static org.eclipse.swt.graphics.Image loadSWTImage(final Display display, final String filename) {
		final InputStream instream = getLoader().getResourceAsStream(filename);
		if (instream != null) {
			return new org.eclipse.swt.graphics.Image(display, instream);
		} else {
			return new org.eclipse.swt.graphics.Image(display, filename);
		}
	}
	
	public static File loadFile(final String filename) {
		final InputStream instream = getLoader().getResourceAsStream(filename);
		log.debug("The inputstream is " + ((instream != null) ? " not null" : "null"));
		if (instream != null) {
			try {
				final File file = File.createTempFile("tmp", "xml");
				final OutputStream out = new FileOutputStream(file);
				final byte buf[] = new byte[1024];
				int len;
				while ((len = instream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				instream.close();
				return file;
			} catch (final IOException e) {
				log.error("The file with the given path " + filename + " could not be loaded", e);
			}
		}
		
		return new File(filename);
		
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * Load an URL with a specific filename. If the filename does not exist in
	 * the JAR file the URL is searched in the file system.
	 * 
	 * @param filename
	 *            location of the URL
	 * @return url The URL
	 */
	public static URL getURL(final String filename) throws MalformedURLException {
		URL url = getLoader().findResource(filename);
		if (url == null) {
			url = new URL("file:./" + filename);
		}
		return url;
	}
	
	// --------------------------------------------------------------------
	// Static helper methods
	// --------------------------------------------------------------------
	
	/**
	 * Get the {@link ResourceLoader} instance.
	 * 
	 * @return instance
	 */
	protected static ResourceLoader getInstance() {
		return LazyHolder.instance;
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * Get the class loader.
	 * 
	 * @return URLClassLoader
	 */
	private static URLClassLoader getLoader() {
		return (URLClassLoader) getInstance().getClass().getClassLoader();
	}
	
	// --------------------------------------------------------------------
	// Constructor
	// --------------------------------------------------------------------
	
	/**
	 * Hidden constructor to prevent instantiating from the outside.
	 */
	private ResourceLoader() {
	}
}
