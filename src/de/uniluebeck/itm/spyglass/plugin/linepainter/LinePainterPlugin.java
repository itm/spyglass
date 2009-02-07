/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.graphics.RGB;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.Uint16ListPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.relationpainter.RelationPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class LinePainterPlugin extends RelationPainterPlugin implements PropertyChangeListener, NodePositionListener {

	@Element(name = "parameters")
	private final LinePainterXMLConfig xmlConfig;

	private Layer layer;

	public LinePainterPlugin() {

		super();

		xmlConfig = new LinePainterXMLConfig();
		layer = Layer.Factory.createQuadTreeLayer();

	}

	@Override
	public PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass);
	}

	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

	public static String getHumanReadableName() {
		return "LinePainter";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	private class Edge {

		public int nodeId1;

		public int nodeId2;

		public LinePainterLine line;

		public Edge(final int nodeId1, final int nodeId2) {
			this.nodeId1 = nodeId1;
			this.nodeId2 = nodeId2;
		}

		@Override
		public String toString() {
			return line.toString();
		}

	}

	public void handleEvent(final NodePositionEvent event) {
		AbsoluteRectangle oldBoundingBox;
		for (final Edge e : getIncidentEdges(event.node)) {
			oldBoundingBox = e.line.getBoundingBox();
			if (e.nodeId1 == event.node) {
				e.line.setPosition(event.newPosition);
			} else {
				e.line.setEnd(event.newPosition);
			}
			fireDrawingObjectChanged(e.line, oldBoundingBox);
		}
	}

	private Map<Edge, Long> edgeTimes = Collections.synchronizedMap(new HashMap<Edge, Long>());

	private List<Edge> newEdges = Collections.synchronizedList(new LinkedList<Edge>());

	private List<Edge> removedEdges = Collections.synchronizedList(new LinkedList<Edge>());

	private StringFormatter defaultStringFormatter;

	public void updateEdgeTime(final Edge edge) {
		edgeTimes.put(edge, System.currentTimeMillis());
	}

	public Edge getExistingEdge(final int nodeId1, final int nodeId2) {
		for (final Edge e : edgeTimes.keySet()) {
			final boolean same = ((e.nodeId1 == nodeId1) && (e.nodeId2 == nodeId2)) || ((e.nodeId1 == nodeId2) && (e.nodeId2 == nodeId1));
			if (same) {
				return e;
			}
		}
		return null;
	}

	public Set<Edge> getIncidentEdges(final int nodeId) {
		final Set<Edge> incidentEdges = new HashSet<Edge>();
		for (final Edge e : edgeTimes.keySet()) {
			if ((e.nodeId1 == nodeId) || (e.nodeId2 == nodeId)) {
				incidentEdges.add(e);
			}
		}
		return incidentEdges;
	}

	public void removeEdge(final Edge edge) {
		if (edgeTimes.remove(edge) != null) {
			removedEdges.add(edge);
			return;
		}
		throw new NullPointerException("Edge was not contained.");
	}

	public void addEdge(final Edge edge) {
		edgeTimes.put(edge, System.currentTimeMillis());
		newEdges.add(edge);
	}

	@Override
	protected void processPacket(final SpyglassPacket p) {

		final Uint16ListPacket packet = (Uint16ListPacket) p;

		final List<Integer> neighboorIds = packet.getNeighborhoodPacketNodeIDs();
		final int senderId = packet.getSenderId();
		Edge e;

		for (final int neighboorId : neighboorIds) {

			final NodePositionerPlugin nodePositioner = getPluginManager().getNodePositioner();
			final AbsolutePosition pos1 = nodePositioner.getPosition(senderId);
			final AbsolutePosition pos2 = nodePositioner.getPosition(neighboorId);

			if ((pos1 != null) && (pos2 != null)) {

				e = getExistingEdge(senderId, neighboorId);

				if (e == null) {
					e = new Edge(senderId, neighboorId);
					e.line = new LinePainterLine();
					e.line.setPosition(pos1);
					e.line.setEnd(pos2);
					final int[] color = xmlConfig.getLineColorRGB();
					e.line.setColor(new RGB(color[0], color[1], color[2]));
					e.line.setLineWidth(xmlConfig.getLineWidth());

					// check if there's a string formatter especially for this
					// semantic type and use it if so, otherwise use default
					// string formatter
					if (stringFormatters.get(p.getSemanticType()) != null) {
						e.line.setStringFormatterResult(stringFormatters.get(p.getSemanticType()).parse(p), false);
					} else {
						e.line.setStringFormatterResult(defaultStringFormatter.parse(p), false);
					}

					addEdge(e);

				} else {

					updateEdgeTime(e);

				}

			}

		}
		
		updateLayer();

	}

	@Override
	protected void resetPlugin() {
		removedEdges.addAll(edgeTimes.keySet());
		edgeTimes.clear();
		newEdges.clear();
		synchronized (layer) {
			layer.clear();
		}
		updateLayer();
	}

	private void updateLayer() {

		LinePainterLine line;
		final List<LinePainterLine> addedLines = new ArrayList<LinePainterLine>();
		final List<LinePainterLine> removedLines = new ArrayList<LinePainterLine>();

		synchronized (layer) {

			while (newEdges.size() > 0) {
				line = newEdges.get(0).line;
				newEdges.remove(0);
				layer.add(line);
				addedLines.add(line);
			}

			while (removedEdges.size() > 0) {
				line = removedEdges.get(0).line;
				removedEdges.remove(0);
				layer.remove(line);
				removedLines.add(line);
			}

		}

		for (final LinePainterLine l : addedLines) {
			fireDrawingObjectAdded(l);
		}

		for (final LinePainterLine l : removedLines) {
			fireDrawingObjectRemoved(l);
		}

	}

	@Override
	public void init(final PluginManager manager) throws Exception {

		super.init(manager);

		defaultStringFormatter = new StringFormatter(xmlConfig.getDefaultStringFormatter());
		stringFormatters = new Hashtable<Integer, StringFormatter>();
		setStringFormatters(xmlConfig.getStringFormatters());
		xmlConfig.addPropertyChangeListener(this);
		pluginManager.addNodePositionListener(this);

		handleTimeoutChange(xmlConfig.getTimeout());

	}

	@Override
	public void shutdown() {

		xmlConfig.removePropertyChangeListener(this);
		pluginManager.removeNodePositionListener(this);

	}

	@Override
	public Set<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void propertyChange(final PropertyChangeEvent event) {
		if (LinePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B.equals(event.getPropertyName())) {
			updateLineColor((int[]) event.getNewValue());
		} else if (LinePainterXMLConfig.PROPERTYNAME_LINE_WIDTH.equals(event.getPropertyName())) {
			updateLineWidth((Integer) event.getNewValue());
		} else if (PluginXMLConfig.PROPERTYNAME_TIMEOUT.equals(event.getPropertyName())) {
			handleTimeoutChange((Integer) event.getNewValue());
		} else if (PluginWithStringFormatterXMLConfig.PROPERTYNAME_STRING_FORMATTERS.equals(event.getPropertyName())) {
			handleStringFormattersChange((Map<Integer, String>) event.getNewValue());
		} else if (PluginWithStringFormatterXMLConfig.PROPERTYNAME_DEFAULT_STRING_FORMATTER.equals(event.getPropertyName())) {
			handleDefaultStringFormatterChange((String) event.getNewValue());
		}
	}

	private void handleDefaultStringFormatterChange(final String defaultStringFormatterExpr) {
		this.defaultStringFormatter = new StringFormatter(defaultStringFormatterExpr);
	}

	private Hashtable<Integer, StringFormatter> stringFormatters = new Hashtable<Integer, StringFormatter>();

	private void setStringFormatters(final Map<Integer, String> newValue) {
		this.stringFormatters.clear();
		for (final int semanticType : newValue.keySet()) {
			this.stringFormatters.put(semanticType, new StringFormatter(newValue.get(semanticType)));
		}
	}

	private void handleStringFormattersChange(final Map<Integer, String> newValue) {
		setStringFormatters(newValue);
	}

	private Timer timer;

	private static final String TIMER_NAME = "LinePainterPlugin-Timeout-Timer";

	private void handleTimeout() {
		final long now = System.currentTimeMillis();
		final long timeout = 1000 * xmlConfig.getTimeout();
		long diff;

		final List<Edge> edgesToRemove = new ArrayList<Edge>();

		for (final Edge e : edgeTimes.keySet()) {

			diff = now - edgeTimes.get(e);
			if (diff > timeout) {
				edgesToRemove.add(e);
			}

		}

		for (final Edge e : edgesToRemove) {
			removeEdge(e);
		}

		updateLayer();
	}

	private void handleTimeoutChange(final long timeout) {
		if (timer != null) {
			timer.cancel();
		}
		if (timeout > 0) {
			timer = new Timer(TIMER_NAME);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handleTimeout();
				}
			}, 1000 * timeout, 1000 * timeout);
		}
	}

	private void updateLineColor(final int[] color) {
		LinePainterLine line;
		synchronized (layer) {
			for (final DrawingObject o : layer.getDrawingObjects()) {
				line = (LinePainterLine) o;
				line.setColor(new RGB(color[0], color[1], color[2]));
				fireDrawingObjectChanged(line, line.getBoundingBox());
			}
		}
	}

	private void updateLineWidth(final int width) {
		LinePainterLine line;
		synchronized (layer) {
			for (final DrawingObject o : layer.getDrawingObjects()) {
				line = (LinePainterLine) o;
				line.setLineWidth(width);
				fireDrawingObjectChanged(line, line.getBoundingBox());
			}
		}
	}

}