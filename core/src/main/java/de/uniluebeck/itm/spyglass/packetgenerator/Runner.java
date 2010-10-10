/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packetgenerator;

import java.io.File;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * Main Class.
 * 
 * @author dariush
 * 
 */
public class Runner {
	private static Logger log = SpyglassLoggerFactory.getLogger(Runner.class);

	/**
	 * Start this generator.
	 * 
	 * 
	 * @param args
	 *            the path to an XML Config.
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length != 1) {
			throw new IllegalArgumentException("Expected exactly one parameter - a filename.");
		}

		final File configFile = new File(args[0]);

		log.debug("Reading config from file: " + configFile);

		if (!configFile.isFile()) {
			throw new RuntimeException("Can't find config file '" + configFile + "'");
		}

		PacketGenerator generator = null;
		final Serializer serializer = new Persister();
		generator = serializer.read(PacketGenerator.class, configFile);

		if (generator == null) {
			throw new RuntimeException("Could not load configuration.");
		}

		try {
			generator.run();
		} catch (final Exception e) {
			generator.shutdown();
			log.error("Shutting down the generator", e);
		}

	}

}
