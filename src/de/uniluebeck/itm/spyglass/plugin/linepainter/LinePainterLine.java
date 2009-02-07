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

	public String getStringFormatterResult() {
		return stringFormatterResult;
	}

	public void setStringFormatterResult(final String stringFormatterResult) {
		setStringFormatterResult(stringFormatterResult, true);
	}

	public void setStringFormatterResult(final String stringFormatterResult, final boolean fireBoundingBoxChangeEvent) {
		this.stringFormatterResult = stringFormatterResult;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		super.draw(drawingArea, gc);
		final Point p = determineStringFormatterPosition(getBoundingBox());
		gc.drawString(stringFormatterResult, p.x, p.y);
		// drawBoundingBox(drawingArea, gc);
	}

	private Point determineStringFormatterPosition(final AbsoluteRectangle lineBBox) {
		if (drawingArea == null) {
			return new Point(0, 0);
		}
		return Geometry.centerPoint(drawingArea.absRect2PixelRect(lineBBox).rectangle);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle lineBBox = super.calculateBoundingBox();

		final Image i = new Image(null, 1,1);
		final GC gc = new GC(i);
		final Point extent = gc.textExtent(stringFormatterResult);
		final Point position = determineStringFormatterPosition(lineBBox);
		final Rectangle textRect = new Rectangle(position.x, position.y, extent.x + 10, extent.y + 5);
		setBoundingBox(new AbsoluteRectangle(lineBBox.rectangle.union(textRect)));
		gc.dispose();
		i.dispose();

		return getBoundingBox();

	}
}
