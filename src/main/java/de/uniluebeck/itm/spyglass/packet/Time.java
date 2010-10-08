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
 * Represents the Time of a Spyglass Packet
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * 
 */
public class Time {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public Time() {
		// nothing to do
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param sec
	 *            Seconds
	 * @param ms
	 *            Milliseconds
	 * 
	 */
	public Time(final long sec, final int ms) {
		sec_ = sec;
		ms_ = ms;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Seconds
	 */
	long sec_;

	// --------------------------------------------------------------------------------
	/**
	 * Milliseconds
	 */
	int ms_;

	// --------------------------------------------------------------------------------
	/**
	 * @author Nils Glombitza, ITM Uni Luebeck
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + sec_ + "." + ms_;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the time in milliseconds
	 * 
	 * @author Sebastian Ebers
	 * @return the time in milliseconds
	 */
	public Long getMillis() {
		return sec_ * 1000 + ms_;
	}
}