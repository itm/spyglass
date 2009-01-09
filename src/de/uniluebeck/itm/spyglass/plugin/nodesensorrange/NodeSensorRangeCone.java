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
public class NodeSensorRangeCone extends NodeSensorRangeDrawingObject {

	private float orientation;

	private int radius;

	private float viewAngle;

	public float getOrientation() {
		return orientation;
	}

	public void setOrientation(final float orientation) {
		this.orientation = orientation;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(final int radius) {
		this.radius = radius;
	}

	public float getViewAngle() {
		return viewAngle;
	}

	public void setViewAngle(final float viewAngle) {
		this.viewAngle = viewAngle;
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle box = new AbsoluteRectangle();

		box.setUpperLeft(new AbsolutePosition(getPosition().x - radius, getPosition().y - radius));
		box.setWidth(2 * radius);
		box.setHeight(2 * radius);

		return box;

	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		drawBoundingBox(drawingArea, gc);
	}

}
