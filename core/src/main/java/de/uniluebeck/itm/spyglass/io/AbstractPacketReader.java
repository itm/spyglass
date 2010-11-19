/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassConfiguration;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.packet.PacketFactory;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;

// ------------------------------------------------------------------------------
/**
 * Abstract super class for PacketReader implementations.<br>
 * Instances of the type PacketReader read packets from different sources including e.g. files.
 * 
 * @author Timo Rumland
 * @author Sebastian Ebers
 */
public abstract class AbstractPacketReader extends PropertyBean implements PacketReader {

	// ------------------------------------------------------------------------------
	/**
	 * Delay between delivering two packets
	 */
	@Element(name = "delayMillies", required = false)
	protected int delayMillies = 0;

	// ------------------------------------------------------------------------------
	/** A factory creating packets */
	protected PacketFactory factory;

	/** The type of the packet source */
	private SOURCE_TYPE sourceType = SOURCE_TYPE.FILE;

	// ------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	protected AbstractPacketReader() {
		this.factory = null;
	}

	// ------------------------------------------------------------------------------
	/**
	 * Initializes the packet reader.
	 * 
	 * @param spyglass
	 *            the spyglass instance
	 */
	public void init(final Spyglass spyglass) {
		this.factory = new PacketFactory(spyglass);
        initConfig(spyglass);
	}

    /**
     * Initalization for WebService-Subclasses (JaxWs collides with init-Method)
     * @param spyglass
     */
    protected void initConfig(final Spyglass spyglass){

    }

	// --------------------------------------------------------------------------------
	/**
	 * @return the delayMillies
	 */
	public int getDelayMillies() {
		return delayMillies;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param delayMillies
	 *            the delayMillies to set
	 */
	public void setDelayMillies(final int delayMillies) {
		final int oldMillies = this.delayMillies;
		this.delayMillies = delayMillies;
		firePropertyChange("delayMillies", oldMillies, delayMillies);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Performs a reset of all transient settings. The configuration of this object is not affected
	 * at all
	 * 
	 * @throws IOException
	 *             thrown if the resetting of the input fails
	 */
	public abstract void reset() throws IOException;

	// --------------------------------------------------------------------------------
	/**
	 * Shuts the packet reader down.
	 * 
	 * @throws IOException
	 *             thrown if the resetting of the input fails
	 */
	public abstract void shutdown() throws IOException;

	// --------------------------------------------------------------------------------
	/**
	 * Sets the type of the packet source
	 * 
	 * @return the type of the packet source
	 */
	public SOURCE_TYPE getSourceType() {
		return sourceType;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the type of the packet source
	 * 
	 * @param sourceType
	 *            the type of the packet source
	 */
	public void setSourceType(final SOURCE_TYPE sourceType) {
		this.sourceType = sourceType;
	}

	// --------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------
	/**
	 * Instances of this class are used to adjust the time which has to elapse between playback
	 * packets according to their time stamps and user defined minimum delays. A time scale factor
	 * may be set up to compress or stretch the time to be waited because of the packets' time stamp
	 * differences - the configured minimum delay time remains untouched.
	 * 
	 * @author Sebastian Ebers
	 * 
	 */
	public static class DelayModule implements PropertyChangeListener {
		// --------------------------------------------------------------------------------
		/** Object to secure packet reading and manipulations at the input gateway */
		private Object mutex = new Object();

		// ----------------------------------------------------------------
		/** The time stamp of the last packed delivery when reading from a file */
		private long lastPlaybackPacketTimestamp = -1;

		// ----------------------------------------------------------------
		/** The time stamp of the last packed read from a file */
		private long lastPlaybackPacketDeliveryTimestamp = -1;

		// ----------------------------------------------------------------
		/** Scales the time difference of two successive packets */
		private volatile float timeScale = 1;

		// ----------------------------------------------------------------
		/** The minimum amount of milliseconds to be waited before delivering the next packet */
		private volatile int minDelayMillies = 0;

		private SpyglassConfiguration config;

		// --------------------------------------------------------------------------------
		/**
		 * Constructor setting up listeners for changes of the configured time scale factor and
		 * delay values
		 * 
		 * @param mutex
		 *            the object which is used for delay
		 * @param config
		 *            the currently active configuration object
		 * @see Object#wait(long)
		 */
		public DelayModule(final Object mutex, final SpyglassConfiguration config) {
			this.mutex = mutex;
			if (config.getGeneralSettings() != null) {
				timeScale = config.getGeneralSettings().getTimeScale();
			}

			this.config = config;
			config.getGeneralSettings().addPropertyChangeListener(this);
			config.getPacketReader().addPropertyChangeListener(this);
			config.addPropertyChangeListener(this);

		}

		// --------------------------------------------------------------------------------
		/**
		 * Waits until either the configured minimum delay time or the time difference between the
		 * the previous and the current packet's time stamps is elapsed.<br>
		 * Note that according to the user defined time scale value, the time difference between the
		 * previously sent packet and the currently processed one might be compressed, stretched or
		 * left as is. Anyway, the configured minimum delay time will remain untouched from the time
		 * scale factor!<br>
		 * To prevent too long delay times, a packet's time stamp is ignored when it is smaller than
		 * the previous packet one's.
		 * 
		 * @param packet
		 *            the packet to be delayed
		 * @throws InterruptedException
		 */
		public void delay(final SpyglassPacket packet) throws InterruptedException {
			synchronized (mutex) {
				// wait either for the configured delay or the time stamp difference between two
				// packets to elapse before forwarding the packet
				final long sleepMillis = determineDelay(packet, lastPlaybackPacketDeliveryTimestamp);
				if (sleepMillis > 0) {
					mutex.wait(sleepMillis);
				}
				lastPlaybackPacketDeliveryTimestamp = System.currentTimeMillis();
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Determines the time to wait until either the configured minimum delay time or the time
		 * difference between the the previous and the current packet's time stamps is elapsed.<br>
		 * Note that according to the user defined minimum time scale value, the time difference
		 * between the previously sent packet and the currently processed one might be compressed,
		 * stretched or left as is.<br>
		 * To prevent too long delay times, a packet's time stamp is ignored when it is smaller than
		 * the previous packet one's.
		 * 
		 * @param packet
		 *            the packet to be delayed
		 * @param lastPlaybackPacketDeliveryTimestamp
		 *            the time stamp of the previous packets delivery
		 * @return the time (in milliseconds) to delay
		 */
		protected long determineDelay(final SpyglassPacket packet, final long lastPlaybackPacketDeliveryTimestamp) {

			long sleepMillis = 0;
			long currentPacketTimestamp = packet.getTime().getMillis();

			if ((lastPlaybackPacketDeliveryTimestamp != -1) && (lastPlaybackPacketTimestamp != -1)) {

				// never go back in time. Otherwise one packet with a corrupt time stamp might delay
				// all following ones.
				if (lastPlaybackPacketTimestamp > currentPacketTimestamp) {
					currentPacketTimestamp = lastPlaybackPacketTimestamp;
				}

				// this is the time difference between the packets according to their time stamp
				// considering the time scale value
				final double packetDiff = (currentPacketTimestamp - lastPlaybackPacketTimestamp) / timeScale;

				// this time elapsed due to the time used for processing the packets
				final double alreadyWaited = System.currentTimeMillis() - lastPlaybackPacketDeliveryTimestamp;

				// this is the time which has to pass before the packet is actually forwarded
				sleepMillis = (packetDiff > minDelayMillies) ? (long) (packetDiff - alreadyWaited) : (long) (minDelayMillies - alreadyWaited);
			}

			lastPlaybackPacketTimestamp = currentPacketTimestamp;
			return sleepMillis > 0 ? sleepMillis : 0;

		}

		// --------------------------------------------------------------------------------
		/**
		 * Resets the module's time stamp values used to coordinate the packets.<br>
		 * The configured delay and time scale factor remain untouched.
		 */
		public void reset() {
			lastPlaybackPacketDeliveryTimestamp = -1;
			lastPlaybackPacketTimestamp = -1;
		}

		// --------------------------------------------------------------------------------
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("timeScale")) {
				timeScale = (Float) evt.getNewValue();
			}
			if (evt.getPropertyName().equals("delayMillies")) {
				minDelayMillies = (Integer) evt.getNewValue();
			}
			if (evt.getPropertyName().equals("packetReader")) {
				((PacketReader) evt.getOldValue()).removePropertyChangeListener(this);
				if (evt.getNewValue() instanceof AbstractPacketReader) {
					((PacketReader) evt.getNewValue()).addPropertyChangeListener(this);
					minDelayMillies = ((AbstractPacketReader) evt.getNewValue()).getDelayMillies();
				}
			}

			if (evt.getPropertyName().equals("generalSettings")) {
				((GeneralSettingsXMLConfig) evt.getOldValue()).removePropertyChangeListener(this);
				((GeneralSettingsXMLConfig) evt.getNewValue()).addPropertyChangeListener(this);
				timeScale = ((GeneralSettingsXMLConfig) evt.getNewValue()).getTimeScale();

			}
		}

		/**
		 * Shuts the module down
		 */
		public void shutdown() {
			config.getGeneralSettings().removePropertyChangeListener(this);
			config.getPacketReader().removePropertyChangeListener(this);
			config.removePropertyChangeListener(this);
			reset();
		}

	}

}