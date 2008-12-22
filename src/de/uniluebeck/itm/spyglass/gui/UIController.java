/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.EventDispatcher;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.RulerArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.DrawingObjectListener;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
// --
/**
 * The UI controller is the interface between the core Spyglass functionality and the graphical user
 * interface. It is bound to a specific GUI library. If the GUI must be completely replaced, the UI
 * controller must also be changed/replaced.
 */
public class UIController {

	private static Logger log = SpyglassLoggerFactory.getLogger(UIController.class);

	private AppWindow appWindow = null;

	private Spyglass spyglass = null;

	private final Display display;

	/** User events will be dispatched here */
	private EventDispatcher eventDispatcher;

	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public UIController(final Spyglass spyglass, final AppWindow appWindow) {
		if ((spyglass == null) || (appWindow == null)) {
			throw new IllegalArgumentException();
		}

		this.spyglass = spyglass;
		this.appWindow = appWindow;
		this.eventDispatcher = new EventDispatcher(spyglass.getPluginManager(), appWindow.getGui().getDrawingArea());

		display = appWindow.getDisplay();

		init();

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener("pluginManager", new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				/*
				 * Add DrawingObjectListeners to all current and future plug-ins (used for knowing
				 * when to update the drawing area)
				 */
				final List<Plugin> plugins = ((PluginManager) evt.getNewValue()).getPlugins();
				for (final Plugin p : plugins) {
					if (p instanceof Drawable) {
						p.addDrawingObjectListener(drawingObjectListener);
					}
				}
				((PluginManager) evt.getNewValue()).addPluginListChangeListener(pluginListChangeListener);
				eventDispatcher = new EventDispatcher(((PluginManager) evt.getNewValue()), appWindow.getGui().getDrawingArea());
			}
		});
	}

	// --------------------------------------------------------------------------
	/**
	 * 
	 */
	private void init() {

		// Add paint listener to the canvas
		appWindow.getGui().getDrawingArea().addPaintListener(paintListener);

		/*
		 * mouse button events - are forwarded to plugins
		 */
		appWindow.getGui().getDrawingArea().addMouseListener(mouseListener);

		/*
		 * Add DrawingObjectListeners to all current and future plug-ins (used for knowing when to
		 * update the drawing area)
		 */
		final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
		for (final Plugin p : plugins) {
			if (p instanceof Drawable) {
				p.addDrawingObjectListener(drawingObjectListener);
			}
		}
		spyglass.getPluginManager().addPluginListChangeListener(pluginListChangeListener);

		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().addPropertyChangeListener(rulerPropertyListener);

		appWindow.getGui().getDrawingArea().addPaintListener(paintRulerListener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Draw all drawing objects inside the bounding box <code>area</code> from the plugin
	 * <code>plugin</code> on <code>gc</code>.
	 * 
	 * @param gc
	 *            a GC
	 * @param plugin
	 *            a Drawable Plugin
	 * @param area
	 *            Only drawing objects inside this area will be redrawn.
	 */
	private void renderPlugin(final GC gc, final Drawable plugin, final AbsoluteRectangle area) {

		final List<DrawingObject> dos = new LinkedList<DrawingObject>(plugin.getDrawingObjects(area));
		if (dos != null) {
			for (final DrawingObject object : dos) {
				object.draw(appWindow.getGui().getDrawingArea(), gc);
			}
		} else {
			log.error("The plugin " + plugin + " did not provide any drawing objects!");
		}
	}

	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public AppWindow getAppWindow() {
		return appWindow;
	}

	// ----------------------------------------------------------------------------

	private MouseListener mouseListener = new MouseAdapter() {

		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			if (e.button == 1) {
				eventDispatcher.handleEvent(e);
			}
		}

		@Override
		public void mouseDown(final MouseEvent e) {
			if (e.button > 1) {
				eventDispatcher.handleEvent(e);
			}
		}

	};

	private PluginListChangeListener pluginListChangeListener = new PluginListChangeListener() {

		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			if (p instanceof Drawable) {

				switch (what) {
					case NEW_PLUGIN:
						p.addDrawingObjectListener(drawingObjectListener);
						p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);
						break;
					case PLUGIN_REMOVED:
						p.removeDrawingObjectListener(drawingObjectListener);
						p.getXMLConfig().removePropertyChangeListener(pluginPropertyListener);
						break;
				}
			}
		}
	};

	private DrawingObjectListener drawingObjectListener = new DrawingObjectListener() {

		@Override
		public void drawingObjectAdded(final DrawingObject dob) {

			// Redrawing the canvas must happen from the SWT display thread
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					final AbsoluteRectangle absBBox = dob.getBoundingBox();
					final PixelRectangle pxBBox = getAppWindow().getGui().getDrawingArea().absRect2PixelRect(absBBox);

					redraw(pxBBox);
				}
			});

		}

		@Override
		public void drawingObjectChanged(final DrawingObject dob, final AbsoluteRectangle oldBoundingBox) {

			// Redrawing the canvas must happen from the SWT display thread
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					// the old area of the drawing object
					if (oldBoundingBox != null) {
						final PixelRectangle pxBBoxOld = getAppWindow().getGui().getDrawingArea().absRect2PixelRect(oldBoundingBox);
						redraw(pxBBoxOld);
					}
					// the new area of the drawing object
					final AbsoluteRectangle absBBox = dob.getBoundingBox();
					final PixelRectangle pxBBox = getAppWindow().getGui().getDrawingArea().absRect2PixelRect(absBBox);

					redraw(pxBBox);

				}
			});

		}

		@Override
		public void drawingObjectRemoved(final DrawingObject dob) {

			// Redrawing the canvas must happen from the SWT display thread
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					final AbsoluteRectangle absBBox = dob.getBoundingBox();
					final PixelRectangle pxBBox = getAppWindow().getGui().getDrawingArea().absRect2PixelRect(absBBox);

					redraw(pxBBox);
				}
			});

		}

		private void redraw(final PixelRectangle pxBBox) {
			appWindow.getGui().getDrawingArea()
					.redraw(pxBBox.getUpperLeft().x, pxBBox.getUpperLeft().y, pxBBox.getWidth(), pxBBox.getHeight(), false);
		}

	};

	/**
	 * Renders the visible plug-in's.<br>
	 * The plug-ins provide objects which are drawn into the drawing area.
	 * 
	 * @param gc
	 *            the graphic context used to actually draw the provided objects
	 * @see DrawingObject
	 */
	private PaintListener paintListener = new PaintListener() {

		@Override
		public void paintControl(final PaintEvent e) {

			final PixelRectangle pxArea = new PixelRectangle(e.gc.getClipping().x, e.gc.getClipping().y, e.gc.getClipping().width,
					e.gc.getClipping().height);

			final AbsoluteRectangle area = appWindow.getGui().getDrawingArea().pixelRect2AbsRect(pxArea);

			final List<Plugin> plugins = spyglass.getPluginManager().getVisibleActivePlugins();

			for (final Plugin plugin : plugins) {
				if (plugin instanceof Drawable) {
					renderPlugin(e.gc, (Drawable) plugin, area);
				}
			}

		}
	};

	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the configuration of an plug-in.<br>
	 */
	PropertyChangeListener pluginPropertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			// if the config of any plugin changes, better redraw the entire screen.
			// TODO: in a perfect world this should be unneseccary, since the plugins would
			// have done this themselves.
			appWindow.getGui().getDrawingArea().redraw();
		}
	};

	// ----------------------------------------------------------------
	/**
	 * Listener for change of visibility of ruler
	 */

	PropertyChangeListener rulerPropertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			appWindow.getGui().changeRulerVis();

		}
	};

	/**
	 * Renders the visible plug-in's.<br>
	 * The plug-ins provide objects which are drawn into the drawing area.
	 * 
	 * @param gc
	 *            the graphic context used to actually draw the provided objects
	 * @see DrawingObject
	 */
	private PaintListener paintRulerListener = new PaintListener() {

		@Override
		public void paintControl(final PaintEvent e) {

			final String unit = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics().getUnit();

			final Point2D upperLeft = appWindow.getGui().getDrawingArea().getUpperLeftPrecise();
			final Point2D lowerRight = appWindow.getGui().getDrawingArea().getLowerRightPrecise();
			final PixelRectangle pxRect = appWindow.getGui().getDrawingArea().getDrawingRectangle();

			GC gc = new GC(appWindow.getGui().getRulerH());
			appWindow.getGui().getRulerH().drawRuler(pxRect, upperLeft, lowerRight, gc, RulerArea.HORIZONTAL);
			gc.dispose();

			gc = new GC(appWindow.getGui().getRulerV());
			appWindow.getGui().getRulerV().drawRuler(pxRect, upperLeft, lowerRight, gc, RulerArea.VERTICAL);
			gc.dispose();

			gc = new GC(appWindow.getGui().getUnitArea());
			appWindow.getGui().getUnitArea().drawUnit(gc, unit);
			gc.dispose();

		}
	};

}
