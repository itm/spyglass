/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.gui.view.DrawingAreaTransformEvent;
import de.uniluebeck.itm.spyglass.gui.view.DrawingAreaTransformListener;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a line.
 */
@Root
public class Line extends DrawingObject implements DrawingAreaTransformListener {

	private AbsolutePosition lineEnd = new AbsolutePosition(1, 0, 0);

	private int lineWidth = 1;

	private boolean listenerConnected = false;

	private DrawingArea drawingArea;

	public Line() {
		super();
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		if (!listenerConnected) {
			listenerConnected = true;
			drawingArea.addDrawingAreaTransformListener(this);
		}

		final Color color = new Color(gc.getDevice(), this.getColor());
		gc.setForeground(color);
		gc.setLineWidth(this.getLineWidth());

		final PixelPosition start = drawingArea.absPoint2PixelPoint(this.getPosition());
		final PixelPosition end = drawingArea.absPoint2PixelPoint(this.getEnd());
		final Rectangle clipping = gc.getClipping();
		final PixelRectangle pxBoundingBox = drawingArea.absRect2PixelRect(boundingBox);

		// should evade some unnecessary painting, not all, but it's better than nothing
		if (pxBoundingBox.rectangle.intersects(clipping)) {

			gc.setClipping(clipping);
			gc.drawLine(start.x, start.y, end.x, end.y);
			System.out.println("(" + start.x + ", " + start.y + ", " + end.x + ", " + end.y + ")");

		}

		color.dispose();

	}

	public AbsolutePosition getEnd() {
		return lineEnd;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setEnd(final AbsolutePosition end) {
		setEnd(end, true);
	}

	public void setEnd(final AbsolutePosition end, final boolean fireBoundingBoxChangeEvent) {
		lineEnd = end;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}

	public void setLineWidth(final int width) {
		setLineWidth(width, true);
	}

	public void setLineWidth(final int width, final boolean fireBoundingBoxChangeEvent) {
		this.lineWidth = width;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}

	@Override
	public String toString() {
		return String.format("Line from %s to %s", this.getPosition(), this.lineEnd);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {

				if (drawingArea == null) {

					final int upperLeftX = Math.min(lineEnd.x, position.x) - (lineWidth / 2);
					final int upperLeftY = Math.min(lineEnd.y, position.y) - (lineWidth / 2);
					final int width = Math.abs(lineEnd.x - position.x) + lineWidth;
					final int height = Math.abs(lineEnd.y - position.y) + lineWidth;
					boundingBox = new AbsoluteRectangle(upperLeftX, upperLeftY, width, height);
					return;
				}

				final PixelPosition pos = drawingArea.absPoint2PixelPoint(position);
				final PixelPosition end = drawingArea.absPoint2PixelPoint(lineEnd);

				final int bbUpperLeftX = Math.min(end.x, pos.x) - ((int) Math.ceil((((double) lineWidth) / 2)));
				final int bbUpperLeftY = Math.min(end.y, pos.y) - ((int) Math.ceil((((double) lineWidth) / 2)));

				final int bbWidth = Math.abs(end.x - pos.x) + lineWidth;
				final int bbHeight = Math.abs(end.y - pos.y) + lineWidth;

				final PixelRectangle bbArea = new PixelRectangle(bbUpperLeftX, bbUpperLeftY, bbWidth, bbHeight);

				boundingBox = drawingArea.pixelRect2AbsRect(bbArea);

			}
		});

		return boundingBox;

	}

	public boolean equals(final Line other) {
		final AbsolutePosition op = other.position;
		final AbsolutePosition oe = other.lineEnd;
		final AbsolutePosition p = position;
		final AbsolutePosition e = lineEnd;
		return ((p.x == op.x) && (p.y == op.y) && (p.z == op.z) && (e.x == oe.x) && (e.y == oe.y) && (e.z == oe.z))
				|| ((p.x == oe.x) && (p.y == oe.y) && (p.z == oe.z) && (e.x == op.x) && (e.y == op.y) && (e.z == op.z));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lineEnd == null) ? 0 : lineEnd.hashCode());
		result = prime * result + lineWidth;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Line other = (Line) obj;
		return equals(other);
	}

	@Override
	public void handleEvent(final DrawingAreaTransformEvent e) {
		drawingArea = e.drawingArea;
		updateBoundingBox();
	}
}
