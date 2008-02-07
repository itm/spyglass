/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.core;

import java.util.EventObject;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.util.Logging;
import de.uniluebeck.itm.spyglass.util.TimeDiff;

/**
 * The visualization task class represents a thread that reads the packet cache and uses a InformationDispatcher object
 * to distribute the packets to the loaded plugins. It then fires an event to notify all listeners for redraw-purposes.
 * This thread stop when the <code>visualizationRunning</code> member of the Spyglass class is set to false.
 */
public class VisualizationTask implements Runnable {
	private static Category log = Logging.get(VisualizationTask.class);

	private long delay = 100;

	private long initialDelay = 1000;

	private Spyglass spyglass = null;

	private EventObject eventObject = null;

	private long fps = 25;

	// --------------------------------------------------------------------------------
	/**
	 * @param delay
	 * @param initialDelay
	 * @param fps
	 * @param spyglass
	 */
	public VisualizationTask(long delay, long initialDelay, long fps, Spyglass spyglass) {
		this.delay = delay;
		this.initialDelay = initialDelay;
		this.spyglass = spyglass;
		this.fps = fps;

		eventObject = new EventObject(spyglass);
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void run() {
		log.debug("Visualization thread start.");
		try {
			long repaintInterval = (long) ((1.0 / (double) fps) * 1000);
			log.debug("Repainting every " + repaintInterval + " ms = " + fps + " fps");

			TimeDiff timeDiff = new TimeDiff(repaintInterval);
			Packet packet = null;

			Thread.sleep(initialDelay);
			timeDiff.touch();

			while (spyglass.isVisualizationRunning()) {
				while ((packet = spyglass.getPacketCache().pollLast()) != null) {
					if (!spyglass.isVisualizationRunning())
						break;

					// Distribute the packet to the plugins
					spyglass.getInfoDispatcher().dispatchPacket(packet);

					if (timeDiff.isTimeout()) {
						// Invoke the redraw of the scene by firing a spyglass event
						spyglass.fireRedrawEvent(eventObject);
						timeDiff.touch();
					}

					if (timeDiff.ms() < delay)
						Thread.sleep(delay - timeDiff.ms());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long getDelay() {
		return delay;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public long getFps() {
		return fps;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setFps(long fps) {
		this.fps = fps;
	}

}
