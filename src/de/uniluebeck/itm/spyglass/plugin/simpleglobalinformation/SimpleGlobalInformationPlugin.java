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

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
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
	public PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleGlobalInformationPreferencePage(dialog, spyglass, this);
	}
	
	public static PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleGlobalInformationPreferencePage(dialog, spyglass);
	}
	
	public static String getHumanReadableName() {
		return "SimpleGlobalInformation";
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
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
	
}