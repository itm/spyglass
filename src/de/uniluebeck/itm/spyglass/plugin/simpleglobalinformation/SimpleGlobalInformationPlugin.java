/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import org.eclipse.swt.widgets.Widget;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.globalinformation.GlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class SimpleGlobalInformationPlugin extends GlobalInformationPlugin {
	
	@Element(name = "parameters")
	private final SimpleGlobalInformationXMLConfig xmlConfig;
	
	public SimpleGlobalInformationPlugin() {
		xmlConfig = new SimpleGlobalInformationXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	/**
	 * 
	 * @param widget
	 */
	@Override
	public void addWidget(final Widget widget) {
		
	}
	
	@Override
	public Widget getWidget() {
		return null;
	}
	
	@Override
	public PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createPreferencePage(final ConfigStore cs) {
		return new SimpleGlobalInformationPreferencePage(cs, this);
	}
	
	public static PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		return new SimpleGlobalInformationPreferencePage(cs);
	}
	
	public static String getHumanReadableName() {
		return "SimpleGlobalInformation";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getName() {
		return xmlConfig.getName();
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
	
}