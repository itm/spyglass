/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.CircleRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.ConeRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.NodeSensorRange;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.RectangleRange;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class NodeSensorRangePlugin extends BackgroundPainterPlugin {

	@Element(name = "parameters")
	private final NodeSensorRangeXMLConfig xmlConfig;

	private Layer layer = new QuadTree();

	public NodeSensorRangePlugin() {
		super(false);
		xmlConfig = new NodeSensorRangeXMLConfig();
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new NodeSensorRangePreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new NodeSensorRangePreferencePage(dialog, spyglass);
	}

	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

	public static String getHumanReadableName() {
		return "NodeSensorRange";
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
	protected void processPacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}

	@Override
	public void reset() {
		dos.clear();
		layer.clear();
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

	private NodeSensorRangeDrawingObject createDefaultNodeRangeDrawingObject(final AbsolutePosition position) {

		NodeSensorRangeDrawingObject drawingObject;
		final NodeSensorRange defaultRange = xmlConfig.getDefaultRange();
		final int[] color = xmlConfig.getDefaultColorRGB();

		if ((defaultRange == null) || (defaultRange instanceof CircleRange)) {

			final NodeSensorRangeCircle circle = new NodeSensorRangeCircle();
			final CircleRange range = (CircleRange) defaultRange;

			circle.setColor(new RGB(color[0], color[1], color[2]));
			circle.setRadius(range.getCircleRadius());
			circle.setPosition(position);

			drawingObject = circle;

		} else if (defaultRange instanceof RectangleRange) {

			final NodeSensorRangeRectangle rect = new NodeSensorRangeRectangle();
			final RectangleRange range = (RectangleRange) defaultRange;

			rect.setColor(new RGB(color[0], color[1], color[2]));
			rect.setWidth(range.getRectangleWidth());
			rect.setHeight(range.getRectangleHeight());
			rect.setOrientation(range.getRectangleOrientation());
			rect.setPosition(position);

			drawingObject = rect;

		} else if (defaultRange instanceof ConeRange) {

			final NodeSensorRangeCone cone = new NodeSensorRangeCone();
			final ConeRange range = (ConeRange) defaultRange;

			cone.setColor(new RGB(color[0], color[1], color[2]));
			cone.setOrientation(range.getConeOrientation());
			cone.setPosition(position);
			cone.setRadius(range.getConeRadius());
			cone.setViewAngle(range.getConeViewAngle());

			drawingObject = cone;

		} else {

			throw new RuntimeException("Unexpected case.");

		}

		return drawingObject;
	}

	private Hashtable<Integer, NodeSensorRangeDrawingObject> dos = new Hashtable<Integer, NodeSensorRangeDrawingObject>();

	private void add(final int node, final NodeSensorRangeDrawingObject drawingObject) {
		dos.put(node, drawingObject);
		layer.addOrUpdate(drawingObject);
	}

	private NodeSensorRangeDrawingObject get(final int node) {
		return dos.get(node);
	}

	private NodeSensorRangeDrawingObject remove(final int node) {

		final NodeSensorRangeDrawingObject drawingObject;

		drawingObject = dos.get(node);
		layer.remove(drawingObject);
		dos.remove(node);

		return drawingObject;

	}

	private void onNodeAdded(final int node, final AbsolutePosition newPosition) {
		final NodeSensorRangeDrawingObject nrdo = createDefaultNodeRangeDrawingObject(newPosition);
		add(node, nrdo);
		fireDrawingObjectAdded(nrdo);

		System.out.println("added " + node);
	}

	private void onNodeMoved(final int node, final AbsolutePosition oldPosition, final AbsolutePosition newPosition) {

		final NodeSensorRangeDrawingObject drawingObject = get(node);

		if (drawingObject != null) {

			final AbsoluteRectangle oldBoundingBox = drawingObject.getBoundingBox();
			drawingObject.setPosition(newPosition);
			fireDrawingObjectChanged(drawingObject, oldBoundingBox);

			System.out.println("moved node " + node);

		} else {

			onNodeAdded(node, newPosition);

			System.out.println("added in onmove " + node);

		}

	}

	private void onNodeRemoved(final int node, final AbsolutePosition oldPosition) {
		fireDrawingObjectRemoved(remove(node));
		System.out.println("removed node " + node);
	}

	@Override
	public void init(final PluginManager manager) {
		manager.addNodePositionListener(nodePositionListener);
	}

	@Override
	protected void updateLayer() {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}

	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}
}