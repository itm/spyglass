/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Abstract class that represents a drawing object. Every DrawingObject should have an unique id, a
 * position and a color, given by the RGB components in a R8G8B8 format.
 */
public abstract class DrawingObject {

	private static final SpyglassLogger log = (SpyglassLogger) SpyglassLoggerFactory.getLogger(DrawingObject.class);

	/** The position of the object's upper left point */
	protected AbsolutePosition position = new AbsolutePosition(0, 0, 0);

	private Set<BoundingBoxChangeListener> changeListeners = new HashSet<BoundingBoxChangeListener>();

	/** The object's bounding box */
	protected AbsoluteRectangle boundingBox;

	/** The object's foreground color */
	protected RGB color = new RGB(200, 0, 0);

	/** The object's background color */
	protected RGB bgColor = new RGB(255, 255, 255);

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
		updateBoundingBox();
		if (fireBoundingBoxChangeEvent) {
			fireBoundingBoxChangeEvent();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Informs all listeners which registered for changes of the drawing objects bounding box that
	 * the box has actually changed
	 */
	protected void fireBoundingBoxChangeEvent() {
		for (final BoundingBoxChangeListener listener : changeListeners) {
			listener.onBoundingBoxChanged(this);
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
			buff.append("\t");
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
		this.changeListeners.add(listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes a listener for changes concerning the object's bounding box
	 * 
	 * @param listener
	 *            a listener for changes concerning the object's bounding box
	 */
	public void removeBoundingBoxChangeListener(final BoundingBoxChangeListener listener) {
		this.changeListeners.remove(listener);
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
		final AbsoluteRectangle oldBox = this.boundingBox;
		try {
			boundingBox = calculateBoundingBox();
			if (fireBoundingBoxChangeEvent && ((oldBox != null) || !boundingBox.equals(oldBox))) {
				fireBoundingBoxChangeEvent();
			}
		} catch (final SWTException e) {
			if (e.getMessage().contains("Widget is disposed")) {
				log.warn("A boundingBox could not determined correctly: " + e);
			} else {
				log.error(e, e, false);
			}
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
