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
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.ConeRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class NodeSensorRangeCone extends NodeSensorRangeDrawingObject {

	private int orientation;

	private int radius;

	private int viewAngle;

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(final int orientation) {
		this.orientation = orientation;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}

	public int getViewAngle() {
		return viewAngle;
	}

	public void setViewAngle(final int viewAngle) {
		this.viewAngle = viewAngle;
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle box = new AbsoluteRectangle();

		box.setUpperLeft(new AbsolutePosition(getPosition().x - radius - 5, getPosition().y - radius - 5));
		box.setWidth((2 * radius) + 10);
		box.setHeight((2 * radius) + 10);

		return box;

	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		final AbsoluteRectangle absRect = new AbsoluteRectangle(getPosition(), radius * 2, radius * 2);
		final PixelRectangle pxRect = drawingArea.absRect2PixelRect(absRect);
		final PixelPosition pxPos = pxRect.getUpperLeft();

		drawCone(gc, pxPos.x, pxPos.y, pxRect.getHeight() / 2);

	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 * @param gc
	 * @param x
	 *            in pixel coordinates
	 * @param y
	 *            in pixel coordinates
	 * @param radius
	 *            in pixel coordinates
	 */
	private void drawCone(final GC gc, final int x, final int y, final int radius) {

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
		transform.rotate(-(orientation));
		gc.setTransform(transform);
		gc.drawLine(0, 0, radius, 0);
		gc.setAlpha(backgroundAlpha);
		gc.fillArc(-radius, -radius, radius * 2, radius * 2, 0, viewAngle);
		gc.setAlpha(255);
		gc.drawArc(-radius, -radius, radius * 2, radius * 2, 0, viewAngle);
		transform.rotate(-(viewAngle));
		gc.setTransform(transform);
		gc.drawLine(0, 0, radius, 0);

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
		this.orientation = ((ConeRange) range).getConeOrientation();
		this.radius = ((ConeRange) range).getConeRadius();
		this.viewAngle = ((ConeRange) range).getConeViewAngle();
	}

}
