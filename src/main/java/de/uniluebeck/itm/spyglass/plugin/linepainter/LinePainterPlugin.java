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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.RGB;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.NodeLinkPacket;
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
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * @author Daniel Bimschas
 */
public class LinePainterPlugin extends RelationPainterPlugin implements PropertyChangeListener, NodePositionListener {

	/** Logs messages */
	private static final Logger log = SpyglassLoggerFactory.getLogger(LinePainterPlugin.class);

	private static final String TIMER_NAME = "LinePainterPlugin-Timeout-Timer";

	public static PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "LinePainter";
	}

	private final GraphData graphData;

	private final Layer layer;

	private final Object lock = new Object();

	private final StringFormatterData stringFormatterData;

	private Timer timer;

	@Element(name = "parameters")
	final LinePainterXMLConfig config;

	public LinePainterPlugin() {

		super();

		config = new LinePainterXMLConfig();
		layer = Layer.Factory.createQuadTreeLayer();
		graphData = new GraphData();
		stringFormatterData = new StringFormatterData();

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
		return config;
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

	// --------------------------------------------------------------------------------
	/**
	 * Adds a new edge to the graph.
	 * 
	 * @param packet
	 * @param senderId
	 * @param neighboorId
	 * @param sourcePos
	 * @param destinationPos
	 */
	private void newEdge(final NodeLinkPacket packet, final int senderId, final int neighboorId, final AbsolutePosition sourcePos,
			final AbsolutePosition destinationPos) {

		final Edge e = new Edge(senderId, neighboorId);

		e.line = new LinePainterLine();
		e.line.setPosition(sourcePos);
		e.line.setEnd(destinationPos);
		final int[] color = config.getLineColorRGB();
		e.line.setColor(new RGB(color[0], color[1], color[2]));
		e.line.setLineWidth(config.getLineWidth());

		final LinePainterLine addedLine;

		synchronized (lock) {

			graphData.addEdge(e);
			layer.add(e.line);
			addedLine = e.line;

		}

		// fire the event outside of the synchronized block
		// to avoid possible deadlocks
		if (addedLine != null) {
			fireDrawingObjectAdded(addedLine);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Parses the packet and, according to its' content updates or adds edges and their respective
	 * string formatter information.
	 * 
	 * @param packet
	 */
	void parsePacket(final NodeLinkPacket packet) {

		Edge e;

		final int sourceNodeId = packet.getSourceNodeId();
		final int destinationNodeId = packet.getDestinationNodeId();

		final NodePositionerPlugin nodePositioner = getPluginManager().getNodePositioner();
		final AbsolutePosition sourcePos = nodePositioner.getPosition(sourceNodeId);
		final AbsolutePosition senderPos = nodePositioner.getPosition(destinationNodeId);

		if ((sourcePos != null) && (senderPos != null)) {

			synchronized (lock) {
				e = graphData.getExistingEdge(sourceNodeId, destinationNodeId);
			}

			if (e != null) {
				updateEdge(packet, e);
			} else {
				newEdge(packet, sourceNodeId, destinationNodeId, sourcePos, senderPos);
			}

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates edge time and string formatter strings for an existing edge.
	 * 
	 * @param packet
	 * @param e
	 */
	private void updateEdge(final NodeLinkPacket packet, final Edge e) {
		synchronized (lock) {
			graphData.updateEdgeTime(e);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Called periodically by {@link #timer} if a timeout occurs and removes stale edges and their
	 * respective drawing objects.
	 */
	private void handleTimeout() {

		if (log.isDebugEnabled()) {
			log.debug("handleTimeout()");
		}

		final long now = System.currentTimeMillis();
		final long timeoutInMs = 1000 * config.getTimeout();

		// if timeout is 0 it means there's no timeout, so we don't have to remove any edges...
		if (timeoutInMs == 0) {
			return;
		}

		final List<LinePainterLine> dos = new ArrayList<LinePainterLine>();

		synchronized (lock) {

			final Iterator<Edge> iterator = graphData.edgeTimes.keySet().iterator();
			Edge e;

			while (iterator.hasNext()) {

				e = iterator.next();

				if ((now - graphData.edgeTimes.get(e)) > timeoutInMs) {

					if (log.isDebugEnabled()) {
						log.debug("removing edge from node " + e.sourceNodeId + " to " + e.destinationNodeId + " which is "
								+ ((now - graphData.edgeTimes.get(e))) + " ms old (timeout is " + timeoutInMs + " ms)");
					}

					dos.add(e.line);
					stringFormatterData.removeEdge(e);
					iterator.remove();
					layer.remove(e.line);

				}

			}
		}

		// fire events outside the lock to avoid possible deadlocks
		for (final LinePainterLine line : dos) {
			fireDrawingObjectRemoved(line);
		}

	}

	private void handleTimeoutChange(final long timeout) {

		// cancel old timer which repeats timeouts periodically
		if (timer != null) {
			timer.cancel();
		}

		// remove stale edges / drawing objects
		handleTimeout();

		// reschedule the timer according to the new timeout (0 means no timeout)
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

	// --------------------------------------------------------------------------------
	/**
	 * Called when a node changed its' position. If the update node is a node that is currently held
	 * in this plug-ins' graph the drawing objects of every incident edge are updated to reflect the
	 * new node position.
	 * 
	 * @param nodeId
	 * @param newPosition
	 */
	void onNodeMoved(final int nodeId, final AbsolutePosition newPosition) {
		synchronized (lock) {

			if (log.isDebugEnabled()) {
				log.debug("onNodeMoved(" + nodeId + "," + newPosition + ")");
			}

			for (final Edge e : graphData.getIngoingOutgoingEdges(nodeId)) {
				if (e.sourceNodeId == nodeId) {
					if (log.isDebugEnabled()) {
						log.debug("moving node " + e.sourceNodeId);
					}
					e.line.setPosition(newPosition);
				} else {
					if (log.isDebugEnabled()) {
						log.debug("moving node " + e.destinationNodeId);
					}
					e.line.setEnd(newPosition);
				}
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Called when a NodePositionerPlugin sends an event indicating a nodes' removal. If the node's
	 * in the graph, the node and it's corresponding drawing objects are removed.
	 * 
	 * @param nodeId
	 */
	void onNodeRemoved(final int nodeId) {

		if (log.isDebugEnabled()) {
			log.debug("onNodeRemoved(" + nodeId + ")");
		}

		final LinkedList<LinePainterLine> removedLines = new LinkedList<LinePainterLine>();

		synchronized (lock) {

			// we need to check if the node exists in the graph and remove it if that's the case

			for (final Edge e : graphData.getIngoingOutgoingEdges(nodeId)) {
				removedLines.add(e.line);
				graphData.removeEdge(e);
				stringFormatterData.removeEdge(e);
				layer.remove(e.line);
			}

		}

		// fire event outside lock block to avoid deadlocks
		fireDrawingObjectRemoved(removedLines);

	}

	@Override
	public void init(final PluginManager manager) throws Exception {

		super.init(manager);

		config.addPropertyChangeListener(PluginXMLConfig.PROPERTYNAME_ACTIVE, this);
		config.addPropertyChangeListener(LinePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B, this);
		config.addPropertyChangeListener(LinePainterXMLConfig.PROPERTYNAME_LINE_WIDTH, this);
		config.addPropertyChangeListener(PluginXMLConfig.PROPERTYNAME_TIMEOUT, this);

		pluginManager.addNodePositionListener(this);
		handleTimeoutChange(config.getTimeout());
		stringFormatterData.init(config, graphData, lock);

	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		assert packet instanceof Uint16ListPacket : "Received packet of wrong class type: " + packet.getClass().getSimpleName();
		parsePacket(NodeLinkPacket.interpret((Uint16ListPacket) packet));
		stringFormatterData.parsePacket(NodeLinkPacket.interpret((Uint16ListPacket) packet));
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (PluginXMLConfig.PROPERTYNAME_ACTIVE.equals(event.getPropertyName())) {
			if (!(Boolean) event.getNewValue()) {
				reset();
			}
		} else if (LinePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B.equals(event.getPropertyName())) {
			handleLineColorChange((int[]) event.getNewValue());
		} else if (LinePainterXMLConfig.PROPERTYNAME_LINE_WIDTH.equals(event.getPropertyName())) {
			handleLineWidthChange((Integer) event.getNewValue());
		} else if (PluginXMLConfig.PROPERTYNAME_TIMEOUT.equals(event.getPropertyName())) {
			handleTimeoutChange((Integer) event.getNewValue());
		} else {
			throw new RuntimeException("Unexpected case.");
		}
	}

	@Override
	protected void resetPlugin() {

		Set<DrawingObject> dos;

		synchronized (lock) {
			dos = layer.getDrawingObjects();
			layer.clear();
			graphData.clear();
			stringFormatterData.clear();
		}

		fireDrawingObjectRemoved(dos);

	}

	@Override
	public void shutdown() throws Exception {

		stringFormatterData.shutdown();

		config.removePropertyChangeListener(PluginXMLConfig.PROPERTYNAME_ACTIVE, this);
		config.removePropertyChangeListener(LinePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B, this);
		config.removePropertyChangeListener(LinePainterXMLConfig.PROPERTYNAME_LINE_WIDTH, this);
		config.removePropertyChangeListener(PluginXMLConfig.PROPERTYNAME_TIMEOUT, this);

		pluginManager.removeNodePositionListener(this);

		if (timer != null) {
			timer.cancel();
		}

		resetPlugin();

		super.shutdown();

	}

	private void handleLineColorChange(final int[] color) {
		synchronized (lock) {
			LinePainterLine line;
			for (final DrawingObject o : layer.getDrawingObjects()) {
				line = (LinePainterLine) o;
				line.setColor(new RGB(color[0], color[1], color[2]));
			}
		}
	}

	private void handleLineWidthChange(final int width) {
		synchronized (lock) {
			LinePainterLine line;
			for (final DrawingObject o : layer.getDrawingObjects()) {
				line = (LinePainterLine) o;
				line.setLineWidth(width);
			}
		}
	}

}