/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

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
import de.uniluebeck.itm.spyglass.drawing.primitive.NodeObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.nodepainter.NodePainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
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
	
	private static final Logger log = SpyglassLogger.getLogger(SimpleNodePainterPlugin.class);
	
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
	 * well (which is done in {@link SimpleNodePainterPlugin#updateQuadTree()}
	 */
	private final Set<DrawingObject> updatedObjects;
	
	/** Objects which are not needed any longer */
	private final Set<DrawingObject> obsoleteObjects;
	
	private Map<Integer, Map<Integer, String>> stringFormatterResults;
	
	private Map<Integer, AbsoluteRectangle> boundingBoxes;
	
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
		boundingBoxes = new HashMap<Integer, AbsoluteRectangle>();
	}
	
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
	public PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
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
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
		
		final int nodeID = packet.getSenderId();
		
		// get the absolute position of the node which sent the packet
		final AbsolutePosition position = getPluginManager().getNodePositioner()
				.getPosition(nodeID);
		
		boolean needsUpdate = false;
		
		// get all drawing objects from the quad tree
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}
		
		// get the instance of the node's visualization
		final NodeObject nodeObject = getMatchingNodeObject(nodeID, position, drawingObjects);
		
		// if the node object did not have any information about the node's
		// position or the position has changed, update the node object
		if ((nodeObject.getPosition() == null) || !position.equals(nodeObject.getPosition())) {
			nodeObject.setPosition(position);
			needsUpdate = true;
		}
		
		needsUpdate = parsePayloadByStringFormatters(packet, nodeObject) || needsUpdate;
		
		if (needsUpdate) {
			nodeObject.setDescription(getStringFormatterResultString(nodeID));
			// add the object to the one which have to be (re)drawn ...
			synchronized (updatedObjects) {
				updatedObjects.add(nodeObject);
			}
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Parses the packets payload using the defined {@link StringFormatter}s.<br>
	 * If a string formatter exists which gains information from the payload, the {@link NodeObject}
	 * corresponding to the node which sent the packet will be updated.
	 * 
	 * @param packet
	 *            the packet containing the payload
	 * @param nodeObject
	 *            the graphical object which corresponds to the physical node which sent the packet-
	 * @return <code>true</code> if a StringFormatter gained information from the payload
	 */
	private boolean parsePayloadByStringFormatters(final SpyglassPacket packet,
			final NodeObject nodeObject) {
		
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
		} catch (final Exception e) {
			log
					.error(
							"An error occured while processing a packet's contents using a StringFormatter",
							e);
		}
		return needsUpdate;
	}
	
	/**
	 * Updates the the part of the string formatter results corresponding to the semantic type
	 * 
	 * @param packetSemanticType
	 *            the semantic type
	 * @param str
	 *            the semantic type's new string
	 * @param nodeObject
	 *            the drawing objects to be updated in order to display the new information
	 */
	private void updateStringFormatterResults(final int packetSemanticType, final String str,
			final int nodeID) {
		
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
	 * @param position
	 *            the node's position
	 * @param drawingObjects
	 *            the drawing objects which are currently available in the quad tree
	 * @return the up to date instance of a node's visualization
	 */
	private NodeObject getMatchingNodeObject(final int nodeID, final AbsolutePosition position,
			final List<DrawingObject> drawingObjects) {
		
		NodeObject nodeObject = null;
		DrawingArea drawingArea = null;
		
		// if there is already an instance of the node's visualization
		// available in the quadTree it has to be updated
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				nodeObject = (NodeObject) drawingObject;
				drawingArea = nodeObject.getDrawingArea();
				
				// if the matching node is found ...
				if (nodeObject.getNodeID() == nodeID) {
					return nodeObject;
				}
			}
		}
		
		// if not, create a new object
		final boolean isExtended = xmlConfig.isExtendedInformationActive(nodeID);
		
		// fetch all information provided by the string formatters
		
		final String stringFormatterResult = getStringFormatterResultString(nodeID);
		
		final int[] lineColorRGB = xmlConfig.getLineColorRGB();
		final int lineWidth = xmlConfig.getLineWidth();
		final NodeObject no = new NodeObject(nodeID, "Node " + nodeID, stringFormatterResult,
				isExtended, lineColorRGB, lineWidth, drawingArea);
		boundingBoxes.put(no.getId(), new AbsoluteRectangle(no.getBoundingBox()));
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
		refreshNodeObjectConfiguration();
		// this has to be done to start or stop the packet consumer thread
		setActive(isActive());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Resets the configuration parameters of the node visualizations according to the node
	 * painter's configuration parameters.<br>
	 * <b>Note:</b> This object updates the quadTree in a synchronized block which means that the
	 * GUI has to wait for the end of the processing
	 */
	public synchronized void refreshNodeObjectConfiguration() {
		
		purgeStringFormatterResults();
		// get all drawing objects from the quad tree
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}
		
		// update all objects which represent nodes and store them in a list
		// of objects to be updated in the quad tree
		final List<DrawingObject> update = new LinkedList<DrawingObject>();
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				final NodeObject nodeObject = (NodeObject) drawingObject;
				final int nodeID = nodeObject.getNodeID();
				
				final String stringFormatterResult = getStringFormatterResultString(nodeID);
				nodeObject.update("Node " + nodeID, stringFormatterResult, xmlConfig
						.isExtendedInformationActive(nodeID), xmlConfig.getLineColorRGB(),
						xmlConfig.getLineWidth());
				update.add(nodeObject);
			}
		}
		
		synchronized (updatedObjects) {
			updatedObjects.addAll(update);
		}
		
		// and update the quad tree
		updateQuadTree();
	}
	
	@Override
	public void reset() {
		
		synchronized (layer) {
			layer.clear();
		}
		
		for (final DrawingObject d : layer.getDrawingObjects()) {
			fireDrawingObjectRemoved(d);
		}
		
		synchronized (updatedObjects) {
			updatedObjects.clear();
		}
		stringFormatterResults.clear();
		refreshConfigurationParameters();
	}
	
	@Override
	protected void updateQuadTree() {
		
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
			
			for (final DrawingObject drawingObject : obsolete) {
				layer.remove(drawingObject);
			}
		}
		
		// after the lock was returned it is time to update the display ...
		for (final DrawingObject drawingObject : update) {
			if ((oldBB = boundingBoxes.get(drawingObject.getId())) != null) {
				fireDrawingObjectChanged(drawingObject, oldBB);
				boundingBoxes.put(drawingObject.getId(), new AbsoluteRectangle(drawingObject
						.getBoundingBox()));
			} else {
				fireDrawingObjectAdded(drawingObject);
				boundingBoxes.put(drawingObject.getId(), new AbsoluteRectangle(drawingObject
						.getBoundingBox()));
			}
		}
		
		for (final DrawingObject drawingObject : obsolete) {
			fireDrawingObjectRemoved(drawingObject);
			boundingBoxes.remove(drawingObject.getId());
		}
		
	}
	
	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}
	
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
	 * Handles the timeout of a node.<br>
	 * That means that the node will not be painted any longer.
	 * 
	 * @param nodeID
	 *            the node's identifier
	 */
	public void handleNodeTimeout(final int nodeID) {
		
		// get all the layer's drawing objects
		final List<DrawingObject> dos = new LinkedList<DrawingObject>();
		synchronized (layer) {
			dos.addAll(layer.getDrawingObjects());
		}
		
		// look for the node object with the matching identifier
		for (final DrawingObject drawingObject : dos) {
			if ((drawingObject instanceof NodeObject)
					&& (((NodeObject) drawingObject).getNodeID() == nodeID)) {
				
				// if the object was found, put it into the set of objects to be removed
				synchronized (obsoleteObjects) {
					obsoleteObjects.add(drawingObject);
				}
				// remove the object by calling updateQuadTree() and return
				updateQuadTree();
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
	private boolean handleDoubleClick(final List<DrawingObject> drawingObjects,
			final Point clickPoint, final DrawingArea drawingArea) {
		
		// check all drawing objects
		// this has to be done in reverse order since the "normal" order is used for painting which
		// means that the topmost element is actually the last element in the list
		for (int i = drawingObjects.size() - 1; i >= 0; i--) {
			final DrawingObject drawingObject = drawingObjects.get(i);
			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject
					.getBoundingBox());
			// check which plug-in's bounding box contains the point which
			// was clicked (if any)
			if (bbox.contains(clickPoint)) {
				// check if the object is representing a node
				if (drawingObject instanceof NodeObject) {
					// if so, toggle its extension state
					final NodeObject no = (NodeObject) drawingObject;
					no.setExtended(!no.isExtended());
					xmlConfig.putExtendedInformationActive(no.getNodeID(), no.isExtended());
					updatedObjects.add(no);
					updateQuadTree();
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
	private boolean handleRightClick(final List<DrawingObject> drawingObjects,
			final Point clickPoint, final DrawingArea drawingArea) {
		
		// check all drawing objects
		for (final DrawingObject drawingObject : drawingObjects) {
			
			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject
					.getBoundingBox());
			// check which plug-in's bounding box contains the point which
			// was clicked (if any)
			if (bbox.contains(clickPoint)) {
				synchronized (layer) {
					layer.bringToFront(drawingObject);
					// since the old and the new bounding box are equal, the map needs no update
					fireDrawingObjectChanged(drawingObject, boundingBoxes
							.get(drawingObject.getId()));
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
	private boolean handleWheelClick(final List<DrawingObject> drawingObjects,
			final Point clickPoint, final DrawingArea drawingArea) {
		
		// check all drawing objects
		for (final DrawingObject drawingObject : drawingObjects) {
			
			final PixelRectangle bbox = drawingArea.absRect2PixelRect(drawingObject
					.getBoundingBox());
			// check which plug-in's bounding box contains the point which
			// was clicked (if any)
			if (bbox.contains(clickPoint)) {
				synchronized (layer) {
					layer.pushBack(drawingObject);
					// since the old and the new bounding box are equal, the map needs no update
					fireDrawingObjectChanged(drawingObject, boundingBoxes
							.get(drawingObject.getId()));
				}
				return true;
			}
		}
		return false;
	}
}