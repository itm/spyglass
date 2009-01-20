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
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.Int16ListPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.Int16ListPacket.TrajectorySection;
import de.uniluebeck.itm.spyglass.plugin.NeedsMetric;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * ObjectPainter plugin
 * 
 * 
 * @author Dariush Forouher
 * 
 */
public class ObjectPainterPlugin extends BackgroundPainterPlugin implements NeedsMetric {

	static Logger log = SpyglassLoggerFactory.getLogger(ObjectPainterPlugin.class);

	@Element(name = "parameters")
	private final ObjectPainterXMLConfig config;

	/**
	 * Timer used for updating the trajectories
	 */
	private final Timer timer = new Timer();

	/**
	 * List of trajectories. Each trajectory is a TimerTask in our timer.
	 */
	final List<Trajectory> trajectories = Collections.synchronizedList(new ArrayList<Trajectory>());
	
	final Layer layer = new QuadTree();

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
	public ObjectPainterXMLConfig getXMLConfig() {
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

		final Trajectory t = new Trajectory(this, list, config.getImageFileName());

		this.trajectories.add(t);
		timer.schedule(t, 0, config.getUpdateInterval());
		
		// NOTE: the trajectories stay in the list until the plugin is shut down. but
		// this doesn't really matter (except that it takes a small amount of mememory)
		// since the list is only used on reset().
	}

	@Override
	public void reset() {
		
		synchronized (trajectories) {
			for (final Trajectory t : this.trajectories) {
				t.cancel();
			}
		}
		this.trajectories.clear();
		synchronized (layer) {
			this.layer.clear();
		}
	}

	@Override
	protected void updateLayer() {
		// nothing to do here
	}

	@Override
	public List<DrawingObject> getAutoZoomDrawingObjects() {
		synchronized (layer) {
			return layer.getDrawingObjects();
		}
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();

		// shutdown the timer (don't bother to clean up the DrawingObjects)
		timer.cancel();
	}
	
	/* Methods needed for scope-reasons */
	
	void fireDrawingObjectChangedInternal(final DrawingObject dob, final AbsoluteRectangle rect) {
		fireDrawingObjectChanged(dob, rect);
	}

	void fireDrawingObjectAddedInternal(final DrawingObject dob) {
		fireDrawingObjectAdded(dob);
	}

	void fireDrawingObjectRemovedInternal(final DrawingObject dob) {
		fireDrawingObjectRemoved(dob);
	}
}
