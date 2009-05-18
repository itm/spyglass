// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.CircleRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.ConeRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RectangleRange;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Flag class for DrawingObjects painted by NodeSensorRangePlugin
 * 
 * @author bimschas
 */
public class NodeSensorRangeDrawingObject extends DrawingObject implements PropertyChangeListener {

	private static final Logger log = SpyglassLoggerFactory.getLogger(NodeSensorRangeDrawingObject.class);
	private NodeSensorRangePlugin plugin;

	public NodeSensorRangeDrawingObject(final NodeSensorRangePlugin plugin, final Config config) {

		super();

		this.plugin = plugin;
		this.config = config;
		this.config.addPropertyChangeListener(this);

		setBgColor(config.getBackgroundRGB());
		setColor(config.getColorRGB());

	}

	private enum RangeType {
		CIRCLE, CONE, RECTANGLE
	}

	private Config config;

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final AbsoluteRectangle box = new AbsoluteRectangle();
		final NodeSensorRange range = config.getRange();
		final RangeType rangeType = range instanceof RectangleRange ? RangeType.RECTANGLE : range instanceof CircleRange ? RangeType.CIRCLE
				: RangeType.CONE;

		switch (rangeType) {

			case CIRCLE:
			case CONE:
				final int radius = rangeType == RangeType.CIRCLE ? ((CircleRange) config.getRange()).getCircleRadius() : ((ConeRange) config
						.getRange()).getConeRadius();
				final int lineWidth = config.getLineWidth();
				// make it a little larger so that lineWidth fragments aren't so bad
				box.setLowerLeft(new AbsolutePosition(getPosition().x - radius - lineWidth * 5, getPosition().y - radius - lineWidth * 5));
				box.setWidth(2 * radius + 10 * lineWidth);
				box.setHeight(2 * radius + 10 * lineWidth);
				return box;

			case RECTANGLE:
				final int width = ((RectangleRange) config.getRange()).getRectangleWidth();
				final int height = ((RectangleRange) config.getRange()).getRectangleHeight();
				// calculate length of hypotenuse
				final int hypLength = (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) + 10;
				final int x = getPosition().x - (hypLength / 2);
				final int y = getPosition().y - (hypLength / 2);
				box.setLowerLeft(new AbsolutePosition(x, y));
				box.setWidth(hypLength);
				box.setHeight(hypLength);
				return box;

			default:
				throw new RuntimeException("We should never reach this code block!");

		}

	}

	@Override
	public void draw(final GC gc) {

		final AbsoluteRectangle absRect;
		final PixelRectangle pxRect;
		final PixelPosition pxPos;
		final int radius;
		final int width;
		final int height;

		final NodeSensorRange range = config.getRange();
		final RangeType rangeType = range instanceof RectangleRange ? RangeType.RECTANGLE : range instanceof CircleRange ? RangeType.CIRCLE
				: RangeType.CONE;

		switch (rangeType) {

			case CIRCLE:
				radius = ((CircleRange) range).getCircleRadius();
				// calculate the real size of the bounding box
				absRect = new AbsoluteRectangle(getPosition(), radius * 2, radius * 2);
				absRect.rectangle.y -= absRect.rectangle.height; // HACK
				pxRect = getDrawingArea().absRect2PixelRect(absRect);
				pxPos = pxRect.getUpperLeft();
				drawCircle(gc, pxPos.x, pxPos.y, pxRect.getHeight() / 2);
				break;

			case CONE:
				radius = ((ConeRange) range).getConeRadius();
				absRect = new AbsoluteRectangle(getPosition(), radius * 2, radius * 2);
				absRect.rectangle.y -= absRect.rectangle.height; // HACK
				pxRect = getDrawingArea().absRect2PixelRect(absRect);
				pxPos = pxRect.getUpperLeft();
				drawCone(gc, pxPos.x, pxPos.y, pxRect.getHeight() / 2);
				break;

			case RECTANGLE:
				width = ((RectangleRange) range).getRectangleWidth();
				height = ((RectangleRange) range).getRectangleHeight();
				absRect = new AbsoluteRectangle(new AbsolutePosition(getPosition().x - (width / 2), getPosition().y - (height / 2)), width, height);
				pxRect = getDrawingArea().absRect2PixelRect(absRect);
				pxPos = pxRect.getUpperLeft();
				drawRect(gc, pxPos.x, pxPos.y, pxRect.getWidth(), pxRect.getHeight());
				break;

		}

	}

	private void drawCircle(final GC gc, final int x, final int y, final int radius) {

		// get config values
		final int backgroundAlpha = config.getBackgroundAlpha();
		final int lineWidth = config.getLineWidth();

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
		final int oldLineWidth = gc.getLineWidth();
		final boolean advancedSubsystem = gc.getAdvanced();

		// set colors and so
		gc.setForeground(foreground);
		gc.setBackground(background);
		gc.setLineWidth(lineWidth);

		// translate so that x,y is 0,0 of coordinate system
		transform.translate(x, y);
		gc.setTransform(transform);

		// do the drawing
		gc.setAlpha(backgroundAlpha);
		gc.fillArc(-radius, -radius, radius * 2, radius * 2, 0, 360);
		gc.setAlpha(255);
		gc.drawArc(-radius, -radius, radius * 2, radius * 2, 0, 360);

		// restore old GC status
		gc.setLineWidth(oldLineWidth);
		gc.setAlpha(oldAlpha);
		gc.setTransform(oldTransform);
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);
		gc.setAdvanced(advancedSubsystem);

		// dispose OS resources
		oldTransform.dispose();
		transform.dispose();
		foreground.dispose();
		background.dispose();

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

		// get config values
		final int backgroundAlpha = config.getBackgroundAlpha();
		final int orientation = ((ConeRange) config.getRange()).getConeOrientation();
		final int viewAngle = ((ConeRange) config.getRange()).getConeViewAngle();
		final int lineWidth = config.getLineWidth();

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
		final int oldLineWidth = gc.getLineWidth();
		final boolean advancedSubsystem = gc.getAdvanced();

		// set colors and so
		gc.setForeground(foreground);
		gc.setBackground(background);
		gc.setLineWidth(lineWidth);

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
		gc.setLineWidth(oldLineWidth);
		gc.setAlpha(oldAlpha);
		gc.setTransform(oldTransform);
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);
		gc.setAdvanced(advancedSubsystem);

		// dispose OS resources
		oldTransform.dispose();
		transform.dispose();
		foreground.dispose();
		background.dispose();

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

		// get config values
		final int backgroundAlpha = config.getBackgroundAlpha();
		final int orientation = ((RectangleRange) config.getRange()).getRectangleOrientation();
		final int lineWidth = config.getLineWidth();

		// initialize OS resources
		final Transform oldTransform = new Transform(Display.getDefault());
		final Transform transform = new Transform(Display.getDefault());
		final Color background = new Color(Display.getDefault(), getBgColor());
		final Color foreground = new Color(Display.getDefault(), getColor());

		// save old GC values
		gc.getTransform(oldTransform);
		final Color oldForeground = gc.getForeground();
		final Color oldBackground = gc.getBackground();
		final int oldLineWidth = gc.getLineWidth();
		final boolean advancedSubsystem = gc.getAdvanced();

		// translate to the center of the rectangle
		transform.translate(x + (width / 2), y + (height / 2));
		transform.rotate(-orientation);
		gc.setTransform(transform);

		// set colors and so
		gc.setBackground(background);
		gc.setForeground(foreground);
		gc.setLineWidth(lineWidth);

		// do the drawing
		gc.setAlpha(backgroundAlpha);
		gc.fillRectangle(-(width / 2), -(height / 2), width, height);
		gc.setAlpha(255);
		gc.drawRectangle(-(width / 2), -(height / 2), width, height);

		// restore old GC values
		gc.setLineWidth(oldLineWidth);
		gc.setTransform(oldTransform);
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);
		gc.setAdvanced(advancedSubsystem);

		// dispose used resources
		oldTransform.dispose();
		transform.dispose();
		background.dispose();
		foreground.dispose();

	}

	public synchronized Config getConfig() {
		return config;
	}

	public synchronized void setConfig(final Config config) {
		this.config = config;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e) {

		final boolean isRange = NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE.equals(e.getPropertyName());
		final boolean isBackground = NodeSensorRangeXMLConfig.PROPERTYNAME_BACKGROUND_R_G_B.equals(e.getPropertyName());
		final boolean isForeground = NodeSensorRangeXMLConfig.PROPERTYNAME_COLOR_R_G_B.equals(e.getPropertyName());

		if (isRange) {
			markBoundingBoxDirty();
		} else if (isBackground) {
			final int[] color = (int[]) e.getNewValue();
			setBgColor(new RGB(color[0], color[1], color[2]));
		} else if (isForeground) {
			final int[] color = (int[]) e.getNewValue();
			setColor(new RGB(color[0], color[1], color[2]));
		}

		markContentDirty();
	}

}
