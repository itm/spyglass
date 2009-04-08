/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.util.Set;
import java.util.SortedSet;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class NodeSensorRangePlugin extends BackgroundPainterPlugin {

	public static PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new NodeSensorRangePreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "NodeSensorRange";
	}

	private NodePositionListener nodePositionListener = new NodePositionListener() {
		@Override
		public void handleEvent(final NodePositionEvent e) {

			switch (e.change) {
				case ADDED:
					onNodeAdded(e.node, e.newPosition);
					break;
				case MOVED:
					onNodeMoved(e.node, e.oldPosition, e.newPosition);
					break;
				case REMOVED:
					onNodeRemoved(e.node, e.oldPosition);
					break;
			}
		}
	};

	@Element(name = "parameters")
	private final NodeSensorRangeXMLConfig xmlConfig;

	private Data data;

	public NodeSensorRangePlugin() {
		super(false);
		data = new Data();
		xmlConfig = new NodeSensorRangeXMLConfig();
	}

	@Override
	public PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new NodeSensorRangePreferencePage(dialog, spyglass, this);
	}

	@Override
	public Set<DrawingObject> getAutoZoomDrawingObjects() {
		return data.getDrawingObjects();
	}

	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return data.getDrawingObjects(area);
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	@Override
	public void handlePacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}

	@Override
	public void init(final PluginManager pluginManager) throws Exception {

		super.init(pluginManager);
		pluginManager.addNodePositionListener(nodePositionListener);

		// add NodeSensorRangeDrawingObjects for all existing nodes
		for (final int node : pluginManager.getNodePositioner().getNodeList()) {
			onNodeAdded(node, pluginManager.getNodePositioner().getPosition(node));
		}

	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		pluginManager.removeNodePositionListener(nodePositionListener);
	}

	private void onNodeAdded(final int node, final AbsolutePosition newPosition) {

		final NodeSensorRangeDrawingObject nrdo = new NodeSensorRangeDrawingObject(this, xmlConfig.getDefaultConfig());
		nrdo.setPosition(newPosition);
		data.add(node, nrdo);

		fireDrawingObjectAdded(nrdo);

	}

	private void onNodeMoved(final int node, final AbsolutePosition oldPosition, final AbsolutePosition newPosition) {

		final NodeSensorRangeDrawingObject drawingObject = data.get(node);

		if (drawingObject != null) {

			drawingObject.setPosition(newPosition);

		} else {

			// got a move event from NodePositionerPlugin before getting an add event
			// which happens when this plug-in was instantiated after the node was added
			onNodeAdded(node, newPosition);

		}

	}

	private void onNodeRemoved(final int node, final AbsolutePosition oldPosition) {

		// don't cause a redraw if we're inactive
		fireDrawingObjectRemoved(data.remove(node));

	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}

	@Override
	protected void resetPlugin() {
		data.clear();
	}

}