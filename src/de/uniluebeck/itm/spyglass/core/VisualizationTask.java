/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.core;

import ishell.util.Logging;

import java.util.Deque;
import java.util.EventObject;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.util.TimeDiff;
import de.uniluebeck.itm.spyglass.util.Tools;

/**
 * The visualization task class represents a thread that reads the packet cache and uses a InformationDispatcher object
 * to distribute the packets to the loaded plugins. It then fires an event to notify all listeners for redraw-purposes.
 * This thread stop when the <code>visualizationRunning</code> member of the Spyglass class is set to false.
 */
public class VisualizationTask implements Runnable {
	private static Category log = Logging.get(VisualizationTask.class);

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
	public VisualizationTask(long fps, Spyglass spyglass) {
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
		long repaintInterval = (long) ((1.0 / (double) fps) * 1000);
		log.debug("Repainting every " + repaintInterval + " ms = " + fps + " fps");

		TimeDiff timeDiff = new TimeDiff(repaintInterval);
		Packet packet = null;

		timeDiff.touch();

		Deque<Packet> q = spyglass.getPacketCache();
		
		while (spyglass.isVisualizationRunning()) {
			while ((packet = q.pollLast()) != null) {
				if (!spyglass.isVisualizationRunning())
					break;

				// Distribute the packet to the plugins
				spyglass.getInfoDispatcher().dispatchPacket(packet);

				if (timeDiff.isTimeout()) {
					// Invoke the redraw of the scene by firing a spyglass event
					spyglass.fireRedrawEvent(eventObject);
					timeDiff.touch();
				}
			}

			//Limit the query intervall if no packets are available
			if( q.size() == 0)
				Tools.sleep(repaintInterval);
		}
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
