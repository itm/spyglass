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
	public String toString() {
		return line.toString();
	}

}
