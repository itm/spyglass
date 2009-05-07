/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.gateway.Gateway;

// --------------------------------------------------------------------------------
/**
 * Instances of this class read packages from a {@link Gateway}
 * 
 * @author Sebastian Ebers
 */
public abstract class AbstractGatewayPacketReader extends AbstractPacketReader implements GatewayPacketReader {

	@Element(name = "gateway", required = false)
	private Gateway gateway = null;

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
}
