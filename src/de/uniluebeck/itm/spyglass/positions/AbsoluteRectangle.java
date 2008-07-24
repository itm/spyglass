package de.uniluebeck.itm.spyglass.positions;

/**
 * This class represents an rectangle, messured in absolute coordinates.
 * 
 * @author dariush
 * 
 */
public class AbsoluteRectangle {
	
	/**
	 * Height of the rectangle
	 */
	private int height;
	
	/**
	 * Upper left position (reference point)
	 */
	private AbsolutePosition upperLeft;
	
	/**
	 * Width of the rectangle
	 */
	private int width;
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the height of the rectangle
	 */
	public int getHeight() {
		return height;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * set the height
	 * 
	 * @param height
	 */
	public void setHeight(final int height) {
		this.height = height;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the upper left point
	 */
	public AbsolutePosition getUpperLeft() {
		return upperLeft;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 * 
	 * @param upperLeft
	 */
	public void setUpperLeft(final AbsolutePosition upperLeft) {
		this.upperLeft = upperLeft;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * set the width
	 * 
	 * @param width
	 */
	public void setWidth(final int width) {
		this.width = width;
	}
	
	public AbsoluteRectangle() {
		
	}
	
	public AbsoluteRectangle(final AbsolutePosition upperLeft, final int height, final int width) {
		this.height = height;
		this.width = width;
		this.upperLeft = upperLeft;
	}
	
}