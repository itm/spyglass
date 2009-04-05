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
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent.Change;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.relationpainter.RelationPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.StringFormatter;
import de.uniluebeck.itm.spyglass.util.Tuple;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginWithStringFormatterXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class LinePainterPlugin extends RelationPainterPlugin implements PropertyChangeListener, NodePositionListener {

	// --------------------------------------------------------------------------------
	/**
	 * Small helper class representing an edge in a graph, denoted by 2-tuple of 2 node IDs.
	 *
	 * @author Daniel Bimschas
	 */
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

	// --------------------------------------------------------------------------------
	/**
	 * A small helper class containing the graph defined by the edges which are read from incoming
	 * packages.
	 *
	 * @author Daniel Bimschas
	 */
	private class Data {

		/**
		 * Map containing timestamps that tell when an edge was added or last referenced by a
		 * package. By reading the keyset of the map you get all edges currently contained in the
		 * node graph.
		 */
		private Map<Edge, Long> edgeTimes = new HashMap<Edge, Long>();

		public void addEdge(final Edge edge) {
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
				return;
			}
			throw new NullPointerException("Edge was not contained.");
		}

		public void clear() {
			edgeTimes.clear();
		}

		public void updateEdgeTime(final Edge edge) {
			data.edgeTimes.put(edge, System.currentTimeMillis());
		}

	}

	public static PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "LinePainter";
	}

	@Element(name = "parameters")
	private final LinePainterXMLConfig xmlConfig;

	private Layer layer;

	private StringFormatter defaultStringFormatter;

	private Hashtable<Integer, StringFormatter> stringFormatters = new Hashtable<Integer, StringFormatter>();

	private Timer timer;

	private final Data data;

	private static final String TIMER_NAME = "LinePainterPlugin-Timeout-Timer";

	private final Object lock = new Object();

	public LinePainterPlugin() {

		super();

		xmlConfig = new LinePainterXMLConfig();
		layer = Layer.Factory.createQuadTreeLayer();
		data = new Data();

	}

	@Override
	public PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass, this);
	}

	@Override
	public Set<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}

	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	private void handleDefaultStringFormatterChange(final String defaultStringFormatterExpr) {
		this.defaultStringFormatter = new StringFormatter(defaultStringFormatterExpr);
	}

	public void handleEvent(final NodePositionEvent event) {

		// we can ignore add events, since we only add a node if it
		// sends a packet with the semantic type we're registered for
		if (event.change == Change.ADDED) {
			return;
		}

		if (event.change == Change.MOVED) {
			onNodeMoved(event.node, event.newPosition);
		} else if (event.change == Change.REMOVED) {
			onNodeRemoved(event.node);
		} else {
			throw new RuntimeException("Unexpected case.");
		}

	}

	void onNodeRemoved(final int node) {

		final LinkedList<LinePainterLine> removedLines = new LinkedList<LinePainterLine>();

		synchronized (lock) {

			/*
			 * we need to check if the node is existing in our data structures and remove it if
			 * that's the case
			 */

			for (final Edge e : data.getIncidentEdges(node)) {
				removedLines.add(e.line);
				data.removeEdge(e);
				layer.remove(e.line);
			}

		}

		// fire event outside lock block to avoid deadlocks
		fireDrawingObjectRemoved(removedLines);

	}

	void onNodeMoved(final int node, final AbsolutePosition newPosition) {

		final LinkedList<Tuple<LinePainterLine, AbsoluteRectangle>> updatedLines = new LinkedList<Tuple<LinePainterLine, AbsoluteRectangle>>();
		Tuple<LinePainterLine, AbsoluteRectangle> tuple;

		synchronized (lock) {

			for (final Edge e : data.getIncidentEdges(node)) {

				tuple = new Tuple<LinePainterLine, AbsoluteRectangle>(e.line, e.line.getBoundingBox());
				updatedLines.add(tuple);

				if (e.nodeId1 == node) {
					e.line.setPosition(newPosition);
				} else {
					e.line.setEnd(newPosition);
				}

			}
		}
	}

	private void handleStringFormattersChange(final Map<Integer, String> newValue) {
		setStringFormatters(newValue);
	}

	private void handleTimeout() {

		final long now = System.currentTimeMillis();
		final long timeout = 1000 * xmlConfig.getTimeout();
		long diff;

		final List<Edge> edgesToRemove = new ArrayList<Edge>();

		synchronized (lock) {

			for (final Edge e : data.edgeTimes.keySet()) {

				diff = now - data.edgeTimes.get(e);
				if (diff > timeout) {
					edgesToRemove.add(e);
				}

			}

			for (final Edge e : edgesToRemove) {
				data.removeEdge(e);
				layer.remove(e.line);
			}

		}

		// fire events outside the lock to avoid deadlock
		// possible deadlocks
		for (final Edge e : edgesToRemove) {
			fireDrawingObjectRemoved(e.line);
		}

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

				synchronized (lock) {
					e = data.getExistingEdge(senderId, neighboorId);
				}

				if (e != null) {

					synchronized (lock) {
						data.updateEdgeTime(e);
					}

				} else {

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
					final boolean hasSemanticType = stringFormatters.get(p.getSemanticType()) != null;
					e.line.setStringFormatterResult(hasSemanticType ? stringFormatters.get(p.getSemanticType()).parse(p) : defaultStringFormatter
							.parse(p));

					LinePainterLine addedLine;

					synchronized (lock) {

						data.addEdge(e);
						layer.add(e.line);
						addedLine = e.line;

					}

					// fire the event outside of the synchronized block
					// to avoid possible deadlocks
					if (addedLine != null) {
						fireDrawingObjectAdded(addedLine);
					}

				}

			}

		}

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

	@Override
	protected void resetPlugin() {

		Set<DrawingObject> dos;

		synchronized (lock) {

			dos = layer.getDrawingObjects();
			data.clear();
			layer.clear();

		}

		fireDrawingObjectRemoved(dos);

	}

	private void setStringFormatters(final Map<Integer, String> newValue) {
		this.stringFormatters.clear();
		for (final int semanticType : newValue.keySet()) {
			this.stringFormatters.put(semanticType, new StringFormatter(newValue.get(semanticType)));
		}
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();

		xmlConfig.removePropertyChangeListener(this);
		pluginManager.removeNodePositionListener(this);

		Set<DrawingObject> dos;

		synchronized (lock) {
			dos = layer.getDrawingObjects();
			layer.clear();
			data.clear();
		}

		fireDrawingObjectRemoved(dos);

		if (timer != null) {
			timer.cancel();
		}

	}

	private void updateLineColor(final int[] color) {

		final LinkedList<LinePainterLine> updatedLines = new LinkedList<LinePainterLine>();

		synchronized (lock) {

			LinePainterLine line;
			for (final DrawingObject o : layer.getDrawingObjects()) {
				line = (LinePainterLine) o;
				updatedLines.add(line);
				line.setColor(new RGB(color[0], color[1], color[2]));
			}

		}

	}

	private void updateLineWidth(final int width) {

		final LinkedList<LinePainterLine> updatedLines = new LinkedList<LinePainterLine>();

		synchronized (lock) {

			LinePainterLine line;
			for (final DrawingObject o : layer.getDrawingObjects()) {
				line = (LinePainterLine) o;
				updatedLines.add(line);
				line.setLineWidth(width);
			}

		}

	}

}