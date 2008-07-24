/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.bsi.flegsens;

import java.util.Random;

import org.apache.log4j.Category;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// ------------------------------------------------------------------------------
// --
/**
 * 
 * 
 */
public class RandomNodePositioner extends NodePositionerPlugin {
	private static Category log = SpyglassLogger.get(RandomNodePositioner.class);
	
	private final Random r = new Random();
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public Position getPosition(final int nodeId) {
		final Position p = new Position(Math.abs(r.nextFloat() * 800), Math.abs(r.nextFloat() * 800));
		log.debug("Random position: " + p);
		return p;
	}
	
	@Override
	public PluginPreferencePage<RandomNodePositioner> createPreferencePage(final Widget parent, final ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PluginPreferencePage<RandomNodePositioner> createTypePreferencePage(final Widget parent, final ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getHumanReadableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void handlePacket(final Packet packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void processPacket(final Packet packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setXMLConfig(final PluginXMLConfig xmlConfig) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getNumNodes() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean offersMetric() {
		// TODO Auto-generated method stub
		return false;
	}
	
	// --------------------------------------------------------------------------
	// ------
	
}
