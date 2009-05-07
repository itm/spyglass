// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class Data {

	private Hashtable<Integer, NodeSensorRangeDrawingObject> dos = new Hashtable<Integer, NodeSensorRangeDrawingObject>();

	private Layer layer = Layer.Factory.createQuadTreeLayer();

	public synchronized void add(final int node, final NodeSensorRangeDrawingObject drawingObject) {
		dos.put(node, drawingObject);
		layer.add(drawingObject);
	}

	public synchronized NodeSensorRangeDrawingObject get(final int node) {
		return dos.get(node);
	}

	public synchronized NodeSensorRangeDrawingObject remove(final int node) {

		final NodeSensorRangeDrawingObject drawingObject;

		drawingObject = dos.get(node);
		layer.remove(drawingObject);
		dos.remove(node);

		return drawingObject;

	}

	public synchronized Collection<NodeSensorRangeDrawingObject> clear() {
		final Collection<NodeSensorRangeDrawingObject> drawingObjects = new LinkedList<NodeSensorRangeDrawingObject>();
		drawingObjects.addAll(dos.values());
		layer.clear();
		dos.clear();
		return drawingObjects;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Delegate method for {@link Layer#getDrawingObjects()}
	 */
	public Set<DrawingObject> getDrawingObjects() {
		return layer.getDrawingObjects();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Delegate method for {@link Layer#getDrawingObjects(AbsoluteRectangle)}
	 */
	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

}