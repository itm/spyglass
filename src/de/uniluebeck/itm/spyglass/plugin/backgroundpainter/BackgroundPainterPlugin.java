/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.backgroundpainter;

import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * 
 * 
 */
public abstract class BackgroundPainterPlugin extends Plugin implements Drawable {
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor<br>
	 * If the plug-in has to administer a packet queue a new thread will be
	 * started.
	 * 
	 * @param needsPacketQueue
	 *            indicates whether or not the plug-in has to administer a
	 *            packet queue
	 */
	public BackgroundPainterPlugin(final boolean needsPacketQueue) {
		super(needsPacketQueue);
	}
	
	public final int getTimeout() {
		return getXMLConfig().getTimeout();
	}
	
	public static String getHumanReadableName() {
		return "BackgroundPainter";
	}
	
	@Override
	public String toString() {
		return BackgroundPainterPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}
}
