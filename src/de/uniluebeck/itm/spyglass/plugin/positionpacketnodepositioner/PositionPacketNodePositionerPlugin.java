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
 * @author dariush
 * 
 */
public class PositionPacketNodePositionerPlugin extends NodePositionerPlugin {
	private static Logger log = SpyglassLoggerFactory.get(PositionPacketNodePositionerPlugin.class);
	
	@Element(name = "parameters")
	private final PositionPacketNodePositionerXMLConfig xmlConfig;
	
	/**
	 * Hashmap containing the position information.
	 */
	private final HashMap<Integer, AbsolutePosition> positionMap = new HashMap<Integer, AbsolutePosition>();
	
	/**
	 * Constructor
	 */
	public PositionPacketNodePositionerPlugin() {
		xmlConfig = new PositionPacketNodePositionerXMLConfig();
	}
	
	@Override
	public AbsolutePosition getPosition(final int nodeId) {
		if (!positionMap.containsKey(nodeId)) {
			throw new IllegalArgumentException("I don't know any node with id " + nodeId);
		} else {
			return positionMap.get(nodeId);
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
		return this.xmlConfig;
	}
	
	/**
	 * Contrary to the usual convention for packet handling, a NodePositioner must handle packets
	 * synchronously.
	 */
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		final int id = packet.getSenderId();
		final AbsolutePosition pos = packet.getPosition().clone();
		this.positionMap.put(id, pos);
		log.debug("Memorized position " + pos + " for node id " + id);
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// is never called.
	}
	
	@Override
	public void reset() {
		this.positionMap.clear();
	}
	
	@Override
	protected void updateQuadTree() {
		// is never called.
	}
	
	@Override
	public int getNumNodes() {
		return this.positionMap.size();
	}
	
	@Override
	public boolean offersMetric() {
		return true;
	}
	
}