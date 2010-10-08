/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;
import org.eclipse.jface.window.Window.IExceptionHandler;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Defines a Exception Handler which can be set as a global handler and will be called if an
 * exception happens in the event loop.
 * 
 * @author Sebastian Ebers
 * 
 */
public class SpyglassExceptionHandler implements IExceptionHandler, UncaughtExceptionHandler {

	private static final Logger log = SpyglassLoggerFactory.getLogger(SpyglassExceptionHandler.class);

	// --------------------------------------------------------------------------------
	@Override
	public void handleException(final Throwable t) {
		log.error("An unexpected exception occured", t);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		log.error("An unexpected exception occured", e);
	}

}
