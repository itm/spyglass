/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class SpringEmbedderPositionerPlugin extends NodePositionerPlugin {
	
	@Element(name = "parameters")
	private final SpringEmbedderPositionerXMLConfig xmlConfig;
	
	public SpringEmbedderPositionerPlugin() {
		xmlConfig = new SpringEmbedderPositionerXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public Position getPosition(final int nodeId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PluginPreferencePage<SpringEmbedderPositionerPlugin> createPreferencePage(final ConfigStore cs) {
		return new SpringEmbedderPositionerPluginPreferences(cs, this);
	}
	
	public static PluginPreferencePage<SpringEmbedderPositionerPlugin> createTypePreferencePage(final ConfigStore cs) {
		return new SpringEmbedderPositionerPluginPreferences(cs);
	}
	
	public static String getHumanReadableName() {
		return "SpringEmbedderPositioner";
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
	
}