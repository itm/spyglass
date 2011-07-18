/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.util;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Instances of this class create and provide instances of classes which can to be used for logging different kinds of
 * events and message types.
 *
 * @author Sebastian Ebers
 */
public class SpyglassLoggerFactory {

	private static Map<String, Logger> loggerMap = new HashMap<String, Logger>();

	/**
	 * Returns an object to log messages which can be related to a certain class
	 *
	 * @param clazz - the class
	 *
	 * @return an object to log messages
	 *
	 * @see Logger
	 */
	public static Logger getLogger(final Class<?> clazz) {
		Logger logger = loggerMap.get(clazz.getName());
		if (logger == null) {
			logger = new SpyglassLogger(clazz);
			loggerMap.put(clazz.getName(), logger);
		}
		return logger;
	}

}
