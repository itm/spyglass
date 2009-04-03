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
public class DataPoint extends MetricPoint {

	/**
	 * Is this a framepoint
	 */
	public boolean isFramepoint = false;

	/**
	 * The position of this pata point
	 */
	public AbsolutePosition position = null;

	/**
	 * sender ID of the node
	 */
	public int nodeID = -1;

	/**
	 * the value
	 */
	public double value = 0;

	@Override
	public boolean equals(final Object o) {
		if (o instanceof DataPoint) {
			return this.nodeID == ((DataPoint)o).nodeID;
		} else {
			return false;
		}
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final MetricPoint o) {
		if (o instanceof DataPoint) {
			return nodeID-((DataPoint)o).nodeID;
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		return nodeID;
	}

}
