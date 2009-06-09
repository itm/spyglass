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

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Represents a packet used to indicate a directed link between two nodes.<br>
 * The packet contains the identifiers of the source and destination node along with a value.
 * 
 * @author Daniel Bimschas
 * @author Sebastian Ebers
 * @see SyntaxTypes#ISENSE_SPYGLASS_PACKET_UINT16
 */
public class NodeLinkPacket extends IntListPacket {

	private static final Logger log = SpyglassLoggerFactory.getLogger(NodeLinkPacket.class);

	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_UINT16;

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		super.deserialize(buf);
		if (getSyntaxType() != Uint16ListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		values = new Integer[(buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE) / 2];
		for (int i = 0; (i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++) {
			final int pos = i * 2 + SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i] = deserializeUint16(buf[pos], buf[pos + 1]);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the identifier of the link's destination node
	 * 
	 * @return the identifier of the link's destination node
	 */
	public Integer getDestinationNodeId() {
		return values[0];
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the value associated to the nodes
	 * 
	 * @return the value associated to the nodes
	 */
	public Integer getValue() {
		return values[1];
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the identifier of the link's source node
	 * 
	 * @return the identifier of the link's source node
	 */
	public Integer getSourceNodeId() {
		return getSenderId();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param packet
	 * @return
	 */
	public static NodeLinkPacket interpret(final Uint16ListPacket packet) {
		final NodeLinkPacket nodeLinkPacket = new NodeLinkPacket();
		try {
			nodeLinkPacket.deserialize(packet.getPacketData());
		} catch (final SpyglassPacketException e) {
			log.error("Goddamnit", e);
		}
		return nodeLinkPacket;
	}

}
