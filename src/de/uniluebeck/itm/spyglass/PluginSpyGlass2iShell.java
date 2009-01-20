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
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.actions.OpenPreferencesAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlayPlayPauseAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlayResetAction;
import de.uniluebeck.itm.spyglass.gui.actions.PlaySelectInputAction;
import de.uniluebeck.itm.spyglass.gui.actions.RecordRecordAction;
import de.uniluebeck.itm.spyglass.gui.actions.RecordSelectOutputAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomCompleteMapAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomInAction;
import de.uniluebeck.itm.spyglass.gui.actions.ZoomOutAction;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.packet.IShellToSpyGlassPacketBroker;
import de.uniluebeck.itm.spyglass.packet.PacketFactory;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

// ------------------------------------------------------------------------------
// --
/**
 * To use this plug-in in iShell, you need to add two option to your iShell configuration file
 * (typically ishell.properties).
 * 
 * The first one determines the additional classpath parameters in where this plug-in and its
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

	private Composite container;

	private Spyglass spyglass = null;

	private IShellToSpyGlassPacketBroker packetBroker;

	// --------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public int[] init() {
		// iShell init (called on each plug-in start)

		final CTabItem tabItem = getTabItem(getName());
		tabItem.setToolTipText(getName());
		tabItem.setImage(IconTheme.lookup("system-search"));

		container = this.getTabContainer(true);
		container.setLayout(new FillLayout());
		container.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(final ControlEvent e) {
				// Nothing to do
			}

			@Override
			public void controlResized(final ControlEvent e) {
				log.debug("Control resized, received event: " + e);
				// TODO Adapt zoom
			}
		});

		// create Model
		try {
			spyglass = new Spyglass();
		} catch (final IOException e1) {
			// TODO What should we do now?
			log.error("", e1);
			return new int[] {};
		}

		// save the packet broker for later
		// XXX: can we really assume this cast?
		packetBroker = (IShellToSpyGlassPacketBroker) spyglass.getPacketReader();

		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("packetReader")) {
					packetBroker = (IShellToSpyGlassPacketBroker) spyglass.getConfigStore().getSpyglassConfig().getPacketReader();
				}
			}
		});

		// create View
		final AppWindow appWindow = new AppWindow(spyglass, container);

		// create Control
		new UIController(spyglass, appWindow);

		// Add Toolbar Actions
		addToolBarAction(new PlaySelectInputAction(container.getShell(), spyglass));
		addToolBarAction(new PlayPlayPauseAction(spyglass));
		addToolBarAction(new PlayResetAction(spyglass));
		addToolBarAction(new RecordSelectOutputAction(spyglass));
		addToolBarAction(new RecordRecordAction(spyglass));
		addToolBarAction(new ZoomInAction(appWindow.getGui().getDrawingArea()));
		addToolBarAction(new ZoomOutAction(appWindow.getGui().getDrawingArea()));
		addToolBarAction(new ZoomCompleteMapAction(spyglass, appWindow.getGui().getDrawingArea()));
		addToolBarAction(new OpenPreferencesAction(container.getShell(), spyglass));
		// addToolBarAction(new LoadConfigurationAction(spyglass));
		// addToolBarAction(new StoreConfigurationAction(spyglass));

		// Start Spyglass
		spyglass.start();

		return new int[] { SPYGLASS_PACKET_TYPE };
	}

	// --------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void receivePacket(final MessagePacket packet) {
		log.debug("receivePacket called from iShell");

		if (isPaused()) {
			return;
		}
		SpyglassPacket spyglassPacket = null;
		try {
			spyglassPacket = PacketFactory.createInstance(packet.getContent());
		} catch (final SpyglassPacketException e) {
			log.error("Illegal Packet, could not deserialize it.", e);
			return;
		}

		log.debug("Received Packet in Spyglass from iShell: " + spyglassPacket);
		// spyglass.getPacketRecorder().handlePacket(spyglassPacket);
		packetBroker.push(spyglassPacket);

	}

	// --------------------------------------------------------------------------
	/**
	 * TODO: shutdown of the other thread doesn't work correctly...
	 */
	@Override
	public void shutdown() {

		// if SpyGlass was not started correctly it might still be null
		if (spyglass != null) {
			spyglass.shutdown();
		}

		spyglass = null;
		container = null;
		log.info("SpyGlass end. Done.");
	}

	// --------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String getName() {
		return "SpyGlass";
	}

	// --------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return getName() + " is a visualization framework for binary data packets arriving from the WSN";
	}

}
