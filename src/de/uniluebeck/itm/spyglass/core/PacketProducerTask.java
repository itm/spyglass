/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.apache.log4j.Logger;

import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;

// ------------------------------------------------------------------------------
// --
/**
 * This class is a Runnable that represents a background task for producing Packet objects. It is
 * usually only be used by the <code>Spyglass</code> class to read new Packet objects from a given
 * gateway, e.g. a FileReaderGateway though a PacketReader object. The PacketProducerTask uses the
 * PacketReader object to get new Packets and put them into a Packet-Cache (
 * <code>packetCache</code> member of the Spyglass class). This thread stop when the
 * <code>visualizationRunning</code> member of the Spyglass class is set to false.
 * 
 * @author Dariush Forouher
 * @author Sebastian Ebers
 */
public class PacketProducerTask implements Runnable {
	private final Logger log = SpyglassLoggerFactory.getLogger(PacketProducerTask.class);

	private PacketReader packetReader = null;

	private final Spyglass spyglass;

	private long initialDelayMs;

	private Boolean paused = false;

	/** Object to securely access the configuration variables */
	private Object mutex = new Object();

	// -------------------------------------------------------------------------
	/**
	 * Constructor.
	 * 
	 * @param spyglass
	 *            the Spyglass current instance
	 * @param initialDelayMillis
	 *            the time in milliseconds which has to elapse before producing a new packet
	 */
	public PacketProducerTask(final Spyglass spyglass, final long initialDelayMillis) {
		this.spyglass = spyglass;
		this.initialDelayMs = initialDelayMillis;

		// packetCache = spyglass.getPacketCache();
		packetReader = spyglass.getPacketReader();

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener(new PropertyChangeListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("generalSettings")) {

					synchronized (mutex) {
						initialDelayMs = ((GeneralSettingsXMLConfig) evt.getNewValue()).getPacketDeliveryInitialDelay();
					}

				} else if (evt.getPropertyName().equals("packetReader")) {
					setPacketReader((PacketReader) evt.getNewValue());
				}

			}
		});

		log.debug("New producer task.");
	}

	// -------------------------------------------------------------------------
	/**
	 * The run() method uses a PacketReader to get the next Packet in line and pushes that packet
	 * into the packet cache. If the PacketReader returns null, there are no more packets and the
	 * run() method returns.
	 */
	public void run() {
		SpyglassPacket packet = null;
		log.debug("Producer task staring.");

		synchronized (mutex) {
			try {
				Thread.sleep(initialDelayMs);
			} catch (final InterruptedException e) {
				log.debug("PacketReader has been interrupted, shutting down.");
				Thread.currentThread().interrupt();
			}
		}

		while (!Thread.currentThread().isInterrupted()) {

			try {
				synchronized (paused) {
					if (paused) {
						paused.wait();
					}
				}

				// wait for the next packet to arrive
				packet = getPacketReader().getNextPacket();

				if (packet == null) {
					log
							.warn("Received a null packet from the PacketReader which will be skipped! If this occured in case of a reset, everyting is fine");
					continue;
				}

				spyglass.getPacketDispatcher().dispatchPacket(packet);

			} catch (final InterruptedException e) {
				log.debug("PacketReader has been interrupted, shutting down.");
				Thread.currentThread().interrupt();
			} catch (final SpyglassPacketException e) {
				log.error("Could not receive a packet from the packetReader.", e);
			}

		}

		log.debug("PacketProducerTask ended. Done.");
	}

	// --------------------------------------------------------------------------------
	/**
	 * Enables or disables the pause mode
	 * 
	 * @param paused
	 *            indicates whether the pause mode is to be enabled
	 */
	public void setPaused(final boolean paused) {
		synchronized (this.paused) {
			this.paused.notifyAll();
			this.paused = paused;
			log.debug("Set Paused to " + this.paused);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the pause mode is enabled or disabled
	 * 
	 * @return whether the pause mode is enabled or disabled
	 */
	public boolean getPaused() {
		synchronized (paused) {
			return paused;
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the currently active packet reader instance
	 * 
	 * @return the currently active packet reader instance
	 */
	private PacketReader getPacketReader() {
		synchronized (mutex) {
			return packetReader;
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Activates a new packet reader instance
	 * 
	 * @param packetReader
	 *            a new packet reader instance
	 */
	private void setPacketReader(final PacketReader packetReader) {
		synchronized (mutex) {

			if (this.packetReader != null) {
				try {
					// the previous packet reader has to be reseted since it may be dreaming of the
					// arrival of a new packet.
					this.packetReader.reset();
				} catch (final IOException e) {
					log.error(e, e);
				}
			}

			this.packetReader = packetReader;
		}
	}

}
