/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.packet;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gateway.Gateway;

//--------------------------------------------------------------------------------
/**
 * Abstract class for all PacketReader implementations. A PacketReader must provide the next packet with the
 * <code>getNextPacket()</code> method.
 * 
 * @author Timo Rumland
 */
@Root
public abstract class PacketReader {
	private Gateway gateway = null;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public abstract Packet getNextPacket();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Element
	public Gateway getGateway() {
		return gateway;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Element
	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}

}