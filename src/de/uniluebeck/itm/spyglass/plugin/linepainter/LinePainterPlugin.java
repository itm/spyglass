/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.relationpainter.RelationPainterPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class LinePainterPlugin extends RelationPainterPlugin {
	
	@Element(name = "parameters")
	private final LinePainterXMLConfig xmlConfig;
	
	// public StringFormatter m_StringFormatter;
	
	public LinePainterPlugin() {
		xmlConfig = new LinePainterXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createPreferencePage(final ConfigStore cs) {
		return new LinePainterPreferencePage(cs, this);
	}
	
	public static PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		return new LinePainterPreferencePage(cs);
	}
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getHumanReadableName() {
		return "LinePainter";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		// TODO Auto-generated method stub
		
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