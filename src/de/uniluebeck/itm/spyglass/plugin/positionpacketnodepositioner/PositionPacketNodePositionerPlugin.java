/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * Default node positioner. it reads the position information from the incoming packets.
 * 
 * @author Dariush Forouher
 * 
 */
public class PositionPacketNodePositionerPlugin extends NodePositionerPlugin {

	private static Logger log = SpyglassLoggerFactory.getLogger(PositionPacketNodePositionerPlugin.class);

	@Element(name = "parameters")
	private final PositionPacketNodePositionerXMLConfig config;

	/**
	 * Time for scheduling packet removals
	 */
	private Timer timer = null;

	/**
	 * Hashmap containing the position information.
	 * 
	 * Set concurrency level to "2", since only two threads (PacketHandler and the TimeoutTimer)
	 * will modify the map.
	 */
	private final Map<Integer, PositionData> nodeMap = new ConcurrentHashMap<Integer, PositionData>(16, 0.75f, 2);

	/**
	 * Constructor
	 */
	public PositionPacketNodePositionerPlugin() {
		config = new PositionPacketNodePositionerXMLConfig();
	}

	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);

		timer = new Timer("PositionPacketNodePositioner NodeTimeout-Timer");

		// Check every second for old nodes
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// Only do something while the plugin is active
				if (config.getActive()) {
					removeOldNodes();
				}
			}

		}, 1000, 100);

	}

	@Override
	public AbsolutePosition getPosition(final int nodeId) {
		log.debug("PPNP " + this.getInstanceName() + " gibt Position für ID: " + nodeId);
		final PositionData d = nodeMap.get(nodeId);
		return d != null ? d.position : null;
	}

	/**
	 * Remove nodes which have timed out.
	 */
	private void removeOldNodes() {
		// log.debug("PPNP " + this.getInstanceName() + " removes old nodes...");
		if (config.getTimeout() == 0) {
			return;
		}

		final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();

		final Iterator<Integer> it = nodeMap.keySet().iterator();
		while (it.hasNext()) {
			final int id = it.next();
			final PositionData data = nodeMap.get(id);
			if (data != null) {
				final long time = data.lastSeen;
				if (System.currentTimeMillis() - time > config.getTimeout() * 1000) {

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

	@Override
	public PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new PositionPacketNodePositionerPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<PositionPacketNodePositionerPlugin, PositionPacketNodePositionerXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new PositionPacketNodePositionerPreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "PositionPacketNodePositioner";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return this.config;
	}

	/**
	 * Contrary to the usual convention for packet handling, a NodePositioner must handle packets
	 * synchronously.
	 */
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		log.debug("PPNP " + this.getInstanceName() + " handles a packet...");
		final int id = packet.getSenderId();

		// check if we already know about this node
		final PositionData oldData = nodeMap.get(id);

		final AbsolutePosition newPos = packet.getPosition().clone();

		// Just overwrite the old PositionData object
		this.nodeMap.put(id, new PositionData(newPos, System.currentTimeMillis()));

		// TODO: Optimization: combine multiple events into one object

		// only send events when the position really changes.
		if (oldData == null) {
			pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.ADDED, null, newPos));
		} else if (!oldData.position.equals(newPos)) {
			pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, oldData.position, newPos));
		}

	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// is never called.
	}

	@Override
	protected void resetPlugin() {
		this.nodeMap.clear();
	}

	@Override
	public int getNumNodes() {
		return this.nodeMap.size();
	}

	@Override
	public boolean offersMetric() {
		return true;
	}

	@Override
	public List<Integer> getNodeList() {
		return new ArrayList<Integer>(this.nodeMap.keySet());
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.plugin.Plugin#shutdown()
	 */
	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		this.timer.cancel();
	}

	@Override
	public void addNodes(final Map<Integer, PositionData> oldNodeMap) {
		nodeMap.putAll(oldNodeMap);
		final Iterator<Integer> it = nodeMap.keySet().iterator();
		while (it.hasNext()) {
			final int id = it.next();
			final PositionData pos = nodeMap.get(id);
			if (pos != null) {
				pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, pos.position, pos.position));
			}
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
		return this.nodeMap;
	}

}