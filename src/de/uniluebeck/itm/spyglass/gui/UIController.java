/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.gui;

import ishell.util.Logging;

import java.util.EventObject;

import org.apache.log4j.Category;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.plugin.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.GlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.plugin.NodePainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.RelationPainterPlugin;

// --------------------------------------------------------------------------------
/**
 * The UI controller is the interface between the core Spyglass functionality and the graphical user interface. It is
 * bound to a specific GUI library. If the GUI must be completely replaced, the UI controller must also be
 * changed/replaced.
 */
public class UIController {
	private static Category log = Logging.get(UIController.class);

	private AppWindow appWindow = null;

	private Spyglass spyglass = null;

	private final Display display;

	private Color canvasBgColor = new Color(null, 255, 255, 255);

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private final Runnable paintRunnable = new Runnable() {
		@Override
		public void run() {
			appWindow.getGui().getCanvas().redraw();
		}
	};

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public UIController(Spyglass spyglass, AppWindow appWindow) {
		if (spyglass == null || appWindow == null)
			throw new IllegalArgumentException();

		this.spyglass = spyglass;
		this.appWindow = appWindow;

		display = appWindow.getDisplay();

		init();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void init() {
		// Add listeners
		appWindow.getGui().getCanvas().addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				render(e.gc);
			}
		});

		// Trigger asynchronous redraw (must happen in the gui thread, see SWT documentation)
		spyglass.addSpyglassListener(new SpyglassListener() {
			@Override
			public void redraw(EventObject e) {
				if (log.isDebugEnabled())
					log.debug("Triggering redraw event from " + e.getSource());

				// Asynchrony execution, waiting for operation to finish
				display.asyncExec(paintRunnable);
			}
		});
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void render(GC gc) {
		gc.setBackground(canvasBgColor);
		gc.fillRectangle(appWindow.getGui().getCanvas().getClientArea());

		// Background Painter Plugins
		for (Plugin plugin : spyglass.getPluginManager().getActivePlugins())
			if (plugin instanceof BackgroundPainterPlugin && plugin.isActive())
				for (DrawingObject object : plugin.getSubLayer().getDrawingObjects().values())
					spyglass.getCanvas().draw(object, gc);

		// Relation Painter Plugins
		for (Plugin plugin : spyglass.getPluginManager().getActivePlugins())
			if (plugin instanceof RelationPainterPlugin && plugin.isActive())
				for (DrawingObject object : plugin.getSubLayer().getDrawingObjects().values())
					spyglass.getCanvas().draw(object, gc);

		// Node Painter Plugins
		for (Plugin plugin : spyglass.getPluginManager().getActivePlugins())
			if (plugin instanceof NodePainterPlugin && plugin.isActive())
				for (DrawingObject object : plugin.getSubLayer().getDrawingObjects().values())
					spyglass.getCanvas().draw(object, gc);

		// Global Information Plugins
		// TODO: Global information plug-ins must add information to a Tree on the GUI, not using drawing objects
		for (Plugin plugin : spyglass.getPluginManager().getActivePlugins())
			if (plugin instanceof GlobalInformationPlugin && plugin.isActive())
				for (DrawingObject object : plugin.getSubLayer().getDrawingObjects().values())
					spyglass.getCanvas().draw(object, gc);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AppWindow getAppWindow() {
		return appWindow;
	}

}
