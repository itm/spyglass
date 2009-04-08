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
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.RGB;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.Grid;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class GridPainterPlugin extends BackgroundPainterPlugin implements PropertyChangeListener {

	private static Logger log = SpyglassLoggerFactory.getLogger(GridPainterPlugin.class);

	@Element(name = "parameters")
	private final GridPainterXMLConfig xmlConfig;

	private final Layer layer;

	private Grid grid;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public GridPainterPlugin() {
		super(false);
		xmlConfig = new GridPainterXMLConfig();
		layer = Layer.Factory.createQuadTreeLayer();
	}

	@Override
	public PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new GridPainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<GridPainterPlugin, GridPainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new GridPainterPreferencePage(dialog, spyglass);
	}

	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		synchronized (layer) {
			return layer.getDrawingObjects(area);
		}
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
	protected void resetPlugin() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final PluginManager manager) throws Exception {

		super.init(manager);

		// adding this listener here so it is called
		// on programm startup (doesn't work in constructor!)
		xmlConfig.addPropertyChangeListener(this);

		// dito
		updateGrid();

	}

	@Override
	public void propertyChange(final PropertyChangeEvent arg0) {
		updateGrid();
	}

	private void updateGrid() {
		if (grid != null) {
			synchronized (layer) {
				layer.remove(grid);
			}
			fireDrawingObjectRemoved(grid);
		}

		grid = new Grid();
		final int[] lineColor = xmlConfig.getLineColorRGB();
		grid.setColor(new RGB(lineColor[0], lineColor[1], lineColor[2]));
		grid.setGridElementHeight(xmlConfig.getGridElementHeight());
		grid.setGridElementWidth(xmlConfig.getGridElementWidth());
		grid.setPosition(new AbsolutePosition(xmlConfig.getGridLowerLeftPointX(), xmlConfig.getGridLowerLeftPointY(), 0));
		grid.setLineWidth((int) xmlConfig.getLineWidth());
		grid.setNumCols(xmlConfig.getNumCols());
		grid.setNumRows(xmlConfig.getNumRows());

		synchronized (layer) {
			layer.add(grid);
		}

		fireDrawingObjectAdded(grid);
	}

}