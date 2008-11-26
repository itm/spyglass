package de.uniluebeck.itm.spyglass.plugin.linepainter;

import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public class LinePainterLine extends Line {

	private String stringFormatterResult;

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
		// final AbsolutePosition pos = getPosition();
		// final AbsolutePosition end = getEnd();
		// final PixelPosition pxpos = drawingArea.absPoint2PixelPoint(pos);
		// final PixelPosition pxend = drawingArea.absPoint2PixelPoint(end);
		// final String posString = "(" + pos.x + "," + pos.y + ")" + " => " + "(" + pxpos.x + ","
		// + pxpos.y + ")";
		// gc.drawText(posString, pxpos.x, pxpos.y);
		// final String endString = "(" + end.x + "," + end.y + ")" + " => " + "(" + pxend.x + ","
		// + pxend.y + ")";
		// gc.drawText(endString, pxend.x, pxend.y);
		// final PixelRectangle bb = drawingArea.absRect2PixelRect(getBoundingBox());
		// gc.drawText("(" + bb.getUpperLeft().x + "," + bb.getUpperLeft().y + ":" + bb.getWidth()
		// + "x" + bb.getHeight() + ")", 0, 0);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		// TODO bounding box neu berechnen wenn sich
		// stringformatter ändert
		return super.calculateBoundingBox();
	}

}
