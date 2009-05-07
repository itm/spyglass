/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * Instances of this class take and provide {@link SpyglassPacket}s offering the functionality of a
 * queue.<br>
 * They are used e.g. as packet broker between iShell and Spyglass.<br>
 * iShell can push packets for Spyglass in a queue and Spyglass can read them afterwards.<br>
 * By extending the interface {@link PacketReader} Spyglass can use this broker as an ordinary
 * packet reader.
 * 
 * @author Sebastian Ebers
 * @author Dariush Forouher
 */
@Root
public class SpyGlassPacketQueue extends AbstractPacketReader {

	/**
	 * The queue where packets provided by iShell are stored
	 */
	private Deque<SpyglassPacket> queue = new ArrayDeque<SpyglassPacket>(50);

	/** Indicates whether the object should skip waiting for the arrival of a new packet */
	private AtomicBoolean skipWaiting = new AtomicBoolean(false);

	private static final Logger log = SpyglassLoggerFactory.getLogger(SpyGlassPacketQueue.class);

	/** The number of elements which are currently hold in the packet queue */
	private volatile int queuedElements;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SpyGlassPacketQueue() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param queue
	 *            the queue where packets provided by iShell are stored
	 */
	public SpyGlassPacketQueue(final Deque<SpyglassPacket> queue) {
		this.queue = queue;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Retrieves and removes the last packet of this packet queue.<br>
	 * If the packet queue is empty, the current thread will be suspended until a new packet
	 * arrives.<br>
	 * As an exception of the rule, <code>null</code> might be returned in case of an reset.
	 * 
	 * @return the tail of the packet queue, or <tt>null</tt> if the packet queue is empty
	 * @exception SpyglassPacketException
	 *                if the packet to return is invalid
	 * @exception InterruptedException
	 *                if the method was interrupted while waiting on a packet.
	 */
	@Override
	public SpyglassPacket getNextPacket() throws SpyglassPacketException, InterruptedException {

		SpyglassPacket packet = null;

		// Loop until we have a packet.
		while ((packet == null) && !skipWaiting.get()) {

			synchronized (queue) {

				// if no packet is available dream of the arrival of a new packet
				if (queue.isEmpty()) {
					queue.wait();
				}

			}
			// the queue might still be empty in case of a requested reset
			packet = queue.poll();
		}

		skipWaiting = new AtomicBoolean(false);
		if (packet != null) {
			log.debug("Number of queued elements: " + (--queuedElements));
		}

		return packet;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Inserts the element at the front of the packet queue
	 * 
	 * @param packet
	 *            the packet to push
	 * @throws NullPointerException
	 *             if the specified packet is null
	 */
	public void push(final SpyglassPacket packet) {
		synchronized (queue) {
			log.debug("Number of queued elements: " + (++queuedElements));
			log.debug("Push packet into the queue");
			queue.push(packet);
			queue.notifyAll(); // wake up all waiting threads
		}
	}

	// ------------------------------------------------------------------------------
	@Override
	public void reset() throws IOException {
		// prevent threads to be waiting for the queue forever...
		skipWaiting = new AtomicBoolean(true);
		synchronized (queue) {
			queue.clear();
			queuedElements = 0;
			queue.notifyAll();
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public void shutdown() throws IOException {
		removeAllPropertyChangeListeners();
		skipWaiting = new AtomicBoolean(true);
		synchronized (queue) {
			queue.clear();
			queuedElements = 0;
			queue.notifyAll();
		}
	}

}
