package de.uniluebeck.itm.spyglass.positions;

import org.eclipse.swt.graphics.Rectangle;
import org.simpleframework.xml.Element;

/**
 * This class represents an rectangle, messured in pixel coordinates.
 * 
 * @author Dariush Forouher
 * 
 */
public class PixelRectangle extends AbstractRectangle {
	
	public PixelRectangle() {
		super();
	}
	
	public PixelRectangle(final AbstractRectangle other) {
		super(other);
	}
	
	public PixelRectangle(final Rectangle other) {
		super();
		this.rectangle = other;
	}
	
	public PixelRectangle(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}
	
	public PixelRectangle(final PixelPosition upperLeft, final int width, final int height) {
		super(upperLeft.x, upperLeft.y, width, height);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the upper left point
	 */
	@Element(name = "upperLeft")
	public PixelPosition getUpperLeft() {
		return new PixelPosition(rectangle.x, rectangle.y);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 * 
	 * @param upperLeft
	 */
	@Element(name = "upperLeft")
	public void setUpperLeft(final PixelPosition upperLeft) {
		this.rectangle.x = upperLeft.x;
		this.rectangle.y = upperLeft.y;
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
	public PixelRectangle union(final PixelRectangle other) {
		final PixelRectangle ret = new PixelRectangle();
		ret.rectangle = this.rectangle.union(other.rectangle);
		return ret;
	}
	
	public Rectangle toSWTRectangle() {
		return rectangle;
	}
	
	/**
	 * Returns a new rectangle which represents the intersection of the receiver and the given
	 * rectangle.
	 * 
	 * The intersection of two rectangles is the rectangle that covers the area which is contained
	 * within both rectangles.
	 * 
	 * @param other
	 *            the rectangle to perform the intersect with
	 * @return the intersection of the receiver and the argument
	 */
	public PixelRectangle intersection(final PixelRectangle other) {
		final PixelRectangle ret = new PixelRectangle();
		ret.rectangle = this.rectangle.intersection(other.rectangle);
		return ret;
	}
}