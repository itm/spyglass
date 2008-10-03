/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.Collection;
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
 * <li>In the <tt>non-extended mode</tt>, the nodes are represented by rectangles which only
 * contain the node's identifier.</li>
 * <li>In the <tt>extended mode</tt> the nodes are again represented by rectangles which contain
 * the node's identifier. But additionally, further information which are extracted from the packets
 * of certain semantic types are displayed, too.</li>
 * </ul>
 * 
 * @author Sebastian Ebers
 */
public class SimpleNodePainterPlugin extends NodePainterPlugin {
	
	public static final Logger log = SpyglassLogger.getLogger(SimpleNodePainterPlugin.class);
	
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
	
	private Map<Integer, Map<Integer, String>> stringFormatterResults;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleNodePainterPlugin() {
		super();
		xmlConfig = new SimpleNodePainterXMLConfig();
		layer = new QuadTree();
		updatedObjects = new HashSet<DrawingObject>();
		stringFormatterResults = new TreeMap<Integer, Map<Integer, String>>();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		synchronized (layer) {
			
		}
		synchronized (updatedObjects) {
			updatedObjects.clear();
		}
		// xmlConfig.finalize();
		// TODO: efficiently finalize the member objects
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
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		synchronized (layer) {
			return layer.getDrawingObjects(drawingArea.getAbsoluteDrawingRectangle());
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
	
	public void setXMLConfig() {
		System.out.println("Test");
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
		
		final int packetSemanticType = packet.getSemanticType();
		try {
			final StringFormatter sf = xmlConfig.getStringFormatter(packetSemanticType);
			if (sf != null) {
				final String str = sf.parse(packet);
				updateStringFormatterResults(packetSemanticType, str, nodeID);
				nodeObject.setDescription(getStringFormatterResultString(nodeID));
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
		
		if (needsUpdate) {
			// add the object to the one which have to be (re)drawn ...
			synchronized (updatedObjects) {
				updatedObjects.add(nodeObject);
			}
		}
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
		
		// if there is already an instance of the node's visualization
		// available in the quadTree it has to be updated
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				nodeObject = (NodeObject) drawingObject;
				
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
		return new NodeObject(nodeID, "Node " + nodeID, stringFormatterResult, isExtended,
				lineColorRGB, lineWidth);
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
	 * Performs internal refreshments of the configuration parameters.<br>
	 * This includes the node object amongst others since e.g. the line color etc. could have been
	 * changed.
	 */
	public void refreshConfigurationParameters() {
		refreshNodeObjectConfiguration();
		// this has to be done to start or stop the packet consumer thread when needed
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
		
		// get all drawing objects from the quad tree
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}
		
		// update all objects o which represent nodes and store them in a list
		// of objects to be updated in the quad tree
		final List<DrawingObject> update = new LinkedList<DrawingObject>();
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				final NodeObject nodeObject = (NodeObject) drawingObject;
				final int nodeID = nodeObject.getNodeID();
				
				final String stringFormatterResult = getStringFormatterResultString(nodeID);
				System.out.println(xmlConfig.isExtendedInformationActive(nodeID));
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
		// TODO Auto-generated method stub
		synchronized (layer) {
			
		}
		
		synchronized (updatedObjects) {
			updatedObjects.clear();
		}
		
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
		
		// if no drawing objects are available, there is no need to get the
		// layer's lock
		if (update.size() == 0) {
			return;
		}
		
		// catch the layer's lock and update the objects
		synchronized (layer) {
			for (final DrawingObject drawingObject : update) {
				layer.addOrUpdate(drawingObject);
			}
		}
	}
	
	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		// TODO Auto-generated method stub
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}
	
	@Override
	public boolean handleEvent(final MouseEvent e, final DrawingArea drawingArea) {
		
		// get the objects to draw
		final List<DrawingObject> dos = new LinkedList<DrawingObject>(layer
				.getDrawingObjects(drawingArea.getAbsoluteDrawingRectangle()));
		
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
	 * Handles a mouse click event which was actually a double click returns <code>true</code> if
	 * a drawing object was found which bounding box contains the point clicked by the user.
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
		for (final DrawingObject drawingObject : drawingObjects) {
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
				}
				return true;
			}
		}
		return false;
	}
}