package de.uniluebeck.itm.spyglass.positions;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class represents an rectangle, messured in absolute coordinates.
 * 
 * @author dariush
 * 
 */
public class AbsoluteRectangle {
	
	private Rectangle rectangle;
	
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
	 * @return the upper left point
	 */
	@Element(name = "upperLeft")
	public AbsolutePosition getUpperLeft() {
		return new AbsolutePosition(rectangle.x, rectangle.y, 0);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 * 
	 * @param upperLeft
	 */
	@Element(name = "upperLeft")
	public void setUpperLeft(final AbsolutePosition upperLeft) {
		this.rectangle.x = upperLeft.x;
		this.rectangle.y = upperLeft.y;
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
	
	public AbsoluteRectangle() {
		this.rectangle = new Rectangle(0, 0, 0, 0);
	}
	
	public AbsoluteRectangle(final int x, final int y, final int width, final int height) {
		this.rectangle = new Rectangle(x, y, width, height);
	}
	
	public AbsoluteRectangle(final AbsoluteRectangle other) {
		this.rectangle = new Rectangle(other.rectangle.x, other.rectangle.y, other.rectangle.width, other.rectangle.height);
	}
	
	public AbsoluteRectangle(final AbsolutePosition upperLeft, final int height, final int width) {
		this.rectangle = new Rectangle(upperLeft.x, upperLeft.y, width, height);
	}
	
	public boolean contains(final int x, final int y) {
		return this.rectangle.contains(x, y);
	}
	
	public boolean contains(final Point p) {
		return this.rectangle.contains(p);
	}
	
	/**
	 * Returns a new rectangle which represents the union of the receiver and the given rectangle.
	 * 
	 * The union of two rectangles is the smallest single rectangle that completely covers both of
	 * the areas covered by the two given rectangles.
	 * 
	 * @param other
	 *            the rectangle to perform union with
	 * @return the union of the receiver and the argument
	 */
	public AbsoluteRectangle union(final AbsoluteRectangle other) {
		final AbsoluteRectangle ret = new AbsoluteRectangle();
		ret.rectangle = this.rectangle.union(other.rectangle);
		return ret;
	}
	
	public boolean contains(final AbsoluteRectangle rect) {
		final Rectangle intersection = this.rectangle.intersection(rect.rectangle);
		return (intersection.x == rect.rectangle.x) && (intersection.y == rect.rectangle.y) && (intersection.width == rect.rectangle.width)
				&& (intersection.height == rect.rectangle.height);
	}
	
	public boolean intersects(final AbsolutePosition upperLeft, final int height, final int width) {
		return this.rectangle.intersects(upperLeft.x, upperLeft.y, width, height);
	}
	
	public boolean intersects(final AbsoluteRectangle rect) {
		return this.rectangle.intersects(rect.rectangle);
	}
	
	public void translate(final int x, final int y) {
		this.rectangle.x += x;
		this.rectangle.y += y;
	}
	
	public void setSize(final int width, final int height) {
		this.rectangle.width = width;
		this.rectangle.height = height;
		
	}
	
}