/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.NodeObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.layer.SubLayer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.nodepainter.NodePainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are used create and administer simple visualizations
 * of sensor nodes.<br>
 * The nodes can be visualized in two way's according to the amount of
 * information the user wants to see.
 * <ul>
 * <li> In the <tt>non-extended mode</tt>, the nodes are represented by
 * rectangles which only contain the node's identifier.</li>
 * <li>In the <tt>extended mode</tt> the nodes are again represented by
 * rectangles which contain the node's identifier. But additionally, further
 * information which are extracted from the packets of certain semantic types
 * are displayed, too.</li>
 * </ul>
 * 
 * @author Sebastian Ebers
 */
public class SimpleNodePainterPlugin extends NodePainterPlugin {
	
	/**
	 * The configuration parameters of this plug-in instance
	 */
	@Element(name = "parameters")
	private SimpleNodePainterXMLConfig xmlConfig;
	
	/**
	 * The quad tree containing the drawing objects
	 */
	private final Layer layer;
	
	/**
	 * Objects which have recently been updated and which needs to be updated in
	 * the quad tree as well (which is done in
	 * {@link SimpleNodePainterPlugin#updateQuadTree()}
	 */
	private final List<DrawingObject> updatedObjects;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleNodePainterPlugin() {
		super();
		xmlConfig = new SimpleNodePainterXMLConfig();
		layer = new SubLayer();
		updatedObjects = new LinkedList<DrawingObject>();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		synchronized (layer) {
			
		}
		synchronized (updatedObjects) {
			updatedObjects.clear();
		}
		xmlConfig.finalize();
		// TODO: efficiently finalize the member objects
	}
	
	@Override
	public PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createPreferencePage(final ConfigStore cs) {
		return new SimpleNodePainterPreferencePage(cs, this);
	}
	
	public static PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		return new SimpleNodePainterPreferencePage(cs);
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
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
		
		final int nodeID = packet.getSender_id();
		
		// get the absolute position of the node which sent the packet
		final AbsolutePosition position = getPluginManager().getNodePositioner().getPosition(nodeID);
		
		// get all drawing objects from the quad tree
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}
		
		// get the up to date instance of the node's visualization
		final NodeObject nodeObject = updateMatchingNodeObject(nodeID, position, drawingObjects);
		
		// if the object is null, there is no need to draw it
		if (nodeObject == null) {
			return;
		}
		// add the object to the one which have to be (re)drawn ...
		synchronized (updatedObjects) {
			updatedObjects.add(nodeObject);
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the up to date instance of a node's visualization or
	 * <code>null</code> if the instance did not change at all.<br>
	 * Note that either a matching instance if found in the quad tree or a new
	 * instance is to be created.<br>
	 * If an existing instance was found, only its position information will be
	 * updated. If the position has not changed, nothing has to be done and
	 * <code>null</code> will be returned.
	 * 
	 * @param nodeID
	 *            the node's identifier
	 * @param position
	 *            the node's position
	 * @param drawingObjects
	 *            the drawing objects which are currently available in the quad
	 *            tree
	 * @return the up to date instance of a node's visualization or
	 *         <code>null</code> if the instance did not change at all.
	 */
	private NodeObject updateMatchingNodeObject(final int nodeID, final AbsolutePosition position, final List<DrawingObject> drawingObjects) {
		
		boolean found = false;
		NodeObject nodeObject = null;
		
		// if there is already an instance of the node's visualization
		// available in the quadTree it has to be updated
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				nodeObject = (NodeObject) drawingObject;
				
				// if the matching node is found ...
				if (nodeObject.getNodeID() == nodeID) {
					
					// and the position is still the same, null is returned to
					// indicate that the matching object has not changed at all
					if (nodeObject.getPosition().equals(position)) {
						return null;
					}
					
					// otherwise update the position
					nodeObject.setPosition(position);
					found = true;
				}
			}
		}
		
		// if not, create a new object
		if (!found) {
			Boolean isExtended = xmlConfig.getIsExtendenInformationActive().get(nodeID);
			if (isExtended == null) {
				isExtended = xmlConfig.isExtendedDefaultValue();
			}
			
			final StringFormatter sf = xmlConfig.getStringFormatters().get(nodeID);
			final int[] lineColorRGB = xmlConfig.getLineColorRGB();
			final int lineWidth = xmlConfig.getLineWidth();
			nodeObject = new NodeObject(nodeID, "Node " + nodeID, sf, isExtended, lineColorRGB, lineWidth);
			nodeObject.setPosition(position);
		}
		return nodeObject;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Resets the configuration parameters of the node visualizations according
	 * to the node painter's configuration parameters.<br>
	 * <b>Note:</b> This object updates the quadTree in a synchronized block
	 * which means that the GUI has to wait for the end of the processing
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
				nodeObject.update("Node " + nodeID, xmlConfig.getStringFormatters().get(nodeID), xmlConfig.getIsExtendenInformationActive().get(
						nodeID), xmlConfig.getLineColorRGB(), xmlConfig.getLineWidth());
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
	public void setXMLConfig(final PluginXMLConfig xmlConfig) {
		this.xmlConfig = (SimpleNodePainterXMLConfig) xmlConfig;
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
		
		// catch the layer's lock and update the objects
		synchronized (layer) {
			for (final DrawingObject drawingObject : update) {
				layer.addOrUpdate(drawingObject);
			}
		}
	}
	
}