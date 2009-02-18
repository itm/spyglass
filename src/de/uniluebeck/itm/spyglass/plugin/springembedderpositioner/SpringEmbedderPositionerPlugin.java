/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner.PositionData;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class SpringEmbedderPositionerPlugin extends NodePositionerPlugin {

	private static Logger log = SpyglassLoggerFactory.getLogger(SpringEmbedderPositionerPlugin.class);

	@Element(name = "parameters")
	private final SpringEmbedderPositionerXMLConfig xmlConfig;

	private int nextX = 0;
	private int nextY = 0;
	private SecureRandom rand = new SecureRandom();

	private Map<Integer, Vector<Integer>> neighbours = new ConcurrentHashMap<Integer, Vector<Integer>>(16, 0.75f, 1);

	private Timer timeoutTimer = null;
	private Timer repositionTimer = null;

	/**
	 * Hashmap containing the position information.
	 * 
	 * Set concurrency level to "2", since only two threads (PacketHandler and the TimeoutTimer)
	 * will modify the map.
	 */
	private final Map<Integer, PositionData> nodeMap = new ConcurrentHashMap<Integer, PositionData>(16, 0.75f, 2);

	public SpringEmbedderPositionerPlugin() {
		xmlConfig = new SpringEmbedderPositionerXMLConfig();

	}

	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);

		timeoutTimer = new Timer("SpringEmbedderPositioner NodeTimeout-Timer");
		repositionTimer = new Timer("SpringEmbedderPositioner Reposition-Timer");

		// Check every second for old nodes
		timeoutTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				removeOldNodes();
			}

		}, 1000, 100);

		// Redraw the existing node every second
		timeoutTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				repositionNodes();
			}

		}, 1000, 100);

	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		
		this.timeoutTimer.cancel();
		this.repositionTimer.cancel();
	}

	@Override
	public AbsolutePosition getPosition(final int nodeId) {
		final PositionData d = nodeMap.get(nodeId);

		return d != null ? d.position : null;
	}

	@Override
	public PluginPreferencePage<SpringEmbedderPositionerPlugin, SpringEmbedderPositionerXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SpringEmbedderPositionerPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<SpringEmbedderPositionerPlugin, SpringEmbedderPositionerXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SpringEmbedderPositionerPreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "SpringEmbedderPositioner";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void resetPlugin() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumNodes() {
		return nodeMap.size();
	}

	@Override
	public boolean offersMetric() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Integer> getNodeList() {
		return new ArrayList<Integer>(this.nodeMap.keySet());
	}

	/**
	 * Contrary to the usual convention for packet handling, a NodePositioner must handle packets
	 * synchronously.
	 */
	@Override
	public void handlePacket(final SpyglassPacket packet) {

		final int id = packet.getSenderId();

		// check if we already know about this node
		final PositionData oldData = nodeMap.get(id);

		// if node is already known, change the "last-seen" value
		if (oldData != null) {
			log.debug("Node " + id + " already exists");
			oldData.lastSeen = System.currentTimeMillis();
			this.nodeMap.put(id, oldData);
		}
		// otherwise create a new entry into the map and fire a NodePositionEvent
		else {

			// log.debug("Node " + id + " added at pos (" + nextX + ", " + nextY + ")");
			final AbsolutePosition newPos = new AbsolutePosition(nextX, nextY);
			this.nodeMap.put(id, new PositionData(newPos, System.currentTimeMillis()));
			nextX += rand.nextInt(100) - 50;
			nextY += rand.nextInt(100) - 50;

			pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.ADDED, null, newPos));
		}

		// TODO: Lies den Semantic Type für Nachbarschaftspakete aus der Konfig
		if (packet.getSemanticType() == 9) {
			final byte[] payload = packet.getPayload();
			final int countNb = payload.length / 2;

			// Put all neighbors of the current node into a Vector
			final Vector<Integer> nbs = new Vector<Integer>(countNb);
			final byte[] cur = new byte[2];

			// log.debug("Node " + id + " has " + countNb + " neighbours: ");

			for (int i = 0; i < countNb; i++) {
				cur[0] = payload[i * 2];
				cur[1] = payload[i * 2 + 1];
				final int nb = byteToInt(cur[0]) * 256 + byteToInt(cur[1]);
				nbs.add(nb);
				// log.debug(id + ": " + nb);
			}

			this.neighbours.put(id, nbs);
		}
	}

	private int byteToInt(final byte b) {
		return b & 0xff;
	}

	/**
	 * Remove nodes which have timed out.
	 */
	private void removeOldNodes() {

		if (xmlConfig.getTimeout() == 0) {
			return;
		}

		final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();

		final Iterator<Integer> it = nodeMap.keySet().iterator();
		while (it.hasNext()) {
			final int id = it.next();
			final PositionData data = nodeMap.get(id);
			if (data != null) {
				final long time = data.lastSeen;
				if (System.currentTimeMillis() - time > xmlConfig.getTimeout() * 1000) {

					final AbsolutePosition oldPos = data.position;

					// remove the element from our map
					it.remove();

					log.debug("Removed node " + id + " after timeout.");
					list.add(new NodePositionEvent(id, NodePositionEvent.Change.REMOVED, oldPos, null));

				}
			}
		}

		for (final NodePositionEvent nodePositionEvent : list) {
			pluginManager.fireNodePositionEvent(nodePositionEvent);
		}

	}

	private void repositionNodes() {

		final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();
		final Map<Integer, PositionData> newMap = new ConcurrentHashMap<Integer, PositionData>(16, 0.75f, 2);

		final Iterator<Integer> it = nodeMap.keySet().iterator();

		while (it.hasNext()) {
			final int id = it.next();
			final PositionData data = nodeMap.get(id).clone();
			if (data != null) {

				final AbsolutePosition oldPos = data.position.clone();

				// change position
				final AbsolutePosition newPos = this.springEmbedding(id);

				data.position = newPos;

				newMap.put(id, data);

				// log.debug("Position of node " + id + " changed from (" + oldPos.x + ", " +
				// oldPos.y + " to (" + newPos.x + "," + newPos.y + ")");

				if (!newPos.equals(oldPos)) {
					list.add(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, oldPos, newPos));
				}
			}
		}

		nodeMap.putAll(newMap);

		for (final NodePositionEvent nodePositionEvent : list) {
			pluginManager.fireNodePositionEvent(nodePositionEvent);
		}
	}

	private AbsolutePosition springEmbedding(final int id) {

		final double springLength = xmlConfig.getOptimumSpringLength();
		final double stiffness = xmlConfig.getSpringStiffness();
		final double repFactor = xmlConfig.getRepulsionFactor();
		final double epsilon = xmlConfig.getEfficiencyFactor();

		final PositionData data = nodeMap.get(id);

		final AbsolutePosition cur = data.position.clone();
		final AbsolutePosition force = new AbsolutePosition(0, 0, 0);

		final Iterator<Integer> it = nodeMap.keySet().iterator();
		final Vector<Integer> nbs = this.neighbours.get(id);

		// add all repulsive and attractive forces to the current value;
		// TODO: neighbourhood

		while (it.hasNext()) {
			final int id2 = it.next();
			if (id2 != id) {
				final AbsolutePosition other = nodeMap.get(id2).position.clone();
				AbsolutePosition dist;
				double[] tmp;

				// compute attraction force only with neighbors
				if (nbs == null) {
					// do nothing;
				} else if (nbs.contains(id2)) {

					// log.debug("Compute attraction between " + id + " and " + id2);
					final double factor1 = (cur.getEuclideanDistance(other) - springLength) * stiffness;
					// log.debug("factor1: " + factor1);

					dist = cur.getDistanceVector(other);
					// log.debug("dist (" + id + ", " + id2 + "): " + dist.x + ", " + dist.y + ", "
					// + dist.z + ")");

					// log.debug("EucDist: " + cur.getEuclideanDistance(other));

					tmp = this.divide(dist, cur.getEuclideanDistance(other));
					dist.x = (int) Math.round(tmp[0] * factor1);
					dist.y = (int) Math.round(tmp[1] * factor1);
					dist.z = (int) Math.round(tmp[2] * factor1);

					// log.debug("att: (" + dist.x + ", " + dist.y + ", " + dist.z + ")");

					force.add(dist);
					// log.debug("force inkl. att: " + force.x + ", " + force.y + ", " + force.z +
					// ")");

				}

				final double factor2 = repFactor / Math.pow(cur.getEuclideanDistance(other), 2);
				// log.debug("factor2: " + factor2);

				dist = other.getDistanceVector(cur);
				// log.debug("dist (" + id2 + ", " + id + "): " + dist.x + ", " + dist.y + ", " +
				// dist.z + ")");

				tmp = this.divide(dist, other.getEuclideanDistance(cur));
				dist.x = (int) Math.round(tmp[0] * factor2);
				dist.y = (int) Math.round(tmp[1] * factor2);
				dist.z = (int) Math.round(tmp[2] * factor2);

				// log.debug("rep: (" + dist.x + ", " + dist.y + ", " + dist.z + ")");

				force.add(dist);
				// log.debug("result inkl. rep: " + force.x + ", " + force.y + ", " + force.z +
				// ")");
			}

		}

		// TODO: add all attractive forces to the current value (only neighbours attract each other)

		force.mult(epsilon);

		final AbsolutePosition result = nodeMap.get(id).position.clone();
		result.add(force);
		return result;
	}

	private double[] divide(final AbsolutePosition vector, final double divisor) {

		if (divisor == 0) {
			final double[] result = { Math.pow(2, 15), Math.pow(2, 15), Math.pow(2, 15) };
			return result;
		}

		final double[] result = new double[3];
		result[0] = (vector.x) / divisor;
		result[1] = (vector.y) / divisor;
		result[2] = (vector.z) / divisor;

		// log.debug("division result: (" + result[0] + ", " + result[1] + ", " + result[2] + ")");
		return result;
	}

}