package de.uniluebeck.itm.spyglass.positions;

import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the coordinate's of a single point when using pixels
 * 
 * @author Dariush Forouher
 */
public class PixelPosition extends AbstractPosition {

	// --------------------------------------------------------------------------------
	/**
	 * Create a new instance based on an SWT point.
	 * 
	 * @param point
	 */
	public PixelPosition(final Point point) {
		super(point.x, point.y, 0);
	}

	public PixelPosition(final int x, final int y) {
		super(x, y, 0);
	}

	public PixelPosition(final Point2D point) {
		super(point);
	}

	public PixelPosition() {
		//
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void mult(final double d) {
		x = (int) (x * d);
		y = (int) (y * d);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void add(final AbstractPosition p) {
		x = x + p.x;
		y = y + p.y;
	}

}