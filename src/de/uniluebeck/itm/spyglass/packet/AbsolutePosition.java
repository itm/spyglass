package de.uniluebeck.itm.spyglass.packet;

import org.simpleframework.xml.Attribute;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent the coordinate's of a single point when
 * using absolute coordinates
 * 
 * @author Sebastian Ebers
 * 
 */
public class AbsolutePosition {
	
	@Attribute
	/** The x-coordinate */
	public int x = 0;
	
	@Attribute
	/** The y-coordinate */
	public int y = 0;
	
	@Attribute
	/** The z-coordinate */
	public int z = 0;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public AbsolutePosition() {
		
	}
	
	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		
	}
	
}