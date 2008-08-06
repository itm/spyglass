package de.uniluebeck.itm.spyglass.positions;

import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the coordinate's of a single point when using pixels
 * 
 * @author Dariush
 */
public class PixelPosition {
	
	/** The x-coordinate */
	public int x = 0;
	
	/** The y-coordinate */
	public int y = 0;
	
	// --------------------------------------------------------------------------------
	/**
	 * Create a new instance based on an SWT point.
	 * 
	 * @param point
	 */
	public PixelPosition(final Point point) {
		x = point.x;
		y = point.y;
	}
	
	public PixelPosition(final int x, final int y) {
		this.x = x;
		this.y = y;
	}
	
	public PixelPosition(final Point2D point) {
		x = (int) point.getX();
		y = (int) point.getY();
	}
	
	public PixelPosition() {
		//
	}
	
	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}
	
}