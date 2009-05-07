/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import java.io.IOException;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.SpyglassPropertyChangeSupport;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;

// --------------------------------------------------------------------------------
/**
 * Instances of this interface read and provide {@link SpyglassPacket}s from various sources
 * 
 * @author Sebastian Ebers
 * 
 */
public interface PacketReader extends SpyglassPropertyChangeSupport {

	// --------------------------------------------------------------------------------
	/**
	 * The available types of packet sources
	 */
	public enum SOURCE_TYPE {
		/** Indicates that packets are read from a file */
		FILE,
		/** Indicates that packets are read from a network interface */
		NETWORK,
		/** Indicates that the source is neither a file nor a network */
		ORTHER
	}

	// ------------------------------------------------------------------------------
	/**
	 * Initializes the packet reader.
	 * 
	 * @param spyglass
	 *            the spyglass instance
	 */
	public void init(final Spyglass spyglass);

	// --------------------------------------------------------------------------------
	/**
	 * Sets the type of the packet source
	 * 
	 * @return the type of the packet source
	 */
	public SOURCE_TYPE getSourceType();

	// --------------------------------------------------------------------------------
	/**
	 * Returns the type of the packet source
	 * 
	 * @param sourceType
	 *            the type of the packet source
	 */
	public void setSourceType(final SOURCE_TYPE sourceType);

	// --------------------------------------------------------------------------
	/**
	 * Returns a new packet, once it arrives. It will never return null, but block until it has
	 * something to return.
	 * 
	 * @exception SpyglassPacketException
	 *                if the packet to return is invalid
	 * @exception InterruptedException
	 *                if the method was interrupted while waiting on a packet.
	 * @return a new SpyGlass packet
	 * 
	 */
	public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException;

	// --------------------------------------------------------------------------------
	/**
	 * Performs a reset of all transient settings. The configuration of this object is not affected
	 * at all
	 * 
	 * @throws IOException
	 *             thrown if the resetting of the input fails
	 */
	public void reset() throws IOException;

	// --------------------------------------------------------------------------------
	/**
	 * Shuts the packet reader down.
	 * 
	 * @throws IOException
	 *             thrown if the resetting of the input fails
	 */
	public void shutdown() throws IOException;

}
