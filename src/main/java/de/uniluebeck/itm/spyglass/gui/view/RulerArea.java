/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.awt.geom.Point2D;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * @author Oliver Kleine
 * 
 */
public class RulerArea extends Canvas {

	public static int HORIZONTAL = 1;
	public static int VERTICAL = 2;
	public static int UNIT = 3;

	private static Logger log = SpyglassLoggerFactory.getLogger(RulerArea.class);

	private int rulerDirection;
	private DrawingArea drawingArea;

	public RulerArea(final Composite parent, final int direction) {

		super(parent, SWT.NONE);
		this.rulerDirection = direction;
		// this.addControlListener(controlListener);

	}

	public void setDrawingArea(final DrawingArea drawingArea) {
		this.drawingArea = drawingArea;
	}

	public void drawUnit(final GC gc, final String unit) {

		gc.fillRectangle(0, 0, this.getClientArea().width, this.getClientArea().height);
		// draw 2 arrows as a polyline
		final int[] pointArray = { 1, 20, 4, 23, 7, 20, 4, 23, 4, 4, 23, 4, 20, 1, 23, 4, 20, 7 };
		gc.drawPolyline(pointArray);

		final Font oldFont = gc.getFont();
		Font newFont = null;

		final FontData fontData = oldFont.getFontData()[0];
		int fontHeight = fontData.getHeight();

		// resize the font to display the unit-string properly
		while (!(gc.stringExtent("" + unit).x <= 22)) {
			gc.setFont(null);
			fontHeight--;
			fontData.setHeight(fontHeight);

			final Font newFont2 = new Font(this.getDisplay(), fontData);
			gc.setFont(newFont2);

			if (newFont != null) {
				newFont.dispose();
			}
			newFont = newFont2;
		}

		gc.drawText("" + unit, 7, 6, true);

		gc.setFont(oldFont);
	}

