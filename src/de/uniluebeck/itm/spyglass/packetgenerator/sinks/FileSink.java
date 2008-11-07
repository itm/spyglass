package de.uniluebeck.itm.spyglass.packetgenerator.sinks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * File sink. Writes all packets in a file.
 * 
 * @author Dariush Forouher
 * @author Sebastian Ebers
 */
public class FileSink extends Sink {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(FileSink.class);
	
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
	private FileOutputStream writer;
	
	@Override
	public void sendPacket(final byte[] packet) throws IOException, ParseException {
		
		if (packet != null) {
			writer.write(packet.length);
			writer.write(packet);
			writer.flush();
		}
		
	}
	
	@Override
	public void init() throws Exception {
		writer = new FileOutputStream(filename, append);
		log.debug("Opened file " + this.filename);
	}
	
	@Override
	public void shutdown() {
		try {
			writer.flush();
			writer.close();
			writer = null;
			log.debug("Closed file " + filename);
		} catch (final IOException e) {
			log.error(e, e);
		}
	}
	
}
