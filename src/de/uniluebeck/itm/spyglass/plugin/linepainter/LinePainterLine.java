package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

public class LinePainterLine extends Line {

	private String stringFormatterResult = "";

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
		// algorithm
		// says this line is currently visible -> then we'll have to draw the string
		if (currentClippingSaysDrawIt) {

			final Point p = determineStringFormatterPosition(getDrawingArea().absRect2PixelRect(super.calculateBoundingBox()), determineTextExtent());
			final Color oldForeground = gc.getForeground();
			gc.drawText(stringFormatterResult, p.x, p.y, SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT);
			gc.setForeground(oldForeground);

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Calculates the position in pixel coordinates in which to draw the string formatter result.
	 * 
	 * @param box
	 *            the bounding box of the underlying line drawing object in pixel coordinates
	 * @param extent
	 *            the extent of the string formatter result in pixel coordinates
	 * @return the point on which to draw the string formatter result in pixel coordinates
	 */
	private Point determineStringFormatterPosition(final PixelRectangle box, final Point extent) {
		final int x = box.rectangle.x + (box.rectangle.width / 2) - (extent.x / 2);
		final int y = box.rectangle.y + (box.rectangle.height / 2);
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

			final AbsoluteRectangle absolutelineBoundingBox;
			final PixelRectangle pixelLineBoundingBox;
			final Point pixelExtent;
			final Point pixelStringFormatterPosition;
			final PixelRectangle pixelStringFormatterRectangle;
			final PixelRectangle pixelUnionRectangle;

			// make sure parent calculates the new bounding box in absolute coordinates for the line
			absolutelineBoundingBox = super.calculateBoundingBox();

			// calculate the bounding box of the line in pixel coordinates
			pixelLineBoundingBox = getDrawingArea().absRect2PixelRect(absolutelineBoundingBox);

			// calculate the text extent in pixel coordinates
			pixelExtent = determineTextExtent();

			// calculate the position in pixel on which to draw the string formatter result
			pixelStringFormatterPosition = determineStringFormatterPosition(pixelLineBoundingBox, pixelExtent);

			// calculate the rectangle in pixel coordinates of the drawn string formatter result
			pixelStringFormatterRectangle = new PixelRectangle(new Rectangle(pixelStringFormatterPosition.x, pixelStringFormatterPosition.y,
					pixelExtent.x, pixelExtent.y));

			// calculate the union of the lines' bounding box in pixel coordinates
			pixelUnionRectangle = pixelLineBoundingBox.union(pixelStringFormatterRectangle);

			// return the transformed (from pixel to absolute coordinates) union rectangle
			return getDrawingArea().pixelRect2AbsRect(pixelUnionRectangle);
		}

		return super.calculateBoundingBox();

	}
}