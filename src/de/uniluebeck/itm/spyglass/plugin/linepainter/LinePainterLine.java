package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

public class LinePainterLine extends Line {

	private String stringFormatterResult = "";

	private boolean paintBox = false;

	public LinePainterLine() {
		super();
	}

	public synchronized String getStringFormatterResult() {
		return stringFormatterResult;
	}

	public synchronized void setStringFormatterResult(final String stringFormatterResult) {
		this.stringFormatterResult = stringFormatterResult;
		markBoundingBoxDirty();
	}

	@Override
	public void draw(final GC gc) {

		super.draw(gc);

		// protected class variable currentClippingSaysDrawIt is set by super.draw() if clipping
		// algorithm says this line is currently visible -> then we'll have to draw the string
		if (currentClippingSaysDrawIt) {

			final Point textExtent = determineTextExtent();
			final Point p = determineStringFormatterPosition(textExtent);

			final Color oldForeground = gc.getForeground();
			final Color foreground = new Color(Display.getCurrent(), getColor());
			gc.setForeground(foreground);

			if (paintBox) {
				final Color oldBackground = gc.getBackground();
				gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

				final Rectangle boxRect = new Rectangle(p.x - 2, p.y - 1, textExtent.x + 4, textExtent.y + 2);
				gc.fillRectangle(boxRect);
				gc.drawRectangle(boxRect);

				gc.setBackground(oldBackground);
			}

			gc.drawText(stringFormatterResult, p.x, p.y, SWT.DRAW_DELIMITER | SWT.DRAW_TAB);

			gc.setForeground(oldForeground);
			foreground.dispose();

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Calculates the position in pixel coordinates in which to draw the string formatter result.
	 * 
	 * @param extent
	 *            the extent of the string formatter result in pixel coordinates
	 * @return the point on which to draw the string formatter result in pixel coordinates
	 */
	private Point determineStringFormatterPosition(final Point extent) {

		// compute point in absolute coordinates
		final double x0 = getPosition().x;
		final double y0 = getPosition().y;
		final double x1 = getEnd().x;
		final double y1 = getEnd().y;

		final double pointX = (x0 + ((1.0 / 4.0) * (x1 - x0)));
		final double pointY = (y0 + ((1.0 / 4.0) * (y1 - y0)));

		// transform to pixel coordinates
		final PixelPosition pixelPosition = getDrawingArea().absPoint2PixelPoint(new AbsolutePosition((int) pointX, (int) pointY));

		final int x = pixelPosition.x - (extent.x / 2);
		final int y = pixelPosition.y - (extent.y / 2);

		return new Point(x, y);

	}

	private Point determineTextExtent() {
		final Image i = new Image(null, 1, 1);
		final GC gc = new GC(i);
		final Point textExtent = gc.textExtent(stringFormatterResult, SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT);
		gc.dispose();
		i.dispose();
		return textExtent;
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		if ((stringFormatterResult != null) && (stringFormatterResult.length() > 0)) {

			final AbsoluteRectangle absBBox;
			final PixelRectangle pxLineBBox;
			final Point pxExtent;
			final Point pxSFPos;
			final PixelRectangle pxSFRect;
			final PixelRectangle pxUnionRect;

			// make sure parent calculates the new bounding box in absolute coordinates for the line
			absBBox = super.calculateBoundingBox();

			// calculate the bounding box of the line in pixel coordinates
			pxLineBBox = getDrawingArea().absRect2PixelRect(absBBox);

			// calculate the text extent in pixel coordinates
			pxExtent = determineTextExtent();

			// calculate the position in pixel on which to draw the string formatter result
			pxSFPos = determineStringFormatterPosition(pxExtent);

			// calculate the rectangle in pixel coordinates of the drawn string formatter result
			if (paintBox) {
				pxSFRect = new PixelRectangle(pxSFPos.x - 2, pxSFPos.y - 1, pxExtent.x + 4, pxExtent.y + 2);
			} else {
				pxSFRect = new PixelRectangle(pxSFPos.x, pxSFPos.y, pxExtent.x, pxExtent.y);
			}

			// calculate the union of the lines' bounding box in pixel coordinates
			pxUnionRect = pxLineBBox.union(pxSFRect);

			// return the transformed (from pixel to absolute coordinates) union rectangle
			return getDrawingArea().pixelRect2AbsRect(pxUnionRect);
		}

		return super.calculateBoundingBox();

	}

}