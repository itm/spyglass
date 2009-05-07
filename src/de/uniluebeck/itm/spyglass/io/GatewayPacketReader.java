/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import de.uniluebeck.itm.spyglass.gateway.Gateway;

//--------------------------------------------------------------------------------
/**
 * Implementations of this interface read packages from a {@link Gateway}
 * 
 * @author Sebastian Ebers
 */
public interface GatewayPacketReader extends PacketReader {

	// --------------------------------------------------------------------------
	/**
	 * Returns the gateway which offers an input stream to the packet data.
	 * 
	 * @return the gateway which offers an input stream to the packet data
	 */
	public Gateway getGateway();

	// --------------------------------------------------------------------------
	/**
	 * Sets the gateway which offers an input stream to the packet data
	 * 
	 * @param gateway
	 *            the gateway which offers an input stream to the packet data
	 */
	public void setGateway(final Gateway gateway);

}
