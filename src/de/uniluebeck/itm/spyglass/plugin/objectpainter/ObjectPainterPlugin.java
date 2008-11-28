/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Image;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.Int16ListPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.Int16ListPacket.TrajectorySection;
import de.uniluebeck.itm.spyglass.plugin.NeedsMetric;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * // -------------------------------------------------------------------------------- /** TODO: -
 * line is not removed (quadtree??) - performance problems? -
 * 
 * 
 * 
 * @author dariush
 * 
 */
public class ObjectPainterPlugin extends BackgroundPainterPlugin implements NeedsMetric {

	private class Trajectory implements Runnable {

		@Override
		public void run() {

			// Draw lines
			for (final DrawingObject d : lines) {
				synchronized (layer) {
					layer.addOrUpdate(d);
				}
				fireDrawingObjectAdded(d);
			}

			synchronized (layer) {
				layer.addOrUpdate(image);
			}
			fireDrawingObjectAdded(image);

			sectionTimestamp = startTime;

			while (running) {

				try {
					Thread.sleep(50);

					final long time = System.currentTimeMillis();

					final long diff = time - this.sectionTimestamp;
					if (diff >= list.get(currentSection).duration * 1000) {

						// were at the end. stop
						if (currentSection == list.size() - 1) {
							running = false;
							continue;
						} else {
							currentSection++;
							sectionTimestamp = time;
						}

					}

					final TrajectorySection sect = list.get(currentSection);

					// move image to the next position
					final double lambda = diff / (sect.duration * 1000.0);

					final AbsolutePosition location = new AbsolutePosition();
					location.x = (int) (sect.start.x + lambda * (sect.end.x - sect.start.x));
					location.y = (int) (sect.start.y + lambda * (sect.end.y - sect.start.y));

					location.x -= image.getImageSizeX() / 2;
					location.y -= image.getImageSizeY() / 2;

					// log.debug(String.format("diff=%d; lambda=%f", diff, lambda));

					final AbsoluteRectangle oldBBox = image.getBoundingBox();
					synchronized (layer) {
						image.setPosition(location);
						layer.bringToFront(image);
					}

					fireDrawingObjectChanged(image, oldBBox);

				} catch (final InterruptedException e) {
					log.error("", e);
				}
			}

			synchronized (layer) {
				layer.remove(image);
			}
			fireDrawingObjectRemoved(image);

			// Remove lines
			for (final DrawingObject d : lines) {
				synchronized (layer) {
					layer.remove(d);
				}
				fireDrawingObjectRemoved(d);
				log.debug("Removed line " + d);
			}

			log.debug("Finished trajectory " + this);

		}

		boolean running = true;

		/** Fetched directly from the packet */
		List<TrajectorySection> list;

		/** When did the packet arrive */
		long startTime;

		int currentSection = 0;

		long sectionTimestamp;

		ArrayList<Line> lines = new ArrayList<Line>();

		Image image = null;

	}

	private static Logger log = SpyglassLoggerFactory.getLogger(ObjectPainterPlugin.class);

	@Element(name = "parameters")
	private final ObjectPainterXMLConfig config;

	private final ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();

	private final Layer layer = new QuadTree();

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ObjectPainterPlugin() {
		super(true);
		config = new ObjectPainterXMLConfig();
	}

	@Override
	public PluginPreferencePage<ObjectPainterPlugin, ObjectPainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new ObjectPainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<ObjectPainterPlugin, ObjectPainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new ObjectPainterPreferencePage(dialog, spyglass);
	}

	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		synchronized (layer) {
			return layer.getDrawingObjects(area);
		}
	}

	public static String getHumanReadableName() {
		return "ObjectPainter";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return config;
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {

		// Requirement for a trajectory packet
		if (!(packet instanceof Int16ListPacket)) {
			return;
		}
		if (!config.containsSemanticType(packet.getSemanticType())) {
			return;
		}

		final Int16ListPacket p = (Int16ListPacket) packet;

		final List<TrajectorySection> list;
		try {
			if (config.isPacketType3D()) {
				list = p.getTrajectory3D();
			} else {
				list = p.getTrajectory2D();
			}
		} catch (final ParseException e) {
			log.error("Could not extract trajectory information from the packet!", e);
			return;
		}

		final Trajectory t = new Trajectory();
		t.startTime = System.currentTimeMillis();
		t.list = list;

		// create drawing objects;

		if (config.isDrawLine()) {
			for (final TrajectorySection s : list) {
				final Line l = new Line();
				l.setPosition(s.start, false);
				l.setEnd(s.end, false);
				l.setColor(config.getLineColorRGB());
				t.lines.add(l);
			}
		}

		final Image i = new Image(config.getImageFileName());
		final AbsolutePosition pos = list.get(0).start.clone();
		pos.x -= config.getImageSizeX() / 2;
		pos.y -= config.getImageSizeY() / 2;
		i.setPosition(pos);
		t.image = i;
		i.setImageSizeX(config.getImageSizeX());
		i.setImageSizeY(config.getImageSizeY());

		this.trajectories.add(t);
		new Thread(t).start();

	}

	@Override
	public void reset() {
		for (final Trajectory t : this.trajectories) {
			t.running = false;
		}
		this.trajectories.clear();
		this.layer.clear();
	}

	@Override
	protected void updateQuadTree() {
		// nothing to do here
	}

	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}

	public void shutdown() {
		// super.shutdown();

		// shutdown the threads
		for (final Trajectory t : this.trajectories) {
			t.running = false;
		}
	}
}
