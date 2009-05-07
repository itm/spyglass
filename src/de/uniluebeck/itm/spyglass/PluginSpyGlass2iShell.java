/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass;

import ishell.device.MessagePacket;
import ishell.util.IconTheme;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassExceptionHandler;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.io.SpyGlassPacketQueue;
import de.uniluebeck.itm.spyglass.packet.PacketFactory;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
/**
 * To use this plug-in in iShell, you need to add two option to your iShell configuration file
 * (typically ishell.properties).
 * 
 * The first one determines the additional class path parameters in where this plug-in and its
 * libraries are located. Please note that you must escape any backslash and colon with a backslash
 * character. An example of how this could look like is shown in the following line:
 * 
 * <pre>
 * plugin_classpath=C\:\\work\\java\\spyglass-lean\\bin\\eclipse;C\:\\work\\java\\spyglass-lean\\lib\\simple-xml-1.6.jar
 * </pre>
 * 
 * The second parameter denotes the fully classified class name of the plug-in. This should remain
 * unchanged and look like the following:
 * 
 * <pre>
 * plugin_classes = de.uniluebeck.itm.spyglass.PluginSpyGlass2iShell
 * </pre>
 * 
 * For further information, please refer to the iShell manual.
 */
public class PluginSpyGlass2iShell extends ishell.plugins.Plugin {
	private static Logger log = SpyglassLoggerFactory.getLogger(PluginSpyGlass2iShell.class);

	private static final int SPYGLASS_PACKET_TYPE = 0x91;

	private Spyglass spyglass = null;

	private PacketFactory factory;

	private UIController controller = null;

	private SpyGlassPacketQueue packetBroker;

	private AppWindow appWindow = null;

	private ToolbarHandler toolbarStuff = null;

	// --------------------------------------------------------------------------
	@Override
	public int[] init() {
		// iShell init (called on each plug-in start)

		final CTabItem tabItem = getTabItem(getName());
		tabItem.setImage(IconTheme.lookup("system-search"));

		final Composite container = this.getTabContainer(true);
		container.setLayout(new FillLayout());

		try {
			// create Model
			SpyglassEnvironment.setIShellPlugin(true);
			spyglass = new Spyglass();

			connectPacketBroker();

			// create view
			appWindow = new AppWindow(spyglass, container);

			// create Control
			controller = new UIController(spyglass, appWindow);

			// add tooltip icons
			toolbarStuff = new ToolbarHandler(getCoolBar(), spyglass, appWindow);

			// Start Spyglass
			spyglass.start();

			factory = new PacketFactory(spyglass);

			// Set an exception handler which will handle uncaught exceptions
			Window.setExceptionHandler(new SpyglassExceptionHandler());

		} catch (final Exception e) {
			log.error("Could not initialize plugin \"Spyglass\" because of an very early error.", e);

			// remove spyglass tab
			this.removeTabItem();
			this.shutdown();
			return new int[] {};
		}

		log.info("Spyglass ready.");

		return new int[] { SPYGLASS_PACKET_TYPE };

	}

	private void connectPacketBroker() throws ClassCastException {

		final String noBroker = "Even so Spyglass is used as iShell plug-in, packets provided by iShell"
				+ " cannot be used since the currently active Packet reader is not capable of receiving packets from iShell.";
		if (spyglass.getPacketReader() instanceof SpyGlassPacketQueue) {

			packetBroker = (SpyGlassPacketQueue) spyglass.getPacketReader();

			spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener(new PropertyChangeListener() {
				@SuppressWarnings("synthetic-access")
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("packetReader")) {
						if ((spyglass.getPacketReader() instanceof SpyGlassPacketQueue)) {
							packetBroker = (SpyGlassPacketQueue) spyglass.getConfigStore().getSpyglassConfig().getPacketReader();
						} else {
							log.warn(noBroker);
						}
					}
				}
			});
		} else {
			log.warn(noBroker);
		}

	}

	// --------------------------------------------------------------------------
	@Override
	public void receivePacket(final MessagePacket packet) {

		if (isPaused()) {
			return;
		}
		SpyglassPacket spyglassPacket = null;
		try {
			spyglassPacket = factory.createInstance(packet.getContent());
		} catch (final SpyglassPacketException e) {
			log.error("Illegal Packet, could not deserialize it.", e);
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Received Packet in Spyglass from iShell: " + spyglassPacket);
		}

		if (packetBroker != null) {
			packetBroker.push(spyglassPacket);
		} else {
			log.error("The packet provided by iShell was droped since no packet broker is available!");
		}

	}

	// --------------------------------------------------------------------------
	/**
	 * Shutdown all components of spyglass
	 */
	@Override
	public void shutdown() {

		log.debug("Terminating spyglass...");

		if (appWindow.getGui().isDisposed()) {
			log.debug("GUI already gone...");
		}

		// Destroy the components in the reverse direction if which they were created.

		if (controller != null) {
			controller.shutdown();
			controller = null;
		}

		if (toolbarStuff != null) {
			toolbarStuff.dispose();
			toolbarStuff = null;
		}

		if (spyglass != null) {
			spyglass.shutdown();
			spyglass = null;
		}

		try {
			if (packetBroker != null) {
				packetBroker.shutdown();
			}
		} catch (final IOException e) {
			// since the application is about to exit, there is no need to display this exception
			((SpyglassLogger) log).error("An error occured while trying to shut down the packet broker.", e, false);
		}

		log.info("SpyGlass end. Done.");
	}

	// --------------------------------------------------------------------------
	@Override
	public String getName() {
		return "SpyGlass";
	}

	// --------------------------------------------------------------------------
	@Override
	public String getDescription() {
		return getName() + " is a visualization framework for binary data packets arriving from the WSN";
	}

}
