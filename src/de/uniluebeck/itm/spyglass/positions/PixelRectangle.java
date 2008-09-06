package de.uniluebeck.itm.spyglass.positions;

/**
 * This class represents an rectangle, messured in absolute pixels.
 * 
 * @author dariush
 * 
 */
public class PixelRectangle {
	
	/**
	 * Height of the rectangle
	 */
	private int height;
	
	/**
	 * Upper left position (reference point) TODO
	 */
	private PixelPosition upperLeft;
	
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
	public PixelPosition getUpperLeft() {
		return upperLeft;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * set the upper left point
	 * 
	 * @param upperLeft
	 */
	public void setUpperLeft(final PixelPosition upperLeft) {
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
	
	public PixelRectangle() {
		
	}
	
	public PixelRectangle(final PixelPosition upperLeft, final int height, final int width) {
		this.height = height;
		this.width = width;
		this.upperLeft = upperLeft;
	}
	
	@Override
	public String toString() {
		return String.format("PixelRectangle {%s, %d, %d}", this.upperLeft, height, width);
	}
	
}