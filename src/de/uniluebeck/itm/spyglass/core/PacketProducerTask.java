/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.Tools;

//------------------------------------------------------------------------------
// --
/**
 * This class is a Runnable that represents a background task for producing
 * Packet objects. It is usually only be used by the <code>Spyglass</code> class
 * to read new Packet objects from a given gateway, e.g. a FileReaderGateway
 * though a PacketReader object. The PacketProducerTask uses the PacketReader
 * object to get new Packets and put them into a Packet-Cache (
 * <code>packetCache</code> member of the Spyglass class). This thread stop when
 * the <code>visualizationRunning</code> member of the Spyglass class is set to
 * false.
 */
public class PacketProducerTask implements Runnable {
	private final Category log = SpyglassLogger.get(PacketProducerTask.class);
	
	// private Deque<Packet> packetCache = null;
	
	private PacketReader packetReader = null;
	
	private Spyglass spyglass = null;
	
	private final long perPacketDelayMs;
	
	private final long initialDelayMs;
	
	// -------------------------------------------------------------------------
	/**
	 * Constructor.
	 * 
	 * @param spyglass
	 */
	public PacketProducerTask(final Spyglass spyglass, final long initialDelayMs, final long perPacketDelayMs) {
		this.spyglass = spyglass;
		this.perPacketDelayMs = perPacketDelayMs;
		this.initialDelayMs = initialDelayMs;
		
		// packetCache = spyglass.getPacketCache();
		packetReader = spyglass.getPacketReader();
		
		log.debug("New producer task.");
	}
	
	// -------------------------------------------------------------------------
	/**
	 * The run() method uses a PacketReader to get the next Packet in line and
	 * pushes that packet into the packet cache. If the PacketReader returns
	 * null, there are no more packets and the run() method returns.
	 */
	public void run() {
		Packet packet = null;
		log.debug("Producer task staring.");
		
		Tools.sleep(initialDelayMs);
		
		while (spyglass.isVisualizationRunning()) {
			try {
				if ((packet = packetReader.getNextPacket()) != null) {
					if (!spyglass.isVisualizationRunning()) {
						break;
					}
					
					if (log.isDebugEnabled()) {
						log.debug("Added packet: " + packet);
					}
					
					// packetCache.push(packet);
					spyglass.getInfoDispatcher().dispatchPacket(packet);
				}
			} catch (final SpyglassPacketException e) {
				log.error("Could not receive a packet from the packetReader.", e);
			}
			
			Tools.sleep(perPacketDelayMs);
		}
		
		log.debug("PacketProducerTask ended. Done.");
	}
}
