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
import java.util.HashMap;
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

/**
 * Spring Embedder Plugin
 * 
 * @author Oliver Kleine
 * 
 */

public class SpringEmbedderPositionerPlugin extends NodePositionerPlugin {

	private static Logger log = SpyglassLoggerFactory.getLogger(SpringEmbedderPositionerPlugin.class);

	@Element(name = "parameters")
	private final SpringEmbedderPositionerXMLConfig xmlConfig;

	private int nextX = 0;
	private int nextY = 0;
	private SecureRandom rand = new SecureRandom();

	private Map<Integer, Vector<Integer>> neighbours = new ConcurrentHashMap<Integer, Vector<Integer>>(16, 0.75f, 1);

	private Timer timer = null;
	// private Timer repositionTimer = null;

	/**
	 * Hashmap containing the position information.
	 * 
	 * Set concurrency level to "2", since only two threads (PacketHandler and the TimeoutTimer)
	 * will modify the map.
	 * 
	 * The first PositionData in the array is the real node position. The second PositionData is the
	 * position calculated by the SpringEmbedder. Ther must always be 2 PositionData for each node.
	 */
	private volatile Map<Integer, PositionDataSE> nodeMap = new ConcurrentHashMap<Integer, PositionDataSE>(16, 0.75f, 2);

