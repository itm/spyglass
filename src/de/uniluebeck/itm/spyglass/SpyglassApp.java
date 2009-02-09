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
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class SpyglassApp extends ApplicationWindow {
	private static Logger log = SpyglassLoggerFactory.getLogger(SpyglassApp.class);
	
	private Spyglass spyglass;
		
	private UIController uic;
	
	// -------------------------------------------------------------------------
	/**
	 * @throws IOException 
	 * 
	 */
	public SpyglassApp(final Shell shell) {
		super(shell);
		this.setBlockOnOpen(true);
		addMenuBar();
		addStatusLine();
		addToolBar(SWT.None);

	}
	
	@Override
	protected Control createContents(final Composite parent) {
		
		// Model
		SpyglassEnvironment.setIShellPlugin(false);
		try {
			spyglass = new Spyglass();

			// View
			final AppWindow appWindow = new AppWindow(spyglass, getShell());
			
			// Control
			uic = new UIController(spyglass, appWindow);
			
			new ToolbarHandler(getToolBarManager(), spyglass, appWindow);

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			log.error("",e);
		}
		
		return parent;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		
		shell.setText("Spyglass");
		shell.setSize(800, 600);
		
	}
	
	public void shutdown() {

		if (uic != null) {
			uic.shutdown();
		}

		if (spyglass != null) {
			spyglass.shutdown();
		}
		
	}

	// -------------------------------------------------------------------------
	/**
	 * @throws IOException 
	 * 
	 */
	public static void main(final String[] args) {

		// SWT stuff
		final DeviceData data = new DeviceData();
		data.tracking = true;
		data.debug = true;
		final Display display = new Display(data);
		final Shell shell = new Shell(display);

		final SpyglassApp app = new SpyglassApp(shell);
				
		app.open();
		app.shutdown();
		shell.dispose();

	}
	
}
