/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class AppWindow {
	private Display display = null;

	private SpyglassGuiComponent gui = null;
	private final DrawingArea drawingArea = new DrawingArea();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AppWindow(Display display, Composite parent) {
		this.display = display;
		gui = new SpyglassGuiComponent(parent, SWT.NULL);
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
	public void setDisplay(Display display) {
		this.display = display;
	}

	public DrawingArea getDrawingArea(){
		return null;
	}

}
