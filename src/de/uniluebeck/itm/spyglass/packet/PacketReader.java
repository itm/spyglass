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
public abstract class PacketReader {
	private static Category log = SpyglassLogger.get(PacketReader.class);
	
	@Element(name = "gateway", required = false)
	private Gateway gateway = null;
	
	/**
	 * Delay between
	 */
	@Element(name = "delayMillies", required = false)
	protected int delayMillies = 1000;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * Returns a new packet, once it arrives. It will never return null, but block until it has
	 * simething to return.
	 * 
	 * @throws SpyglassPacketException
	 * @throws {@link InterruptedException} if the method was interrupted while waiting on a packet.
	 * 
	 */
	public abstract SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public Gateway getGateway() {
		return gateway;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public void setGateway(final Gateway gateway) {
		this.gateway = gateway;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the delayMillies
	 */
	public int getDelayMillies() {
		return delayMillies;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param delayMillies
	 *            the delayMillies to set
	 */
	public void setDelayMillies(final int delayMillies) {
		this.delayMillies = delayMillies;
	}
	
}