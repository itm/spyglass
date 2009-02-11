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
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.gui.view.DrawingAreaTransformEvent;
import de.uniluebeck.itm.spyglass.gui.view.DrawingAreaTransformListener;
import de.uniluebeck.itm.spyglass.gui.view.RulerArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.DrawingObjectListener;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

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
	
	private final static boolean ENABLE_DRAW_PROFILING = false;

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

				// TODO: what about releasing the old listener?
				
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

		// EventHandler for the rulers
		appWindow.getGui().getDrawingArea().addDrawingAreaTransformListener(drawingAreaTransformListener);
		appWindow.getGui().getRulerH().addPaintListener(paintRulerListener);
		appWindow.getGui().getRulerV().addPaintListener(paintRulerListener);
		appWindow.getGui().getUnitArea().addPaintListener(paintRulerListener);

		/*
		 * mouse button events - are forwarded to plugins
		 */
		appWindow.getGui().getDrawingArea().addMouseListener(mouseListener);

		final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
		for (final Plugin p : plugins) {
			if (p instanceof Drawable) {

				/*
				 * Add property listener, to listen to visibility/acitivty changes
				 */
				p.getXMLConfig().addPropertyChangeListener(this.pluginPropertyListener);

				/*
				 * Add DrawingObjectListeners to all current and future plug-ins (used for knowing when to
				 * update the drawing area)
				 */
				p.addDrawingObjectListener(drawingObjectListener);
				
				// handle all drawingobjects that already exist
				for(final DrawingObject dob: ((Drawable)p).getDrawingObjects(DrawingArea.getGlobalBoundingBox())) {
					handleDrawingObjectAdded(dob);
				}
				
			}
		}
		spyglass.getPluginManager().addPluginListChangeListener(pluginListChangeListener);

		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().addPropertyChangeListener(rulerPropertyListener);

	}

	/**
	 * Must be called during shutdown. Should be called before model and view are destroyed.
	 * 
	 * Removes all existing listeners.
	 */
	public void shutdown() {
		// Note: We don't have to unregister listeners to Widgets, since they
		// are automatically removed when the widget is disposed
		
		final List<Plugin> plugins = spyglass.getPluginManager().getPlugins();
		for (final Plugin p : plugins) {
			if (p instanceof Drawable) {
				p.removeDrawingObjectListener(drawingObjectListener);
			}
		}
		spyglass.getPluginManager().removePluginListChangeListener(pluginListChangeListener);
		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().removePropertyChangeListener(rulerPropertyListener);
		
		log.debug("UIController shut down.");
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	private AppWindow getAppWindow() {
		return appWindow;
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
		for (final DrawingObject object : dos) {
			
			switch (object.getState()) {
				case ALIVE:
					object.draw(appWindow.getGui().getDrawingArea(), gc);
					break;
				case INFANT:
					log.debug(String.format("Plugin %s contains an unitialized drawing object in its layer: %s (skipping it)", plugin, object));
					break;
				case ZOMBIE:
					log.warn(String.format("Plugin %s contains a zombie drawing object in its layer: %s", plugin, object));
			}
		}
	}

	private void handleDrawingObjectAdded(final DrawingObject dob) {
		final DrawingArea da = getAppWindow().getGui().getDrawingArea();
	
		// the drawingarea might have been disposed while we were waiting
		if (da.isDisposed()) {
			return;
		}
		
		dob.init(da);
		
		final AbsoluteRectangle absBBox = dob.getBoundingBox();
		final PixelRectangle pxBBox = da.absRect2PixelRect(absBBox);
	
		redraw(pxBBox);
	}

	private void handleDrawingObjectChanged(final DrawingObject dob, final AbsoluteRectangle oldBoundingBox) {
		final DrawingArea da = getAppWindow().getGui().getDrawingArea();
	
		// the drawingarea might have been disposed while we were waiting
		if (da.isDisposed()) {
			return;
		}
		
		// the old area of the drawing object
		if (oldBoundingBox != null) {
			final PixelRectangle pxBBoxOld = da.absRect2PixelRect(oldBoundingBox);
			redraw(pxBBoxOld);
		}
		// the new area of the drawing object
		final AbsoluteRectangle absBBox = dob.getBoundingBox();
		final PixelRectangle pxBBox = da.absRect2PixelRect(absBBox);
	
		redraw(pxBBox);
	}

	private void handleDrawingObjectRemoved(final DrawingObject dob) {
		final DrawingArea da = getAppWindow().getGui().getDrawingArea();
	
		// the drawingarea might have been disposed while we were waiting
		if (da.isDisposed()) {
			return;
		}
		
		dob.destroy();
	
		final AbsoluteRectangle absBBox = dob.getBoundingBox();
		final PixelRectangle pxBBox = da.absRect2PixelRect(absBBox);
	
		redraw(pxBBox);
	}

	private void redraw(final PixelRectangle pxBBox) {
		appWindow.getGui().getDrawingArea()
				.redraw(pxBBox.getUpperLeft().x, pxBBox.getUpperLeft().y, pxBBox.getWidth(), pxBBox.getHeight(), false);
	}

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

			log.debug("Redraw caused by "+dob);
			
			// Redrawing the canvas must happen from the SWT display thread
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					handleDrawingObjectAdded(dob);
				}
			});

		}

		@Override
		public void drawingObjectChanged(final DrawingObject dob, final AbsoluteRectangle oldBoundingBox) {

			log.debug("Redraw caused by "+dob);

			// Redrawing the canvas must happen from the SWT display thread
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					handleDrawingObjectChanged(dob, oldBoundingBox);

				}
			});

		}

		@Override
		public void drawingObjectRemoved(final DrawingObject dob) {

			log.debug("Redraw caused by "+dob);
			
			// Redrawing the canvas must happen from the SWT display thread
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					handleDrawingObjectRemoved(dob);
				}
			});

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

			final long time = System.nanoTime();
			
			final PixelRectangle pxArea = new PixelRectangle(e.gc.getClipping().x, e.gc.getClipping().y, e.gc.getClipping().width,
					e.gc.getClipping().height);

			final AbsoluteRectangle area = appWindow.getGui().getDrawingArea().pixelRect2AbsRect(pxArea);

			final List<Plugin> plugins = spyglass.getPluginManager().getVisibleActivePlugins();

			for (final Plugin plugin : plugins) {
				if (plugin instanceof Drawable) {
					renderPlugin(e.gc, (Drawable) plugin, area);
				}
			}

			if (ENABLE_DRAW_PROFILING) {
				final long time2 = System.nanoTime();
				final double pxCount = pxArea.getHeight() * pxArea.getWidth();
				final boolean clipping = !appWindow.getGui().getDrawingArea().getClientArea().equals(pxArea.rectangle);
				if (clipping) {
					log.warn( String.format("Partial redraw (%.0f px). Time: %.03f ms (%.0f ns per pixel).",pxCount, (time2-time)/1000000d,((time2-time)/pxCount)));
				} else {
					log.warn( String.format("Complete redraw. Time: %.03f ms (%.0f ns per pixel).",(time2-time)/1000000d,((time2-time)/pxCount)));
				}
			}
		}
	};

	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the configuration of an plug-in.<br>
	 */
	private PropertyChangeListener pluginPropertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			/**
			 * Redraw the entire screen if visibilty or activity of a plugin
			 * changes.
			 */
			if (evt.getPropertyName().equalsIgnoreCase(PluginXMLConfig.PROPERTYNAME_ACTIVE)
					|| evt.getPropertyName().equalsIgnoreCase(PluginXMLConfig.PROPERTYNAME_VISIBLE)) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						if (!appWindow.getGui().getDrawingArea().isDisposed()) {
							appWindow.getGui().getDrawingArea().redraw();
						}

					}
				});
					
			}

				
		}
			
	};

	// ----------------------------------------------------------------
	/**
	 * Listener for change of visibility of ruler
	 */

	private PropertyChangeListener rulerPropertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			appWindow.getGui().changeRulerVis();

		}
	};
	
	private PaintListener paintRulerListener = new PaintListener() {

		@Override
		public void paintControl(final PaintEvent e) {

			final Point2D upperLeft = appWindow.getGui().getDrawingArea().getUpperLeftPrecise();
			final Point2D lowerRight = appWindow.getGui().getDrawingArea().getLowerRightPrecise();
			final PixelRectangle pxRect = appWindow.getGui().getDrawingArea().getDrawingRectangle();

			// this paintListener is used for all three areas. so we have to check
			// which one we're supposed to redraw...
			if (e.widget == appWindow.getGui().getRulerH()) {
				appWindow.getGui().getRulerH().drawRuler(pxRect, upperLeft, lowerRight, e.gc, RulerArea.HORIZONTAL);
			} else if (e.widget == appWindow.getGui().getRulerV()) {
				appWindow.getGui().getRulerV().drawRuler(pxRect, upperLeft, lowerRight, e.gc, RulerArea.VERTICAL);
			} else {
				final String unit = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics().getUnit();
				appWindow.getGui().getUnitArea().drawUnit(e.gc, unit);
			}
		}
	};
	
	/**
	 *  
	 */
	private DrawingAreaTransformListener drawingAreaTransformListener = new DrawingAreaTransformListener() {

		@Override
		public void handleEvent(final DrawingAreaTransformEvent e) {
			
			// we are already in the SWT-Thread
			appWindow.getGui().getRulerH().redraw();
			appWindow.getGui().getRulerV().redraw();
			
		}
	};
	
	

}
