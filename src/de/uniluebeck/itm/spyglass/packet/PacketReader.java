/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

import org.apache.log4j.Category;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// ------------------------------------------------------------------------------
// --
/**
 * Abstract class for all PacketReader implementations. A PacketReader must provide the next packet
 * with the <code>getNextPacket()</code> method.
 * 
 * @author Timo Rumland
 */
@Root
public abstract class PacketReader {
	private static Category log = SpyglassLogger.get(PacketReader.class);
	
	private Gateway gateway = null;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * @throws SpyglassPacketException
	 * 
	 */
	public abstract SpyglassPacket getNextPacket() throws SpyglassPacketException;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Element
	public Gateway getGateway() {
		return gateway;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Element
	public void setGateway(final Gateway gateway) {
		this.gateway = gateway;
	}
	
}