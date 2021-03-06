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
 * Represents a Packet with the syntaxtype FloatListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * @author Dariush Forouher
 */
public class FloatListPacket extends SpyglassPacket {

	/**
	 * Syntaxtype of this Packet
	 */
	public static final SyntaxTypes SYNTAXTYPE = SyntaxTypes.ISENSE_SPYGLASS_PACKET_FLOAT;

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#deserialize(byte[])
	 */
	@Override
	void deserialize(final byte[] buf) throws SpyglassPacketException {
		super.deserialize(buf);
		if (getSyntaxType() != FloatListPacket.SYNTAXTYPE) {
			throw new SpyglassPacketException("Wrong Syntaxtype");
		}
		values = new float[(buf.length - SpyglassPacket.EXPECTED_PACKET_SIZE) / 4];
		for (int i = 0; (i * 4 + SpyglassPacket.EXPECTED_PACKET_SIZE) < buf.length; i++) {
			final int pos = i * 4 + SpyglassPacket.EXPECTED_PACKET_SIZE;
			values[i] = deserializeFloat(buf[pos], buf[pos + 1], buf[pos + 2], buf[pos + 3]);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Listelements
	 */
	protected float[] values = new float[0];

	// --------------------------------------------------------------------------------
	/**
	 * Returns the Listelements
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the values
	 */
	public float[] getValues() {
		return values;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the Listelements
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param values
	 *            the values to set
	 */
	public void setValues(final float[] values) {
		this.values = values;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see SpyglassPacket#toString()
	 */
	@Override
	public String toString() {
		String ret = super.toString() + ", values: ";
		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				ret = ret + ", ";
			}
			ret = ret + values[i];
		}
		return ret;
	}
}
