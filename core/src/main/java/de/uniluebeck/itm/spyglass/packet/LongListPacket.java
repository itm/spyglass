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

/**
 * Represents a generic LongListPacket
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * @author Dariush Forouher
 */
public class LongListPacket extends SpyglassPacket {

	/**
	 * Listelements
	 */
	protected long[] values = new long[0];

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

	/**
	 * Returns the Listelements
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @return the values
	 */
	public long[] getValues() {
		return values;
	}

	/**
	 * Sets the Listelements
	 * 
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @param values
	 *            the values to set
	 */
	void setValues(final long[] values) {
		this.values = values;
	}
}