	public void drawRuler(final PixelRectangle pxRect, final Point2D upperLeft, final Point2D lowerRight, final GC gc, final int direction) {

		gc.fillRectangle(0, 0, this.getClientArea().width, this.getClientArea().height);

		int pxMin;
		int pxMax;
		double absMin;
		double absMax;

		if (direction == RulerArea.HORIZONTAL) {
			pxMin = pxRect.getUpperLeft().x;
			pxMax = pxMin + pxRect.getWidth();
			absMin = upperLeft.getX();
			absMax = lowerRight.getX();
		} else if (direction == RulerArea.VERTICAL) {
			pxMin = pxRect.getUpperLeft().y;
			pxMax = pxMin + pxRect.getHeight();
			absMin = lowerRight.getY();
			absMax = upperLeft.getY();
		} else {
			// do nothing and quit
			return;
		}

		// number of pixels per absolute unit
		final double pxPerAbs = (pxMax - pxMin) / (absMax - absMin);

		// absolute distance between two ruler lines
		int absScale;
		if (pxPerAbs > 20) {
			absScale = 5;
		} else if (pxPerAbs > 10) {
			absScale = 10;
		} else if (pxPerAbs > 5) {
			absScale = 20;
		} else if (pxPerAbs > 2) {
			absScale = 50;
		} else if (pxPerAbs > 1) {
			absScale = 100;
		} else if (pxPerAbs > 0.5) {
			absScale = 200;
		} else if (pxPerAbs > 0.2) {
			absScale = 500;
		} else if (pxPerAbs > 0.1) {
			absScale = 1000;
		} else if (pxPerAbs > 0.05) {
			absScale = 2000;
		} else if (pxPerAbs > 0.02) {
			absScale = 5000;
		} else {
			absScale = 10000;
		}

		// most left (horizontal) or top (vertical) absolute value to be shown on ruler
		final int shownFirst;
		if (direction == RulerArea.HORIZONTAL) {
			if (absMin >= 0) {
				shownFirst = (int) Math.round((absMin + absScale - absMin % absScale) - absScale);
			} else {
				shownFirst = (int) Math.round((absMin + absScale + (-absScale - (absMin % absScale))) - absScale);
			}
		} else {
			if (absMax >= 0) {
				shownFirst = (int) Math.round((absMax + absScale - absMax % absScale));
			} else {
				shownFirst = (int) Math.round((absMax + absScale + (-absScale - (absMax % absScale))));
			}
		}

		// horizontal: x-Coordinate (in px) of the most left shown ruler value
		// vertical: y-Coordinate (in px) of the most up shown ruler Value
		// This value (firstLine) is always outside of the visible area. Thus ruler labels can be
		// drawn even if the caption is located next to a threshold but within the visible area.

		int firstLine;
		if (direction == RulerArea.HORIZONTAL) {
			firstLine = (int) Math.round((shownFirst - absMin) * pxPerAbs);
		} else {
			firstLine = -(int) Math.round((shownFirst - absMax) * pxPerAbs);
		}
		final int numOfLines = (int) (absMax - absMin) / absScale + 1;

		// fit the fontsize so that all values can be displayed completely
		final Font oldFont = gc.getFont();
		Font newFont = null;
		final FontData fontData = gc.getFont().getFontData()[0];
		int fontHeight = fontData.getHeight();

		while (!(gc.stringExtent("-00000").x <= 28)) {
			fontHeight--;
			fontData.setHeight(fontHeight);
			// final Font oldFont = gc.getFont();
			final Font newFont2 = new Font(this.getDisplay(), fontData);
			gc.setFont(newFont2);

			if (newFont != null) {
				newFont.dispose();
			}
			newFont = newFont2;

		}

		// draw all lines and the caption of the ruler
		if (direction == RulerArea.HORIZONTAL) {
			for (int i = 0; i <= numOfLines; i++) {
				final int curX = (int) Math.round((i * absScale * pxPerAbs + 1) + firstLine);
				gc.drawLine(curX, 15, curX, 27);
				gc.drawLine(curX + 1, 15, curX + 1, 27);

				// draw the caption centered above the line
				final String curTxt = "" + (shownFirst + i * absScale);
				final int txtWidth = gc.stringExtent(curTxt).x;
				gc.drawText("" + (shownFirst + i * absScale), curX - txtWidth / 2, 0);

				// draw short lines before and behind (the 5th line is longer)
				for (int j = 1; j < 10; j++) {
					final int curX2 = (int) (curX + Math.round(j * absScale * pxPerAbs / 10));
					if (j == 5) {
						gc.drawLine(curX2, 15, curX2, 27);
					} else {
						gc.drawLine(curX2, 20, curX2, 27);
					}
				}
			}
		} else if (direction == RulerArea.VERTICAL) {
			for (int i = 0; i <= numOfLines; i++) {
				final int curY = (int) Math.round((i * absScale * pxPerAbs + 1) + firstLine);

				gc.drawText("" + (shownFirst - (i * absScale)), 0, curY - gc.getFontMetrics().getHeight());
				gc.drawLine(15, curY, 27, curY);
				gc.drawLine(15, curY + 1, 27, curY + 1);

				// draw short lines before and behind (the 5th line is longer)
				for (int j = 1; j < 10; j++) {
					final int curY2 = (int) (curY + Math.round(j * absScale * pxPerAbs / 10));
					if (j == 5) {
						gc.drawLine(15, curY2, 27, curY2);
					} else {
						gc.drawLine(20, curY2, 27, curY2);
					}
				}
			}
		}

		gc.setFont(oldFont);
		if (!newFont.isDisposed()) {
			newFont.dispose();
		}
	}

	// private ControlListener controlListener = new ControlListener() {
	//
	// @Override
	// public void controlMoved(final ControlEvent e) {
	// // TODO Auto-generated method stub
	// log.debug("Ruler moved...");
	// test();
	//
	// }
	//
	// @Override
	// public void controlResized(final ControlEvent e) {
	// // TODO Auto-generated method stub
	// log.debug("Ruler resized...");
	// test();
	// }
	//
	// };
}