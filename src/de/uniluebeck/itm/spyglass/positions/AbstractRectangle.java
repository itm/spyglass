package de.uniluebeck.itm.spyglass.positions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.simpleframework.xml.Attribute;

/**
 * This class represents an abstract rectangle
 * 
 * @author Dariush Forouher, Sebastian Ebers
 * 
 */
public abstract class AbstractRectangle {

	public Rectangle rectangle;

	// --------------------------------------------------------------------------------
	/**
	 * @return the height of the rectangle
	 */
	@Attribute(name = "height")
	public int getHeight() {
		return rectangle.height;
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the height
	 * 
	 * @param height
	 */
	@Attribute(name = "height")
	public void setHeight(final int height) {
		this.rectangle.height = height;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the width
	 */
	@Attribute(name = "width")
	public int getWidth() {
		return rectangle.width;
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the width
	 * 
	 * @param width
	 */
	@Attribute(name = "width")
	public void setWidth(final int width) {
		this.rectangle.width = width;
	}

	public AbstractRectangle() {
		this.rectangle = new Rectangle(0, 0, 0, 0);
	}

	public AbstractRectangle(final int x, final int y, final int width, final int height) {
		this.rectangle = new Rectangle(x, y, width, height);
	}

	public AbstractRectangle(final AbstractRectangle other) {
		this.rectangle = new Rectangle(other.rectangle.x, other.rectangle.y, other.rectangle.width, other.rectangle.height);
	}

	public AbstractRectangle(final Rectangle other) {
		this.rectangle = new Rectangle(other.x, other.y, other.width, other.height);
	}


	public boolean contains(final int x, final int y) {
		return this.rectangle.contains(x, y);
	}

	public boolean contains(final double x, final double y) {
		return this.rectangle.contains((int) x, (int) y);
	}

	public boolean contains(final Point p) {
		return this.rectangle.contains(p);
	}

	public boolean contains(final AbstractRectangle rect) {
		final Rectangle intersection = this.rectangle.intersection(rect.rectangle);
		return (intersection.x == rect.rectangle.x) && (intersection.y == rect.rectangle.y) && (intersection.width == rect.rectangle.width)
				&& (intersection.height == rect.rectangle.height);
	}

	public boolean intersects(final AbstractRectangle rect) {
		return this.rectangle.intersects(rect.rectangle);
	}

	public boolean intersects(final Rectangle rect) {
		return this.rectangle.intersects(rect);
	}

	public void translate(final int x, final int y) {
		this.rectangle.x += x;
		this.rectangle.y += y;
	}

	public void setSize(final int width, final int height) {
		this.rectangle.width = width;
		this.rectangle.height = height;

	}

	@Override
	public String toString() {
		return this.rectangle.toString();
	}
	
	public abstract AbstractPosition getUpperLeft();
	
	/**
	 * Performs the given Transformation on this Rectangle.
	 * 
	 * @param a transform
	 */
	public void transform(final AffineTransform at) {

		final Rectangle newRect = new Rectangle(0,0,0,0);
		
		final Point2D upLeft = at.transform(this.getUpperLeft().toPoint2D(), null);
		newRect.x = (int) Math.floor(upLeft.getX());
		newRect.y = (int) Math.floor(upLeft.getY());
		
		final Point2D lowerRightAbs = new Point2D.Double(this.getUpperLeft().x + this.getWidth(), this.getUpperLeft().y + this.getHeight());
		final Point2D lRight = at.transform(lowerRightAbs, null);

		final int lowerRightX = (int) Math.floor(lRight.getX() + 1);
		final int lowerRightY = (int) Math.floor(lRight.getY() + 1);
		
		newRect.width = Math.abs(lowerRightX - newRect.x);
		newRect.height = Math.abs(newRect.y - lowerRightY);
		
		this.rectangle = newRect;

	}


}