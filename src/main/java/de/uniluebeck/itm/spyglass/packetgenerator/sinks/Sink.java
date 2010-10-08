/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packetgenerator.sinks;

import org.simpleframework.xml.Root;

/**
 * A Sink takes samples and stores them to a file, sends them over the network, or does other things
 * with it.
 * 
 * @author dariush
 * 
 */
@Root
public abstract class Sink {

	/**
	 * Takes a packet and does something with it (depends on the implementing class)
	 * 
	 * @param packet
	 * @throws Exception
	 */
	public abstract void sendPacket(byte[] packet) throws Exception;

	/**
	 * Should be called before the first packet is sent. (e.g. open the file or socket)
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;

	/**
	 * Should be called after the last packet is sent. (e.g. close the file or socket)
	 */
	public abstract void shutdown();
}
