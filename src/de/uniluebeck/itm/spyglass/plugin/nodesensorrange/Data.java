// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.util.Hashtable;
import java.util.List;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.layer.QuadTree;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class Data {

	private Hashtable<Integer, NodeSensorRangeDrawingObject> dos = new Hashtable<Integer, NodeSensorRangeDrawingObject>();

	private Layer layer = new QuadTree();

	public synchronized void add(final int node, final NodeSensorRangeDrawingObject drawingObject) {
		dos.put(node, drawingObject);
		layer.addOrUpdate(drawingObject);
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

	public synchronized void clear() {
		layer.clear();
		dos.clear();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Delegate method for {@link Layer#getDrawingObjects()}
	 */
	public List<DrawingObject> getDrawingObjects() {
		return layer.getDrawingObjects();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Delegate method for {@link Layer#getDrawingObjects(AbsoluteRectangle)}
	 */
	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

}