/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * Abstract class that represents a drawing object. Every DrawingObject should have an unique id, a
 * position and a color, given by the RGB components in a R8G8B8 format.
 */
public abstract class DrawingObject {

	/** The position of the object's upper left point */
	private AbsolutePosition position = new AbsolutePosition(0, 0, 0);

	private EventListenerList changeListeners = new EventListenerList();

	/** The object's bounding box */
	private AbsoluteRectangle boundingBox;

	/** The object's foreground color */
	private RGB color = new RGB(200, 0, 0);

	/** The object's background color */
	private RGB bgColor = new RGB(255, 255, 255);

	// --------------------------------------------------------------------------------
	/**
	 * Returns the position of the upper left point of the <code>DrawingObject</code>.
	 * 
	 * @return the position of the upper left point of the <code>DrawingObject</code>
	 */
	public AbsolutePosition getPosition() {
		return position;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the position of the object's upper left point
	 * 
	 * @param position
	 *            the position of the upper left point of the <code>DrawingObject</code>.
	 */
	public void setPosition(final AbsolutePosition position) {
		setPosition(position, true);
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
	public void setPosition(final AbsolutePosition position, final boolean fireBoundingBoxChangeEvent) {

		this.position = position;
		updateBoundingBox(fireBoundingBoxChangeEvent);

	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners which registered for changes of the drawing objects bounding box that
	 * the box has actually changed
	 * 
	 * @param oldBox
	 */
	protected void fireBoundingBoxChangeEvent(final AbsoluteRectangle oldBox) {
		// Get listeners
		final BoundingBoxChangeListener[] list = changeListeners.getListeners(BoundingBoxChangeListener.class);

		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			(list[i]).onBoundingBoxChanged(this, oldBox);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the instance's foreground color
	 * 
	 * @return the instance's foreground color
	 */
	public RGB getColor() {
		return color;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the instance's background color
	 * 
	 * @return the instance's background color
	 */
	public RGB getBgColor() {
		return bgColor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the the instance's background color
	 * 
	 * @param bgColor
	 *            the the background color to set
	 */
	public void setBgColor(final RGB bgColor) {
		this.bgColor = bgColor;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the instance's foreground color
	 * 
	 * @param color
	 *            the foreground color to set
	 */
	public void setColor(final RGB color) {
		this.color = color;
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
	 * Optionally the DrawingObject can inform itself about the current zoom level by querying the
	 * given drawingArea.
	 * 
	 * @param drawingArea
	 *            the currently used drawing area
	 * @param gc
	 *            the currently used graphics context
	 */
	public abstract void draw(DrawingArea drawingArea, GC gc);

	// --------------------------------------------------------------------------------
	/**
	 * Returns the bounding box of this drawing object.<br>
	 * The bounding box is recalculated every time the drawing object itself is changed. Hence, it
	 * is always up to date.
	 * 
	 * @return the bounding box of this drawing object
	 */
	public final AbsoluteRectangle getBoundingBox() {
		if (boundingBox == null) {
			updateBoundingBox();
		}
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
	 * Adds a listener for changes concerning the object's bounding box
	 * 
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public void addBoundingBoxChangedListener(final BoundingBoxChangeListener listener) {
		boolean alreadyIn = false;
		for (final BoundingBoxChangeListener bbcl : this.changeListeners.getListeners(BoundingBoxChangeListener.class)) {
			if (bbcl == listener) {
				alreadyIn = true;
			}
		}
		if (!alreadyIn) {
			this.changeListeners.add(BoundingBoxChangeListener.class, listener);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a listener for changes concerning the object's bounding box
	 * 
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public void removeBoundingBoxChangeListener(final BoundingBoxChangeListener listener) {
		this.changeListeners.remove(BoundingBoxChangeListener.class, listener);
	}

	/**
	 * Update's the object's bounding box and fires an event which informs all listeners for changes
	 * concerning the objects bounding box
	 */
	protected final void updateBoundingBox() {
		updateBoundingBox(true);
	}

	/**
	 * Indicates that the bounding box may have changed.
	 * 
	 * This method _must_ be called whenever the bounding box may have been changed.
	 * 
	 * @param fireBoundingBoxChangeEvent
	 *            indicate that the listeners should be notified.
	 */
	protected final void updateBoundingBox(final boolean fireBoundingBoxChangeEvent) {

		AbsoluteRectangle oldBox = null;

		// This mutex is necessary since a change of the bounding box needs to be reported to
		// registered listeners. A change means that there is an "old" and a new bounding box.
		// Determining the old and the new one is to be done in one atomic step which will be
		// achieved by using this lock
		synchronized (this) {
			oldBox = this.boundingBox;
			boundingBox = calculateBoundingBox();
		}

		if ((fireBoundingBoxChangeEvent && (oldBox == null)) || ((oldBox != null) && !boundingBox.equals(oldBox))) {
			fireBoundingBoxChangeEvent(oldBox);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the bounding box
	 * 
	 * @param boundingBox
	 *            the bounding box to set
	 */
	protected void setBoundingBox(final AbsoluteRectangle boundingBox) {
		this.boundingBox = boundingBox;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Calculates and returns the object's bounding box
	 * 
	 * @return the calculated bounding box
	 */
	protected abstract AbsoluteRectangle calculateBoundingBox();

}
