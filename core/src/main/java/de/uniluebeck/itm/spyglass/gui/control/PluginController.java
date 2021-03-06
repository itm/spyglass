/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.drawing.BoundingBoxChangeListener;
import de.uniluebeck.itm.spyglass.drawing.BoundingBoxIsDirtyListener;
import de.uniluebeck.itm.spyglass.drawing.ContentChangedListener;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject.State;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.DrawingObjectListener;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * This controller deals with handling a plugin and its drawingobjects.
 * 
 * Among other things, it gets notified when a drawingobject's boundingbox gets dirty and is
 * responsible for issuing redraws if a drawingobject is changed.
 * 
 * @author Dariush Forouher
 */
public class PluginController {

	/** The controlled plug-in */
	protected final Plugin plugin;

	/** The display to be used */
	protected final Display display;

	/** The drawing area */
	protected final DrawingArea drawingArea;

	/** List of drawingObjects with out-dated bounding boxes */
	protected final Set<DrawingObject> drawingObjectsWithDirtyBoundingBox = Collections.synchronizedSet(new HashSet<DrawingObject>());

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param display
	 *            the display to be used
	 * @param drawingArea
	 *            the drawing area
	 * @param plugin
	 *            the plug-in to be controlled
	 */
	public PluginController(final Display display, final DrawingArea drawingArea, final Plugin plugin) {
		if (!(plugin instanceof Drawable)) {
			throw new IllegalArgumentException("Plugin must implement Drawable!");
		}

		this.plugin = plugin;
		this.display = display;
		this.drawingArea = drawingArea;

		/*
		 * Add property listener, to listen to visibility/activity changes
		 */
		plugin.getXMLConfig().addPropertyChangeListener(this.pluginPropertyListener);

		/*
		 * Add DrawingObjectListeners to all current and future plug-ins (used for knowing when to
		 * update the drawing area)
		 */
		plugin.addDrawingObjectListener(drawingObjectListener);

		// If a Plugin creates DrawingObjects during its init() method, we won't initialize those
		// drawingobjects since we haven't connected with them yet. Thus we have to do this now.
		for (final DrawingObject dob : ((Drawable) plugin).getDrawingObjects(DrawingArea.getGlobalBoundingBox())) {
			handleDrawingObjectAdded(dob);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Listens for added and removed drawing objects, respectively
	 */
	protected final DrawingObjectListener drawingObjectListener = new DrawingObjectListener() {

		@Override
		public void drawingObjectAdded(final Plugin p, final DrawingObject dob) {

			handleDrawingObjectAdded(dob);

		}

		@Override
		public void drawingObjectRemoved(final Plugin p, final DrawingObject dob) {
			handleDrawingObjectRemoved(dob);
		}

	};

	// --------------------------------------------------------------------------
	/**
	 * Listener for changes in the configuration of an plug-in.<br>
	 */
	protected final PropertyChangeListener pluginPropertyListener = new PropertyChangeListener() {

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

						if (!drawingArea.isDisposed()) {
							drawingArea.redraw();
						}

					}
				});

			}

		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for bounding box changes
	 */
	protected final BoundingBoxChangeListener bboxChangeListener = new BoundingBoxChangeListener() {

		@Override
		public void onBoundingBoxChanged(final DrawingObject updatedDrawingObject, final AbsoluteRectangle oldBox) {
			handleDrawingObjectChanged(oldBox);
		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for content changes of drawing objects
	 */
	protected final ContentChangedListener contentChangeListener = new ContentChangedListener() {

		@Override
		public void onContentChanged(final DrawingObject updatedDrawingObject) {

			// this may be called after the drawingObject has been destroyed to clean up the area
			// on the canvas. Thus don't check for the state of the DrawingObject

			// do the redraw synchronously if possible
			if (Display.getCurrent() != null) {
				handleDrawingObjectChanged(updatedDrawingObject.getBoundingBox());
			} else {

				if (!display.isDisposed()) {
					// Redrawing the canvas must happen from the SWT display thread
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							if (display.isDisposed()) {
								return;
							}

							handleDrawingObjectChanged(updatedDrawingObject.getBoundingBox());
						}
					});
				}
			}
		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Listens for bounding boxes which need to be updated
	 */
	protected final BoundingBoxIsDirtyListener syncListener = new BoundingBoxIsDirtyListener() {

		@Override
		public void syncNeeded(final DrawingObject dob) {
			drawingObjectsWithDirtyBoundingBox.add(dob);
		}

	};

	// --------------------------------------------------------------------------------
	/**
	 * Stuff to do when a new drawing object arrives on the scene.
	 * 
	 * @param dob
	 *            a drawing object
	 */
	protected void handleDrawingObjectAdded(final DrawingObject dob) {

		// log.warn("Added DrawingObject "+dob, new Exception());

		if (dob.getState() != State.INFANT) {
			throw new RuntimeException("Can only add fresh new DrawingObjects!");
		}

		dob.addBoundingBoxChangedListener(bboxChangeListener);
		dob.addContentChangedListener(contentChangeListener);

		// needed to synchronize the bounding box of the drawingObject if the objects wishes so.
		dob.addBoundingBoxIsDirtyListener(syncListener);

		dob.init(drawingArea);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Handles a change of a drawing object's parameter(s)
	 * 
	 * @param boundingBox
	 *            a bounding box
	 */
	protected void handleDrawingObjectChanged(final AbsoluteRectangle boundingBox) {

		// the drawing area might have been disposed while we were waiting
		if (drawingArea.isDisposed()) {
			return;
		}

		if (plugin.isActive() && plugin.isVisible()) {

			// the new area of the drawing object
			final PixelRectangle pxBBox = drawingArea.absRect2PixelRect(boundingBox);

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
	protected void handleDrawingObjectRemoved(final DrawingObject dob) {

		// log.warn("Removed DrawingObject "+dob, new Exception());

		if (dob.getState() != State.ALIVE) {
			throw new RuntimeException("Can only remove alive DrawingObjects!");
		}

		dob.destroy();

		// remove all the listener we have registered before...
		dob.removeContentChangeListener(contentChangeListener);
		dob.removeBoundingBoxChangeListener(bboxChangeListener);
		dob.removeBoundingBoxIsDirtyListener(syncListener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Redraws a certain part of the drawing area
	 * 
	 * @param pxBBox
	 *            the part of the drawing area to be redrawn
	 */
	protected void redraw(final PixelRectangle pxBBox) {
		drawingArea.redraw(pxBBox.getUpperLeft().x, pxBBox.getUpperLeft().y, pxBBox.getWidth(), pxBBox.getHeight(), false);
	}

	/**
	 * Disconnects all listeners from the plugin.
	 */
	public void disconnect() {
		plugin.removeDrawingObjectListener(drawingObjectListener);
		plugin.getXMLConfig().removePropertyChangeListener(pluginPropertyListener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the bounding boxes of all drawing objects which are currently marked as dirty
	 */
	public void updateBoundingBoxes() {

		// get a list of all drawingObjects from active and visible plug-ins which are in need of a
		// new bounding box
		final List<DrawingObject> list = new ArrayList<DrawingObject>();
		synchronized (drawingObjectsWithDirtyBoundingBox) {
			final Iterator<DrawingObject> it = drawingObjectsWithDirtyBoundingBox.iterator();
			while (it.hasNext()) {
				final DrawingObject next = it.next();
				if (plugin.isActive() && plugin.isVisible()) {
					list.add(next);
					it.remove();
				}
			}
		}

		for (final DrawingObject dob : list) {
			dob.syncBoundingBox();
		}
	}
}
