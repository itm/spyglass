/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.Spyglass;

// --------------------------------------------------------------------------------
/**
 * The application's window.<br>
 *
 * Instances of this class manage references to the graphical user interface's main components.
 *
 * In M-V-C, this is the VIEW.
 */
public class AppWindow implements DisposeListener {

	private Display display = null;

	private SpyglassGuiComponent gui = null;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 *
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 * @param parent
	 *            the parent widget
	 */
	public AppWindow(final Spyglass spyglass, final Composite parent) {

		this.display = parent.getDisplay();

		gui = new SpyglassGuiComponent(parent, SWT.NULL, spyglass);

		parent.addDisposeListener(this);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns Spyglass's graphical user interface manager
	 *
	 * @return Spyglass's graphical user interface manager
	 */
	public SpyglassGuiComponent getGui() {
		return gui;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the current display
	 *
	 * @return the current display
	 */
	public Display getDisplay() {
		return display;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		if (gui != null) {
			this.gui.dispose();
		}
	}

}
