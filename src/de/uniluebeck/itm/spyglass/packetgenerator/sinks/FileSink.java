package de.uniluebeck.itm.spyglass.packetgenerator.sinks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

/**
 * File sink. Writes all packets in a file. Each line consists of a timestamp and the packet in hex,
 * seperated by a colon.
 * 
 * example:
 * 
 * 15435354311:04A21264265632365232FFAABB
 * 
 * @author dariush
 * 
 */
public class FileSink extends Sink {
	
	private static Logger log = SpyglassLogger.get(FileSink.class);
	
	/**
	 * The filename
	 */
	@Element
	private File filename;
	
	/**
	 * Should the file be appended or overwritten?
	 */
	@Element
	private boolean append;
	
	/**
	 * The writer. Will be assigned when the first packet is transmitted.
	 */
	private FileWriter writer;
	
	@Override
	public void sendPacket(final byte[] packet) throws IOException, ParseException {
		
		final long timestamp = System.currentTimeMillis();
		
		final StringBuffer buf = new StringBuffer();
		buf.append(timestamp);
		buf.append(": ");
		for (int i = 0; i < packet.length; i++) {
			buf.append(String.format("%02X", packet[i]));
		}
		buf.append("\n");
		
		writer.append(buf);
		writer.flush();
		
	}
	
	@Override
	public void init() throws Exception {
		writer = new FileWriter(filename, append);
		log.debug("Opened file " + this.filename);
	}
	
	@Override
	public void shutdown() {
		try {
			writer.flush();
			this.writer.close();
			log.debug("Closed file " + this.filename);
		} catch (final IOException e) {
			//
		}
	}
	
}
