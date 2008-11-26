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

		// links nach rechts horizontal
		final LinePainterLine line1 = new LinePainterLine();
		line1.setPosition(new AbsolutePosition(-1000, 0, 0), false);
		line1.setEnd(new AbsolutePosition(1000, 0, 0), false);
		line1.setLineWidth(1, false);
		line1.setColor(55, 55, 55);
		layer.addOrUpdate(line1);

		// rechts nach links horizontal
		final LinePainterLine line2 = new LinePainterLine();
		line2.setPosition(new AbsolutePosition(1000, 0, 0), false);
		line2.setEnd(new AbsolutePosition(-1000, 0, 0), false);
		line2.setLineWidth(1, false);
		line2.setColor(55, 55, 55);
		layer.addOrUpdate(line2);

		// oben nach unten vertikal
		final LinePainterLine line3 = new LinePainterLine();
		line3.setPosition(new AbsolutePosition(0, 1000, 0), false);
		line3.setEnd(new AbsolutePosition(0, -1000, 0), false);
		line3.setLineWidth(1, false);
		line3.setColor(55, 55, 55);
		layer.addOrUpdate(line3);

		// unten nach oben vertikal
		final LinePainterLine line4 = new LinePainterLine();
		line4.setPosition(new AbsolutePosition(0, -1000, 0), false);
		line4.setEnd(new AbsolutePosition(0, 1000, 0), false);
		line4.setLineWidth(1, false);
		line4.setColor(55, 55, 55);
		layer.addOrUpdate(line4);

		// links unten nach rechts oben diagonal
		final LinePainterLine line5 = new LinePainterLine();
		line5.setPosition(new AbsolutePosition(-1000, -1000, 0), false);
		line5.setEnd(new AbsolutePosition(1000, 1000, 0), false);
		line5.setLineWidth(1, false);
		line5.setColor(55, 55, 55);
		layer.addOrUpdate(line5);

		// rechts oben nach links unten diagonal
		final LinePainterLine line6 = new LinePainterLine();
		line6.setPosition(new AbsolutePosition(1000, 1000, 0), false);
		line6.setEnd(new AbsolutePosition(-1000, -1000, 0), false);
		line6.setLineWidth(1, false);
		line6.setColor(55, 55, 55);
		layer.addOrUpdate(line6);

		// links oben nach rechts unten diagonal
		final LinePainterLine line7 = new LinePainterLine();
		line7.setPosition(new AbsolutePosition(-1000, 1000, 0), false);
		line7.setEnd(new AbsolutePosition(1000, -1000, 0), false);
		line7.setLineWidth(1, false);
		line7.setColor(55, 55, 55);
		layer.addOrUpdate(line7);

		// rechts unten nach links oben diagonal
		final LinePainterLine line8 = new LinePainterLine();
		line8.setPosition(new AbsolutePosition(1000, -1000, 0), false);
		line8.setEnd(new AbsolutePosition(-1000, 1000, 0), false);
		line8.setLineWidth(1, false);
		line8.setColor(55, 55, 55);
		layer.addOrUpdate(line8);
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new LinePainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
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