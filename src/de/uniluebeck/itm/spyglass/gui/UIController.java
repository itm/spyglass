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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener;
import de.uniluebeck.itm.spyglass.drawing.BoundingBoxIsDirtyListener;
import de.uniluebeck.itm.spyglass.drawing.ContentChangedListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject.State;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.gui.view.RulerArea;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedListener;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent.Type;
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
/**
 * The UI controller is the interface between the core Spyglass functionality and the graphical user
 * interface. It is bound to a specific GUI library. If the GUI must be completely replaced, the UI
 * controller must also be changed/replaced.
 */
public class UIController {

	private static Logger log = SpyglassLoggerFactory.getLogger(UIController.class);

	private final static boolean ENABLE_DRAW_PROFILING = true;

	/** Counts the number of open preference dialogs */
	private static final AtomicInteger openPrefDialogs = new AtomicInteger(0);

	/** Default number of milliseconds to wait between checking for new boundingBox changes */
	private static final int DEFAULT_REDRAW_PERIOD = 100;

	/** Current number of milliseconds to wait between checking for new boundingBox changes */
	private static int currentRedrawPeriod = DEFAULT_REDRAW_PERIOD;

	private AppWindow appWindow = null;

	private Spyglass spyglass = null;

	private final Display display;

	/** User events will be dispatched here */
	private EventDispatcher eventDispatcher;

	/** List of drawingObjects with outdated bounding boxes */
	private Set<DrawingObject> drawingObjectsWithDirtyBoundingBox = Collections.synchronizedSet(new HashSet<DrawingObject>());

