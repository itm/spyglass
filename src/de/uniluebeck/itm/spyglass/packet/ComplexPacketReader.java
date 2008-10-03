/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

// --------------------------------------------------------------------------------
/**
 * This class implements a PacketReader. It reads packets from a text file line by line of the
 * format
 * 
 * id:x:y
 * 
 * Each line is a packet with the given id and position (x and y coordinates).
 */
public class ComplexPacketReader extends PacketReader {
	
	private static Logger log = SpyglassLogger.get(ComplexPacketReader.class);
	
	private BufferedReader bufferedReader = null;
	
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
		
		if (bufferedReader == null) {
			bufferedReader = new BufferedReader(new InputStreamReader(this.getGateway()
					.getInputStream()));
		}
		
		SpyglassPacket packet = null;
		
		try {
			
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				packet = parsePacketLine(line);
				if (packet != null) {
					break;
				}
			}
			
		} catch (final IOException e) {
			log.error("Error while reading a new packet...", e);
		}
		
		// Hold back the packet at least for delayMillies
		final long now = System.currentTimeMillis();
		final long diff = now - this.lastPacketTimestamp;
		if (diff < this.delayMillies) {
			Thread.sleep(this.delayMillies - diff);
		}
		this.lastPacketTimestamp = System.currentTimeMillis();
		return packet;
	}
	
	// --------------------------------------------------------------------------
	// ------
	
	/**
	 * each line is made up by a timestamp and a packet in hex, seperated by a colon.
	 * 
	 * @param line
	 *            A String containing the packet format.
	 * @return A packet object or null, if the line could be parsed.
	 * @throws SpyglassPacketException
	 */
	private SpyglassPacket parsePacketLine(String line) throws SpyglassPacketException {
		if ((line == null) || line.trim().equals("")) {
			return null;
		}
		
		line = line.trim();
		
		if (line.startsWith("//")) {
			return null;
		}
		
		final String[] tokens = line.split(":\\s*");
		
		final String hexPacket = tokens[1];
		final int length = hexPacket.length() / 2;
		final byte[] array = new byte[length];
		
		for (int i = 0; i < hexPacket.length(); i += 2) {
			final String byteString = hexPacket.substring(i, i + 2);
			array[i / 2] = (byte) Integer.parseInt(byteString, 16);
		}
		
		final SpyglassPacket packet = PacketFactory.createInstance(array);
		
		return packet;
	}
	
}
