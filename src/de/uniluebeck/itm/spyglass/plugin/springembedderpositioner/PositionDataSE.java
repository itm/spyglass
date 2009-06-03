// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionData;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * @author olli
 * 
 */
public class PositionDataSE extends PositionData {

	public AbsolutePosition sePosition;

	// --------------------------------------------------------------------------------
	/**
	 * @param position
	 * @param lastSeen
	 */
	public PositionDataSE(final AbsolutePosition position, final AbsolutePosition sePosition, final long lastSeen) {
		super(position, lastSeen);
		this.sePosition = sePosition;
	}

	@Override
	public PositionDataSE clone() {
		return new PositionDataSE(position, sePosition, lastSeen);

	}

}
