/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class GridPainterPlugin extends BackgroundPainterPlugin {
	
	@Element(name = "parameters")
	private GridPainterXMLConfig xmlConfig;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public GridPainterPlugin() {
		super(false);
		xmlConfig = new GridPainterXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> createPreferencePage(final ConfigStore cs) {
		return new GridPainterPreferencePage(cs, this);
	}
	
	public static PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		return new GridPainterPreferencePage(cs);
	}
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getHumanReadableName() {
		return "GridPainter";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setXMLConfig(final PluginXMLConfig xmlConfig) {
		this.xmlConfig = (GridPainterXMLConfig) xmlConfig;
		
	}
	
	@Override
	protected void updateQuadTree() {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
}