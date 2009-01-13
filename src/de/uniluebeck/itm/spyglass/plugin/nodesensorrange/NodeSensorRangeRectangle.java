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
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RectangleRange;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class NodeSensorRangeRectangle extends NodeSensorRangeDrawingObject {

	private int width;

	private int height;

	private float orientation;

	public int getWidth() {
		return width;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(final float orientation) {
		this.orientation = orientation;
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final int maxLength = (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) + 10;

		final int x = getPosition().x - (maxLength / 2);
		final int y = getPosition().y - (maxLength / 2);

		return new AbsoluteRectangle(new AbsolutePosition(x, y), maxLength, maxLength);

	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		final AbsolutePosition absPos = getPosition();
		final AbsoluteRectangle absRect = new AbsoluteRectangle(new AbsolutePosition(absPos.x - (width / 2), absPos.y - (height / 2)), width, height);
		final PixelRectangle pxRect = drawingArea.absRect2PixelRect(absRect);

		drawRect(gc, pxRect.getUpperLeft().x, pxRect.getUpperLeft().y, pxRect.getWidth(), pxRect.getHeight());

	}

	// --------------------------------------------------------------------------------
	/**
	 * @param gc
	 * @param x
	 *            in pixel coordinates
	 * @param y
	 *            in pixel coordinates
	 * @param width
	 *            in pixel coordinates
	 * @param height
	 *            in pixel coordinates
	 */
	private void drawRect(final GC gc, final int x, final int y, final int width, final int height) {

		// initialize OS resources
		final Transform oldTransform = new Transform(Display.getDefault());
		final Transform transform = new Transform(Display.getDefault());
		final Color background = new Color(Display.getDefault(), getBgColor());
		final Color foreground = new Color(Display.getDefault(), getColor());

		// save old GC values
		gc.getTransform(oldTransform);
		final Color oldForeground = gc.getForeground();
		final Color oldBackground = gc.getBackground();

		// translate to the center of the rectangle
		transform.translate(x + (width / 2), y + (height / 2));
		transform.rotate(-getOrientation());
		gc.setTransform(transform);

		// do the drawing
		gc.setBackground(background);
		gc.setForeground(foreground);
		gc.setAlpha(backgroundAlpha);
		gc.fillRectangle(-(width / 2), -(height / 2), width, height);
		gc.setAlpha(255);
		gc.drawRectangle(-(width / 2), -(height / 2), width, height);

		// restore old GC values
		gc.setTransform(oldTransform);
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);

		// dispose used resources
		oldTransform.dispose();
		transform.dispose();
		background.dispose();
		foreground.dispose();

	}

	@Override
	public void setRange(final NodeSensorRange range) {
		this.width = ((RectangleRange) range).getRectangleWidth();
		this.height = ((RectangleRange) range).getRectangleHeight();
		this.orientation = ((RectangleRange) range).getRectangleOrientation();
	}

}
