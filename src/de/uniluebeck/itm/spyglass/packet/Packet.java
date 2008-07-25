/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

// ------------------------------------------------------------------------------
// --
/**
 * The class represents a data packet. Every packet has at least a unique id and a position.
 */
public class Packet {
	private int packetID = 0;
	
	private byte[] content;
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public int getPacketId() {
		return packetID;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public void setId(final int id) {
		this.packetID = id;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public byte[] getContent() {
		return content;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	public void setContent(final byte[] content) {
		this.content = content;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + packetID;
		
		return result;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final Packet other = (Packet) obj;
		
		if (packetID != other.packetID) {
			return false;
		}
		
		return true;
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Packet [id=" + packetID + "]";
	}
	
}
