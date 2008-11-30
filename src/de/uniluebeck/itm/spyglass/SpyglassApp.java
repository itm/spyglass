/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
// --
/**
 * Application class for wrapping the Spyglass core class and it's user interface/GUI. It
 * instantiate and injects the core classes that are needed to run the application.
 */
public class SpyglassApp {
	private static Logger log = SpyglassLoggerFactory.getLogger(SpyglassApp.class);
	
	// -------------------------------------------------------------------------
	/**
	 * @throws IOException 
	 * 
	 */
	public SpyglassApp() throws IOException {
		log.info("New SpyGlass instance.");
		
		// GUI
		final DeviceData data = new DeviceData();
		// data.tracking = true;
		final Display display = new Display(data);
		// final Sleak sleak = new Sleak();
		// sleak.open();
		
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.layout();
		shell.setText("Spyglass");
		shell.setSize(800, 600);
		shell.open();
		
		// Model
		final Spyglass spyglass = new Spyglass();
		
		// View
		final AppWindow appWindow = new AppWindow(spyglass, shell);
		
		// Control
		new UIController(spyglass, appWindow);
		
		// Start visualization
		spyglass.start();
		
		// SWT message loop
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		spyglass.shutdown();
		
		log.info("SpyGlass end. Done.");
	}
	
	// -------------------------------------------------------------------------
	/**
	 * @throws IOException 
	 * 
	 */
	public static void main(final String[] args) throws IOException {
		log.info("Starting SpyGlass app.");
		new SpyglassApp();
	}
	
}
