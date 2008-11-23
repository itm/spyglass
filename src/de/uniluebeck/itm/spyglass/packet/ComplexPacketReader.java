/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * This class implements a PacketReader. It reads packets from a text file line by line of the
 * format
 * 
 * id:x:y
 * 
 * Each line is a packet with the given id and position (x and y coordinates).
 * 
 * @author Dariush Forouher
 */
public class ComplexPacketReader extends PacketReader {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(ComplexPacketReader.class);
	
	private InputStream playbackFileReader = null;
	
	private long lastPacketTimestamp = -1;
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * @throws InterruptedException
	 * @throws Exception
	 * 
	 */
	@Override
	public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException {
		
		if (playbackFileReader == null) {
			playbackFileReader = getGateway().getInputStream();
		}
		
		SpyglassPacket packet = null;
		try {
			
			int next;
			byte[] packetData;
			if ((next = playbackFileReader.read()) != -1) {
				packetData = new byte[next];
				System.out.println(next);
				playbackFileReader.read(packetData);
				packet = PacketFactory.createInstance(packetData);
			}
			
		} catch (final IOException e) {
			log.error("Error while reading a new packet...", e);
		}
		
		// Hold back the packet at least for delayMillies
		final long now = System.currentTimeMillis();
		final long diff = now - lastPacketTimestamp;
		if (diff < delayMillies) {
			Thread.sleep(delayMillies - diff);
		}
		lastPacketTimestamp = System.currentTimeMillis();
		return packet;
	}
	
	@Override
	public void reset() throws IOException {
		lastPacketTimestamp = -1;
		playbackFileReader.reset();
	}
	
}
