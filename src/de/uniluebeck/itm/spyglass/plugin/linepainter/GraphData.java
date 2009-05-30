// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//--------------------------------------------------------------------------------
/**
 * A small helper class containing the graph defined by the edges which are read from incoming
 * packages.
 * 
 * @author Daniel Bimschas
 */
class GraphData {

	/**
	 * Map containing timestamps that tell when an edge was added or last referenced by a package.
	 * By reading the keyset of the map you get all edges currently contained in the node graph.
	 */
	Map<Edge, Long> edgeTimes;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor. That's it ;-)
	 */
	public GraphData() {
		edgeTimes = new HashMap<Edge, Long>();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds an edge to the LinePainterPlugins' graph
	 * 
	 * @param edge
	 */
	public synchronized void addEdge(final Edge edge) {
		edgeTimes.put(edge, System.currentTimeMillis());
	}

	// --------------------------------------------------------------------------------
	/**
	 * Clears the LinePainterPlugins' graph
	 */
	public synchronized void clear() {
		edgeTimes.clear();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Searches for an existing edge in the graph which connects the nodes with ids
	 * <code>nodeId1</code> and <code>nodeId2</code>.
	 * 
	 * @param nodeId1
	 * @param nodeId2
	 * @return an Edge instance or <code>null</code> if there's no such node in the graph
	 */
	public synchronized Edge getExistingEdge(final int nodeId1, final int nodeId2) {
		for (final Edge e : edgeTimes.keySet()) {
			final boolean same = ((e.nodeId1 == nodeId1) && (e.nodeId2 == nodeId2)) || ((e.nodeId1 == nodeId2) && (e.nodeId2 == nodeId1));
			if (same) {
				return e;
			}
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a set of all edges incident to the node with id <code>nodeId</code>.
	 * 
	 * @param nodeId
	 * @return
	 */
	public synchronized Set<Edge> getIncidentEdges(final int nodeId) {
		final Set<Edge> incidentEdges = new HashSet<Edge>();
		for (final Edge e : edgeTimes.keySet()) {
			if ((e.nodeId1 == nodeId) || (e.nodeId2 == nodeId)) {
				incidentEdges.add(e);
			}
		}
		return incidentEdges;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes the edge from the LinePainters' graph.
	 * 
	 * @param edge
	 */
	public synchronized void removeEdge(final Edge edge) {
		if (edgeTimes.remove(edge) != null) {
			return;
		}
		throw new NullPointerException("Edge was not contained.");
	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the timestamp for the last received packet including the edge.
	 * 
	 * @param edge
	 */
	public synchronized void updateEdgeTime(final Edge edge) {
		edgeTimes.put(edge, System.currentTimeMillis());
	}

}