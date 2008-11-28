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

	private AbsolutePosition position = new AbsolutePosition(0, 0, 0);

	private Set<BoundingBoxChangeListener> changeListeners = new HashSet<BoundingBoxChangeListener>();

	protected AbsoluteRectangle boundingBox;

	private RGB color = new RGB(200, 0, 0);

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
	 * Sets the position of the upper left point of the <code>DrawingObject</code>
	 * 
	 * @param position
	 *            the position of the upper left point of the <code>DrawingObject</code>.
	 */
	public void setPosition(final AbsolutePosition position) {
		setPosition(position, true);
	}

	public void setPosition(final AbsolutePosition position, final boolean fireBoundingBoxChangeEvent) {

		this.position = position;
		updateBoundingBox();
		if (fireBoundingBoxChangeEvent) {
			fireBoundingBoxChangeEvent();
		}
	}

	protected void fireBoundingBoxChangeEvent() {
		for (final BoundingBoxChangeListener listener : changeListeners) {
			listener.onBoundingBoxChanged(this);
		}
	}

	/**
	 * 
	 */
	public RGB getColor() {
		return color;
	}

	public RGB getBgColor() {
		return bgColor;
	}

	public void setBgColor(final RGB bgColor) {
		this.bgColor = bgColor;
	}

	public void setColor(final RGB color) {
		this.color = color;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
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

	public void addBoundingBoxChangedListener(final BoundingBoxChangeListener listener) {
		this.changeListeners.add(listener);
	}

	public void removeBoundingBoxChangeListener(final BoundingBoxChangeListener listener) {
		this.changeListeners.remove(listener);
	}

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
		boundingBox = calculateBoundingBox();
		if (fireBoundingBoxChangeEvent && ((oldBox != null) || !boundingBox.equals(oldBox))) {
			fireBoundingBoxChangeEvent();
		}
	}

	protected abstract AbsoluteRectangle calculateBoundingBox();

}
