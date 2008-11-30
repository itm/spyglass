/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.Spyglass;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class AppWindow {
	
	private Display display = null;
	
	private SpyglassGuiComponent gui = null;
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AppWindow(final Spyglass spyglass, final Composite parent) {
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
