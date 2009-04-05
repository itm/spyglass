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

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Abstract class that represents a drawing object.
 *
 * A drawing object has a lifetime, bounded by the calls to {@link DrawingObject#init(DrawingArea)}
 * and {@link DrawingObject#destroy()}, respectively. During this time, the drawingObject is bounded
 * to an drawingArea.
 *
 * All DrawingObjects are guaranteed to be thread-safe. (Implementors of subclasses should make sure
 * of this too!)
 *
 * HOWEVER: Calls to setter-Methods are likely to synchronize with the SWT-Display thread. So to
 * avoid deadlocks, any caller should not hold any monitors (which could potentially be obtained by
 * the SWT thread) while calling any setter method.
 *
 * A drawingObject can have only one life, i.e. once it has been destroyed, it cannot be initialized
 * again.
 *
 * @author Daniel Bimschas
 * @author Dariush Forouher
 * @author Sebastian Ebers
 *
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
	private boolean isBoundingBoxDirty = true;

	// --------------------------------------------------------------------------------
	/**
	 * This initializes the drawing object.
	 *
	 * It must only be called once.
	 *
	 * Subclasses may overwrite this method if they want to to additional things (e.g. add listener
	 * to the drawing area).
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

		this.isBoundingBoxDirty = true;
		syncBoundingBox();

		this.shadowCopy = clone();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Destroys this drawing object.
	 *
	 * It must only be called after {@link DrawingObject#init(DrawingArea)} and even then only once.
	 *
	 * subclasses may overwrite this method if they want to to additional things (e.g. release
	 * listener from drawing area).
	 */
	public synchronized void destroy() {
		if (state != State.ALIVE) {
			throw new RuntimeException("Object ether not yet initialized or already dead!");
		}

		this.drawingArea = null;
		this.state = State.ZOMBIE;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a reference to the drawing area. Between the calls to
	 * {@link DrawingObject#init(DrawingArea)} and destroy(), this method is guaranteed to return a
	 * valid reference.
	 *
	 * Before {@link DrawingObject#init(DrawingArea)} or after destroy() are called, this method
	 * will return null!
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
	 * Returns the position of the upper left point of the <code>DrawingObject</code>.
	 *
	 * @return the position of the upper left point of the <code>DrawingObject</code>
	 */
	public synchronized AbsolutePosition getPosition() {
		return position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the position of the object's upper left point
	 *
	 * @param position
	 *            the position of the upper left point of the <code>DrawingObject</code>
	 * @param fireBoundingBoxChangeEvent
	 *            indicates whether a changeEvent has to be thrown
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
	 * Returns the string representation of the object
	 *
	 * @param tabCount
	 *            the number of tab stops used to intent the string representation
	 * @return the string representation of the object
	 */
	public String toString(final int tabCount) {
		final StringBuffer buff = new StringBuffer();
		for (int i = 0; i < tabCount; i++) {
			buff.append("--");
		}
		buff.append(toString());
		return buff.toString();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Forces this DrawingObject to paint itself on the given GC.
	 *
	 * @param drawingArea
	 *            the currently used drawing area
	 * @param gc
	 *            the currently used graphics context
	 */
	protected abstract void draw(DrawingArea drawingArea, GC gc);

	// --------------------------------------------------------------------------------
	/**
	 * Redraw this drawing object on the given GC.
	 *
	 * @param drawingArea
	 * @param gc
	 */
	public final synchronized void drawObject(final DrawingArea drawingArea, final GC gc) {
		shadowCopy.draw(drawingArea, gc);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the bounding box of this drawing object.<br>
	 * The bounding box is recalculated every time the drawing object itself is changed. Hence, it
	 * is always up to date.
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
	protected void drawBoundingBox(final DrawingArea drawingArea, final GC gc) {
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
	 * Recalculates the bounding box of this drawing object.
	 *
	 * Some notes about consistency: The boudingBox of a drawing object is updated whenever updateBoundingBox()
	 * is called. HOWEVER: the calculation of the bounding box is usually done async!
	 *
	 * This method _must_ be called whenever the bounding box may have been changed.
	 *
	 */
	protected synchronized final void markBoundingBoxDirty() {

		// The bounding box doens't matter in these cases
		if (state != State.ALIVE) {
			return;
		}

		// if there is already one pending, then don't do another one.
		if (!isBoundingBoxDirty) {

//			log.debug("Scheduling async boundingBox update on "+this);
			isBoundingBoxDirty = true;
			fireSyncEvent();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners that some property of this drawing object has changed.
	 *
	 * Indirectly this will ensure a repaint of this drawing object.
	 */
	public final synchronized void markContentDirty() {

		fireContentChangedEvent();
	}

	/**
	 * Brings the boundingBox up-to-date if necessary.
	 *
	 * This method must be called from within the SWT thread. It is used in the interaction between
	 * DrawingObjects and the UIController. Plugins should not use this method.
	 *
	 */
	public final synchronized void syncBoundingBox() {

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
	 * It is guaranteed that this method is only called when there is a valid drawingArea available
	 * and that this method is only called
	 *
	 * a) from within the SWT-Thread
	 *
	 * b) with the monitor to "this" held.
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
	 * This listener is used soly for the communication between drawingObjects and
	 * the UIController.
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
		if (!isBoundingBoxDirty) {
			shadowCopy = clone();
		}

		// Get listeners
		final BoundingBoxIsDirtyListener[] list = listeners.getListeners(BoundingBoxIsDirtyListener.class);

		// Fire the event (call-back method)
		for (final BoundingBoxIsDirtyListener l: list) {
			l.syncNeeded(this);
		}
	}

}
