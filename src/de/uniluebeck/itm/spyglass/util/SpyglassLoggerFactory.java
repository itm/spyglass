/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SocketAppender;

/**
 * Instances of this class create and provide instances of classes which can to be used for logging
 * different kinds of events and message types.
 * 
 * @author Sebastian Ebers
 */
public class SpyglassLoggerFactory {

	private static long timestamp = new Date().getTime();
	private static final String fileNameLog = "." + File.separator + "logs" + File.separator + timestamp + "_messages.log";
	private static PatternLayout patternLayout = new PatternLayout("%-9d{HH:mm:ss} %-8p %-20c{1} - %m%n");
	private static PatternLayout fileLoggerLayout = patternLayout;

	private static Map<String, Logger> loggerMap = new HashMap<String, Logger>();
	private static Level loglevel = Level.DEBUG;

	/** Indicates if the logged messages have to be written to a file */
	public static boolean writeLogfile = true;

	/** Indicates if the logged messages have to be written to a socket */
	public static boolean write2Socket = false;

	/**
	 * Returns an object to log messages which can be related to a certain class
	 * 
	 * @param clazz
	 *            - the class
	 * @return an object to log messages
	 * @see Logger
	 */
	public static Logger getLogger(final Class<?> clazz) {
		Logger logger = null;
		try {
			if ((logger = loggerMap.get(clazz.getName())) == null) {
				logger = new SpyglassLogger(clazz);
				if (writeLogfile) {
					logger.addAppender(new FileAppender(fileLoggerLayout, fileNameLog));
				}
				if (write2Socket) {
					logger.addAppender(new SocketAppender("127.0.0.1", 4560));
				}
				logger.setLevel(loglevel);
				loggerMap.put(clazz.getName(), logger);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return logger;
	}

}
