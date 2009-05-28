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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepainter.NodePainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.util.Tools;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;
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

	/** Logs messages */
	private static final Logger log = SpyglassLoggerFactory.getLogger(SimpleNodePainterPlugin.class);

	/** The configuration parameters of this plug-in instance */
	@Element(name = "parameters")
	private final SimpleNodePainterXMLConfig xmlConfig;

	/** The layer containing the drawing objects */
	private final Layer layer;

	/** Tracks which node was added by sending packets of which semantic type */
	private Map<Integer, Collection<Integer>> nodeSemanticTypes;

	/** Listens for changes of the nodes' positions */
	private NodePositionListener npcl;

	/** Listens for changes of configuration properties */
	private PropertyChangeListener pcl;

	/** Indicates whether or not a refresh operation is pending */
	private volatile Boolean refreshPending = false;

	/** Maps node identifiers to actual node objects */
	private Map<Integer, Node> nodes = new HashMap<Integer, Node>();

	/** Object used for StringFormatter handling */
	private SimpleNodePainterPluginData data;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleNodePainterPlugin() {
		super();
		xmlConfig = new SimpleNodePainterXMLConfig();
		layer = Layer.Factory.createQuadTreeLayer();
		nodeSemanticTypes = new HashMap<Integer, Collection<Integer>>();
		data = new SimpleNodePainterPluginData();
	}

	// --------------------------------------------------------------------------------
	@SuppressWarnings("synthetic-access")
	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);
		npcl = new NodePositionListener() {
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
		xmlConfig.addPropertyChangeListener(pcl = new SNPPPropertyChangeListener());
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
			nodes.clear();
		}
		data.finalize();
		xmlConfig.finalize();
	}

	// --------------------------------------------------------------------------------
	@Override
	public PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new SimpleNodePainterPreferencePage(dialog, spyglass, this);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a widget used for the configuration of an instance of this class
	 * 
	 * @param dialog
	 *            the dialog where the widget is attached
	 * @param spyglass
	 *            a {@link Spyglass} instance
	 * @return a widget used for the configuration of an instance of this class
	 */
	public static PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleNodePainterPreferencePage(dialog, spyglass);
	}

	// --------------------------------------------------------------------------------
	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
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

		// since the packet made it to this point, its semantic type is listened to
		// add the packet's semantic type to the ones associated with the node
		synchronized (nodeSemanticTypes) {
			Collection<Integer> existingTypes = nodeSemanticTypes.get(nodeID);
			if (existingTypes == null) {
				// create a new set where the semantic types of packages of this node are stored
				existingTypes = new HashSet<Integer>();
				nodeSemanticTypes.put(nodeID, existingTypes);
			}
			existingTypes.add(packet.getSemanticType());
		}

		// get the instance of the node's visualization
		final Node node = getMatchingNodeObject(nodeID);

		// if the description was changed, an update is needed
		if (data.parsePayloadByStringFormatters(packet)) {
			node.setDescription(data.getStringFormatterString(nodeID));
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

		Node node = null;
		synchronized (layer) {
			if ((node = nodes.get(nodeID)) != null) {
				return node;
			}
		}

		// if not, create and return a new object...
		return createNodeObject(nodeID);

	}

	// --------------------------------------------------------------------------------
	/**
	 * Creates and returns a new {@link Node} object
	 * 
	 * @param nodeID
	 *            the identifier to be used
	 * @param drawingArea
	 *            the area where the node object will be drawn
	 * @return a new {@link Node} object
	 */
	private Node createNodeObject(final int nodeID) {

		// if the node was existing previously, some useful information might be available
		final boolean isExtended = xmlConfig.isExtendedInformationActive(nodeID);
		final int[] lineColorRGB = xmlConfig.getLineColorRGB();
		final int lineWidth = xmlConfig.getLineWidth();

		final String id = !xmlConfig.getNodeIDsAsHex() ? String.valueOf(nodeID) : "0x" + Tools.convertDecToHex(nodeID);

		final Node node = new Node(nodeID, id, "", isExtended, lineColorRGB, lineWidth, getPluginManager().getNodePositioner().getPosition(nodeID));

		synchronized (layer) {
			layer.add(node);
			nodes.put(nodeID, node);
		}
		fireDrawingObjectAdded(node);
		return node;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes obsolete nodes and redraws active ones
	 */
	private void refreshNodeObjects() {

		final List<Node> obsoleteNodes = new LinkedList<Node>();

		// get all drawing objects from the layer
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(nodes.values());
		}

		// process and update or remove all objects which represent nodes
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof Node) {
				final Node node = (Node) drawingObject;
				final int nodeID = node.getNodeID();

				// if the node is not associated to any semantic type, it is to be removed
				if (!xmlConfig.isAllSemanticTypes() && !isAssociatedToSemanticType(node)) {
					obsoleteNodes.add(node);
					continue;
				}

				// otherwise it needs to be updated
				final String denotation = (xmlConfig.getNodeIDsAsHex() ? "0x" + Tools.convertDecToHex(nodeID) : String.valueOf(nodeID));
				final String stringFormatterResult = data.getStringFormatterString(nodeID);
				node.update(denotation, stringFormatterResult, xmlConfig.isExtendedInformationActive(nodeID), xmlConfig.getLineColorRGB(), xmlConfig
						.getLineWidth());

			}
		}

		synchronized (layer) {
			for (final Node node : obsoleteNodes) {
				layer.remove(node);
				nodes.remove(node.getNodeID());
			}
		}

		// put all nodes which have to be removed into the designated data structure
		for (final DrawingObject node : obsoleteNodes) {
			fireDrawingObjectRemoved(node);
		}
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
	boolean isAssociatedToSemanticType(final Node node) {
		synchronized (nodeSemanticTypes) {
			final Collection<Integer> semanticTypes = nodeSemanticTypes.get(node.getNodeID());
			return ((semanticTypes == null) || (semanticTypes.size() == 0));
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the collection of semantic types associated to each node.<br>
	 * Semantic types which are no longer listened to will be removed from the association to a
	 * node.
	 */
	private void purgeNodeSemanticTypes() {

		if (!xmlConfig.isAllSemanticTypes()) {
			final Collection<Integer> validSemanticTypes = new LinkedList<Integer>();
			final int[] semTypes = xmlConfig.getSemanticTypes();
			for (int i = 0; i < semTypes.length; i++) {
				validSemanticTypes.add(semTypes[i]);
			}
			synchronized (nodeSemanticTypes) {
				for (final Collection<Integer> values : nodeSemanticTypes.values()) {
					values.retainAll(validSemanticTypes);
				}
			}
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void resetPlugin() {

		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();

		synchronized (nodeSemanticTypes) {
			nodeSemanticTypes.clear();
		}

		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
			layer.clear();
			nodes.clear();
		}

		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject.getState().equals(DrawingObject.State.ALIVE)) {
				fireDrawingObjectRemoved(drawingObject);
			}
		}

		data.reset();

	}

	// --------------------------------------------------------------------------------
	@Override
	public Set<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}

	// ############################################################################ //
	// ############################# Event handling ############################### //
	// ############################################################################ //

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
		Node node = null;
		synchronized (layer) {
			if ((node = nodes.get(nodeID)) != null) {
				layer.remove(node);
				nodes.remove(nodeID);
			}

		}

		if (node != null) {
			// since the node does not exist any more, its list of associated semantic types can
			// be removed
			synchronized (nodeSemanticTypes) {
				nodeSemanticTypes.remove(nodeID);
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
			// check if the drawing objects bounding box contains the point which
			// was clicked
			if (bbox.contains(clickPoint)) {
				if (drawingObject instanceof Node) {
					// if the object is representing a node, toggle its extension state
					final Node node = (Node) drawingObject;
					node.setExtended(!node.isExtended());
					xmlConfig.putExtendedInformationActive(node.getNodeID(), node.isExtended());
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

		for (final DrawingObject drawingObject : drawingObjects) {

			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject.getBoundingBox());
			// check if the drawing objects bounding box contains the point which
			// was clicked
			if (bbox.contains(clickPoint)) {
				synchronized (layer) {
					layer.bringToFront(drawingObject);
					// since the old and the new bounding box are equal ...
					drawingObject.markContentDirty();
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

		for (final DrawingObject drawingObject : drawingObjects) {

			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject.getBoundingBox());
			// check if the drawing objects bounding box contains the point which
			// was clicked
			if (bbox.contains(clickPoint)) {
				synchronized (layer) {
					layer.pushBack(drawingObject);

					// since the old and the new bounding box are equal ...
					drawingObject.markContentDirty();
				}
				return true;
			}
		}
		return false;
	}

	// ############################################################################ //
	// ################### Listening to property changes ########################## //
	// ############################################################################ //

	// --------------------------------------------------------------------------------
	/**
	 * Listens for changing properties of the {@link SimpleNodePainterPlugin}'s configuration
	 * 
	 * @author Sebastian Ebers
	 */
	private class SNPPPropertyChangeListener implements PropertyChangeListener {
		@SuppressWarnings("synthetic-access")
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			// indicates whether the nodes should be updated afterwards
			boolean updateNodes = true;

			// if the plug-in is no longer active, reset it completely
			if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_ACTIVE)) {

				if (!(Boolean) evt.getNewValue()) {
					// this has to be done to stop the packet consumer thread
					setActive(false);
					reset();
					updateNodes = false;
				} else {
					// this has to be done to start the packet consumer thread
					setActive(true);
				}
			}

			// the default string formatter was removed
			else if (evt.getPropertyName().equals(PluginWithStringFormatterXMLConfig.PROPERTYNAME_DEFAULT_STRING_FORMATTER)) {
				if ((evt.getNewValue() == null) || evt.getNewValue().equals("")) {
					data.purgeDefaultStringFormatterResults();
				}
			}

			// the plug-in does not listen to all semantic types any more
			else if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_ALL_SEMANTIC_TYPES) && !((Boolean) evt.getNewValue())) {
				data.purgeStringFormatterResults();
				purgeNodeSemanticTypes();
			}

			// the nodes identifier representation has changed
			else if (evt.getPropertyName().equals(SimpleNodePainterXMLConfig.PROPERTYNAME_NODE_IDS_AS_HEX)) {
				synchronized (layer) {
					final Collection<Node> nodeObjects = nodes.values();
					for (final Node node : nodeObjects) {
						node.setDenotation((((Boolean) evt.getNewValue()) == true ? "0x" + Tools.convertDecToHex(node.getNodeID()) : String
								.valueOf(node.getNodeID())));
					}
				}
				updateNodes = false;
			}

			// the list of semantic types the plug-in listens to has changed
			else if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_SEMANTIC_TYPES)) {
				if (!xmlConfig.isAllSemanticTypes()) {

					// remove all results cached for semantic types which are no longer listened to
					final List<Integer> purge = Tools.intArrayToIntegerList((int[]) evt.getOldValue());
					purge.removeAll(Tools.intArrayToIntegerList((int[]) evt.getNewValue()));
					for (final Integer p : purge) {
						data.removeStringFormatterResults(p);
					}
					purgeNodeSemanticTypes();
				}
			}

			// the set of string formatter configuration changed
			else if (evt.getPropertyName().equals(PluginWithStringFormatterXMLConfig.PROPERTYNAME_STRING_FORMATTERS)) {
				data.purgeStringFormatterResults();
			}

			if (updateNodes) {
				updateNodes();
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Starts a {@link Thread} to update all node objects
		 */
		@SuppressWarnings("synthetic-access")
		private void updateNodes() {

			// to refresh the nodes only once ...
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
				@Override
				public void run() {
					try {
						Thread.sleep(500);

						refreshNodeObjects();

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
	}

	// ############################################################################ //
	// ####################### StringFormatter handling ########################### //
	// ############################################################################ //

	/**
	 * Instances of this class handle create and cache formatter results
	 */
	private class SimpleNodePainterPluginData {

		/**
		 * Buffers StringFormatter result strings associated with nodes and semantic types.<br>
		 * The first map's key is the node's identifier and the second ones the semantic type.<br>
		 * The results have to be concatenated before they can be displayed.
		 */
		private Map<Integer, Map<Integer, String>> stringFormatterResults;

		/**
		 * Buffers StringFormatter result strings associated with nodes and semantic types.<br>
		 * This map is the concatenation of all string formatter results associated to each node.
		 */
		private Map<Integer, String> stringFormatterResultCache;

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 */
		public SimpleNodePainterPluginData() {
			stringFormatterResults = new TreeMap<Integer, Map<Integer, String>>();
			stringFormatterResultCache = new ConcurrentHashMap<Integer, String>();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Returns the {@link StringFormatter} result which is related to a certain node
		 * 
		 * @param nodeID
		 *            a node identifier
		 * @return the {@link StringFormatter} result which is related to a certain node
		 */
		public String getStringFormatterString(final int nodeID) {
			return stringFormatterResultCache.get(nodeID);
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
		@SuppressWarnings("synthetic-access")
		boolean parsePayloadByStringFormatters(final SpyglassPacket packet) {

			boolean needsUpdate = false;
			final int nodeID = packet.getSenderId();
			final int packetSemanticType = packet.getSemanticType();
			StringFormatter stringFormatter = null;

			// check if a default StringFormatter exists. If so, use to parse the packet and store
			// the resulting string independently of the packet's identifier
			final String stringFormatterString = xmlConfig.getDefaultStringFormatter();
			if ((stringFormatterString != null) && !stringFormatterString.equals("")) {
				stringFormatter = new StringFormatter(stringFormatterString);
				needsUpdate = updateStringFormatterResults(-1, stringFormatter.parse(packet), nodeID);
			}

			// Additionally, check if a StringFormatter exists which was designed to process the
			// semantic type of the packet. If so, use it to parse the packet
			stringFormatter = xmlConfig.getStringFormatter(packetSemanticType);
			if (stringFormatter != null) {
				needsUpdate = updateStringFormatterResults(packetSemanticType, stringFormatter.parse(packet), nodeID);
			}

			return needsUpdate;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Returns the semantic types which are currently listened to and which belong to an active
		 * string formatter configuration
		 * 
		 * @return the semantic types which are currently listened to and which belong to an active
		 *         string formatter configuration
		 */
		@SuppressWarnings("synthetic-access")
		private List<Integer> getActiveSemanticTypes() {
			// get all semantic types which are currently listened to
			final List<Integer> valid = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes());

			// if it is listening to all semantic types, which is indicated
			// by a single "-1" element
			if (xmlConfig.isAllSemanticTypes()) {
				if ((xmlConfig.getDefaultStringFormatter() == null) || xmlConfig.getDefaultStringFormatter().equals("")) {
					valid.clear();
				}
				// add the semantic types which belong to active string formatters to the list
				valid.addAll(xmlConfig.getStringFormatters().keySet());

			} else {
				// otherwise remove all semantic types which are listened to but
				// which don't belong to any active string formatter
				valid.retainAll(xmlConfig.getStringFormatters().keySet());
				if ((xmlConfig.getDefaultStringFormatter() != null) && !xmlConfig.getDefaultStringFormatter().equals("")) {
					valid.add(0, -1);
				}
			}
			return valid;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Updates a part of the string formatter results corresponding to a semantic type.<br>
		 * If the provided information are already buffered, <code>false</code> will be returned to
		 * indicate that an update was not necessary.
		 * 
		 * @param packetSemanticType
		 *            the semantic type
		 * @param str
		 *            the semantic type's new string
		 * @param nodeID
		 *            the node which sent the packet
		 * @return <code>true</code> if the results were actually updated because of the provided
		 *         information
		 */
		private boolean updateStringFormatterResults(final int packetSemanticType, final String str, final int nodeID) {

			// if the provided value is null, the result is obsolete and has to be removed
			if (str == null) {
				return removeStringFormatterResults(packetSemanticType, nodeID);
			}

			boolean needsUpdate = false;

			// update the part of the string formatter results corresponding to the semantic type
			synchronized (stringFormatterResults) {
				Map<Integer, String> nodeResults = stringFormatterResults.get(nodeID);
				if (nodeResults == null) {
					nodeResults = new TreeMap<Integer, String>();
					stringFormatterResults.put(nodeID, nodeResults);
					needsUpdate = true;
				}

				// if the current node result changed, update the cached value
				final String refStr = nodeResults.get(packetSemanticType);
				if (((refStr == null) || !refStr.equals(str))) {
					nodeResults.put(packetSemanticType, str);
					updateStringFormatterResultCache(nodeID);
					needsUpdate = true;
				}
			}

			return needsUpdate;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes the part of a node's string formatter results which corresponds to a semantic
		 * type.<br>
		 * 
		 * @param packetSemanticType
		 *            a semantic type
		 * @param nodeID
		 *            a node's identifier
		 * @return <code>true</code> if the results were actually removed
		 */
		private boolean removeStringFormatterResults(final int packetSemanticType, final int nodeID) {
			synchronized (stringFormatterResults) {
				final Map<Integer, String> nodeResults = stringFormatterResults.get(nodeID);
				if ((nodeResults != null) && (nodeResults.remove(packetSemanticType) != null)) {
					updateStringFormatterResultCache(nodeID);
					return true;
				}
			}
			return false;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes the parts of the string formatter results which corresponds to a semantic type.<br>
		 * 
		 * @param packetSemanticType
		 *            the semantic type
		 * @return <code>true</code> if the results were actually removed
		 */
		private boolean removeStringFormatterResults(final int packetSemanticType) {
			boolean returnValue = false;
			synchronized (stringFormatterResults) {
				for (final Integer nodeID : stringFormatterResults.keySet()) {
					returnValue = returnValue || removeStringFormatterResults(packetSemanticType, nodeID);
				}
			}
			return returnValue;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Updates the result cache of all {@link StringFormatter}s which are associated to a
		 * certain node
		 * 
		 * @param nodeID
		 *            the node's identifier
		 */
		private void updateStringFormatterResultCache(final int nodeID) {

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

			stringFormatterResultCache.put(nodeID, str);

		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes cached {@link StringFormatter} result strings which are no longer needed because
		 * the corresponding semantic type is no longer listened to.
		 */
		private void purgeStringFormatterResults() {

			final List<Integer> valid = getActiveSemanticTypes();

			synchronized (stringFormatterResults) {

				// purge the cache for every node
				final Collection<Integer> nodeIDs = stringFormatterResults.keySet();
				for (final Integer nodeID : nodeIDs) {

					boolean needsUpdate = false;
					final Map<Integer, String> nodeSemTypes = stringFormatterResults.get(nodeID);

					// compute keys which are currently available but no longer valid
					final Set<Integer> keys = nodeSemTypes.keySet();
					keys.removeAll(valid);

					for (final Integer key : keys) {
						nodeSemTypes.remove(key);
						needsUpdate = true;
					}
					if (needsUpdate) {
						updateStringFormatterResultCache(nodeID);
					}
				}

			}

		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes the cached default {@link StringFormatter} result string
		 */
		private void purgeDefaultStringFormatterResults() {
			synchronized (stringFormatterResults) {
				final Collection<Map<Integer, String>> nodeResults = stringFormatterResults.values();

				for (final Map<Integer, String> results : nodeResults) {
					results.remove(-1);
				}
				for (final Integer nodeID : stringFormatterResultCache.keySet()) {
					updateStringFormatterResultCache(nodeID);
				}

			}

		}

		// --------------------------------------------------------------------------------
		/**
		 * Resets the data
		 */
		public void reset() {
			stringFormatterResults.clear();
			stringFormatterResultCache.clear();
		}

		// --------------------------------------------------------------------------------
		@Override
		public void finalize() {
			stringFormatterResults.clear();
			stringFormatterResultCache.clear();
		}

	}

}