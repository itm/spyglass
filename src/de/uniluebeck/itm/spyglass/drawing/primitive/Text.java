package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

public class Text extends DrawingObject {

	public String text;

	public enum TextJustification {
		center, right, left
	}

	public TextJustification justification;

	public Text(final String s, final AbsolutePosition p, final int id) {
		setPosition(p);
		text = s;
		justification = TextJustification.left;
	}

	public void setText(final String s) {
		text = s;
	}

	public String getText() {
		return text;
	}

	public TextJustification getJustification() {
		return justification;
	}

	public void setJustification(final TextJustification justification) {
		this.justification = justification;
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		final Color color = new Color(null, this.getColor());
		final Color bgColor = new Color(null, this.getBgColor());
		final Font f = new Font(gc.getDevice(), "Arial", 6, SWT.NORMAL);

		final PixelPosition px = drawingArea.absPoint2PixelPoint(this.getPosition());

		gc.setFont(f);
		gc.setForeground(color);
		gc.setBackground(bgColor);
		final String s = getText();
		int offsetX = 0;
		final Point p = gc.stringExtent(s);
		if (getJustification() == TextJustification.center) {
			offsetX = (p.x / -2) + 1;
		} else if (getJustification() == TextJustification.right) {
			offsetX = -(p.x) + 1;
		}
		final int offsetY = p.y / -2;
		gc.drawString(s, (px.x) + offsetX, (px.y) + offsetY);
		// TODO: Implement the drawing of the line primitive
		color.dispose();
		bgColor.dispose();
		f.dispose();
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

}
