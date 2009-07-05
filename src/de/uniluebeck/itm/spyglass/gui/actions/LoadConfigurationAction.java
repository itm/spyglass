/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.actions;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

//--------------------------------------------------------------------------------
/**
 * Implementation of an {@link Action} needed for loading a Spyglass configuration.
 * 
 * @author Dariush Forouher
 * @author Sebastian Ebers
 * 
 */
public class LoadConfigurationAction extends Action {

	private static Logger log = SpyglassLoggerFactory.getLogger(LoadConfigurationAction.class);

	private final ImageDescriptor imageDescriptor = getImageDescriptor("page_gear.png");

	private final Spyglass spyglass;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param spyglass
	 *            the spyglass instance
	 */
	public LoadConfigurationAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void run() {
		try {
			loadFromFileSystem();
		} catch (final Exception e) {
			log.error(e, e);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Loads the configuration from a file which is selected using a {@link FileDialog}
	 * 
	 * @return <code>true</code> if the configuration was loaded successfully
	 * @throws Exception
	 *             thrown if the configuration is not loaded successfully due to an exception
	 */
	public boolean loadFromFileSystem() throws Exception {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.xml" });
		fd.setFilterPath(SpyglassEnvironment.getConfigFileWorkingDirectory());
		final String path = fd.open();
		if (path != null) {
			final File f = new File(path);
			if (!f.canRead()) {
				log.error("Could not read file " + f);
				return false;
			}

			// first we have to load the new config, to avoid that it is overwritten in the meantime
			try {
				spyglass.getConfigStore().importConfig(f);
				spyglass.reset();
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (final Exception e) {
				throw new Exception("Could not load the config.", e);
			}

			try {
				SpyglassEnvironment.setConfigFileWorkingDirectory(f.getParent());
			} catch (final IOException e) {
				throw new IOException("Could not store the new config path in my property file.", e);
			}
			return true;
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	@Override
	public String getText() {
		return "&Open file";
	}

	// --------------------------------------------------------------------------------
	@Override
	public String getToolTipText() {
		return "Load Configuration";
	}

	// --------------------------------------------------------------------------------
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

}
