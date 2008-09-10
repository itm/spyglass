package de.uniluebeck.itm.spyglass.positions;

import org.simpleframework.xml.Element;

/**
 * This class represents an rectangle, messured in absolute coordinates.
 * 
 * @author Dariush Forouher
 * 
 */
public class AbsoluteRectangle extends AbstractRectangle {
	
	public AbsoluteRectangle() {
		super();
	}
	
	public AbsoluteRectangle(final AbstractRectangle other) {
		super(other);
	}
	
	public AbsoluteRectangle(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}
	
	public AbsoluteRectangle(final AbsolutePosition upperLeft, final int width, final int height) {
		super(upperLeft.x, upperLeft.y, width, height);
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
	
}