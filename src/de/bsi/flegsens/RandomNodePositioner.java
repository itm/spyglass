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

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.AbstractPluginTypePreferencePage;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
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
	public AbsolutePosition getPosition(final int nodeId) {
		final AbsolutePosition p = new AbsolutePosition((int) Math.abs(r.nextFloat() * 800), (int) Math.abs(r.nextFloat() * 800), 0);
		log.debug("Random position: " + p);
		return p;
	}
	
	@Override
	public PluginPreferencePage<Plugin, PluginXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new AbstractPluginTypePreferencePage(dialog, spyglass, RandomNodePositioner.getHumanReadableName());
	}
	
	public static PluginPreferencePage<Plugin, PluginXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new AbstractPluginTypePreferencePage(dialog, spyglass, RandomNodePositioner.getHumanReadableName());
	}
	
	public static String getHumanReadableName() {
		return "RandomNodePositioner";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return new PluginXMLConfig() {
			@Override
			public String getName() {
				return "unnamedRNP";
			}
			
			@Override
			public boolean equals(final PluginXMLConfig other) {
				return false;
			}
		};
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
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
