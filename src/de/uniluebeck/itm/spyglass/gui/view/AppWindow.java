/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.SWTButcherClass;
import org.eclipse.swt.graphics.Sleak;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class AppWindow {
	private static Logger log = SpyglassLoggerFactory.getLogger(AppWindow.class);

	private Display display = null;

	private SpyglassGuiComponent gui = null;

	/**
	 * Enables SWT Object tracking. This allows tracking memory leaks (e.g. missing dispose)
	 * 
	 * IMPORTANT: This activates some really hacky butchering in internal SWT data structures. Only
	 * the bravest of all should consider enabling this. You have been warned.
	 */
	public static final boolean ENABLE_SWT_DEBUGGING = false;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AppWindow(final Spyglass spyglass, final Composite parent) {

		if (ENABLE_SWT_DEBUGGING) {
			if (!parent.getDisplay().getDeviceData().tracking) {
				log.warn("Enabling SWT-Tracking hack. Expect problems.");
				SWTButcherClass.enableTracking(parent.getDisplay());
			}
			final Sleak sleak = new Sleak();
			sleak.open();
		}

		this.display = parent.getDisplay();
		gui = new SpyglassGuiComponent(parent, SWT.NULL, spyglass);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public SpyglassGuiComponent getGui() {
		return gui;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Display getDisplay() {
		return display;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setDisplay(final Display display) {
		this.display = display;
	}

}
