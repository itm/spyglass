/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

//--------------------------------------------------------------------------------
/**
 * Represents a Packet with the syntaxtype Uint8ListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * @author Dariush Forouher
 * 
 */
public class Uint8ListPacket extends IntListPacket {
	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_UINT8;

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	void deserialize(final byte[] buf) throws SpyglassPacketException {

		super.deserialize(buf);
		if (getSyntaxType() != Uint8ListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		values = new Integer[buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE];
		for (int i = 0; (i + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++) {
			values[i] = deserializeUint8(buf[i + SpyglassPacket.EXPECTED_PACKET_SIZE]);
		}
	}

}
