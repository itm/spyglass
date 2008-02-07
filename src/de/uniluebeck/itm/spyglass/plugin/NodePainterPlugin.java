/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.plugin;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.util.Logging;

// --------------------------------------------------------------------------------
/**
 * A plugin to paint a node.
 */
public class NodePainterPlugin extends Plugin {
	private static Category log = Logging.get(NodePainterPlugin.class);

	// --------------------------------------------------------------------------------
	/**
	 * @see Plugin#handlePacket(Packet)
	 */
	@Override
	public void handlePacket(Packet packet) {
		if (log.isDebugEnabled())
			log.debug("Handling packet " + packet);

		DrawingObject object = createDrawingObject();
		if (object == null)
			throw new RuntimeException("NodePainterPlugin: Cloning of default drawing object (" + getDefaultDrawingObject().getClass()
					+ ") not successful.");

		object.setId(packet.getId());
		object.setPosition(packet.getPosition());
		getSubLayer().addOrUpdateDrawingObject(object);
	}

}
