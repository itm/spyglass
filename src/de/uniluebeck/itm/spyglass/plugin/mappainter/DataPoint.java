// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Element for the DataStore structure.
 * 
 * @author Dariush Forouher
 *
 */
public class DataPoint {
	
	/**
	 * Is this a framepoint
	 */
	public boolean isFramepoint = false;
	
	/**
	 * The position of this pata point
	 */
	public AbsolutePosition position = null;

	/**
	 * the value
	 */
	public double value = 0;

}
