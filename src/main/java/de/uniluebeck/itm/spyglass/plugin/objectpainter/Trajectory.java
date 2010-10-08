/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.eclipse.swt.graphics.RGB;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Image;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.packet.Int16ListPacket.TrajectorySection;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

public class Trajectory extends TimerTask {

	private final ObjectPainterPlugin plugin;

	/** Fetched directly from the packet */
	private final List<TrajectorySection> list;

	/** When did the packet arrive */
	private long startTime;

	private int currentSection = 0;

	private long sectionTimestamp;

	private ArrayList<Line> lines = new ArrayList<Line>();

	private Image image = null;

	Trajectory(final ObjectPainterPlugin objectPainterPlugin, final List<TrajectorySection> list, final String filename) {
		this.plugin = objectPainterPlugin;
		this.image = new Image(filename);
		this.list = list;
		startTime = System.currentTimeMillis();

		final boolean isDrawLine = plugin.getXMLConfig().isDrawLine();
		final RGB lineColor = plugin.getXMLConfig().getLineColorRGB();
		final int sizeX = plugin.getXMLConfig().getImageSizeX();
		final int sizeY = plugin.getXMLConfig().getImageSizeY();

		// create drawing objects;
		if (isDrawLine) {
			for (final TrajectorySection s : list) {
				final Line l = new Line();
				l.setPosition(s.start);
				l.setEnd(s.end);
				l.setColor(lineColor);
				lines.add(l);
			}
		}

		final AbsolutePosition pos = list.get(0).start.clone();

		pos.x -= sizeX / 2;
		pos.y -= sizeY / 2;
		image.setPosition(pos);
		image.setImageSizeX(sizeX);
		image.setImageSizeY(sizeY);

		this.init();
	}

	private void init() {

		// Draw lines
		for (final DrawingObject d : lines) {
			this.plugin.layer.add(d);
			this.plugin.fireDrawingObjectAddedInternal(d);
		}

		this.plugin.layer.add(image);
		this.plugin.fireDrawingObjectAddedInternal(image);

		sectionTimestamp = startTime;
	}

	@Override
	public void run() {

		if (System.currentTimeMillis() - scheduledExecutionTime() >= 50) {
			return; // Too late; skip this execution.
		}

		final long time = System.currentTimeMillis();

		long diff = time - this.sectionTimestamp;
		if (diff >= list.get(currentSection).duration * 1000) {

			// were at the end. stop
			if (currentSection == list.size() - 1) {
				this.cancel();
			} else {
				currentSection++;
				sectionTimestamp = time;
				diff = 0;
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

		synchronized (this.plugin) {
			image.setPosition(location);

			// the lines should lie behind the moving image
			for (final Line l : lines) {
				this.plugin.layer.pushBack(l);
			}

		}
	}

	/**
	 * Cancel this timer and remove the drawing objects while we're at it.
	 */
	@Override
	public boolean cancel() {
		final boolean ret = super.cancel();

		// remove ourself
		plugin.trajectories.remove(this);

		// clean up, before we go
		if (ret) {

			this.plugin.layer.remove(image);
			this.plugin.fireDrawingObjectRemovedInternal(image);

			// Remove lines
			for (final DrawingObject d : lines) {
				this.plugin.layer.remove(d);
				this.plugin.fireDrawingObjectRemovedInternal(d);
			}
		}

		return ret;
	}

}