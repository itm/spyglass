// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.CircleRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class NodeSensorRangeCircle extends NodeSensorRangeDrawingObject {

	private int radius = 1;

	public int getRadius() {
		return radius;
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle box = new AbsoluteRectangle();

		// make it a little larger so that errors from rounding aren't so bad
		box.setUpperLeft(new AbsolutePosition(getPosition().x - radius - 5, getPosition().y - radius - 5));
		box.setWidth(2 * radius + 10);
		box.setHeight(2 * radius + 10);

		return box;

	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		// calculate the real size of the bounding box
		final AbsoluteRectangle absRect = new AbsoluteRectangle(getPosition(), radius * 2, radius * 2);
		final PixelRectangle pxRect = drawingArea.absRect2PixelRect(absRect);
		final PixelPosition pxPos = pxRect.getUpperLeft();

		drawCircle(gc, pxPos.x, pxPos.y, pxRect.getHeight() / 2);
		drawBoundingBox(drawingArea, gc);

	}

	private void drawCircle(final GC gc, final int x, final int y, final int radius) {

		// initialize OS resources
		final Transform oldTransform = new Transform(Display.getDefault());
		final Transform transform = new Transform(Display.getDefault());
		final Color foreground = new Color(Display.getDefault(), getColor());
		final Color background = new Color(Display.getDefault(), getBgColor());

		// save the old GC status
		gc.getTransform(oldTransform);
		final int oldAlpha = gc.getAlpha();
		final Color oldForeground = gc.getForeground();
		final Color oldBackground = gc.getBackground();

		// set colors
		gc.setForeground(foreground);
		gc.setBackground(background);

		// translate so that x,y is 0,0 of coordinate system
		transform.translate(x, y);
		gc.setTransform(transform);

		// do the drawing
		gc.setAlpha(backgroundAlpha);
		gc.fillArc(-radius, -radius, radius * 2, radius * 2, 0, 360);
		gc.setAlpha(255);
		gc.drawArc(-radius, -radius, radius * 2, radius * 2, 0, 360);

		// restore old GC status
		gc.setAlpha(oldAlpha);
		gc.setTransform(oldTransform);
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);

		// dispose OS resources
		oldTransform.dispose();
		transform.dispose();
		foreground.dispose();
		background.dispose();
	}

	@Override
	public void setRange(final NodeSensorRange range) {
		this.radius = ((CircleRange) range).getCircleRadius();
	}

}
