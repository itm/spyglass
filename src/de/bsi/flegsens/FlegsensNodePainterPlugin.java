/*
 * ----------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * -------------------------------------------------------------------------
 */
package de.bsi.flegsens;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.HistoricalPlugin;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

//------------------------------------------------------------------------------
// --
/**
 * A plugin to paint a node.
 */
public class FlegsensNodePainterPlugin extends HistoricalPlugin {
	private static Category log = SpyglassLogger.get(FlegsensNodePainterPlugin.class);
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * @see Plugin#handlePacket(Packet)
	 */
	@Override
	public void handlePacket(final Packet packet) {
		if (log.isDebugEnabled()) {
			log.debug("Handling packet " + packet);
		}
		
		log.debug("pm: " + getPluginManager());
		log.debug("np: " + getPluginManager().getNodePositioner());
		
		final Position p = getPluginManager().getNodePositioner().getPosition(packet.getId());
		if (p != null) {
			final DrawingObject rect = new Rectangle();
			rect.setId(11);
			rect.setColorR(255);
			rect.setColorG(0);
			rect.setColorB(0);
			rect.setPosition(p);
			
			getSubLayer().addOrUpdate(rect);
		}
	}
	
}
