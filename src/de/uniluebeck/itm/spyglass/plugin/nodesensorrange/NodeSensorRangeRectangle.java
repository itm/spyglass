// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

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

		final AbsoluteRectangle box = new AbsoluteRectangle();

		final int x = getPosition().x - (width / 2);
		final int y = getPosition().y - (height / 2);

		box.setUpperLeft(new AbsolutePosition(x, y));
		box.setWidth(width);
		box.setHeight(height);

		return box;

	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		// TODO implement
		drawBoundingBox(drawingArea, gc);
	}

}
