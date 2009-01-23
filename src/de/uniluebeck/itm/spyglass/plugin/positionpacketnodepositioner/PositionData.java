// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Data representing one node
 * 
 * (the nodeID itself is not part of this object)
 * 
 * @author Dariush Forouher
 *
 */
public class PositionData {

	/**
	 * Current position of the node
	 */
	public AbsolutePosition position;
	
	/**
	 * Timestamp (in millies) when the node was last seen.
	 */
	public long lastSeen; 

	
	public PositionData(final AbsolutePosition position, final long lastSeen) {
		super();
		this.position = position;
		this.lastSeen = lastSeen;
	}
}