/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

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
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
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

	public static PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new NodeSensorRangePreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "NodeSensorRange";
	}

	private Hashtable<Integer, NodeSensorRangeDrawingObject> dos = new Hashtable<Integer, NodeSensorRangeDrawingObject>();

	private Layer layer = new QuadTree();

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

	public NodeSensorRangePlugin() {
		super(false);
		xmlConfig = new NodeSensorRangeXMLConfig();
	}

	private void add(final int node, final NodeSensorRangeDrawingObject drawingObject) {
		dos.put(node, drawingObject);
		layer.addOrUpdate(drawingObject);
		fireDrawingObjectAdded(drawingObject);
	}

	private NodeSensorRangeCircle createCircle(final CircleRange range) {

		final NodeSensorRangeCircle circle = new NodeSensorRangeCircle();
		circle.setRadius(range.getCircleRadius());
		return circle;

	}

	private NodeSensorRangeCone createCone(final ConeRange range) {

		final NodeSensorRangeCone cone = new NodeSensorRangeCone();
		cone.setOrientation(range.getConeOrientation());
		cone.setRadius(range.getConeRadius());
		cone.setViewAngle(range.getConeViewAngle());
		return cone;

	}

	private NodeSensorRangeDrawingObject createNodeRangeDrawingObject(final Config config, final AbsolutePosition position) {

		NodeSensorRangeDrawingObject drawingObject;
		final NodeSensorRange defaultRange = config.getRange();
		final int[] background = config.getBackgroundRGB();
		final int[] foreground = config.getColorRGB();
		final RGB foregroundRGB = new RGB(foreground[0], foreground[1], foreground[2]);
		final RGB backgroundRGB = new RGB(background[0], background[1], background[2]);

		final boolean isRectangle = defaultRange instanceof RectangleRange;
		final boolean isCone = defaultRange instanceof ConeRange;

		drawingObject = isRectangle ? createRectangle((RectangleRange) defaultRange) : isCone ? createCone((ConeRange) defaultRange)
				: createCircle((CircleRange) defaultRange);

		drawingObject.setPosition(position);
		drawingObject.setColor(foregroundRGB);
		drawingObject.setBgColor(backgroundRGB);

		return drawingObject;
	}

	@Override
	public PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new NodeSensorRangePreferencePage(dialog, spyglass, this);
	}

	private NodeSensorRangeRectangle createRectangle(final RectangleRange range) {

		final NodeSensorRangeRectangle rect = new NodeSensorRangeRectangle();
		rect.setWidth(range.getRectangleWidth());
		rect.setHeight(range.getRectangleHeight());
		rect.setOrientation(range.getRectangleOrientation());
		return rect;

	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

	private NodeSensorRangeDrawingObject get(final int node) {
		return dos.get(node);
	}

	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}

	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
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
	public void init(final PluginManager pluginManager) {
		super.init(pluginManager);
		pluginManager.addNodePositionListener(nodePositionListener);
		xmlConfig.addPropertyChangeListener(defaultConfigPropertyChangeListener);
		xmlConfig.getDefaultConfig().addPropertyChangeListener(defaultConfigPropertyChangeListener);
	}

	private void onNodeAdded(final int node, final AbsolutePosition newPosition) {

		final NodeSensorRangeDrawingObject nrdo = createNodeRangeDrawingObject(xmlConfig.getDefaultConfig(), newPosition);
		add(node, nrdo);
		fireDrawingObjectAdded(nrdo);

	}

	private void onNodeMoved(final int node, final AbsolutePosition oldPosition, final AbsolutePosition newPosition) {

		final NodeSensorRangeDrawingObject drawingObject = get(node);

		if (drawingObject != null) {

			final AbsoluteRectangle oldBoundingBox = drawingObject.getBoundingBox();
			drawingObject.setPosition(newPosition);
			fireDrawingObjectChanged(drawingObject, oldBoundingBox);

		} else {

			onNodeAdded(node, newPosition);

		}

	}

	private void onNodeRemoved(final int node, final AbsolutePosition oldPosition) {
		fireDrawingObjectRemoved(remove(node));
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}

	private PropertyChangeListener defaultConfigPropertyChangeListener = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent e) {

			if (NodeSensorRangeXMLConfig.PROPERTYNAME_DEFAULT_CONFIG.equals(e.getPropertyName())) {
				onDefaultConfigChanged((Config) e.getOldValue(), (Config) e.getNewValue());
				return;
			}

			else if (NodeSensorRangeXMLConfig.PROPERTYNAME_RANGE.equals(e.getPropertyName())) {
				onDefaultConfigRangeChanged(e.getPropertyName(), (NodeSensorRange) e.getOldValue(), (NodeSensorRange) e.getNewValue());
				return;
			}

			throw new RuntimeException("Unexpected property: " + e.getPropertyName());
		}
	};

	// --------------------------------------------------------------------------------
	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	private void onDefaultConfigRangeChanged(final String propertyName, final NodeSensorRange oldValue, final NodeSensorRange newValue) {

		final boolean isRangeRectangle = newValue instanceof RectangleRange;
		final boolean isRangeCone = newValue instanceof ConeRange;

		NodeSensorRangeDrawingObject oldDob;

		final Integer[] nodes = dos.keySet().toArray(new Integer[dos.size()]);

		layer.clear();

		for (final int node : nodes) {

			oldDob = dos.get(node);

			final NodeSensorRangeDrawingObject newDob = isRangeRectangle ? new NodeSensorRangeRectangle() : isRangeCone ? new NodeSensorRangeCone()
					: new NodeSensorRangeCircle();

			newDob.setBackgroundAlpha(oldDob.getBackgroundAlpha());
			newDob.setBgColor(oldDob.getBgColor());
			newDob.setColor(oldDob.getColor());
			newDob.setPosition(oldDob.getPosition(), false);
			newDob.setRange(newValue);

			dos.put(node, newDob);
			layer.remove(oldDob);
			fireDrawingObjectRemoved(oldDob);
			layer.addOrUpdate(newDob);
			fireDrawingObjectAdded(newDob);

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * @param newConfig
	 * @param newConfig
	 */
	private void onDefaultConfigChanged(final Config oldValue, final Config newConfig) {

		oldValue.removePropertyChangeListener(defaultConfigPropertyChangeListener);
		newConfig.addPropertyChangeListener(defaultConfigPropertyChangeListener);

		// get lock for the plugin so that the following code-block is atomatic
		synchronized (this) {

			final Set<Integer> nodeSet = dos.keySet();

			dos.clear();
			layer.clear();

			AbsolutePosition position;

			for (final int node : nodeSet) {

				position = pluginManager.getNodePositioner().getPosition(node);
				add(node, createNodeRangeDrawingObject(newConfig, position));

			}

		}

	}

	private NodeSensorRangeDrawingObject remove(final int node) {

		final NodeSensorRangeDrawingObject drawingObject;

		drawingObject = dos.get(node);
		layer.remove(drawingObject);
		dos.remove(node);

		fireDrawingObjectRemoved(drawingObject);

		return drawingObject;

	}

	@Override
	public void reset() {
		dos.clear();
		layer.clear();
	}

	@Override
	protected void updateLayer() {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
}