	public SpringEmbedderPositionerPlugin() {
		xmlConfig = new SpringEmbedderPositionerXMLConfig();

	}

	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);

		timer = new Timer("SpringEmbedderPositioner NodeTimeout-Timer");
		// repositionTimer = new Timer("SpringEmbedderPositioner Reposition-Timer");

		// Check every second for old nodes
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Only do something while the plugin is active
				if (xmlConfig.getActive()) {
					removeOldNodes();
				}
			}

		}, 0, 1000);

		// Recalculate the existing node positions ten times a second
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Only do something while the plugin is active
				if (xmlConfig.getActive()) {
					repositionNodes();
				}
			}

		}, 500, 100);

	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();

		this.timer.cancel();
		// this.repositionTimer.cancel();
	}

	@Override
	public AbsolutePosition getPosition(final int nodeId) {
		log.debug("SEP " + this.getInstanceName() + " was asked for position of node ID: " + nodeId);
		final PositionDataSE d = nodeMap.get(nodeId);

		return d != null ? d.sePosition : null;
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
		this.nodeMap.clear();
		this.neighbours.clear();
	}

	@Override
	public int getNumNodes() {
		return nodeMap.size();
	}

	@Override
	public boolean offersMetric() {
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
		log.debug("SEP " + this.getInstanceName() + " handles packet with hashcode " + packet.hashCode());
		final int id = packet.getSenderId();

		// check if we already know about this node
		final PositionDataSE oldData = nodeMap.get(id);

		// if node is already known, change the "last-seen" value and the position
		if (oldData != null) {
			// log.debug("Node " + id + " already exists");
			oldData.lastSeen = System.currentTimeMillis();
			oldData.position = packet.getPosition().clone();
			this.nodeMap.put(id, oldData);
		}
		// otherwise create a new entry into the map and fire a NodePositionEvent
		else {

			// log.debug("Node " + id + " added at pos (" + nextX + ", " + nextY + ")");
			final AbsolutePosition sePos = new AbsolutePosition(nextX, nextY);
			this.nodeMap.put(id, new PositionDataSE(packet.getPosition().clone(), sePos, System.currentTimeMillis()));
			nextX += rand.nextInt(100) - 50;
			nextY += rand.nextInt(100) - 50;
			log.debug("SEP " + this.getInstanceName() + " fires NodePositionEvent (ADD) because of packet with hashcode " + packet.hashCode());
			pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.ADDED, null, sePos));
			log.debug("SEP " + this.getInstanceName() + " finishes NodePositionEvent (ADD) because of packet with hashcode " + packet.hashCode());
		}

		// Check wether the current packet is a neighbourhood package
		if (xmlConfig.containsEdgeSemanticType(packet.getSemanticType())) {
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
		log.debug("SEP " + this.getInstanceName() + " finished handling of packet with hashcode " + packet.hashCode());
	}

	private int byteToInt(final byte b) {
		return b & 0xff;
	}

	/**
	 * Remove nodes which have timed out.
	 */
	private void removeOldNodes() {
		// log.debug("SEP removes old nodes...");
		try {
			if (xmlConfig.getTimeout() == 0) {
				return;
			}

			final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();

			final Iterator<Integer> it = nodeMap.keySet().iterator();
			while (it.hasNext()) {
				final int id = it.next();
				final PositionDataSE data = nodeMap.get(id);
				if (data != null) {
					final long time = data.lastSeen;
					if (System.currentTimeMillis() - time > xmlConfig.getTimeout() * 1000) {

						final AbsolutePosition oldPos = data.sePosition;

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
		} catch (final Exception e) {
			log.error("Error while removing a node", e);
		}

	}

	private void repositionNodes() {

		try {
			final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();
			final Map<Integer, PositionDataSE> newMap = new ConcurrentHashMap<Integer, PositionDataSE>(16, 0.75f, 2);

			final Iterator<Integer> it = nodeMap.keySet().iterator();

			while (it.hasNext()) {
				final int id = it.next();

				final PositionDataSE data = nodeMap.get(id).clone();

				if (data != null) {

					final AbsolutePosition oldPos = data.sePosition.clone();

					// change position
					final AbsolutePosition newPos = this.springEmbedding(id);

					data.sePosition = newPos;

					newMap.put(id, data);

					// log.debug("Position of node " + id + " changed from (" + oldPos.x + ", " +
					// oldPos.y + " to (" + newPos.x + "," + newPos.y + ")");

					if (newPos != null) {
						if (!newPos.equals(oldPos)) {
							list.add(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, oldPos, newPos));
						}
					}
				}
			}

			nodeMap.putAll(newMap);

			for (final NodePositionEvent nodePositionEvent : list) {
				pluginManager.fireNodePositionEvent(nodePositionEvent);
			}
		} catch (final Exception e) {
			log.error("Reposition-Exception", e);
		}
	}

	private AbsolutePosition springEmbedding(final int id) {
		try {
			final double springLength = xmlConfig.getOptimumSpringLength();
			final double stiffness = xmlConfig.getSpringStiffness();
			final double repFactor = xmlConfig.getRepulsionFactor();
			final double epsilon = xmlConfig.getEfficiencyFactor();

			final PositionDataSE data = nodeMap.get(id);

			final AbsolutePosition cur = data.sePosition.clone();
			final AbsolutePosition force = new AbsolutePosition(0, 0, 0);

			final Iterator<Integer> it = nodeMap.keySet().iterator();
			final Vector<Integer> nbs = this.neighbours.get(id);

			// add all repulsive and attractive forces to the current value;
			while (it.hasNext()) {
				final int id2 = it.next();
				if (id2 != id) {
					final AbsolutePosition other = nodeMap.get(id2).sePosition.clone();
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
						// log.debug("dist (" + id + ", " + id2 + "): " + dist.x + ", " + dist.y +
						// ", "
						// + dist.z + ")");

						// log.debug("EucDist: " + cur.getEuclideanDistance(other));

						tmp = this.divide(dist, cur.getEuclideanDistance(other));
						dist.x = (int) Math.round(tmp[0] * factor1);
						dist.y = (int) Math.round(tmp[1] * factor1);
						dist.z = (int) Math.round(tmp[2] * factor1);

						// log.debug("att: (" + dist.x + ", " + dist.y + ", " + dist.z + ")");

						force.add(dist);
						// log.debug("force inkl. att: " + force.x + ", " + force.y + ", " + force.z
						// +
						// ")");

					}

					// compute repulsion force
					final double factor2 = repFactor / Math.pow(cur.getEuclideanDistance(other), 2);

					dist = other.getDistanceVector(cur);

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

			// reduce the force by the given factor (must only be between 0 an 1)
			force.mult(epsilon);

			final AbsolutePosition result = nodeMap.get(id).sePosition.clone();
			result.add(force);
			return result;
		} catch (final Exception e) {
			log.error("An error occured while computing a node's position", e);
			return null;
		}
	}

	private double[] divide(final AbsolutePosition vector, final double divisor) {

		if (divisor == 0) {
			final double[] result = { Math.pow(2, 15), Math.pow(2, 15), Math.pow(2, 15) };
			// log.debug("division result: (" + result[0] + ", " + result[1] + ", " + result[2] +
			// ")");
			return result;
		}

		final double[] result = new double[3];
		result[0] = (vector.x) / divisor;
		result[1] = (vector.y) / divisor;
		result[2] = (vector.z) / divisor;

		// log.debug("division result: (" + result[0] + ", " + result[1] + ", " + result[2] + ")");
		return result;
	}

	@Override
	public void addNodes(final Map<Integer, PositionData> oldNodeMap) {
		final Iterator<Integer> it = oldNodeMap.keySet().iterator();

		while (it.hasNext()) {
			final Integer key = it.next();
			final PositionData posData = oldNodeMap.get(key);
			final PositionDataSE tmp = new PositionDataSE(posData.position, posData.position, posData.lastSeen);
			nodeMap.put(key, tmp);
		}
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin#getNodeMap()
	 */
	@Override
	public Map<Integer, PositionData> getNodeMap() {
		final Map<Integer, PositionData> result = new HashMap<Integer, PositionData>();

		final Iterator<Integer> it = nodeMap.keySet().iterator();

		while (it.hasNext()) {
			final Integer key = it.next();
			final PositionDataSE tmp = nodeMap.get(key);
			final PositionData posData = new PositionData(tmp.position, tmp.lastSeen);

			result.put(key, posData);
		}

		return result;
	}

}