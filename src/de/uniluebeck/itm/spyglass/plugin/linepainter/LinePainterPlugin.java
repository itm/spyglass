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
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
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
import de.uniluebeck.itm.spyglass.packet.Uint16ListPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionChangedEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionChangedListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.relationpainter.RelationPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class LinePainterPlugin extends RelationPainterPlugin implements PropertyChangeListener, NodePositionChangedListener {

	@Element(name = "parameters")
	private final LinePainterXMLConfig xmlConfig;

	private Layer layer;

	public LinePainterPlugin() {

		super();

		xmlConfig = new LinePainterXMLConfig();
		layer = new QuadTree();

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

	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		synchronized (layer) {
			return layer.getDrawingObjects(area);
		}
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

		public boolean equals(final Edge other) {
			return ((nodeId1 == other.nodeId1) && (nodeId2 == other.nodeId2)) || ((nodeId1 == other.nodeId2) && (nodeId2 == other.nodeId1));
		}

		@Override
		public int hashCode() {
			return 7 + (31 * nodeId1) + (31 * nodeId2);
		}

	}

	public void handleEvent(final NodePositionChangedEvent event) {
		AbsoluteRectangle oldBoundingBox;
		for (final Edge e : nodeGraph.getIncidentEdges(event.node)) {
			oldBoundingBox = e.line.getBoundingBox();
			if (e.nodeId1 == event.node) {
				e.line.setPosition(event.newPosition);
			} else {
				e.line.setEnd(event.newPosition);
			}
			fireDrawingObjectChanged(e.line, oldBoundingBox);
		}
	}

	private class UndirectedNodeGraph {

		public Set<Edge> edges = Collections.synchronizedSet(new HashSet<Edge>());

		public Deque<Edge> newEdges = new LinkedList<Edge>();

		public Deque<Edge> updatedEdges = new LinkedList<Edge>();

		public Deque<Edge> removedEdges = new LinkedList<Edge>();

		public void addEdge(final int nodeId1, final int nodeId2) {

			final Edge edge = new Edge(nodeId1, nodeId2);

			if (!edges.contains(edge)) {
				edges.add(edge);
				newEdges.add(edge);
			}

		}

		public Set<Edge> getIncidentEdges(final int nodeId) {
			final Set<Edge> incidentEdges = new HashSet<Edge>();
			for (final Edge e : edges) {
				if ((e.nodeId1 == nodeId) || (e.nodeId2 == nodeId)) {
					incidentEdges.add(e);
				}
			}
			return incidentEdges;
		}

		public void addEdge(final int nodeId, final List<Integer> toNodeIds) {

			for (final int id : toNodeIds) {
				addEdge(nodeId, id);
			}

		}

		public void removeEdge(final int nodeId1, final int nodeId2) {

			final Edge edge = new Edge(nodeId1, nodeId2);

			if (edges.contains(edge)) {
				edges.remove(edge);
				removedEdges.add(edge);
			}

		}

		public void clear() {
			removedEdges.addAll(edges);
			edges.clear();
		}

	}

	private UndirectedNodeGraph nodeGraph = new UndirectedNodeGraph();

	@Override
	protected void processPacket(final SpyglassPacket p) {

		final Uint16ListPacket packet = (Uint16ListPacket) p;

		synchronized (nodeGraph) {
			nodeGraph.addEdge(packet.getSenderId(), packet.getNeighborhoodPacketNodeIDs());
		}

		final List<Edge> edgesToRemove = new ArrayList<Edge>();

		for (final Edge e : nodeGraph.newEdges) {

			if (e.line != null) {
				throw new RuntimeException("Damnit. This should not happen...");
			}

			final NodePositionerPlugin nodePositioner = getPluginManager().getNodePositioner();
			final AbsolutePosition pos1 = nodePositioner.getPosition(e.nodeId1);
			final AbsolutePosition pos2 = nodePositioner.getPosition(e.nodeId2);

			if ((pos1 == null) || (pos2 == null)) {

				// remove edge, since it can't be painted anyway
				// until NodePositioner knows it's position
				edgesToRemove.add(e);

			} else {

				e.line = new LinePainterLine();
				e.line.setPosition(pos1);
				e.line.setEnd(pos2);
				final int[] color = xmlConfig.getLineColorRGB();
				e.line.setColor(new RGB(color[0], color[1], color[2]));
				e.line.setLineWidth(xmlConfig.getLineWidth());

			}

		}

		synchronized (nodeGraph) {
			for (final Edge edge : edgesToRemove) {
				nodeGraph.edges.remove(edge);
				nodeGraph.newEdges.remove(edge);
			}
		}

	}

	@Override
	public void reset() {
		synchronized (nodeGraph) {
			nodeGraph.clear();
		}
		synchronized (layer) {
			layer.clear();
		}
	}

	@Override
	protected void updateQuadTree() {

		LinePainterLine line;

		while (nodeGraph.newEdges.peek() != null) {

			synchronized (layer) {
				line = nodeGraph.newEdges.poll().line;
				layer.addOrUpdate(line);
				fireDrawingObjectAdded(line);
			}

		}

		while (nodeGraph.removedEdges.peek() != null) {

			synchronized (layer) {
				line = nodeGraph.removedEdges.poll().line;
				layer.remove(line);
				fireDrawingObjectRemoved(line);
			}

		}

	}

	@Override
	public void init(final PluginManager manager) {

		super.init(manager);

		xmlConfig.addPropertyChangeListener(this);
		pluginManager.addNodePositionChangedListener(this);

	}

	@Override
	public void shutdown() {

		xmlConfig.removePropertyChangeListener(this);
		pluginManager.removeNodePositionChangedListener(this);

	}

	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (LinePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B.equals(event.getPropertyName())) {
			updateLineColor((int[]) event.getNewValue());
		} else if (LinePainterXMLConfig.PROPERTYNAME_LINE_WIDTH.equals(event.getPropertyName())) {
			updateLineWidth((Integer) event.getNewValue());
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