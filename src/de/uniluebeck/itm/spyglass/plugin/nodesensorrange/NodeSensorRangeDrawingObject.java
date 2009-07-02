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
 * Class for DrawingObjects painted by NodeSensorRangePlugin. Can paint
 * <ul>
 * <li>a rectangle of arbitrary size, rotatable by any degree</li>
 * <li>a circle of arbitrary size</li>
 * <li>a cone with an arbitrary angle, rotatable by any degree</li>
 * </ul>
 * 
 * @author Daniel Bimschas
 */
public class NodeSensorRangeDrawingObject extends DrawingObject {

	private static Logger log = SpyglassLoggerFactory.getLogger(NodeSensorRangeDrawingObject.class);

	private enum RangeType {
		CIRCLE, CONE, RECTANGLE
	}

	private Config config;

	private PropertyChangeListener configPropertyChangeListener = new PropertyChangeListener() {
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
	};

	private boolean initCalled = false;

	/**
	 * To be called by the plug-in before (!) adding it to the layer
	 */
	public void init(final Config config) {
		initCalled = true;
		setConfig(config);
	}

	/**
	 * To be called by the plug-in before removing it from the layer
	 */
	public void shutdown() {
		this.config.removePropertyChangeListener(configPropertyChangeListener);
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		if (!initCalled) {
			log.debug("Fuck it");
		}

		final AbsoluteRectangle absBox = new AbsoluteRectangle();
		final NodeSensorRange range = config.getRange();
		final RangeType rangeType = range instanceof RectangleRange ? RangeType.RECTANGLE : range instanceof CircleRange ? RangeType.CIRCLE
				: RangeType.CONE;
		final PixelRectangle pxBox;
		final int lineWidth = config.getLineWidth();

		switch (rangeType) {

			case CIRCLE:
			case CONE:
				final int radius = rangeType == RangeType.CIRCLE ? ((CircleRange) config.getRange()).getCircleRadius() : ((ConeRange) config
						.getRange()).getConeRadius();

				// calculate box in absolute coordinates resulting from radius
				absBox.setLowerLeft(new AbsolutePosition(getPosition().x - radius, getPosition().y - radius));
				absBox.setWidth(2 * radius);
				absBox.setHeight(2 * radius);

				// now transform to pixel coordinates and add the width of the line
				pxBox = getDrawingArea().absRect2PixelRect(absBox);
				pxBox.rectangle.x -= 2 * lineWidth;
				pxBox.rectangle.y -= 2 * lineWidth;
				pxBox.rectangle.width += 4 * lineWidth;
				pxBox.rectangle.height += 4 * lineWidth;

				// transform back to absolute coordinates and return
				return getDrawingArea().pixelRect2AbsRect(pxBox);

			case RECTANGLE:
				final int width = ((RectangleRange) config.getRange()).getRectangleWidth();
				final int height = ((RectangleRange) config.getRange()).getRectangleHeight();
				// calculate length of hypotenuse
				final int hypLength = (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) + 10;
				final int x = getPosition().x - (hypLength / 2);
				final int y = getPosition().y - (hypLength / 2);
				absBox.setLowerLeft(new AbsolutePosition(x, y));
				absBox.setWidth(hypLength);
				absBox.setHeight(hypLength);

				// now transform to pixel coordinates and add the width of the line
				pxBox = getDrawingArea().absRect2PixelRect(absBox);
				pxBox.rectangle.x -= lineWidth;
				pxBox.rectangle.y -= lineWidth;
				pxBox.rectangle.width += 2 * lineWidth;
				pxBox.rectangle.height += 2 * lineWidth;

				// transform back to absolute coordinates and return
				return getDrawingArea().pixelRect2AbsRect(pxBox);

			default:
				throw new RuntimeException("We should never reach this code block!");

		}

	}

	@Override
	public void draw(final GC gc) {

		if (!initCalled) {
			System.out.println("Fuck it");
		}

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

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public Config getConfig() {
		return config;
	}

	public void setConfig(final Config c) {

		if (!initCalled) {
			log.debug("Fuck it");
		}

		if (this.config != null) {
			this.config.removePropertyChangeListener(configPropertyChangeListener);
		}
		this.config = c;
		this.config.addPropertyChangeListener(configPropertyChangeListener);
		setColor(c.getColorRGB());
		setBgColor(c.getBackgroundRGB());
		markBoundingBoxDirty();
	}

}
