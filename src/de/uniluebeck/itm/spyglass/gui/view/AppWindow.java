/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class AppWindow {
	private Shell shell = null;

	private Display display = null;

	private SpyglassGuiComponent gui = null;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AppWindow() {
		display = Display.getDefault();

		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.layout();
		shell.setText("Spyglass");

		gui = new SpyglassGuiComponent(shell, SWT.NULL);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Shell getShell() {
		return shell;
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

}