	/** Maps each drawingObject to a plug-in */
	private Map<DrawingObject, Plugin> drawingObjectMap = Collections.synchronizedMap(new HashMap<DrawingObject, Plugin>());

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
			throw new IllegalArgumentException();
		}
		this.spyglass = spyglass;
		this.appWindow = appWindow;
		this.eventDispatcher = new EventDispatcher(spyglass.getPluginManager(), appWindow.getGui().getDrawingArea());

		display = appWindow.getDisplay();

		init();

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener("pluginManager", new PropertyChangeListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				// TODO: what about releasing the old listener?

				/*
				 * Add DrawingObjectListeners to all current and future plug-ins (used for knowing
				 * when to update the drawing area)
				 */
				final List<Plugin> plugins = ((PluginManager) evt.getNewValue()).getPlugins();
				for (final Plugin p : plugins) {
					// sanity check
					if (p.getState() != Plugin.State.ALIVE) {
						throw new IllegalArgumentException("Plugin is not alive!");
					}

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
	 * Initializes the object
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

			// sanity check
			if (p.getState() != Plugin.State.ALIVE) {
				throw new IllegalArgumentException("Plugin is not alive!");
			}

			if (p instanceof Drawable) {

				/*
				 * Add property listener, to listen to visibility/acitivty changes
				 */
				p.getXMLConfig().addPropertyChangeListener(this.pluginPropertyListener);

				/*
				 * Add DrawingObjectListeners to all current and future plug-ins (used for knowing
				 * when to update the drawing area)
				 */
				p.addDrawingObjectListener(drawingObjectListener);

				// handle all drawing objects that already exist
				for (final DrawingObject dob : ((Drawable) p).getDrawingObjects(DrawingArea.getGlobalBoundingBox())) {
					handleDrawingObjectAdded(p, dob);
				}

			}
		}
		spyglass.getPluginManager().addPluginListChangeListener(pluginListChangeListener);

		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().addPropertyChangeListener(rulerPropertyListener);

		display.timerExec(currentRedrawPeriod, new Runnable() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				updateBoundingBoxes();
				display.timerExec(currentRedrawPeriod, this);
			}

		});

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
	/**
	 * Returns the application's window
	 * 
	 * @return the application's window
	 */
	private AppWindow getAppWindow() {
		return appWindow;
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
	private void renderPlugin(final GC gc, final Drawable plugin, final AbsoluteRectangle area) {

		final List<DrawingObject> dos = new LinkedList<DrawingObject>(plugin.getDrawingObjects(area));
		for (final DrawingObject object : dos) {

			switch (object.getState()) {
				case ALIVE:
					object.drawObject(appWindow.getGui().getDrawingArea(), gc);
					break;
				case INFANT:
					log.debug(String.format("Plugin %s contains an unitialized drawing object in its layer: %s (skipping it)", plugin, object));
					break;
				case ZOMBIE:
					log.error(String.format("Plugin %s contains a zombie drawing object in its layer: %s", plugin, object));
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Stuff to do when a new drawing object arrives on the scene.
	 */
	private void handleDrawingObjectAdded(final Plugin p, final DrawingObject dob) {

		if (dob.getState() != State.INFANT) {
			throw new RuntimeException("Can only add fresh new DrawingObjects!");
		}

		final DrawingArea da = getAppWindow().getGui().getDrawingArea();

		drawingObjectMap.put(dob, p);

		dob.addBoundingBoxChangedListener(bboxChangeListener);
		dob.addContentChangedListener(contentChangeListener);

		// needed to synchronize the bounding box of the drawingObject if the objects wishes so.
		dob.addBoundingBoxIsDirtyListener(syncListener);

		dob.init(da);
	}

	// --------------------------------------------------------------------------------
	private void handleDrawingObjectChanged(final Plugin p, final AbsoluteRectangle boundingBox) {
		final DrawingArea da = getAppWindow().getGui().getDrawingArea();

		// the drawing area might have been disposed while we were waiting
		if (da.isDisposed()) {
			return;
		}

		if ((p == null) || (p.isActive() && p.isVisible())) {

			// the new area of the drawing object
			final PixelRectangle pxBBox = da.absRect2PixelRect(boundingBox);

			redraw(pxBBox);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Handles an event which will occur if a drawing object is removed.
	 * 
	 * @param dob
	 *            the drawing object to be removed
	 * @exception RuntimeException
	 *                thrown if the drawing objects state is not {@link DrawingObject.State#ALIVE}
	 */
	private void handleDrawingObjectRemoved(final DrawingObject dob) {

		if (dob.getState() != State.ALIVE) {
			throw new RuntimeException("Can only remove alive DrawingObjects!");
		}

		dob.destroy();

		// remove all the listener we have registered before...
		dob.removeContentChangeListener(contentChangeListener);
		dob.removeBoundingBoxChangeListener(bboxChangeListener);
		dob.removeBoundingBoxIsDirtyListener(syncListener);
		drawingObjectMap.remove(dob);

	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the bounding boxes of all drawing objects which are currently marked as dirty
	 */
	private void updateBoundingBoxes() {

		// get a list of all drawingObjects from active and visible plug-ins which are in need of a
		// new bounding box
		final List<DrawingObject> list = new ArrayList<DrawingObject>();
		synchronized (drawingObjectsWithDirtyBoundingBox) {
			final Iterator<DrawingObject> it = drawingObjectsWithDirtyBoundingBox.iterator();
			while (it.hasNext()) {
				final DrawingObject next = it.next();
				final Plugin p = this.drawingObjectMap.get(next);
				if ((p != null) && p.isActive() && p.isVisible()) {
					list.add(next);
					it.remove();
				}
			}
		}

		for (final DrawingObject dob : list) {
			dob.syncBoundingBox();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Redraws a certain part of the drawing area
	 * 
	 * @param pxBBox
	 *            the part of the drawing area to be redrawn
	 */
	private void redraw(final PixelRectangle pxBBox) {
		appWindow.getGui().getDrawingArea().redraw(pxBBox.getUpperLeft().x, pxBBox.getUpperLeft().y, pxBBox.getWidth(), pxBBox.getHeight(), false);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Listens for bounding boxes which need to be updated
	 */
	private final BoundingBoxIsDirtyListener syncListener = new BoundingBoxIsDirtyListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void syncNeeded(final DrawingObject dob) {
			drawingObjectsWithDirtyBoundingBox.add(dob);
		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for content changes of drawing objects
	 */
	private final ContentChangedListener contentChangeListener = new ContentChangedListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void onContentChanged(final DrawingObject updatedDrawingObject) {

			// this may be called after the drawingObject has been destroyed to clean up the area
			// on the canvas. Thus don't check for the state of the DrawingObject

			// do the redraw synchronously if possible
			if (Display.getCurrent() != null) {
				handleDrawingObjectChanged(drawingObjectMap.get(updatedDrawingObject), updatedDrawingObject.getBoundingBox());
			} else {

				// Redrawing the canvas must happen from the SWT display thread
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						if (display.isDisposed()) {
							return;
						}

						handleDrawingObjectChanged(drawingObjectMap.get(updatedDrawingObject), updatedDrawingObject.getBoundingBox());
					}
				});
			}
		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for bounding box changes
	 */
	private final BoundingBoxChangeListener bboxChangeListener = new BoundingBoxChangeListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void onBoundingBoxChanged(final DrawingObject updatedDrawingObject, final AbsoluteRectangle oldBox) {
			handleDrawingObjectChanged(drawingObjectMap.get(updatedDrawingObject), oldBox);
		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for mouse events
	 */
	private final MouseListener mouseListener = new MouseAdapter() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			if (e.button == 1) {
				eventDispatcher.handleEvent(e);
			}
		}

		@SuppressWarnings("synthetic-access")
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
	private final PluginListChangeListener pluginListChangeListener = new PluginListChangeListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
			if (p instanceof Drawable) {

				switch (what) {
					case NEW_PLUGIN:

						if (p.getState() != Plugin.State.ALIVE) {
							throw new IllegalArgumentException("Plugin is not alive!");
						}

						p.addDrawingObjectListener(drawingObjectListener);
						p.getXMLConfig().addPropertyChangeListener(pluginPropertyListener);

						// handle all drawing objects that already exist
						for (final DrawingObject dob : ((Drawable) p).getDrawingObjects(DrawingArea.getGlobalBoundingBox())) {
							handleDrawingObjectAdded(p, dob);
						}

						break;
					case PLUGIN_REMOVED:
						if (p.getState() != Plugin.State.ZOMBIE) {
							throw new IllegalArgumentException("Plugin is not dead yet!");
						}

						// hopefully the plug-in has already shut down at this point
						p.removeDrawingObjectListener(drawingObjectListener);
						p.getXMLConfig().removePropertyChangeListener(pluginPropertyListener);
						break;
				}
			}
		}
	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for added and removed drawing objects, respectively
	 */
	private final DrawingObjectListener drawingObjectListener = new DrawingObjectListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void drawingObjectAdded(final Plugin p, final DrawingObject dob) {

			handleDrawingObjectAdded(p, dob);

		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void drawingObjectRemoved(final Plugin p, final DrawingObject dob) {
			handleDrawingObjectRemoved(dob);
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

		@SuppressWarnings( { "synthetic-access" })
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
					log.debug(String.format("Partial redraw (%.0f px). Time: %.03f ms (%.0f ns per pixel).", pxCount, (time2 - time) / 1000000d,
							((time2 - time) / pxCount)));
				} else {
					log.debug(String.format("Complete redraw. Time: %.03f ms (%.0f ns per pixel).", (time2 - time) / 1000000d,
							((time2 - time) / pxCount)));
				}
			}
		}
	};

	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the configuration of an plug-in.<br>
	 */
	private final PropertyChangeListener pluginPropertyListener = new PropertyChangeListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			/**
			 * Redraw the entire screen if visibility or activity of a plug-in changes.
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
	private final PropertyChangeListener rulerPropertyListener = new PropertyChangeListener() {

		@SuppressWarnings("synthetic-access")
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			appWindow.getGui().changeRulerVis();

		}
	};

	// ----------------------------------------------------------------
	/**
	 * Listens for ruler paint events
	 */
	private final PaintListener paintRulerListener = new PaintListener() {

		@SuppressWarnings("synthetic-access")
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

		@SuppressWarnings("synthetic-access")
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
		if (openPrefDialogs.getAndIncrement() == 0) {
			UIController.currentRedrawPeriod = 200;
		}
		new PluginPreferenceDialog(parentShell, spyglass).open();
		if (openPrefDialogs.decrementAndGet() == 0) {
			UIController.currentRedrawPeriod = UIController.DEFAULT_REDRAW_PERIOD;
		}
	}

}
