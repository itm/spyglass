package de.uniluebeck.itm.spyglass.positions;

import org.eclipse.swt.graphics.Rectangle;
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

	public AbsoluteRectangle(final Rectangle other) {
		super(other);
	}

	public AbsoluteRectangle(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}

	public AbsoluteRectangle(final AbsolutePosition lowerLeft, final int width, final int height) {
		super(lowerLeft.x, lowerLeft.y, width, height);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the lower left point
	 */
	public AbsolutePosition getLowerLeft() {
		return new AbsolutePosition(rectangle.x, rectangle.y, 0);
	}

	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 *
	 * @param upperLeft
	 */
	public void setLowerLeft(final AbsolutePosition upperLeft) {
		this.rectangle.x = upperLeft.x;
		this.rectangle.y = upperLeft.y;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the coordinates of the center point
	 */
	public AbsolutePosition getCenter() {
		return new AbsolutePosition(rectangle.x + rectangle.width / 2, rectangle.y
				+ rectangle.height / 2, 0);
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
	public AbsoluteRectangle intersection(final AbsoluteRectangle other) {
		final AbsoluteRectangle ret = new AbsoluteRectangle();
		ret.rectangle = this.rectangle.intersection(other.rectangle);
		return ret;
	}

	/**
	 * Inherits the values of another rectangle
	 *
	 * @param other
	 *            the tectangle which values are to be inherited
	 */
	public void inherit(final AbsoluteRectangle other) {
		this.rectangle.x = other.rectangle.x;
		this.rectangle.y = other.rectangle.y;
		this.rectangle.height = other.rectangle.height;
		this.rectangle.width = other.rectangle.width;
	}

	public boolean equals(final AbsoluteRectangle other) {
		return other == null ? false : rectangle.equals(other.rectangle);
	}

}