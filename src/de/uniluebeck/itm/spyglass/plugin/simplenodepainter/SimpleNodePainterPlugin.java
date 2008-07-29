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
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class SimpleNodePainterPlugin extends NodePainterPlugin {
	
	@Element(name = "parameters")
	private SimpleNodePainterXMLConfig xmlConfig;
	
	private final Layer layer;
	
	private final List<DrawingObject> objectsToUpdate;
	
	public SimpleNodePainterPlugin() {
		xmlConfig = new SimpleNodePainterXMLConfig();
		layer = new SubLayer();
		objectsToUpdate = new LinkedList<DrawingObject>();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
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
		// TODO Auto-generated method stub
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
		
		// add the object to the one which have to be (re)drawn ...
		synchronized (objectsToUpdate) {
			objectsToUpdate.add(nodeObject);
		}
		
		// and update the quad tree
		updateQuadTree();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the up to date instance of a node's visualization
	 * 
	 * @param nodeID
	 *            the node's identifier
	 * @param position
	 *            the node's position
	 * @param drawingObjects
	 *            the drawing objects which are currently available in the quad
	 *            tree
	 * @return the up to date instance of a node's visualization
	 */
	private NodeObject updateMatchingNodeObject(final int nodeID, final AbsolutePosition position, final List<DrawingObject> drawingObjects) {
		
		boolean found = false;
		NodeObject nodeObject = null;
		
		// if there is already an instance of the node's visualization
		// available in the quadTree it has to be updated
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				nodeObject = (NodeObject) drawingObject;
				if (nodeObject.getNodeID() == nodeID) {
					nodeObject.setPosition(position);
					found = true;
				}
			}
		}
		
		// if not, create a new object if no matching
		if (!found) {
			nodeObject = new NodeObject(nodeID, "Node " + nodeID, xmlConfig.getStringFormatters().get(nodeID), xmlConfig
					.getIsExtendenInformationActive().get(nodeID), xmlConfig.getLineColorRGB(), xmlConfig.getLineWidth());
			nodeObject.setPosition(position);
		}
		return nodeObject;
	}
	
	/**
	 * Resets the configuration parameters of the node visualizations according
	 * to the node painter's configuration parameters.<br>
	 * <b>Note:</b> This object updates the quadTree in a synchronized block
	 * which means that the GUI has to wait for the end of the processing
	 */
	public void refreshNodeObjectConfiguration() {
		
		// get all drawing objects from the quad tree
		final List<DrawingObject> drawingObjects = new LinkedList<DrawingObject>();
		synchronized (layer) {
			drawingObjects.addAll(layer.getDrawingObjects());
		}
		
		final List<DrawingObject> update = new LinkedList<DrawingObject>();
		// update all objects o which represent nodes and store them in a list
		// of objects to be updated in the quad tree
		for (final DrawingObject drawingObject : drawingObjects) {
			if (drawingObject instanceof NodeObject) {
				final NodeObject nodeObject = (NodeObject) drawingObject;
				final int nodeID = nodeObject.getNodeID();
				nodeObject.update("Node " + nodeID, xmlConfig.getStringFormatters().get(nodeID), xmlConfig.getIsExtendenInformationActive().get(
						nodeID), xmlConfig.getLineColorRGB(), xmlConfig.getLineWidth());
				update.add(nodeObject);
			}
		}
		
		synchronized (objectsToUpdate) {
			objectsToUpdate.addAll(update);
		}
		
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setXMLConfig(final PluginXMLConfig xmlConfig) {
		this.xmlConfig = (SimpleNodePainterXMLConfig) xmlConfig;
	}
	
	@Override
	protected void updateQuadTree() {
		final List<DrawingObject> update = layer.getDrawingObjects();
		
		// copy all objects which have to be updated in a separate List and
		// clear the original list
		synchronized (objectsToUpdate) {
			update.addAll(objectsToUpdate);
			objectsToUpdate.clear();
		}
		
		// catch the layer's lock and update the objects
		synchronized (layer) {
			for (final DrawingObject drawingObject : update) {
				layer.addOrUpdate(drawingObject);
			}
		}
	}
	
}