/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class MapPainterPlugin extends BackgroundPainterPlugin {
	
	@Element(name = "parameters")
	private final MapPainterXMLConfig xmlConfig;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MapPainterPlugin() {
		super(true);
		xmlConfig = new MapPainterXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new MapPainterPreferencePage(dialog, spyglass, this);
	}
	
	public static PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new MapPainterPreferencePage(dialog, spyglass);
	}
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return new ArrayList<DrawingObject>();
	}
	
	public static String getHumanReadableName() {
		return "MapPainter";
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
	
	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		// TODO Auto-generated method stub
		return new ArrayList<DrawingObject>();
	}
	
}