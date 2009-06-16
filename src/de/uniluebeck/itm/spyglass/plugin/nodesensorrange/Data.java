// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

// --------------------------------------------------------------------------------
/**
 * Data structure to back the {@link NodeSensorRangePlugin}.
 * 
 * @author bimschas
 */
public class Data {

	/**
	 * A {@link Hashtable} for referencing all drawing objects that were added to the layer easily
	 * by using their node ID as the key
	 */
	private Hashtable<Integer, NodeSensorRangeDrawingObject> dos = new Hashtable<Integer, NodeSensorRangeDrawingObject>();

	/**
	 * The layer instance for this plugin
	 */
	private Layer layer = Layer.Factory.createQuadTreeLayer();

	/**
	 * A reference to the plug-ins' XML configuration
	 */
	private NodeSensorRangeXMLConfig config;

	/**
	 * Listens to changes of the plug-ins' per node configuration and updates the drawing objects'
	 * config objects to reflect these changes on the drawing area
	 */
	private PropertyChangeListener perNodeConfigsPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent e) {
			updateNodeConfigs();
		}
	};

	private void updateNodeConfigs() {
		final HashSet<Config> perNodeConfigs = config.getPerNodeConfigs();
		final Config defaultConfig = config.getDefaultConfig();
		NodeSensorRangeDrawingObject nsrdo;
		boolean foundPerNodeConfig;

		synchronized (this) {

			System.out.println(layer.getDrawingObjects().size() + " " + dos.size());

			for (final Integer key : dos.keySet()) {

				foundPerNodeConfig = false;
				nsrdo = dos.get(key);

				// check if perNodeConfigs contains a config for this drawing object
				for (final Config c : perNodeConfigs) {
					if (c.getNodeId() == key) {
						System.out.println("updating one per node");
						if (!nsrdo.getConfig().equals(c)) {
							nsrdo.setConfig(c);
							foundPerNodeConfig = true;
						}
					}
				}

				// as we didn't find any per node config for this node we set it to the default
				// config if it's not already set to
				if (!foundPerNodeConfig && !nsrdo.getConfig().equals(defaultConfig)) {
					nsrdo.setConfig(defaultConfig);
					System.out.println("updating one default");
				}

			}
		}
	}

	public void init(final NodeSensorRangeXMLConfig config) {
		this.config = config;
		this.config.addPropertyChangeListener(perNodeConfigsPropertyChangeListener);
		updateNodeConfigs();
	}

	public void shutdown() {
		clear();
		this.config.removePropertyChangeListener(perNodeConfigsPropertyChangeListener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Adds the drawing object to the data structure.
	 * 
	 * @param node
	 *            the nodes' ID
	 * @param drawingObject
	 *            the nodes' drawing object
	 */
	public synchronized void add(final int node, final NodeSensorRangeDrawingObject drawingObject) {

		// make sure drawing object is initialized so that it can add listeners
		drawingObject.init(findNodeConfig(node));

		// add it to the data structures
		dos.put(node, drawingObject);
		layer.add(drawingObject);

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the appriopriate config object for the node with ID <code>node</code> which is either
	 * a config out of the set of per node config or the default node config.
	 * 
	 * @param node
	 * @return
	 */
	private Config findNodeConfig(final int node) {

		// check if there's a per node config for this node
		for (final Config c : config.getPerNodeConfigs()) {
			if (c.getNodeId() == node) {
				return c;
			}
		}

		// obviously, there was no per node config for this node so return the default node config
		return config.getDefaultConfig();

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the drawing object for the node with ID <code>node</code>.
	 * 
	 * @param node
	 *            the ID of the node whichs' drawing object is to be fetched
	 * @return the drawing object for the node with ID <code>node</code>
	 */
	public synchronized NodeSensorRangeDrawingObject get(final int node) {
		return dos.get(node);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes the drawing object from this data structure. Calls
	 * {@link NodeSensorRangeDrawingObject#shutdown()} on the drawing object, so that it can remove
	 * its' listeners.
	 * 
	 * @param node
	 *            the ID of the node whichs' drawing object is to be removed
	 * @return the drawing object instance that was removed
	 */
	public synchronized NodeSensorRangeDrawingObject remove(final int node) {

		// fetch the node that is to be removed
		final NodeSensorRangeDrawingObject drawingObject = dos.get(node);

		// make sure the drawing object is shut down, so that it can remove its' listeners
		drawingObject.shutdown();

		// remove it from our data structures
		layer.remove(drawingObject);
		dos.remove(node);

		return drawingObject;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Clears the data. Calls {@link NodeSensorRangeDrawingObject#shutdown()} on every drawing
	 * object, so that it can remove its' listeners.
	 * 
	 * @return a collection of all drawing object instances that were removed
	 */
	public synchronized Collection<NodeSensorRangeDrawingObject> clear() {

		// make a collection of all object that are removed so that the plug-in can send an event
		// informing of their removal
		final Collection<NodeSensorRangeDrawingObject> drawingObjects = new LinkedList<NodeSensorRangeDrawingObject>();
		drawingObjects.addAll(dos.values());

		// make sure all drawing object shut down, i.e. remove their listeners
		for (final NodeSensorRangeDrawingObject nsrdo : dos.values()) {
			nsrdo.shutdown();
		}

		// clear the data structures
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