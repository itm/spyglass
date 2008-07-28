/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ImagePainterPlugin extends BackgroundPainterPlugin {
	
	@Element(name = "parameters")
	private ImagePainterXMLConfig xmlConfig;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ImagePainterPlugin() {
		super(false);
		xmlConfig = new ImagePainterXMLConfig();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public float getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> createPreferencePage(final ConfigStore cs) {
		return new ImagePainterPreferencePage(cs, this);
	}
	
	public static PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> createTypePreferencePage(final ConfigStore cs) {
		return new ImagePainterPreferencePage(cs);
	}
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return new ArrayList<DrawingObject>();
	}
	
	public static String getHumanReadableName() {
		return "ImagePainter";
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
		this.xmlConfig = (ImagePainterXMLConfig) xmlConfig;
		
	}
	
	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}
	
}