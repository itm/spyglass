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
 * Exception, thrown if deserialize fails.
 * 
 * @author Nils Glombitza, ITM Uni Luebeck
 * 
 */
public class SpyglassPacketException extends Exception {

	private static final long serialVersionUID = 1L;

	// --------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SpyglassPacketException() {
		// nothing to do
	}

	// --------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param msg
	 *            the exception's message
	 */
	public SpyglassPacketException(final String msg) {
		super(msg);
	}

	// --------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param message
	 *            the exception's message
	 * @param cause
	 *            the exception's cause
	 */
	public SpyglassPacketException(final String message, final Throwable cause) {
		super(message, cause);
	}

	// --------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param cause
	 *            the exception's cause
	 */
	public SpyglassPacketException(final Throwable cause) {
		super(cause);
	}

}
