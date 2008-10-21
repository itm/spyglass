package de.uniluebeck.itm.spyglass.plugin.linepainter;

import de.uniluebeck.itm.spyglass.drawing.primitive.Line;

public class LinePainterLine extends Line {
	
	private String stringFormatterResult;
	
	public LinePainterLine() {
		super();
	}
	
	public String getStringFormatterResult() {
		return stringFormatterResult;
	}
	
	public void setStringFormatterResult(final String stringFormatterResult) {
		this.stringFormatterResult = stringFormatterResult;
	}
	
}
