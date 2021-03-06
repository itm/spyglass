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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//--------------------------------------------------------------------------------
/**
 * Represents a Packet with the syntaxtype VariableListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * @author Dariush Forouher
 * 
 */
public class VariableListPacket extends SpyglassPacket {
	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_VARIABLE;

	private List<Number> values = new LinkedList<Number>();

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		values.clear();
		super.deserialize(buf);
		if (getSyntaxType() != VariableListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		for (int i = SpyglassPacket.EXPECTED_PACKET_SIZE; i < buf.length; i++) {
			final SyntaxTypes type = SyntaxTypes.toEnum(deserializeUint8(buf[i]));
			i++;
			switch (type) {
				case ISENSE_SPYGLASS_PACKET_UINT8:
					final int uint8 = deserializeUint8(buf[i]);
					values.add(new Integer(uint8));
					break;
				case ISENSE_SPYGLASS_PACKET_UINT16:
					final int uint16 = deserializeUint16(buf[i], buf[i + 1]);
					i++;
					values.add(new Integer(uint16));
					break;
				case ISENSE_SPYGLASS_PACKET_INT16:
					final int int16 = deserializeInt16(buf[i], buf[i + 1]);
					i++;
					values.add(new Integer(int16));
					break;
				case ISENSE_SPYGLASS_PACKET_UINT32:
					final long uint32 = deserializeUint32(buf[i], buf[i + 1], buf[i + 2], buf[i + 3]);
					i = i + 3;
					values.add(new Long(uint32));
					break;
				case ISENSE_SPYGLASS_PACKET_INT64:
					final long int64 = deserializeInt64(buf[i], buf[i + 1], buf[i + 2], buf[i + 3], buf[i + 4], buf[i + 5], buf[i + 6], buf[i + 7]);
					i = i + 7;
					values.add(new Long(int64));
					break;
				case ISENSE_SPYGLASS_PACKET_FLOAT:
					final float f = deserializeFloat(buf[i], buf[i + 1], buf[i + 2], buf[i + 3]);
					i = i + 3;
					values.add(new Float(f));
					break;
				default:
					break;
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the Listelements of this Packet
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the Listelements of this Packet
	 */
	public Number[] getValues() {
		return values.toArray(new Number[0]);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#toString()
	 */
	@Override
	public String toString() {
		String ret = super.toString() + ", values: ";
		final Iterator<Number> it = values.iterator();
		for (int i = 0; it.hasNext(); i++) {
			if (i > 0) {
				ret = ret + ", ";
			}
			ret = ret + it.next().toString();
		}
		return ret;
	}
}
