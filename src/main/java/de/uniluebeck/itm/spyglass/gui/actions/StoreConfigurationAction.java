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

public class StoreConfigurationAction extends Action {

	private static Logger log = SpyglassLoggerFactory.getLogger(StoreConfigurationAction.class);

	private final ImageDescriptor imageDescriptor = getImageDescriptor("page_save.png");

	private final Spyglass spyglass;

	public StoreConfigurationAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}

	@Override
	public void run() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.xml" });
		fd.setFilterPath(SpyglassEnvironment.getConfigFileWorkingDirectory());
		String path = fd.open();
		if (path != null) {
			if (!path.endsWith(".xml")) {
				path += ".xml";
			}
			final File file = new File(path);

			if (!file.exists()) {
				boolean ret = false;
				try {
					ret = file.createNewFile();
				} catch (final IOException e) {
					log.error("", e);
				} finally {
					if (!ret) {
						log.error("Could not create file!");
						return;
					}
				}
			}

			if (!file.canWrite()) {
				log.error("Could not open file for writing.");
				return;
			}

			// store
			spyglass.getConfigStore().exportConfig(file);

			// this is our new config file
			try {
				SpyglassEnvironment.setConfigFileWorkingDirectory(file.getParent());
			} catch (final IOException e) {
				log.error("Could not store the new config path in my property file.", e);
			}

		}
	};

	@Override
	public String getText() {
		return "&Save file";
	}

	@Override
	public String getToolTipText() {
		return "Store Configuration";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

}
