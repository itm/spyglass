/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simplenodepainter;

import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.layer.SubLayer;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.nodepainter.NodePainterPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class SimpleNodePainterPlugin extends NodePainterPlugin {
	
	@Element(name = "parameters")
	private SimpleNodePainterXMLConfig xmlConfig;
	
	private final Layer layer;
	
	// @Element
	// public StringFormatter m_StringFormatter;
	
	public SimpleNodePainterPlugin() {
		xmlConfig = new SimpleNodePainterXMLConfig();
		layer = new SubLayer();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createPreferencePage(final ConfigStore cs) {
		return new SimpleNodePainterPreferencePage(cs, this);
	}
	
	public static PluginPreferencePage<SimpleNodePainterPlugin, SimpleNodePainterXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		return new SimpleNodePainterPreferencePage(cs);
	}
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		return layer.getDrawingObjects();
	}
	
	public static String getHumanReadableName() {
		return "SimpleNodePainter";
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
		this.xmlConfig = (SimpleNodePainterXMLConfig) xmlConfig;
		
	}
	
	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
	
}