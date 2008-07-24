package de.uniluebeck.itm.spyglass.positions;

import org.simpleframework.xml.Attribute;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the coordinate's of a single point when using absolute
 * coordinates
 * 
 * @author Sebastian Ebers
 * 
 */
public class AbsolutePosition {
	
	@Attribute
	/* The x-coordinate */
	public int x = 0;
	
	@Attribute
	/* The y-coordinate */
	public int y = 0;
	
	@Attribute
	/* The z-coordinate */
	public int z = 0;
	
	public AbsolutePosition() {
		
	}
	
	public AbsolutePosition(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
}