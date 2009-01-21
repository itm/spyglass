/*
 * -------------------------------------------------------------------------------- 
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Node;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.nodepainter.NodePainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are used create and administer simple visualizations of sensor nodes.<br>
 * The nodes can be visualized in two way's according to the amount of information the user wants to
 * see.
 * <ul>
 * <li>In the <tt>non-extended mode</tt>, the nodes are represented by rectangles which only contain
 * the node's identifier.</li>
 * <li>In the <tt>extended mode</tt> the nodes are again represented by rectangles which contain the
 * node's identifier. But additionally, further information which are extracted from the packets of
 * certain semantic types are displayed, too.</li>
 * </ul>
 * 
 * @author Sebastian Ebers
 */
public class SimpleNodePainterPlugin extends NodePainterPlugin {

	/** Loggs messages */
	private static final Logger log = SpyglassLoggerFactory.getLogger(SimpleNodePainterPlugin.class);

	/**
	 * The configuration parameters of this plug-in instance
	 */
	@Element(name = "parameters")
	private final SimpleNodePainterXMLConfig xmlConfig;

	/**
	 * The quad tree containing the drawing objects
	 */
	private final Layer layer;

	/**
	 * Objects which have recently been updated and which needs to be updated in the quad tree as
	 * well (which is done in {@link SimpleNodePainterPlugin#updateLayer()}
	 */
	private final Set<DrawingObject> updatedObjects;

	/** Objects which are not needed any longer */
	private final Set<DrawingObject> obsoleteObjects;

	private Map<Integer, Map<Integer, String>> stringFormatterResults;

	/** Temporarily stores the bounding boxes which are used during redraw events */
	private Map<DrawingObject, AbsoluteRectangle> boundingBoxes;

	/** Listens for changes of the nodes' positions */
	private NodePositionListener npcl;

	/** Listens for changes of configuration properties */
	private PropertyChangeListener pcl;

	private volatile Boolean refreshPending = false;

