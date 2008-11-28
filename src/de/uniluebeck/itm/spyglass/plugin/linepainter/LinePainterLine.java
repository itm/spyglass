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
		// TODO paint stringformatter result
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		// TODO bounding box neu berechnen wenn sich
		// stringformatter ändert
		return super.calculateBoundingBox();
	}

}
