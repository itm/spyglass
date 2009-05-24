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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.EventDispatcher;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.RulerArea;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedListener;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent.Type;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
/**
 * The UI controller is the interface between the core Spyglass functionality and the graphical user
 * interface.
 */
public class UIController {

	protected static Logger log = SpyglassLoggerFactory.getLogger(UIController.class);

	private final static boolean ENABLE_DRAW_PROFILING = false;

	/** Counts the number of open preference dialogs */
	private static final AtomicInteger openPrefDialogs = new AtomicInteger(0);

	/** Default number of milliseconds to wait between checking for new boundingBox changes */
	private static final int DEFAULT_REDRAW_PERIOD = 100;

	/** Current number of milliseconds to wait between checking for new boundingBox changes */
	protected static int currentRedrawPeriod = DEFAULT_REDRAW_PERIOD;

	protected final AppWindow appWindow;

	protected final Spyglass spyglass;

	protected final Display display;

	/** User events will be dispatched here */
	protected EventDispatcher eventDispatcher;

	/**
	 * List of pluginControllers. Each controller is responsible for handling the drawingobjects
	 * of one plugin.
	 */
	protected final Map<Plugin,PluginController> pluginControllers = Collections.synchronizedMap(new HashMap<Plugin,PluginController>());

	// --------------------------------------------------------------------------
	/**
	 * Constructor
	 *
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 * @param appWindow
	 *            the application's GUI manager
	 *
	 */
	public UIController(final Spyglass spyglass, final AppWindow appWindow) {
		if ((spyglass == null) || (appWindow == null)) {
			throw new NullPointerException("spyglass and appWindow must not be null!");
		}

		this.spyglass = spyglass;
		this.appWindow = appWindow;
		display = appWindow.getDisplay();

		init();

	}

	// --------------------------------------------------------------------------
	/**
	 * Initializes the object
	 */
	private void init() {

		eventDispatcher = new EventDispatcher(spyglass.getPluginManager(), appWindow.getGui().getDrawingArea());

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener("pluginManager", spyglassConfigPluginManagerChangeListener);

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

		registerPluginManager(spyglass.getPluginManager());

		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().addPropertyChangeListener(rulerPropertyListener);

		display.timerExec(currentRedrawPeriod, new Runnable() {

			@Override
			public void run() {
				updateBoundingBoxes();
				display.timerExec(currentRedrawPeriod, this);
			}

		});

		log.info("UI init done.");

	}

	// --------------------------------------------------------------------------
	/**
	 * Must be called during shutdown. Should be called before model and view are destroyed.
	 *
	 * Removes all existing listeners.
	 */
	public void shutdown() {
		// Note: We don't have to unregister listeners to Widgets, since they
		// are automatically removed when the widget is disposed

		unregisterPluginManager(spyglass.getPluginManager());

		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().removePropertyChangeListener(rulerPropertyListener);

		log.info("UI shut down.");
	}

	// --------------------------------------------------------------------------------
	/**
	 * Draw all drawing objects inside the bounding box <code>area</code> from the plug-in
	 * <code>plug-in</code> on <code>gc</code>.
	 *
	 * @param gc
	 *            a GC
	 * @param plugin
	 *            a plug-in which is capable of drawing objects
	 * @param area
	 *            Only drawing objects inside this area will be redrawn.
	 * @see Drawable
	 */
	protected void renderPlugin(final GC gc, final Drawable plugin, final AbsoluteRectangle area) {

		if (ENABLE_DRAW_PROFILING) {
			log.debug("Rendering plugin "+plugin+" on "+area);
		}

		final List<DrawingObject> dos = new LinkedList<DrawingObject>(plugin.getDrawingObjects(area));
		for (final DrawingObject object : dos) {

			switch (object.getState()) {
				case ALIVE:
					if (ENABLE_DRAW_PROFILING) {
						log.debug("Rendering DOB "+object);
					}
					object.drawObject(gc);
					break;
				case INFANT:
					log.debug(String.format("Plugin %s contains an unitialized drawing object in its layer: %s (skipping it)", plugin, object));
					break;
				case ZOMBIE:
					log.error(String.format("Plugin %s contains a zombie drawing object in its layer: %s", plugin, object));
			}
		}
	}

	/**
	 * Update the bounding boxes of all plugins
	 */
	protected void updateBoundingBoxes() {
		final Collection<PluginController> list;
		synchronized (pluginControllers) {
			list = pluginControllers.values();
		}
		for (final PluginController pc : list) {
			pc.updateBoundingBoxes();
		}
	}

	/**
	 * Registers Listener to the PluginManager and creats PluginControllers for Plugins
	 * inside the PluginManager.
	 *
	 * Note that at this point we assume that the PluginManager has already fully initialized
	 * all his Plugins (i.e. it is in a "running" state).
	 */
	protected void registerPluginManager(final PluginManager manager) {

		for (final Plugin p : manager.getPlugins()) {

			// sanity check
			if (p.getState() != Plugin.State.ALIVE) {
				throw new IllegalArgumentException("Plugin is not alive!");
			}

			if (p instanceof Drawable) {

				final PluginController c = new PluginController(display, appWindow.getGui().getDrawingArea(),p);
				pluginControllers.put(p,c);

			}
		}

		spyglass.getPluginManager().addPluginListChangeListener(pluginListChangeListener);
	}

