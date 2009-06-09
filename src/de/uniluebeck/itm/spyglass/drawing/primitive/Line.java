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
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedListener;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent.Type;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * A primitive drawing object, representing a line.
 */
@Root
public class Line extends DrawingObject implements TransformChangedListener {

	private AbsolutePosition lineEnd = new AbsolutePosition(1, 0, 0);

	private int lineWidth = 1;

	protected boolean currentClippingSaysDrawIt;

	protected DoublePoint currentStartPoint;

	protected DoublePoint currentEndPoint;

	public Line() {
		super();
	}

	protected static class DoublePoint {

		public double x, y;

		public DoublePoint(final double x, final double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

	}

	private static int outCodes(final DoublePoint point, final Rectangle rect) {

		int code = 0;

		if (point.y > rect.y + rect.height) {
			code += 1; /* code for above */
		} else if (point.y < rect.y) {
			code += 2; /* code for below */
		}

		if (point.x > rect.x + rect.width) {
			code += 4; /* code for right */
		} else if (point.x < rect.x) {
			code += 8; /* code for left */
		}

		return code;

	}

	private static boolean cohenSutherlandClip(final DoublePoint p1, final DoublePoint p2, final Rectangle rect) {

		int outCode1, outCode2;

		while (true) {

			outCode1 = outCodes(p1, rect);
			outCode2 = outCodes(p2, rect);

			// do reject check
			if ((outCode1 & outCode2) != 0) {
				return false;
			}

			// do accept check
			if ((outCode1 == 0) && (outCode2 == 0)) {
				return true;
			}

			if (outCode1 == 0) {
				double tempCoord;
				int tempCode;
				tempCoord = p1.x;
				p1.x = p2.x;
				p2.x = tempCoord;
				tempCoord = p1.y;
				p1.y = p2.y;
				p2.y = tempCoord;
				tempCode = outCode1;
				outCode1 = outCode2;
				outCode2 = tempCode;
			}

			if ((outCode1 & 1) != 0) {
				p1.x += (p2.x - p1.x) * (rect.y + rect.height - p1.y) / (p2.y - p1.y);
				p1.y = rect.y + rect.height;
			} else if ((outCode1 & 2) != 0) {
				p1.x += (p2.x - p1.x) * (rect.y - p1.y) / (p2.y - p1.y);
				p1.y = rect.y;
			} else if ((outCode1 & 4) != 0) {
				p1.y += (p2.y - p1.y) * (rect.x + rect.width - p1.x) / (p2.x - p1.x);
				p1.x = rect.x + rect.width;
			} else if ((outCode1 & 8) != 0) {
				p1.y += (p2.y - p1.y) * (rect.x - p1.x) / (p2.x - p1.x);
				p1.x = rect.x;
			}
		}

	}

	@Override
	public void draw(final GC gc) {

		final Color color = new Color(gc.getDevice(), this.getColor());
		gc.setForeground(color);
		gc.setLineWidth(this.getLineWidth());

		final PixelPosition start = getDrawingArea().absPoint2PixelPoint(this.getPosition());
		final PixelPosition end = getDrawingArea().absPoint2PixelPoint(this.getEnd());

		// set protected class variables so that extending classes don't have to calculate the
		// points once again
		currentStartPoint = new DoublePoint(start.x, start.y);
		currentEndPoint = new DoublePoint(end.x, end.y);

		// set protected class variable so that extending classes can read if to draw
		currentClippingSaysDrawIt = cohenSutherlandClip(currentStartPoint, currentEndPoint, gc.getClipping());

		if (currentClippingSaysDrawIt) {

			gc.drawLine((int) currentStartPoint.x, (int) currentStartPoint.y, (int) currentEndPoint.x, (int) currentEndPoint.y);

		}

		color.dispose();

	}

	public synchronized AbsolutePosition getEnd() {
		return lineEnd;
	}

	public synchronized int getLineWidth() {
		return lineWidth;
	}

	public synchronized void setEnd(final AbsolutePosition end) {
		lineEnd = end;
		markBoundingBoxDirty();
	}

	public void setLineWidth(final int width) {
		this.lineWidth = width;
		markBoundingBoxDirty();
	}

	@Override
	public String toString() {
		return String.format("Line from %s to %s", this.getPosition(), this.lineEnd);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final PixelPosition pos = getDrawingArea().absPoint2PixelPoint(getPosition());
		final PixelPosition end = getDrawingArea().absPoint2PixelPoint(lineEnd);

		final int bbUpperLeftX = Math.min(end.x, pos.x) - ((int) Math.ceil((((double) lineWidth) / 2)));
		final int bbUpperLeftY = Math.min(end.y, pos.y) - ((int) Math.ceil((((double) lineWidth) / 2)));

		final int bbWidth = Math.abs(end.x - pos.x) + lineWidth;
		final int bbHeight = Math.abs(end.y - pos.y) + lineWidth;

		final PixelRectangle bbArea = new PixelRectangle(bbUpperLeftX, bbUpperLeftY, bbWidth, bbHeight);

		return getDrawingArea().pixelRect2AbsRect(bbArea);

	}

	@Override
	public void handleEvent(final TransformChangedEvent e) {
		// don't update the boundingbox if we're only moving
		if (e.type == Type.ZOOM_MOVE) {
			markBoundingBoxDirty();
		}
	}

	@Override
	public void destroy() {
		getDrawingArea().removeTransformChangedListener(this);
		super.destroy();

	}

	@Override
	public void init(final DrawingArea drawingArea) {
		super.init(drawingArea);
		getDrawingArea().addTransformChangedListener(this);
	}

}