	private Map<Integer, Collection<Integer>> nodeSemanticTypes;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleNodePainterPlugin() {
		super();
		xmlConfig = new SimpleNodePainterXMLConfig();
		layer = new QuadTree();
		updatedObjects = new HashSet<DrawingObject>();
		obsoleteObjects = new HashSet<DrawingObject>();
		stringFormatterResults = new TreeMap<Integer, Map<Integer, String>>();
		boundingBoxes = new HashMap<DrawingObject, AbsoluteRectangle>();
		nodeSemanticTypes = new HashMap<Integer, Collection<Integer>>();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);
		npcl = new NodePositionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void handleEvent(final NodePositionEvent e) {
				if (e.change.equals(NodePositionEvent.Change.REMOVED) || (e.newPosition == null)) {
					handleNodeTimeout(e.node);
				} else {
					setNodePosition(e.node, e.newPosition);
				}
			}

		};
		manager.addNodePositionListener(npcl);
		pcl = new PropertyChangeListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				// if the plug-in is no longer active, reset it completely
				if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_ACTIVE) && !((Boolean) evt.getNewValue())) {
					reset();
				}

				// if the plug-in is no longer visible, hide its graphical objects
				else if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_VISIBLE) && !((Boolean) evt.getNewValue())) {
					synchronized (layer) {
						for (final DrawingObject drawingObject : layer.getDrawingObjects()) {
							fireDrawingObjectRemoved(drawingObject);
							boundingBoxes.remove(drawingObject);
						}
					}
				}

				// refresh the configuration parameters no matter what property changed
				refreshConfigurationParameters();

			}
		};
		xmlConfig.addPropertyChangeListener(pcl);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		getPluginManager().removeNodePositionListener(npcl);
		xmlConfig.removePropertyChangeListener(pcl);
		reset();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		synchronized (layer) {
			layer.clear();
		}
		synchronized (updatedObjects) {
			updatedObjects.clear();
		}
		stringFormatterResults.clear();
		boundingBoxes.clear();
		xmlConfig.finalize();
	}

	@Override
	public PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new SimpleNodePainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleNodePainterPreferencePage(dialog, spyglass);
	}

	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		synchronized (layer) {
			return layer.getDrawingObjects(area);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the plug-in's denotation in a human readable style
	 * 
	 * @return the plug-in's denotation in a human readable style
	 */
	public static String getHumanReadableName() {
		return "SimpleNodePainter";
	}

	// --------------------------------------------------------------------------------
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void processPacket(final SpyglassPacket packet) {

		final int nodeID = packet.getSenderId();

		// if the description was changed, an update is needed
		if (parsePayloadByStringFormatters(packet)) {
			// get the instance of the node's visualization
			final Node node = getMatchingNodeObject(nodeID);
			node.setDescription(getStringFormatterResultString(nodeID));

			// add the packet's semantic type to the ones associated with the node
			synchronized (nodeSemanticTypes) {
				Collection<Integer> semanticTypes = nodeSemanticTypes.get(nodeID);
				if (semanticTypes == null) {
					semanticTypes = new HashSet<Integer>();
					nodeSemanticTypes.put(nodeID, semanticTypes);
				}
				semanticTypes.add(packet.getSemanticType());
			}

			// add the object to the one which have to be (re)drawn ...
			synchronized (updatedObjects) {
				updatedObjects.add(node);
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the position of a node
	 * 
	 * @param nodeID
	 *            the node's identifier
	 * @param position
	 *            the node's position
	 */
	private void setNodePosition(final int nodeID, final AbsolutePosition position) {

		// the node has to be associated to a valid semantic type to be marked for redraw
		synchronized (nodeSemanticTypes) {
			if (nodeSemanticTypes.get(nodeID) == null) {
				return;
			}
		}

		// get the instance of the node's visualization
		final Node node = getMatchingNodeObject(nodeID);
		node.setPosition(position);

		synchronized (updatedObjects) {
			updatedObjects.add(node);
		}
		updateLayer();

	}

	// --------------------------------------------------------------------------------
	/**
	 * Parses the packets payload using the defined {@link StringFormatter}s.<br>
	 * If a string formatter exists which gains information from the payload, the {@link Node}
	 * corresponding to the node which sent the packet will be updated.
	 * 
	 * @param packet
	 *            the packet containing the payload
	 * @return <code>true</code> if a StringFormatter gained information from the payload
	 */
	private boolean parsePayloadByStringFormatters(final SpyglassPacket packet) {

		boolean needsUpdate = false;
		final int nodeID = packet.getSenderId();
		final int packetSemanticType = packet.getSemanticType();
		try {
			StringFormatter stringFormatter = null;

			// check if a default StringFormatter exists. If so, use to parse the packet
			final String strinFormatterString = xmlConfig.getDefaultStringFormatter();
			if ((strinFormatterString != null) && !strinFormatterString.equals("")) {
				stringFormatter = new StringFormatter(strinFormatterString);

				// parses the packet. The resulting string will be stored separately
				final String str = stringFormatter.parse(packet);
				updateStringFormatterResults(-1, packetSemanticType + ": " + str, nodeID);
				// updateStringFormatterResults(-1, str, nodeID);
				needsUpdate = true;

			}
			// check if a StringFormatter exists which was designed to process the semantic type of
			// the packet. If so, use it to parse the packet
			stringFormatter = xmlConfig.getStringFormatter(packetSemanticType);
			if (stringFormatter != null) {
				final String str = stringFormatter.parse(packet);
				updateStringFormatterResults(packetSemanticType, str, nodeID);
				needsUpdate = true;
			}

		} catch (final IllegalArgumentException e) {
			log.error(e);
		} catch (final RuntimeException e) {
			log.error("An error occured while processing a packet's contents using a StringFormatter", e);
		}
		return needsUpdate;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the the part of the string formatter results corresponding to the semantic type
	 * 
	 * @param packetSemanticType
	 *            the semantic type
	 * @param str
	 *            the semantic type's new string
	 * @param nodeID
	 *            the node which sent the packet
	 */
	private void updateStringFormatterResults(final int packetSemanticType, final String str, final int nodeID) {

		// actually update the part of the string formatter results corresponding to the semantic
		// type
		synchronized (stringFormatterResults) {
			Map<Integer, String> nodeResults = stringFormatterResults.get(nodeID);
			if (nodeResults == null) {
				nodeResults = new TreeMap<Integer, String>();
				stringFormatterResults.put(nodeID, nodeResults);
			}
			if (str != null) {
				nodeResults.put(packetSemanticType, str);
			} else {
				nodeResults.remove(packetSemanticType);
			}
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the instance of a node's visualization<br>
	 * Note that either a matching instance if found in the quad tree or a new instance is to be
	 * created and initialized as configured by the default parameters.<br>
	 * 
	 * @param nodeID
	 *            the node's identifier
	 * @return the up to date instance of a node's visualization
	 */
	private synchronized Node getMatchingNodeObject(final int nodeID) {

		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}
		synchronized (updatedObjects) {
			drawingObjects.addAll(updatedObjects);
		}

		Node node = null;
		DrawingArea drawingArea = null;

		// if there is already an instance of the node's visualization
		// available in the quadTree it has to be updated
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof Node) {
				node = (Node) drawingObject;
				drawingArea = node.getDrawingArea();

				// if the matching node is found ...
				if (node.getNodeID() == nodeID) {
					return node;
				}
			}
		}

		// if not, create a new object
		final boolean isExtended = xmlConfig.isExtendedInformationActive(nodeID);

		// fetch all information provided by the string formatters

		final String stringFormatterResult = getStringFormatterResultString(nodeID);

		final int[] lineColorRGB = xmlConfig.getLineColorRGB();
		final int lineWidth = xmlConfig.getLineWidth();
		final Node no = new Node(nodeID, "Node " + nodeID, stringFormatterResult, isExtended, lineColorRGB, lineWidth, drawingArea,
				getPluginManager().getNodePositioner().getPosition(nodeID));
		// this is hacky but since no drawing area is accessible, I don't know what to do...
		boundingBoxes.put(no, (drawingArea != null) ? new AbsoluteRectangle(no.getBoundingBox()) : new AbsoluteRectangle(0, 0, 1000, 1000));

		// synchronized (updatedObjects) {
		// updatedObjects.add(no);
		// }

		return no;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the result of all {@link StringFormatter}s
	 * 
	 * @return a string containing all information provided by the various {@link StringFormatter}s
	 */
	private String getStringFormatterResultString(final int nodeID) {
		final StringBuffer stringFormatterResult = new StringBuffer("");
		final Collection<String> sfResults = new Vector<String>();
		synchronized (stringFormatterResults) {
			final Map<Integer, String> nodeResults = stringFormatterResults.get(nodeID);
			if (nodeResults != null) {
				sfResults.addAll(nodeResults.values());
			}
		}
		for (final String str : sfResults) {
			stringFormatterResult.append("\r\n" + str);
		}
		String str = stringFormatterResult.toString();
		if (str.length() > 4) {
			str = str.substring(2);
		}
		return str;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes cached {@link StringFormatter} result strings which are no longer needed because the
	 * corresponding semantic type is no longer listened to.
	 */
	private void purgeStringFormatterResults() {

		// if the plug-in is configured to evaluate packages of any semantic type, purging is not
		// necessary
		if (xmlConfig.isAllSemanticTypes()) {
			return;
		}

		final List<?> l = Arrays.asList(xmlConfig.getSemanticTypes());

		synchronized (stringFormatterResults) {
			final Collection<Map<Integer, String>> nodeResults = stringFormatterResults.values();

			for (final Map<Integer, String> results : nodeResults) {
				for (final Integer key : results.keySet()) {
					// if a positive key on behalf of a semantic type is found which is no longer to
					// be evaluated, delete the buffered result
					// The key has to be positive since the default string formatter string will be
					// stored as value of the key '-1'.
					if ((key >= 0) && !l.contains(key)) {
						results.remove(key);
					}
				}
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Performs internal refreshments of the configuration parameters.<br>
	 * This includes the node object amongst others since e.g. the line color etc. could have been
	 * changed.
	 */
	public void refreshConfigurationParameters() {

		synchronized (refreshPending) {
			// if so, return
			if (refreshPending) {
				return;
			}
			// otherwise proceed
			refreshPending = true;
		}

		// create the thread to refresh concurrently
		final Thread t = new Thread() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				try {
					Thread.sleep(500);

					// this has to be done to start or stop the packet consumer thread
					setActive(isActive());

					refreshNodeObjectConfiguration();

				} catch (final InterruptedException e) {
					log.error("Sleeping was interrupted while waiting to perform a refresh of the configuration parameters");
					Thread.currentThread().interrupt();
				} catch (final Exception e) {
					log.error(e, e);
				} finally {
					refreshPending = false;
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Resets the configuration parameters of the node visualizations according to the node
	 * painter's configuration parameters.<br>
	 * <b>Note:</b> This object updates the quadTree in a synchronized block which means that the
	 * GUI has to wait for the end of the processing
	 */
	public void refreshNodeObjectConfiguration() {

		// delete all cached string formatter results which are no longer needed
		purgeStringFormatterResults();

		final List<DrawingObject> obsoleteNodes = new LinkedList<DrawingObject>();

		// get all drawing objects from the layer
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}

		// process and update or remove all objects which represent nodes
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof Node) {
				final Node node = (Node) drawingObject;
				final int nodeID = node.getNodeID();

				// if the node is not associated to any semantic type, it is to be removed
				if (!isAssociatedToSemanticType(node)) {
					obsoleteNodes.add(node);
					continue;
				}

				// otherwise it needs to be updated
				final String stringFormatterResult = getStringFormatterResultString(nodeID);
				node.update("Node " + nodeID, stringFormatterResult, xmlConfig.isExtendedInformationActive(nodeID), xmlConfig.getLineColorRGB(),
						xmlConfig.getLineWidth());

			}
		}

		// put all nodes which have to be removed into the designated data structure
		synchronized (obsoleteObjects) {
			obsoleteObjects.addAll(obsoleteNodes);
		}

		// put all nodes which have to be removed into the designated data structure
		drawingObjects.removeAll(obsoleteNodes);
		synchronized (updatedObjects) {
			updatedObjects.addAll(drawingObjects);
		}

		// and update the layer
		updateLayer();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns if the node is associated to any semantic type
	 * 
	 * @param node
	 *            a node
	 * @return <code>true</code> if the node is associated to a semantic type, <code>false</code>
	 *         otherwise
	 */
	private boolean isAssociatedToSemanticType(final Node node) {

		Collection<Integer> semanticTypes = null;
		synchronized (nodeSemanticTypes) {
			final int nodeID = node.getNodeID();
			semanticTypes = nodeSemanticTypes.get(nodeID);

			// if a list of semantic types is associated to the node at all, check if the semantic
			// types in the list are still valid
			if (semanticTypes != null) {

				if (xmlConfig.isAllSemanticTypes()) {
					return true;
				}

				// retain all still valid semantic type's in the node's list of associated semantic
				// types
				// Note: Arrays.asList(xmlConfig.getSemanticTypes()) does not work here!
				final Collection<Integer> validSemanticTypes = new LinkedList<Integer>();
				final int[] semTypes = xmlConfig.getSemanticTypes();
				for (int i = 0; i < semTypes.length; i++) {
					validSemanticTypes.add(semTypes[i]);
				}
				semanticTypes.retainAll(validSemanticTypes);

				// if not, remove the whole list
				if (semanticTypes.size() == 0) {
					nodeSemanticTypes.remove(nodeID);
					return false;
				}
				return true;

			}
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void reset() {

		synchronized (updatedObjects) {
			updatedObjects.clear();
		}

		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
			// TODO (SE) remove this workaround when the layer's bug concerning the removal of
			// elements is fixed
			layer.clear();
		}

		synchronized (nodeSemanticTypes) {
			nodeSemanticTypes.clear();
		}

		synchronized (obsoleteObjects) {
			obsoleteObjects.addAll(drawingObjects);
			updateLayer();
		}

		stringFormatterResults.clear();
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void updateLayer() {

		// copy all objects which have to be updated in a separate List and
		// clear the original list
		final List<DrawingObject> update = new LinkedList<DrawingObject>();
		synchronized (updatedObjects) {
			update.addAll(updatedObjects);
			updatedObjects.clear();
		}

		final List<DrawingObject> obsolete = new LinkedList<DrawingObject>();
		synchronized (obsoleteObjects) {
			obsolete.addAll(obsoleteObjects);
			obsoleteObjects.clear();
		}

		// if no drawing objects are available, there is no need to get the
		// layer's lock
		if ((update.size() == 0) && (obsolete.size() == 0)) {
			return;
		}
		AbsoluteRectangle oldBB;
		// catch the layer's lock and update the objects
		synchronized (layer) {
			for (final DrawingObject drawingObject : update) {
				layer.addOrUpdate(drawingObject);
			}

			if (obsolete.size() > 0) {
				// TODO (SE) Remove the debugging comments
				// log.debug("========================================================================");
				for (final DrawingObject drawingObject : obsolete) {
					// log.debug("Remove from QuadTree: " + drawingObject);
					if (!layer.remove(drawingObject)) {
						// log.debug("Object " + drawingObject + " was not found in QuadTree");
					}
				}
				// final int size = layer.getDrawingObjects().size();
				// if (size > 0) {
				// log.debug(size + " remain in the QuadTree: ");
				// for (final DrawingObject drawingObject : layer.getDrawingObjects()) {
				// log.debug(drawingObject + " " + drawingObject.getBoundingBox());
				// }
				// } else {
				// log.debug("All objects removed successfully!");
				// }
			}
		}

		// after the lock was returned it is time to update the display ...
		for (final DrawingObject drawingObject : update) {
			if ((oldBB = boundingBoxes.get(drawingObject)) != null) {
				fireDrawingObjectChanged(drawingObject, oldBB);
			} else {
				fireDrawingObjectAdded(drawingObject);
			}
			boundingBoxes.put(drawingObject, new AbsoluteRectangle(drawingObject.getBoundingBox()));
		}

		for (final DrawingObject drawingObject : obsolete) {
			fireDrawingObjectRemoved(drawingObject);
			// log.debug("Calling fireDrawingObjectRemoved(" + drawingObject + " " +
			// drawingObject.getBoundingBox() + ")");
			boundingBoxes.remove(drawingObject);
		}
		// if (obsolete.size() > 0) {
		// log.debug("========================================================================");
		// }

	}

	// --------------------------------------------------------------------------------
	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public boolean handleEvent(final MouseEvent e, final DrawingArea drawingArea) {

		// get the objects to draw
		final List<DrawingObject> dos = new LinkedList<DrawingObject>();
		synchronized (layer) {
			dos.addAll(layer.getDrawingObjects(drawingArea.getAbsoluteDrawingRectangle()));
		}

		final Point clickPoint = new Point(e.x, e.y);

		// if the mouse event was a (left) double click
		if ((e.button == 1)) {
			if (handleDoubleClick(dos, clickPoint, drawingArea)) {
				return true;
			}
		}

		// if the mouse event was a wheel click
		else if (e.button == 2) {
			if (handleWheelClick(dos, clickPoint, drawingArea)) {
				return true;
			}
		}

		// if the mouse event was a right click
		else if (e.button == 3) {
			if (handleRightClick(dos, clickPoint, drawingArea)) {
				return true;
			}
		}

		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Handles the timeout of a node - the node will not be painted any longer.
	 * 
	 * @param nodeID
	 *            the node's identifier
	 */
	private void handleNodeTimeout(final int nodeID) {

		// get all the layer's drawing objects
		final List<DrawingObject> dos = new LinkedList<DrawingObject>();
		synchronized (layer) {
			dos.addAll(layer.getDrawingObjects());
		}

		// since the node does not exist any more, its list of associated semantic types can
		// be removed
		synchronized (nodeSemanticTypes) {
			nodeSemanticTypes.remove(nodeID);
		}

		// look for the node object with the matching identifier
		for (final DrawingObject drawingObject : dos) {
			if ((drawingObject instanceof Node) && (((Node) drawingObject).getNodeID() == nodeID)) {

				// if the object was found, put it into the set of objects to be removed
				synchronized (obsoleteObjects) {
					obsoleteObjects.add(drawingObject);
				}

				// remove the object by calling updateQuadTree() and return
				updateLayer();
				return;
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Handles a mouse click event which was actually a double click returns <code>true</code> if a
	 * drawing object was found which bounding box contains the point clicked by the user.
	 * 
	 * @param drawingObjects
	 *            the plug-in's drawing objects
	 * @param clickPoint
	 *            the point which was clicked
	 * @param drawingArea
	 *            the place where all nodes are painted
	 * @return <code>true</code> if a matching drawing object was found
	 */
	private boolean handleDoubleClick(final List<DrawingObject> drawingObjects, final Point clickPoint, final DrawingArea drawingArea) {

		// check all drawing objects
		// this has to be done in reverse order since the "normal" order is used for painting which
		// means that the topmost element is actually the last element in the list
		for (int i = drawingObjects.size() - 1; i >= 0; i--) {
			final DrawingObject drawingObject = drawingObjects.get(i);
			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject.getBoundingBox());
			// check which plug-in's bounding box contains the point which
			// was clicked (if any)
			if (bbox.contains(clickPoint)) {
				// check if the object is representing a node
				if (drawingObject instanceof Node) {
					// if so, toggle its extension state
					final Node no = (Node) drawingObject;
					no.setExtended(!no.isExtended());
					xmlConfig.putExtendedInformationActive(no.getNodeID(), no.isExtended());
					synchronized (updatedObjects) {
						updatedObjects.add(no);
					}
					updateLayer();
				}
				return true;
			}
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Handles a mouse click event which was not a left click and returns <code>true</code> if a
	 * drawing object was found which bounding box contains the point clicked by the user.
	 * 
	 * @param drawingObjects
	 *            the plug-in's drawing objects
	 * @param clickPoint
	 *            the point which was clicked
	 * @return <code>true</code> if a matching drawing object was found
	 */
	private boolean handleRightClick(final List<DrawingObject> drawingObjects, final Point clickPoint, final DrawingArea drawingArea) {

		// check all drawing objects
		for (final DrawingObject drawingObject : drawingObjects) {

			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject.getBoundingBox());
			// check which plug-in's bounding box contains the point which
			// was clicked (if any)
			if (bbox.contains(clickPoint)) {
				synchronized (layer) {
					layer.bringToFront(drawingObject);
					// since the old and the new bounding box are equal, the map needs no update
					fireDrawingObjectChanged(drawingObject, boundingBoxes.get(drawingObject));
				}
				return true;
			}
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Handles a mouse click event which was not a left click and returns <code>true</code> if a
	 * drawing object was found which bounding box contains the point clicked by the user.
	 * 
	 * @param drawingObjects
	 *            the plug-in's drawing objects
	 * @param clickPoint
	 *            the point which was clicked
	 * @return <code>true</code> if a matching drawing object was found
	 */
	private boolean handleWheelClick(final List<DrawingObject> drawingObjects, final Point clickPoint, final DrawingArea drawingArea) {

		// check all drawing objects
		for (final DrawingObject drawingObject : drawingObjects) {

			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject.getBoundingBox());
			// check which plug-in's bounding box contains the point which
			// was clicked (if any)
			if (bbox.contains(clickPoint)) {
				synchronized (layer) {
					layer.pushBack(drawingObject);
					// since the old and the new bounding box are equal, the map needs no update
					fireDrawingObjectChanged(drawingObject, boundingBoxes.get(drawingObject));
				}
				return true;
			}
		}
		return false;
	}
}