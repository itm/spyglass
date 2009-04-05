package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
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
	public void draw(final DrawingArea drawingArea, final GC gc) {
		super.draw(drawingArea, gc);
		final Point p = determineStringFormatterPosition(getBoundingBox());
		gc.drawString(stringFormatterResult, p.x, p.y);
		// drawBoundingBox(drawingArea, gc);
	}

	private Point determineStringFormatterPosition(final AbsoluteRectangle lineBBox) {
		return Geometry.centerPoint(getDrawingArea().absRect2PixelRect(lineBBox).rectangle);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle lineBBox = super.calculateBoundingBox();

		if ((stringFormatterResult != null) && (stringFormatterResult.length()>0)) {
			final Image i = new Image(null, 1,1);
			final GC gc = new GC(i);
			final Point extent = gc.textExtent(stringFormatterResult);
			final Point position = determineStringFormatterPosition(lineBBox);
			final Rectangle textRect = new Rectangle(position.x, position.y, extent.x + 10, extent.y + 5);
			gc.dispose();
			i.dispose();
			return new AbsoluteRectangle(lineBBox.rectangle.union(textRect));
		} else {
			return new AbsoluteRectangle(lineBBox);
		}

	}
}