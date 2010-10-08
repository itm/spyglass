/*----------------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by
 * the SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License. Refer to
 * spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gateway.FileReaderGateway;
import de.uniluebeck.itm.spyglass.gateway.Gateway;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// --------------------------------------------------------------------------------
/**
 * This class implements a PacketReader which reads packets from a binary file.
 * 
 * @author Dariush Forouher
 * @author Sebastian Ebers
 */
public class ComplexPacketReader extends AbstractGatewayPacketReader {

	private static Logger log = SpyglassLoggerFactory.getLogger(ComplexPacketReader.class);

	private final Object gatewayMutex;

	private DelayModule delayModule;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ComplexPacketReader() {
		gatewayMutex = new Object();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param mutex
	 *            the mutex object to use
	 */
	public ComplexPacketReader(final Object mutex) {
		gatewayMutex = mutex;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final Spyglass spyglass) {

		super.init(spyglass);
		delayModule = new DelayModule(gatewayMutex, spyglass.getConfigStore().getSpyglassConfig());
	}

	// --------------------------------------------------------------------------------
	@Override
	public SpyglassPacket getNextPacket(final boolean block) throws SpyglassPacketException, InterruptedException {

		SpyglassPacket packet = null;
		synchronized (gatewayMutex) {

			final Gateway gw = getGateway();
			InputStream is = null;
			if ((gw != null) && ((is = gw.getInputStream()) != null)) {
				packet = getNextPacketFromInputStream(is);
				if ((packet == null) && block) {
					gatewayMutex.wait();
				}
			} else {
				log.warn("No packet could be fetched since the gateway is not completely initializey, yet. Going to sleep ...");
				if (block) {
					gatewayMutex.wait();
				}
			}

			if (packet != null) {
				delayModule.delay(packet);
			}
		}
		return packet;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the next {@link SpyglassPacket} to be found in the provided input stream
	 * 
	 * @param playbackFileReader
	 *            an input streams containing {@link SpyglassPacket}s
	 * @return the next {@link SpyglassPacket} to be found in the provided input stream
	 * @throws SpyglassPacketException
	 * @throws InterruptedException
	 */
	private SpyglassPacket getNextPacketFromInputStream(final InputStream playbackFileReader) throws SpyglassPacketException, InterruptedException {

		synchronized (gatewayMutex) {

			try {
				final int next;
				byte[] packetData;
				if (playbackFileReader == null) {
					return null;
				} else if ((next = playbackFileReader.read()) != -1) {
					packetData = new byte[next];
					playbackFileReader.read(packetData);
					return factory.createInstance(packetData);
				} else {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(null, "No more data", "The end of the playback file was reached!");
						}
					});
					return null;
				}

			} catch (final IOException e) {
				log.error("Error while reading a new packet...", e);
				return null;
			}
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public void reset() throws IOException {
		delayModule.reset();
		synchronized (gatewayMutex) {

			Gateway gw = null;
			if ((gw = getGateway()) != null) {
				if (gw instanceof FileReaderGateway) {
					((FileReaderGateway) gw).reset();
				}
			}

			gatewayMutex.notifyAll();
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public void shutdown() throws IOException {
		reset();
		delayModule.shutdown();
	}

}
