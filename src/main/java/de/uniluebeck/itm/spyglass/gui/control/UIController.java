/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.control;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
/**
 * In the M-V-C pattern, this is the CONTROL.
 * 
 * It connects to the model and view. Events coming from the user and partly coming from the plugins
 * are handled by this class or its children.
 * 
 * @author Dariush Forouher
 */
public class UIController {

	private static final Logger log = SpyglassLoggerFactory.getLogger(UIController.class);

	// --------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 * @param appWindow
	 *            the application's GUI (view)
	 * 
	 */
	public static void connect(final Spyglass spyglass, final AppWindow appWindow) {

		if ((spyglass == null) || (appWindow == null)) {
			throw new NullPointerException("spyglass and appWindow must not be null!");
		}

		new PluginManagerController(appWindow, spyglass);

		new MouseClickController(appWindow.getGui().getDrawingArea(), spyglass);
		new KeyController(appWindow.getGui().getDrawingArea());
		new MouseDragController(appWindow.getGui().getDrawingArea());
		new MouseWheelController(appWindow.getGui().getDrawingArea());
		new ScrollbarController(appWindow.getGui().getDrawingArea());

		log.info("Controller connected with Model and View.");

	}

}
