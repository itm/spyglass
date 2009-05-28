/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.gui.control.PluginController;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Abstract class that represents a drawing object.
 *
 * <p>A drawing object has a lifetime, bounded by the calls to {@link DrawingObject#init(DrawingArea)}
 * and {@link DrawingObject#destroy()}, respectively. During this time, the drawingObject is bounded
 * to a drawingArea. A drawingObject can have only one life, i.e. once it has been destroyed, it cannot
 * be initialized again.</p>
 *
 * <p>All DrawingObjects are guaranteed to be thread-safe. (Implementors of subclasses should make sure
 * of this too!)</p>
 *
 * <p>Every drawing object has a bounding box. The bounding box is a rectangle describing the boundaries
 * of the object in absolute coordinates. The bounding box is used to decide whether a drawing object
 * is inside the part of the screen which needs to be repainted at given time. Because of this, it
 * is of vital importance that the bounding box is at all times in sync with the "image", which will be
 * drawn by from {@link #drawObject(GC)}.</p>
 *
 * <p>The bounding box of the drawing object may change over time, e.g. if the position of the drawing
 * object is changed. Because of this, there is the dilemma when to update the bounding box to bring
 * it back to sync with the content of the drawing object.  Unfortunately calculating the bounding box
 * may require access to the SWT thread, so we may run into deadlocking issues if we tried to update
 * the bounding box synchronously every time some property (e.g. the position) of the drawing object
 * has been modified.</p>
 *
 * <p>So to avoid deadlocks we have to recalculate the bounding box asynchronously. This is done by marking
 * the bounding box dirty whenever it needs to be updated and then sending a signal to the PluginController
 * to recalculate the bounding box at the next opportunity.</p>
 *
 * <p>But now that the bounding box is updated asynchronously the bounding box may become out of sync
 * for periods of time with the "image" that ends up to be drawn on the screen by {@link #drawObject(GC)}.
 * How can we avoid that? By keeping a "shadow copy" of the drawing object which is used for actual
 * painting. This works like this: We always keep a copy ourself (a {@link #clone()} of the drawing object) held
 * back in {@link #shadowCopy}. Usually this shadowCopy is always kept in sync with the master object (see
 * {@link #fireContentChangedEvent()}). All drawing operations are not done on the master object, but
 * in fact on this shadow copy (see {@link #drawObject(GC)}. In the case the bounding box get dirtied,
 * we then stop updating the shadow copy, to let it stay on the last state known to be in sync with
 * the bounding box. Only later, when out {@link PluginController} has updated our bounding box, we
 * sync the shadow copy again.</p>
 *
 * <p>To avoid graphical errors during the periods where the shadow copy is out of sync with the master object,
 * it is important, that {@link #clone()} is able to make clean copies of the object which result in
 * independent data structures. E.g. if the drawing object would store parts of its data in an object
 * which is not cloned by {@link #clone()}, then both the master object and the shadow copy would reference
 * the same object. This could result in short time graphical errors.</p>
 *
 * @author Daniel Bimschas
 * @author Dariush Forouher
 * @author Sebastian Ebers
 */
public abstract class DrawingObject implements Cloneable {

	private static Logger log = SpyglassLoggerFactory.getLogger(DrawingObject.class);

	/**
	 * The state of the drawing object
	 *
	 */
	public enum State {

		/**
		 * The drawing object has not been initialized yet.
		 */
		INFANT,

		/**
		 * The drawing object has been initialized.
		 */
		ALIVE,

		/**
		 * The drawing object has been destroyed.
		 */
		ZOMBIE
	}

	/** The drawing object's state */
	private State state = State.INFANT;

	/** The position of the object's upper left point */
	private AbsolutePosition position = new AbsolutePosition(0, 0, 0);

	/** Event listeners */
	private EventListenerList listeners = new EventListenerList();

	/** The object's bounding box */
	private AbsoluteRectangle boundingBox = new AbsoluteRectangle(0, 0, 1, 1);

	/** The object's foreground color */
	private RGB color = new RGB(200, 0, 0);

	/** The object's background color */
	private RGB bgColor = new RGB(255, 255, 255);

	/**
	 * Reference to the drawing area. it is only valid while the object is ALIVE.
	 */
	private DrawingArea drawingArea = null;

	/**
	 * After a property of this DrawingObject has changed, it may not be possible to
	 * immediately update the boundingBox. In such cases we have to do the drawing
	 * on a copy of our drawingObject which is known to be consistent with its boundingBox.
	 */
	private DrawingObject shadowCopy = null;

	/**
	 * Is the boundingBox dirty? It is dirty if and only if the shadowCopy is not completely
	 * consistent with the main object.
	 */
	private boolean isBoundingBoxDirty = false;

	// --------------------------------------------------------------------------------
	/**
	 * This initializes the drawing object.
	 *
	 * <p>It must only be called once.</p>
	 *
	 * <p>Subclasses may overwrite this method if they want to to additional things (e.g. add listener
	 * to the drawing area).</p>
	 *
	 * @param drawingArea
	 *            a reference to the drawing area
	 */
	public synchronized void init(final DrawingArea drawingArea) {
		if (state != State.INFANT) {
			throw new RuntimeException("Object ether already initialized or already dead!");
		}

		this.drawingArea = drawingArea;

		this.state = State.ALIVE;

		this.shadowCopy = clone();

		markBoundingBoxDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Destroys this drawing object.
	 *
	 * <p>It must only be called after {@link DrawingObject#init(DrawingArea)} and even then only once.</p>
	 *
	 * <p>Subclasses may overwrite this method if they want to to additional things (e.g. release
	 * listener from drawing area).</p>
	 *
	 */
	public synchronized void destroy() {
		if (state != State.ALIVE) {
			throw new RuntimeException("Object ether not yet initialized or already dead!");
		}

		this.state = State.ZOMBIE;
		this.drawingArea = null;

		fireContentChangedEvent();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a reference to the drawing area. Between the calls to
	 * {@link #init(DrawingArea)} and {@link #destroy()}, this method is guaranteed to return a
	 * valid reference.
	 *
	 * <p>Before {@link #init(DrawingArea)} or after {@link #destroy()} are called, this method
	 * will return null!</p>
	 *
	 * @return a reference to the drawing area
	 */
	protected synchronized final DrawingArea getDrawingArea() {
		return drawingArea;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the state of the drawing object
	 *
	 * @return the state of the drawing object
	 */
	public final synchronized State getState() {
		return state;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the position of the lower left point of the <code>DrawingObject</code>.
	 *
	 * @return the position of the lower left point of the <code>DrawingObject</code>
	 */
	public synchronized AbsolutePosition getPosition() {
		return position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the position of the object's lower left point
	 *
	 * @param position
	 *            the position of the lower left point of the <code>DrawingObject</code>
	 */
	public synchronized void setPosition(final AbsolutePosition position) {
		this.position = position;
		markBoundingBoxDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the instance's foreground color
	 *
	 * @return the instance's foreground color
	 */
	public synchronized RGB getColor() {
		return color;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the instance's foreground color
	 *
	 * @param color
	 *            the foreground color to set
	 */
	public synchronized void setColor(final RGB color) {
		this.color = color;
		markContentDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the instance's background color
	 *
	 * @return the instance's background color
	 */
	public synchronized RGB getBgColor() {
		return bgColor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the the instance's background color
	 *
	 * @param bgColor
	 *            the the background color to set
	 */
	public synchronized void setBgColor(final RGB bgColor) {
		this.bgColor = bgColor;
		markContentDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 * In this method the drawingObject should draw itself in the drawingArea.
	 * While doing so, it must not draw beyond the limits imposed by its bounding box.
	 *
	 * @param gc
	 *            the currently used graphics context
	 */
	protected abstract void draw(GC gc);

	// --------------------------------------------------------------------------------
	/**
	 * Redraw this drawing object on the given GC.
	 *
	 * @param drawingArea
	 * @param gc
	 */
	public final synchronized void drawObject(final GC gc) {

		// since a drawing object may be destroyed from another thread than this method
		// is called, we have to fail silently if this drawing object became a zombie.
		if (state != State.ALIVE) {
			return;
		}

		shadowCopy.draw(gc);
		//shadowCopy.drawBoundingBox(gc);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the bounding box of this drawing object.<br>
	 *
	 * <p>The bounding box is kept in sync with the image that is drawn through draw().
	 * It is not automatically updated whenever a property of a drawing object is modified.</p>
	 *
	 * @return the bounding box of this drawing object
	 */
	public synchronized final AbsoluteRectangle getBoundingBox() {
		return boundingBox;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Draws a line which indicates the object's bounding box
	 *
	 * @param drawingArea
	 *            the currently used drawing area
	 * @param gc
	 *            the currently used graphics context
	 */
	protected void drawBoundingBox(final GC gc) {
		final PixelRectangle rect = drawingArea.absRect2PixelRect(getBoundingBox());
		final int x = rect.getUpperLeft().x;
		final int y = rect.getUpperLeft().y;
		final int width = rect.getWidth();
		final int height = rect.getHeight();
		final Color color = new Color(gc.getDevice(), 255, 0, 0);
		gc.setLineWidth(1);
		gc.setForeground(color);
		gc.drawRectangle(x, y, width, height);
		color.dispose();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Marks the bounding box of this drawing object dirty.
	 *
	 * <p>If the bounding box is dirty, at some point later in time the PluginController will
	 * recalculate the bounding box. Until then the old bounding box will remain in place.</p>
	 *
	 * <p>This method must be called whenever the bounding box may have been changed.</p>
	 *
	 */
	protected synchronized final void markBoundingBoxDirty() {

		// The bounding box doens't matter in these cases
		if (state != State.ALIVE) {
			return;
		}

		// if there is already one pending, then don't do another one.
		if (!isBoundingBoxDirty) {

			log.trace("Scheduling async boundingBox update on "+this);
			isBoundingBoxDirty = true;
			fireSyncEvent();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners that some property of this drawing object has changed.
	 *
	 * <p>Indirectly this will ensure a repaint of this drawing object.</p>
	 */
	public final synchronized void markContentDirty() {

		fireContentChangedEvent();
	}

	/**
	 * Brings the boundingBox up-to-date if necessary.
	 *
	 * <p>This method must be called from within the SWT thread. It is used in the interaction between
	 * DrawingObjects and the UIController. Plugins should not use this method.</p>
	 *
	 */
	public final synchronized void syncBoundingBox() {

		// since a drawing object may be destroyed from another thread than this method
		// is called, we have to fail silently if this drawing object became a zombie.
		if (state != State.ALIVE) {
			return;
		}

		if (Display.getCurrent()==null) {
			throw new RuntimeException("This method must only be called from the SWT thread!");
		}

		// nothing to do
		if (!isBoundingBoxDirty) {
			return;
		}


		final AbsoluteRectangle oldBox = boundingBox;

		boundingBox = calculateBoundingBox();

		isBoundingBoxDirty = false;

		fireBoundingBoxChangeEvent(oldBox);
		markContentDirty();

	}

	// --------------------------------------------------------------------------------
	/**
	 * Calculates and returns the object's bounding box (and should do nothing else!)
	 *
	 * <p>It is guaranteed that this method is only called when there is a valid drawingArea available
	 * and that this method is only called
	 *
	 * <ul>
     * <li>from within the SWT-Thread and
     * <li>with the monitor to "this" held.
     * </ul>
     * </p>
	 *
	 * @return the calculated bounding box
	 */
	protected abstract AbsoluteRectangle calculateBoundingBox();

	/**
	 * Clones this drawing object. Subclasses have to overwrite this method if a deep-copy
	 * is necessary to avoid that modifications on the original will reflect on the copy.
	 *
	 * @returns a shallow copy
	 */
	@Override
	protected DrawingObject clone() {
		try {
			return (DrawingObject) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException("",e); // should never occur
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a listener for changes concerning the object's bounding box
	 *
	 * <p>Note that, unlike listeners in SWT or in other parts of Spyglass, a listener may only
	 * registered once to this event (consecutive registrations will simply be ignored.)</p>
	 *
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public final void addBoundingBoxChangedListener(final BoundingBoxChangeListener listener) {
		boolean alreadyIn = false;
		for (final BoundingBoxChangeListener bbcl : this.listeners.getListeners(BoundingBoxChangeListener.class)) {
			if (bbcl == listener) {
				alreadyIn = true;
			}
		}
		if (!alreadyIn) {
			this.listeners.add(BoundingBoxChangeListener.class, listener);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a listener for changes concerning the object's bounding box
	 *
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public final void removeBoundingBoxChangeListener(final BoundingBoxChangeListener listener) {
		this.listeners.remove(BoundingBoxChangeListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners which registered for changes of the drawing objects bounding box iff
	 * the box has actually changed
	 *
	 * @param oldBox
	 */
	private final void fireBoundingBoxChangeEvent(final AbsoluteRectangle oldBox) {

		if (!boundingBox.equals(oldBox)) {
			// Get listeners
			final BoundingBoxChangeListener[] list = listeners.getListeners(BoundingBoxChangeListener.class);

			// Fire the event (call-back method)
			for (int i = list.length - 1; i >= 0; i -= 1) {
				(list[i]).onBoundingBoxChanged(this, oldBox);
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds a listener for changes concerning the content of the drawing object
	 * (e.g. anything that would result in a different drawing)
	 *
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public final void addContentChangedListener(final ContentChangedListener listener) {
		this.listeners.add(ContentChangedListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a listener for changes concerning the content of the drawing object
	 * (e.g. anything that would result in a different drawing)
	 *
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public final void removeContentChangeListener(final ContentChangedListener listener) {
		this.listeners.remove(ContentChangedListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners that some property of this drawing object has changed.
	 *
	 * Indirectly this will ensure a repaint of this drawing object.
	 */
	private final void fireContentChangedEvent() {

		// If the boundingbox is currently stable (up-to-date) we can directly push
		// the changes made to the shadowCopy (which is used for drawing).
		if (!isBoundingBoxDirty) {
			shadowCopy = clone();
		}

		// Get listeners
		final ContentChangedListener[] list = listeners.getListeners(ContentChangedListener.class);

		// Fire the event (call-back method)
		for (final ContentChangedListener l: list) {
			l.onContentChanged(this);
		}
	}

	/**
	 * Adds a listener that is evoked whenever the boundingbox of this drawingobject
	 * gets dirty (i.e. needs to updated).
	 *
	 * <p>This listener is used solely for the communication between drawingObjects and
	 * the PluginController.</p>
	 *
	 * @param listener
	 */
	public final void addBoundingBoxIsDirtyListener(final BoundingBoxIsDirtyListener listener) {
		this.listeners.add(BoundingBoxIsDirtyListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a listener that is evoked whenever the boundingbox of this drawingobject
	 * gets dirty (i.e. needs to updated).
	 *
	 * @param listener
	 */
	public final void removeBoundingBoxIsDirtyListener(final BoundingBoxIsDirtyListener listener) {
		this.listeners.remove(BoundingBoxIsDirtyListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners that this drawing object has an outdated boundingBox and
	 * is in need of an SWT display thread to get it back up-to-date.
	 *
	 */
	private final void fireSyncEvent() {

		// Get listeners
		final BoundingBoxIsDirtyListener[] list = listeners.getListeners(BoundingBoxIsDirtyListener.class);

		// Fire the event (call-back method)
		for (final BoundingBoxIsDirtyListener l: list) {
			l.syncNeeded(this);
		}
	}

}