	protected void unregisterPluginManager(final PluginManager manager) {
		synchronized (pluginControllers) {
			for (final PluginController pc: pluginControllers.values()) {
				pc.disconnect();
			}
			pluginControllers.clear();
		}

		manager.removePluginListChangeListener(pluginListChangeListener);
	}

	// ----------------------------------------------------------------
	/**
	 * Opens a non-modal dialog window to change the preferences.<br>
	 * Note that the drawing area' frames per second will be decreased for convenient configuration
	 * as long as at least one preference dialog is open.
	 *
	 * @param parentShell
	 *            the parent shell
	 * @param spyglass
	 *            the application's main class
	 */
	public static void openPreferencesDialog(final Shell parentShell, final Spyglass spyglass) {

		// we don't want more than one dialog to be open to avoid unpredictable databinding behaviour
		// (changes don't alway propagate between the windows.)
		if (openPrefDialogs.get()>0) {
			return;
		}

		if (openPrefDialogs.getAndIncrement() == 0) {
			UIController.currentRedrawPeriod = 200;
		}
		new PluginPreferenceDialog(parentShell, spyglass).open();
		if (openPrefDialogs.decrementAndGet() == 0) {
			UIController.currentRedrawPeriod = UIController.DEFAULT_REDRAW_PERIOD;
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Listens for mouse events
	 */
	private final MouseListener mouseListener = new MouseAdapter() {

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

	// --------------------------------------------------------------------------------
	/**
	 * Listens for changes of the plug-in manager's list
	 */
	protected final PluginListChangeListener pluginListChangeListener = new PluginListChangeListener() {

		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			if (p instanceof Drawable) {

				switch (what) {
					case NEW_PLUGIN:

						if (p.getState() != Plugin.State.ALIVE) {
							throw new IllegalArgumentException("Plugin is not alive!");
						}

						final PluginController c = new PluginController(display, appWindow.getGui().getDrawingArea(), p);
						pluginControllers.put(p,c);

						break;
					case PLUGIN_REMOVED:
						if (p.getState() != Plugin.State.ZOMBIE) {
							throw new IllegalArgumentException("Plugin is not dead yet!");
						}

						pluginControllers.remove(p).disconnect();

						break;
					case NEW_NODE_POSITIONER:
						break;
					case PRIORITY_CHANGED:
						break;
				}
			}
		}
	};

	// --------------------------------------------------------------------------------
	/**
	 * Renders the visible plug-ins.<br>
	 * The plug-ins provide objects which are drawn into the drawing area.
	 *
	 * @see DrawingObject
	 */
	private final PaintListener paintListener = new PaintListener() {

		@Override
		public void paintControl(final PaintEvent e) {

			final long time = System.nanoTime();

			final PixelRectangle pxArea = new PixelRectangle(e.gc.getClipping().x, e.gc.getClipping().y, e.gc.getClipping().width,
					e.gc.getClipping().height);

			final AbsoluteRectangle area = appWindow.getGui().getDrawingArea().pixelRect2AbsRect(pxArea);
			//final AbsoluteRectangle area = DrawingArea.getGlobalBoundingBox();

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
					log.debug(String.format("Partial redraw (%.0f px). Time: %.03f ms (%.0f ns per pixel).", pxCount, (time2 - time) / 1000000d,
							((time2 - time) / pxCount)));
				} else {
					log.debug(String.format("Complete redraw. Time: %.03f ms (%.0f ns per pixel).", (time2 - time) / 1000000d,
							((time2 - time) / pxCount)));
				}
			}
		}
	};

	// ----------------------------------------------------------------
	/**
	 * Listener for change of visibility of ruler
	 */
	private final PropertyChangeListener rulerPropertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			appWindow.getGui().changeRulerVis();

		}
	};

	// ----------------------------------------------------------------
	/**
	 * Listener for change of visibility of ruler
	 */
	private final PropertyChangeListener spyglassConfigPluginManagerChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final PluginManager oldManager = (PluginManager) evt.getOldValue();
			unregisterPluginManager(oldManager);

			final PluginManager newManager = (PluginManager) evt.getNewValue();
			registerPluginManager(newManager);

			// the eventDispatcher doesn't register any listeners, so we can just put in the garbage.
			eventDispatcher = new EventDispatcher(newManager, appWindow.getGui().getDrawingArea());

		}
	};

	// ----------------------------------------------------------------
	/**
	 * Listens for ruler paint events
	 */
	private final PaintListener paintRulerListener = new PaintListener() {

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

	// ----------------------------------------------------------------
	/**
	 * Listens for transformation of the drawing area
	 */
	private final TransformChangedListener drawingAreaTransformListener = new TransformChangedListener() {

		@Override
		public void handleEvent(final TransformChangedEvent e) {

			// we are already in the SWT-Thread
			appWindow.getGui().getRulerH().redraw();
			appWindow.getGui().getRulerV().redraw();

			// On ZOOM we have to flush all outstanding bounding box changes. Otherwise
			// the repaint (which will follow soon after this listener finishes)
			// would work with out-of-date bounding boxes.
			if (e.type == Type.ZOOM_MOVE) {
				updateBoundingBoxes();
			}

		}
	};

}
