package de.uniluebeck.itm.spyglass.positions;

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
	
	protected Rectangle rectangle;
	
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
		this.rectangle = new Rectangle(other.rectangle.x, other.rectangle.y, other.rectangle.width,
				other.rectangle.height);
	}
	
	public boolean contains(final int x, final int y) {
		return this.rectangle.contains(x, y);
	}
	
	public boolean contains(final Point p) {
		return this.rectangle.contains(p);
	}
	
	public boolean contains(final AbstractRectangle rect) {
		final Rectangle intersection = this.rectangle.intersection(rect.rectangle);
		return (intersection.x == rect.rectangle.x) && (intersection.y == rect.rectangle.y)
				&& (intersection.width == rect.rectangle.width)
				&& (intersection.height == rect.rectangle.height);
	}
	
	public boolean intersects(final AbstractRectangle rect) {
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
	
	@Override
	public String toString() {
		return this.rectangle.toString();
	}
	
}