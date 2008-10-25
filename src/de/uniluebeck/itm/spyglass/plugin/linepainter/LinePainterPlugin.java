/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.relationpainter.RelationPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class LinePainterPlugin extends RelationPainterPlugin implements PropertyChangeListener {
	
	@Element(name = "parameters")
	private final LinePainterXMLConfig xmlConfig;
	
	private Layer layer;
	
	private LinePainterLine line;
	
	// public StringFormatter m_StringFormatter;
	
	public LinePainterPlugin() {
		super();
		xmlConfig = new LinePainterXMLConfig();
		layer = new QuadTree();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass, this);
	}
	
	public static PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass);
	}
	
	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		synchronized (layer) {
			return layer.getDrawingObjects(area);
		}
	}
	
	public static String getHumanReadableName() {
		return "LinePainter";
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
	public void init(final PluginManager manager) {
		super.init(manager);
		xmlConfig.addPropertyChangeListener(this);
	}
	
	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}
	
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		updateLine();
	}
	
	private void updateLine() {
		
		synchronized (layer) {
			layer.remove(line);
		}
		
		line = new LinePainterLine();
		final int[] lineColorRGB = xmlConfig.getLineColorRGB();
		line.setColor(lineColorRGB[0], lineColorRGB[1], lineColorRGB[2]);
		line.setLineWidth(xmlConfig.getLineWidth());
		line.setPosition(new AbsolutePosition(0, 0, 0));
		line.setEnd(new AbsolutePosition(+1000, +1000, 0));
		
		synchronized (layer) {
			layer.addOrUpdate(line);
		}
		
	}
	
}