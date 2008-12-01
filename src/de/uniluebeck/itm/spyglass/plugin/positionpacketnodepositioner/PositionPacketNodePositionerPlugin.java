/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.positionpacketnodepositioner;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
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
	 * Hashmap containing the position information.
	 */
	private final HashMap<Integer, AbsolutePosition> positionMap = new HashMap<Integer, AbsolutePosition>();

	/**
	 * System time, when the node has been last seen.
	 */
	private final HashMap<Integer, Long> lastSeem = new HashMap<Integer, Long>();

	private Object mutex = new Object();
	
	/**
	 * Constructor
	 */
	public PositionPacketNodePositionerPlugin() {
		config = new PositionPacketNodePositionerXMLConfig();
	}

	@Override
	public AbsolutePosition getPosition(final int nodeId) {
		
		synchronized (mutex) {
			removeOldNodes();

			return positionMap.get(nodeId);
		}
	}

	/** 
	 * Remove nodes which have timed out.
	 */
	private void removeOldNodes() {
		
		if (config.getTimeout()==0) {
			return;
		}
		
		synchronized (mutex) {

			final Iterator<Integer> it = lastSeem.keySet().iterator();
			while (it.hasNext()) {
				final int id = it.next();
				if (lastSeem.get(id) != null) {
					final long time = lastSeem.get(id);
					if (System.currentTimeMillis() - time > config.getTimeout()*1000) {
						positionMap.remove(id);
						it.remove();
					}
				}
			}
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

			this.lastSeem.put(id, System.currentTimeMillis());
			this.positionMap.put(id, newPos);

		}

		// only send events when the position really changes.
		if ((oldPos == null) || !oldPos.equals(newPos)) {
			pluginManager.fireNodePositionChangedEvent(id, oldPos, newPos);
		}

	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// is never called.
	}

	@Override
	public void reset() {
		synchronized (mutex) {
			this.positionMap.clear();
		}
	}

	@Override
	protected void updateQuadTree() {
		// is never called.
	}

	@Override
	public int getNumNodes() {

		synchronized (mutex) {
			removeOldNodes();
			return this.positionMap.size();
		}
	}

	@Override
	public boolean offersMetric() {
		return true;
	}

}