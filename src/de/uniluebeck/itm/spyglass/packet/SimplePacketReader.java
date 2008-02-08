/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gateway.Gateway;

//--------------------------------------------------------------------------------
/**
 * This class implements a PacketReader. It reads packets from a text file line by line of the format id:x:y Each line
 * is a packet with the given id and position (x and y coordinates).
 */
@Root
public class SimplePacketReader extends PacketReader {
	private BufferedReader bufferedReader = null;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public Packet getNextPacket() {
		try {
			Packet packet = null;
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				packet = parsePacketLine(line);
				if (packet != null)
					return packet;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void setGateway(Gateway gateway) {
		super.setGateway(gateway);

		bufferedReader = new BufferedReader(new InputStreamReader(gateway.getInputStream()));
	}

	// --------------------------------------------------------------------------------

	/**
	 * Parses the given string to retrieve a apropriate id, x and y data. The method then creates and returns a new
	 * Packet instance.
	 * 
	 * @param line
	 *            A String containing the packet format.
	 * @return A packet object or null, if the line could be parsed.
	 */
	private Packet parsePacketLine(String line) {
		if (line == null || line.trim().equals(""))
			return null;

		line = line.trim();

		if (line.startsWith("//"))
			return null;

		Packet packet = new Packet();
		String[] tokens = line.split(":");

		int id = Integer.parseInt(tokens[0]);
		//int x = Integer.parseInt(tokens[1]);
		//int y = Integer.parseInt(tokens[2]);

		packet.setId(id);

		return packet;
	}

}
