package de.uniluebeck.itm.spyglass.plugin.linepainter;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
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
	
	public void setStringFormatterResult(final String stringFormatterResult,
			final boolean fireBoundingBoxChangeEvent) {
		this.stringFormatterResult = stringFormatterResult;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}
	
	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		// TODO bounding box neu berechnen wenn sich
		// stringformatter ändert
		return super.calculateBoundingBox();
	}
	
}
