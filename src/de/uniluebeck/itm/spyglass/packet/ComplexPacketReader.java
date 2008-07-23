/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Category;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

//------------------------------------------------------------------------------
// --
/**
 * This class implements a PacketReader. It reads packets from a text file line
 * by line of the format id:x:y Each line is a packet with the given id and
 * position (x and y coordinates).
 */
@Root
public class ComplexPacketReader extends PacketReader {
	
	private static Category log = SpyglassLogger.get(ComplexPacketReader.class);
	
	private BufferedReader bufferedReader = null;
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * 
	 * @throws Exception
	 * 
	 */
	@Override
	public Packet getNextPacket() throws SpyglassPacketException {
		try {
			Packet packet = null;
			String line = null;
			
			log.debug("called getNextPacket...");
			
			while ((line = bufferedReader.readLine()) != null) {
				packet = parsePacketLine(line);
				if (packet != null) {
					return packet;
				}
			}
			
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public void setGateway(final Gateway gateway) {
		super.setGateway(gateway);
		
		bufferedReader = new BufferedReader(new InputStreamReader(gateway.getInputStream()));
	}
	
	//--------------------------------------------------------------------------
	// ------
	
	/**
	 * each line is made up by a timestamp and a packet in hex, seperated by a
	 * colon.
	 * 
	 * @param line
	 *            A String containing the packet format.
	 * @return A packet object or null, if the line could be parsed.
	 * @throws SpyglassPacketException
	 */
	private Packet parsePacketLine(String line) throws SpyglassPacketException {
		if ((line == null) || line.trim().equals("")) {
			return null;
		}
		
		line = line.trim();
		
		if (line.startsWith("//")) {
			return null;
		}
		
		final String[] tokens = line.split(":\\s*");
		
		final SpyglassPacket packet = new SpyglassPacket();
		
		final String hexPacket = tokens[1];
		final int length = hexPacket.length() / 2;
		final byte[] array = new byte[length];
		
		for (int i = 0; i < hexPacket.length(); i += 2) {
			final String byteString = hexPacket.substring(i, i + 2);
			array[i / 2] = (byte) Integer.parseInt(byteString, 16);
		}
		
		packet.deserialize(array);
		
		return packet;
	}
	
}
