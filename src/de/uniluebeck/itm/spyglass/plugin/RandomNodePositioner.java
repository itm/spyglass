/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.plugin;

import ishell.util.Logging;

import java.util.Random;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class RandomNodePositioner extends NodePositionerPlugin {
	private static Category log = Logging.get(RandomNodePositioner.class);

	private Random r = new Random();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public Position getPosition(int nodeId) {
		Position p = new Position(Math.abs(r.nextFloat() * 800), Math.abs(r.nextFloat() * 800));
		log.debug("Random position: " + p);
		return p;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void handlePacket(Packet packet) {
		//
	}

}
