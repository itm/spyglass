/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.util.EventObject;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.TimeDiff;
import de.uniluebeck.itm.spyglass.util.Tools;

//------------------------------------------------------------------------------
// --
/**
 * The visualization task class represents a thread that reads the packet cache
 * and uses a InformationDispatcher object to distribute the packets to the
 * loaded plugins. It then fires an event to notify all listeners for
 * redraw-purposes. This thread stop when the <code>visualizationRunning</code>
 * member of the Spyglass class is set to false.
 */
public class VisualizationTask implements Runnable {
	private static Category log = SpyglassLogger.get(VisualizationTask.class);
	
	private Spyglass spyglass = null;
	
	private EventObject eventObject = null;
	
	private long fps = 25;
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * @param delay
	 * @param initialDelay
	 * @param fps
	 * @param spyglass
	 */
	public VisualizationTask(final long fps, final Spyglass spyglass) {
		this.spyglass = spyglass;
		this.fps = fps;
		
		eventObject = new EventObject(spyglass);
	}
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public void run() {
		log.debug("Visualization thread start.");
		final long repaintInterval = (long) ((1.0 / fps) * 1000);
		log.debug("Repainting every " + repaintInterval + " ms = " + fps + " fps");
		
		final TimeDiff timeDiff = new TimeDiff(repaintInterval);
		timeDiff.touch();
		
		/*
		 * Packet packet = null; LinkedList<Packet> q =
		 * spyglass.getPacketCache();
		 * 
		 * while (spyglass.isVisualizationRunning()) { while ((packet =
		 * q.getLast()) != null) { q.removeLast(); if
		 * (!spyglass.isVisualizationRunning()) break;
		 * 
		 * // Distribute the packet to the plugins
		 * spyglass.getInfoDispatcher().dispatchPacket(packet);
		 * 
		 * if (timeDiff.isTimeout()) { // Invoke the redraw of the scene by
		 * firing a spyglass event spyglass.fireRedrawEvent(eventObject);
		 * timeDiff.touch(); } }
		 * 
		 * //Limit the query intervall if no packets are available if( q.size()
		 * == 0) Tools.sleep(repaintInterval); }
		 */
		while (!Thread.currentThread().isInterrupted() && spyglass.isVisualizationRunning()) {
			try {
				if (timeDiff.isTimeout()) {
					// Invoke the redraw of the scene by firing a spyglass event
					spyglass.fireRedrawEvent(eventObject);
					timeDiff.touch();
				}
				Tools.sleep(repaintInterval);
			} catch (final Exception e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		
	}
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public long getFps() {
		return fps;
	}
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public void setFps(final long fps) {
		this.fps = fps;
	}
	
}
