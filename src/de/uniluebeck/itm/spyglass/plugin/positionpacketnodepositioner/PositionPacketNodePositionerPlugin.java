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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * Default node positioner. it reads the position information from the incoming packets.
 * 
 * TODO: add a time to give precise events when a node is removed.
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
	private final Timer timer = new Timer("PositionPacketNodePositioner NodeTimeout-Timer");

	/**
	 * Hashmap containing the position information.
	 */
	private final HashMap<Integer, AbsolutePosition> positionMap = new HashMap<Integer, AbsolutePosition>();

	/**
	 * System time, when the node has been last seen.
	 */
	private final HashMap<Integer, Long> lastSeen = new HashMap<Integer, Long>();

	/**
	 * Mutex to protect lastSeen and positionMap
	 */
	private final Object mutex = new Object();

	/**
	 * Constructor
	 */
	public PositionPacketNodePositionerPlugin() {
		config = new PositionPacketNodePositionerXMLConfig();

		// Check every second for old nodes
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				removeOldNodes();
			}

		}, 1000, 100);
	}

	@Override
	public AbsolutePosition getPosition(final int nodeId) {

		synchronized (mutex) {
			return positionMap.get(nodeId);
		}
	}

	/**
	 * Remove nodes which have timed out.
	 */
	private void removeOldNodes() {

		if (config.getTimeout() == 0) {
			return;
		}

		final List<NodePositionEvent> list = new ArrayList<NodePositionEvent>();

		// we have to aquire the mutex to ensure that lastSeen isn't modified
		synchronized (mutex) {

			final Iterator<Integer> it = lastSeen.keySet().iterator();
			while (it.hasNext()) {
				final int id = it.next();
				if (lastSeen.get(id) != null) {
					final long time = lastSeen.get(id);
					if (System.currentTimeMillis() - time > config.getTimeout() * 1000) {

						final AbsolutePosition oldPos = positionMap.get(id);
						;

						positionMap.remove(id);
						it.remove();

						log.debug("Removed node " + id + " after timeout.");
						list.add(new NodePositionEvent(id, NodePositionEvent.Change.REMOVED, oldPos, null));

					}
				}
			}

		}

		// fire the events after we release the lock
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

		final int id = packet.getSenderId();

		final AbsolutePosition newPos = packet.getPosition().clone();
		final AbsolutePosition oldPos = positionMap.get(id);

		synchronized (mutex) {

			this.lastSeen.put(id, System.currentTimeMillis());
			this.positionMap.put(id, newPos);

		}

		// only send events when the position really changes.
		if (oldPos == null) {
			pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.ADDED, null, newPos));
		} else if (!oldPos.equals(newPos)) {
			pluginManager.fireNodePositionEvent(new NodePositionEvent(id, NodePositionEvent.Change.MOVED, oldPos, newPos));
		}

	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// is never called.
	}

	@Override
	protected void resetPlugin() {
		synchronized (mutex) {
			this.positionMap.clear();
		}
	}

	@Override
	protected void updateLayer() {
		// is never called.
	}

	@Override
	public int getNumNodes() {

		synchronized (mutex) {
			return this.positionMap.size();
		}
	}

	@Override
	public boolean offersMetric() {
		return true;
	}

	@Override
	public List<Integer> getNodeList() {
		synchronized (mutex) {
			return new ArrayList<Integer>(this.positionMap.keySet());
		}
	}

}