// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

//--------------------------------------------------------------------------------
/**
 * Small helper class representing an edge in a graph, denoted by 2-tuple of 2 node IDs.
 * 
 * @author Daniel Bimschas
 */
public class Edge {

	public int sourceNodeId;

	public int destinationNodeId;

	public LinePainterLine line;

	public Edge(final int nodeId1, final int nodeId2) {
		this.sourceNodeId = nodeId1;
		this.destinationNodeId = nodeId2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + destinationNodeId;
		result = prime * result + sourceNodeId;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Edge other = (Edge) obj;
		if (destinationNodeId != other.destinationNodeId) {
			return false;
		}
		if (sourceNodeId != other.sourceNodeId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Line from " + sourceNodeId + " to " + destinationNodeId;
	}

}
