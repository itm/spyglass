/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Category;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.util.Logging;

// --------------------------------------------------------------------------------
/**
 * A gateway implementation that offers access to a network/socket input stream.
 */
@Root
public class NetworkGateway implements Gateway {
	private static Category log = Logging.get(NetworkGateway.class);

	@Element
	private String hostname = "server";

	@Element
	private int port = 4711;

	private Socket socket = null;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public InputStream getInputStream() {
		try {
			// Create socket when needed
			if (socket == null)
				socket = new Socket(hostname, port);

			if (socket != null)
				return socket.getInputStream();
		} catch (UnknownHostException e) {
			log.error("Unknown host[" + hostname + "]: " + e, e);
		} catch (IOException e) {
			log.error("I/O exception on[" + hostname + ":" + port + "]: " + e, e);
		}

		return null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public String getHostname() {
		return hostname;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getPort() {
		return port;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPort(int port) {
		this.port = port;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Socket getSocket() {
		return socket;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
