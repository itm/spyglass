/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass;

import org.apache.log4j.Category;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * Application class for wrapping the Spyglass core class and it's user interface/GUI. It
 * instantiate and injects the core classes that are needed to run the application.
 */
public class SpyglassApp {
	private static Category log = SpyglassLogger.getLogger(SpyglassApp.class);
	
	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	public SpyglassApp() {
		log.info("New SpyGlass instance.");
		
		// GUI
		final Display display = Display.getDefault();
		
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.layout();
		shell.setText("Spyglass");
		shell.setSize(800, 600);
		shell.open();
		
		// Application objects
		final AppWindow appWindow = new AppWindow(display, shell);
		final Spyglass spyglass = new Spyglass(false);
		
		@SuppressWarnings("unused")
		final UIController uiController = new UIController(spyglass, appWindow);
		
		spyglass.getDrawingArea().setAppWindow(appWindow);
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
	 * 
	 */
	public static void main(final String[] args) {
		log.info("Starting SpyGlass app.");
		new SpyglassApp();
	}
	
}
