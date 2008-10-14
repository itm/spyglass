/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.gridpainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.Grid;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class GridPainterPlugin extends BackgroundPainterPlugin implements PropertyChangeListener {
	
	@Element(name = "parameters")
	private final GridPainterXMLConfig xmlConfig;
	
	private Grid grid;
	
	private QuadTree layer;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public GridPainterPlugin() {
		super(false);
		xmlConfig = new GridPainterXMLConfig();
		xmlConfig.addPropertyChangeListener(this);
		layer = new QuadTree();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new GridPainterPreferencePage(dialog, spyglass, this);
	}
	
	public static PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new GridPainterPreferencePage(dialog, spyglass);
	}
	
	public List<DrawingObject> getDrawingObjects(final DrawingArea drawingArea) {
		return layer.getDrawingObjects(drawingArea.getAbsoluteDrawingRectangle());
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
	protected void updateQuadTree() {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
	@Override
	public void init(final PluginManager pluginManager) {
		super.init(pluginManager);
		updateGrid();
	}
	
	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}
	
	@Override
	public void propertyChange(final PropertyChangeEvent arg0) {
		updateGrid();
	}
	
	private void updateGrid() {
		
		synchronized (layer) {
			layer.remove(grid);
		}
		
		grid = new Grid();
		final int[] lineColor = xmlConfig.getLineColorRGB();
		grid.setColor(lineColor[0], lineColor[1], lineColor[1]);
		grid.setGridElementHeight(xmlConfig.getGridElementHeight());
		grid.setGridElementWidth(xmlConfig.getGridElementWidth());
		grid.setPosition(xmlConfig.getGridLowerLeftPoint());
		grid.setLineWidth(xmlConfig.getLineWidth());
		grid.setNumCols(xmlConfig.getNumCols());
		grid.setNumRows(xmlConfig.getNumRows());
		
		synchronized (layer) {
			layer.addOrUpdate(grid);
		}
		
	}
	
}