// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.awt.geom.Point2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * @author Oliver Kleine
 * 
 */
public class RulerArea extends Canvas {

	public static int HORIZONTAL = 1;
	public static int VERTICAL = 2;

	Spyglass spyglass;

	public RulerArea(final Composite parent) {

		super(parent, SWT.NONE);

	}

	public void redraw(final PixelRectangle pxRect, final Point2D upperLeft, final Point2D lowerRight, final GC gc, final int direction) {

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
			absMin = upperLeft.getY();
			absMax = lowerRight.getY();
		} else {
			// do nothing...
			return;
		}
		final double absSize = absMax - absMin;

		final int pxSizeH = pxMax - pxMin;

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

		// most left absolute value to be shown on ruler
		final int shownMin;
		if (absMin >= 0) {
			shownMin = (int) (absMin + absScale - absMin % absScale);
		} else {
			shownMin = (int) (absMin + absScale + (-absScale - (absMin % absScale)));
		}

		// horizontal: x-Coordinate (in px) of the most left shown ruler value
		// vertical: y-Coordinate (in px) of the most up shown ruler Value
		final int firstLine = (int) Math.round((shownMin - absMin) * pxPerAbs);
		final int numOfLines = (int) (absMax - absMin) / absScale;

		// draw all lines of the ruler

		if (direction == RulerArea.HORIZONTAL) {
			for (int i = 0; i <= numOfLines; i++) {
				final int curX = (int) Math.round((i * absScale * pxPerAbs + 1) + firstLine);
				gc.drawLine(curX, 15, curX, 25);
				gc.drawText("" + (shownMin + (i * absScale)), curX, 0);
			}
		} else if (direction == RulerArea.VERTICAL) {
			for (int i = 0; i <= numOfLines; i++) {
				final int curY = (int) Math.round((i * absScale * pxPerAbs + 1) + firstLine);
				gc.drawText("" + (shownMin + (i * absScale)), 0, curY);
				gc.drawLine(15, curY, 25, curY);
			}
		}
	}
}