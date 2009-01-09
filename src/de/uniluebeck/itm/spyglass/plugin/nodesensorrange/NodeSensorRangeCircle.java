// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class NodeSensorRangeCircle extends NodeSensorRangeDrawingObject {

	private float radius = 1;

	private RGB color = new RGB(0, 0, 0);

	public float getRadius() {
		return radius;
	}

	public void setRadius(final float radius) {
		this.radius = radius;
	}

	@Override
	public RGB getColor() {
		return color;
	}

	@Override
	public void setColor(final RGB color) {
		this.color = color;
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle box = new AbsoluteRectangle();

		box.setUpperLeft(new AbsolutePosition(getPosition().x - (int) radius - 5, getPosition().y - (int) radius - 5));
		box.setWidth((2 * (int) radius) + 10);
		box.setHeight((2 * (int) radius) + 10);

		return box;

	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		// calculate "real" bounding box (bounding box from calculateBoundingBox is a little larger)
		final AbsoluteRectangle realBox = new AbsoluteRectangle();
		realBox.setUpperLeft(new AbsolutePosition(getPosition().x - (int) radius, getPosition().y - (int) radius));
		realBox.setWidth(2 * (int) radius);
		realBox.setHeight(2 * (int) radius);

		// get positions
		final PixelPosition pxPos = drawingArea.absPoint2PixelPoint(getPosition());
		final PixelRectangle pxRealBoundingBox = drawingArea.absRect2PixelRect(realBox);

		// save GC values
		final int oldAlpha = gc.getAlpha();
		final Color oldForeground = gc.getForeground();
		final Color oldBackground = gc.getBackground();
		final int oldLineWidth = gc.getLineWidth();

		// create colors
		final Color bgColor = new Color(Display.getDefault(), 0, 140, 0);
		final Color lineColor = new Color(Display.getDefault(), color.red, color.green, color.blue);

		// calculate parameters for circle
		final int x = pxPos.x - (pxRealBoundingBox.getWidth() / 2);
		final int y = pxPos.y - (pxRealBoundingBox.getHeight() / 2);
		final int width = pxRealBoundingBox.getWidth();
		final int height = pxRealBoundingBox.getHeight();
		final int startAngle = 0;
		final int arcAngle = 360;

		// draw background
		gc.setBackground(bgColor);
		gc.setAlpha(40);
		gc.fillArc(x, y, width, height, startAngle, arcAngle);

		// draw line
		gc.setLineWidth(1);
		gc.setForeground(lineColor);
		gc.setAlpha(255);
		gc.drawArc(x, y, width, height, startAngle, arcAngle);

		// restore GC values
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);
		gc.setAlpha(oldAlpha);
		gc.setLineWidth(oldLineWidth);

		// dispose resources
		bgColor.dispose();
		lineColor.dispose();

	}
}
