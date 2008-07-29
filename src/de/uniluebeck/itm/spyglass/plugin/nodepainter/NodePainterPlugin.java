/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepainter;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// --------------------------------------------------------------------------------
/**
 * A plugin to paint a node.
 */
public abstract class NodePainterPlugin extends Plugin implements Drawable {
	
	private final Logger log = SpyglassLogger.getLogger(NodePainterPlugin.class);
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public NodePainterPlugin() {
		super(true);
	}
	
	public final int getTimeout() {
		return getXMLConfig().getTimeout();
	}
	
	public static String getHumanReadableName() {
		return "NodePainter";
	}
	
	@Override
	public String toString() {
		return NodePainterPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		if (packet != null) {
			final int[] mySemanticTypes = getXMLConfig().getSemanticTypes();
			final int packetSemanticType = packet.getSemantic_type();
			for (int i = 0; i < mySemanticTypes.length; i++) {
				if (mySemanticTypes[i] == packetSemanticType) {
					enqueuePacket(packet);
					break;
				}
			}
		}
		this.notify();
	}
	
}
