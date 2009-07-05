/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packetgenerator.samples;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;

import org.simpleframework.xml.Element;

//--------------------------------------------------------------------------------
/**
 * This sample has no abstract knowledge of the package, it just contains a whole packet as a byte
 * array.
 * 
 * @author dariush
 */
public class RawSample extends Sample {

	/**
	 * the packet in hex
	 */
	@Element
	private String packet;

	// --------------------------------------------------------------------------------
	/**
	 * Returns a packet
	 * 
	 * @return a packet
	 */
	public String getPacket() {
		return packet;
	}

	/**
	 * Transforms the Payload from an hex string to an byte array.
	 */
	private byte[] getBytePacket() throws ParseException {
		final int length = this.packet.length() / 2;
		final byte[] array = new byte[length];

		for (int i = 0; i < this.packet.length(); i += 2) {
			final String byteString = packet.substring(i, i + 2);
			array[i / 2] = (byte) Integer.parseInt(byteString, 16);
		}
		return array;
	}

	@Override
	public byte[] generatePacket() throws ParseException {

		// compute the packet length
		final short packetLength = (short) (this.getBytePacket().length);

		// this byte array will contain the packet
		final byte[] backendArray = new byte[packetLength];

		// create ByteBuffer for more convinient access
		final ByteBuffer buf = ByteBuffer.wrap(backendArray);

		// should already be default. but better safe than sorry...
		buf.order(ByteOrder.BIG_ENDIAN);

		// Now fill the array with data

		buf.put(this.getBytePacket()); // the Payload

		// Return the Byte array
		return backendArray;
	}

}
