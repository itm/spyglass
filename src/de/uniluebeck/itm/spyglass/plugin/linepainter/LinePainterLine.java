package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

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

			final Point p = determineStringFormatterPosition(super.calculateBoundingBox());
			final Color oldForeground = gc.getForeground();
			gc.drawString(stringFormatterResult, p.x, p.y, true);
			gc.setForeground(oldForeground);

		}
	}

	private Point determineStringFormatterPosition(final AbsoluteRectangle lineBBox) {
		final Rectangle pxRect = getDrawingArea().absRect2PixelRect(lineBBox).rectangle;
		final Image i = new Image(null, 1, 1);
		final GC gc = new GC(i);
		final Point extent = gc.textExtent(stringFormatterResult);
		gc.dispose();
		i.dispose();
		final int x = pxRect.x + (pxRect.width / 2) - (extent.x / 2);
		final int y = pxRect.y + (pxRect.height / 2);
		return new Point(x, y);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle lineBBox = super.calculateBoundingBox();

		if ((stringFormatterResult != null) && (stringFormatterResult.length() > 0)) {
			final Image i = new Image(null, 1, 1);
			final GC gc = new GC(i);
			final Point extent = gc.textExtent(stringFormatterResult);
			final Point position = determineStringFormatterPosition(lineBBox);
			final Rectangle textRect = new Rectangle(position.x - (extent.x / 2), position.y + (extent.y / 2), extent.x + 10, extent.y + 5);
			gc.dispose();
			i.dispose();
			return new AbsoluteRectangle(lineBBox.rectangle.union(textRect));
		} else {
			return new AbsoluteRectangle(lineBBox);
		}

	}
}