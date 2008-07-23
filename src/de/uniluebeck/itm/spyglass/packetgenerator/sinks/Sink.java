package de.uniluebeck.itm.spyglass.packetgenerator.sinks;

import org.simpleframework.xml.Root;

/**
 * A Sink takes samples and stores them to a file, sends them over the network,
 * or does other things with it.
 * 
 * @author dariush
 * 
 */
@Root
public abstract class Sink {
	
	/**
	 * Takes a packet and does something with it (depends on the implementing
	 * class)
	 * 
	 * @param packet
	 * @throws Exception
	 */
	public abstract void sendPacket(byte[] packet) throws Exception;
	
	/**
	 * Should be called before the first packet is sent. (e.g. open the file or
	 * socket)
	 */
	public abstract void init() throws Exception;
	
	/**
	 * Should be called after the last packet is sent. (e.g. close the file or
	 * socket)
	 */
	public abstract void shutdown();
}
