// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;


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
	 * sender ID of the node
	 */
	public int nodeID = -1;

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

	@Override
	public DataPoint clone() {
		final DataPoint newP = new DataPoint();
		newP.isFramepoint = this.isFramepoint;
		newP.nodeID = this.nodeID;
		newP.position = this.position;
		newP.value = this.value;
		return newP;
	}
}
