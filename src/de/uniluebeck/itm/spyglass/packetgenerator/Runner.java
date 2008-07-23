package de.uniluebeck.itm.spyglass.packetgenerator;

import java.io.File;

import org.apache.log4j.Category;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

/**
 * Main Class.
 * 
 * @author dariush
 * 
 */
public class Runner {
	private static Category log = SpyglassLogger.get(Runner.class);
	
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
		
		generator.run();
	}
	
}
