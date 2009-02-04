/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packet;

import java.io.IOException;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;

// ------------------------------------------------------------------------------
/**
 * Abstract class for all PacketReader implementations. A PacketReader must provide the next packet
 * with the <code>getNextPacket()</code> method.
 * 
 * @author Timo Rumland
 */
public abstract class PacketReader extends PropertyBean {

	@Element(name = "gateway", required = false)
	private Gateway gateway = null;

	/**
	 * Indicates whether the packets to be provided should be read from a file or to be forwarded
	 * from iShell
	 */
	protected boolean readFromFile = true;

	/**
	 * Delay between
	 */
	@Element(name = "delayMillies", required = false)
	protected int delayMillies = 1000;
	
	protected PacketFactory factory;

	protected PacketReader() {
		this.factory = null;
	}
	
	public void init(final Spyglass spyglass) {
		this.factory = new PacketFactory(spyglass);
	}

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
	public abstract SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException;

	// --------------------------------------------------------------------------
	/**
	 * Returns the gateway which offers an input stream to the packet data.
	 * 
	 * @return the gateway which offers an input stream to the packet data
	 */
	public Gateway getGateway() {
		if (gateway == null) {
			return null;
		}
		synchronized (gateway) {
			return gateway;
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the gateway which offers an input stream to the packet data
	 * 
	 * @param gateway
	 *            the gateway which offers an input stream to the packet data
	 */
	public void setGateway(final Gateway gateway) {
		if (this.gateway == null) {
			this.gateway = gateway;
		} else {
			synchronized (this.gateway) {
				this.gateway = gateway;
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the delayMillies
	 */
	public int getDelayMillies() {
		return delayMillies;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param delayMillies
	 *            the delayMillies to set
	 */
	public void setDelayMillies(final int delayMillies) {
		final int oldMillies = this.delayMillies;
		this.delayMillies = delayMillies;
		firePropertyChange("delayMillies", oldMillies, delayMillies);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Performs a reset of all transient settings. The configuration of this object is not affected
	 * at all
	 * 
	 * @throws IOException
	 *             thrown if the resetting of the input fails
	 */
	public abstract void reset() throws IOException;

	// --------------------------------------------------------------------------------
	/**
	 * Returns if a file is used as input.
	 * 
	 * @return <code>true</code> if the input is a file, <code>false</code> otherwise
	 */
	public boolean isReadFromFile() {
		return readFromFile;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets an indicator which indicates if a file is used as input
	 * 
	 * @param readFromFile
	 *            indicates whether a file is to be used as input
	 * @return <code>true</code> if the input is a file, <code>false</code> otherwise
	 */
	public boolean setReadFromFile(final boolean readFromFile) {
		return (this.readFromFile = readFromFile);
	}

}