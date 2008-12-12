/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.FloatListPacket;
import de.uniluebeck.itm.spyglass.packet.IntListPacket;
import de.uniluebeck.itm.spyglass.packet.LongListPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class MapPainterPlugin extends BackgroundPainterPlugin implements PropertyChangeListener {

	private static Logger log = SpyglassLoggerFactory.getLogger(MapPainterPlugin.class);

	@Element(name = "parameters")
	private final MapPainterXMLConfig xmlConfig;

	/**
	 * The layer. it contains only one static element, so it isn't really necessary. but since the
	 * QuadTree already has some nice features like boundingBox comparison, we use it for
	 * convenience.
	 */
	private final Layer layer = new QuadTree();

	/**
	 * The drawing object representing the map.
	 */
	private Map map = null;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MapPainterPlugin() {
		super(true);
		xmlConfig = new MapPainterXMLConfig();
	}

	@Override
	public PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new MapPainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new MapPainterPreferencePage(dialog, spyglass);
	}

	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

	public static String getHumanReadableName() {
		return "MapPainter";
	}

	@Override
	public MapPainterXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	@Override
	public void reset() {
		// clear the drawing object
		if (map != null) {
			synchronized (map) {
				map.positions.clear();
				map.values.clear();
				updateFramepoints();
			}
		}
	}

	@Override
	public void init(final PluginManager manager) {
		super.init(manager);

		this.map = new Map(xmlConfig);
		layer.addOrUpdate(map);
		xmlConfig.addPropertyChangeListener(this);
		updateFramepoints();
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {

		final AbsolutePosition position = pluginManager.getNodePositioner().getPosition(packet.getSenderId());

		// ignore nodes outside the area
		if (!xmlConfig.getBoundingBox().contains(position.x, position.y)) {
			return;
		}

		if (!xmlConfig.containsSemanticType(packet.getSemanticType())) {
			return;
		}

		// log.debug(String.format("Parsing packet %s", packet));

		double value = Double.NaN;

		switch (packet.getSyntaxType()) {
			case ISENSE_SPYGLASS_PACKET_FLOAT: {
				final FloatListPacket p = ((FloatListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0];
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}
			case ISENSE_SPYGLASS_PACKET_UINT32:
			case ISENSE_SPYGLASS_PACKET_INT64: {
				final LongListPacket p = ((LongListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0];
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}
			case ISENSE_SPYGLASS_PACKET_INT16:
			case ISENSE_SPYGLASS_PACKET_STD:
			case ISENSE_SPYGLASS_PACKET_UINT16:
			case ISENSE_SPYGLASS_PACKET_UINT8: {
				final IntListPacket p = ((IntListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0];
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}
			case ISENSE_SPYGLASS_PACKET_VARIABLE: {
				// TODO: handle those
				break;
			}

		}

		// log.debug(String.format("Parsed packet %s: Got value %f", packet, value));

		// update the map
		if (!Double.isNaN(value)) {
			synchronized (map) {
				map.positions.put(packet.getSenderId(), position);
				map.values.put(packet.getSenderId(), value);
			}

		}

	}

	@Override
	protected void updateLayer() {
		// TODO: should the redraw be delayed for performance reasons?
		fireDrawingObjectChanged(map, null);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		updateFramepoints();

		// now cause a redraw
		fireDrawingObjectChanged(map, null);
	}

	private void updateFramepoints() {
		// Update the framepoints
		synchronized (map) {

			// kill all old framepoints
			final Iterator<Integer> it = map.positions.keySet().iterator();
			while (it.hasNext()) {
				final Integer i = it.next();
				if (i < 0) {
					it.remove();
				}
			}

			// add framepoints horizontally
			int j = 1;
			final float numFramePointsHorizontal = xmlConfig.getNumFramePointsHorizontal();
			final int width = xmlConfig.getBoundingBox().getWidth();
			final int height = xmlConfig.getBoundingBox().getHeight();
			final AbsolutePosition upperLeft = xmlConfig.getBoundingBox().getUpperLeft().clone();
			final Double defaultValue = new Double(xmlConfig.getDefaultValue());

			for (int i = 0; i < numFramePointsHorizontal; i++) {
				final AbsolutePosition pos = upperLeft.clone();
				pos.x += i / (numFramePointsHorizontal - 1) * width;
				map.positions.put(-j, pos);
				map.values.put(-j, defaultValue);
				j++;
			}
			for (int i = 0; i < numFramePointsHorizontal; i++) {
				final AbsolutePosition pos = upperLeft.clone();
				pos.x += i / (numFramePointsHorizontal - 1) * width;
				pos.y += height;
				map.positions.put(-j, pos);
				map.values.put(-j, defaultValue);
				j++;
			}

			// add framepoints vertically
			final float numFramePointsVertical = xmlConfig.getNumFramePointsVertical();
			for (int i = 0; i < numFramePointsVertical; i++) {
				final AbsolutePosition pos = upperLeft.clone();
				pos.y += i / (numFramePointsVertical - 1) * height;
				map.positions.put(-j, pos);
				map.values.put(-j, defaultValue);
				j++;
			}
			for (int i = 0; i < numFramePointsVertical; i++) {
				final AbsolutePosition pos = upperLeft.clone();
				pos.y += i / (numFramePointsVertical - 1) * height;
				pos.x += width;
				map.positions.put(-j, pos);
				map.values.put(-j, defaultValue);
				j++;
			}

		}
	}

}