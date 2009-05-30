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

	public int nodeId1;

	public int nodeId2;

	public LinePainterLine line;

	public Edge(final int nodeId1, final int nodeId2) {
		this.nodeId1 = nodeId1;
		this.nodeId2 = nodeId2;
	}

	@Override
	public String toString() {
		return line.toString();
	}

}
