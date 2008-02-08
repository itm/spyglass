/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass;

import ishell.device.MessagePacket;
import ishell.plugins.Plugin;
import ishell.util.IconTheme;
import ishell.util.Logging;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Category;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import de.bsi.flegsens.RandomNodePositioner;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassConfiguration;
import de.uniluebeck.itm.spyglass.drawing.Canvas2D;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.packet.PacketReader;

// --------------------------------------------------------------------------------
/** To use this plug-in in iShell, you need to add two option to your iShell configuration file (typically
 * ishell.properties).
 * 
 * The first one determines the additional classpath parameters in where this plug-in and its libraries 
 * are located. Please note that you must escape any backslash and colon with a backslash character. An example
 * of how this could look like is shown in the following line: 
 * <pre>
 * plugin_classpath=C\:\\work\\java\\spyglass-lean\\bin\\eclipse;C\:\\work\\java\\spyglass-lean\\lib\\simple-xml-1.6.jar
 * </pre>
 * 
 * The second parameter denotes the fully classified class name of the plug-in. This should remain unchanged and
 * look like the following:
 * <pre>
 * plugin_classes=de.uniluebeck.itm.spyglass.PluginSpyGlass2iShell
 * </pre>
 *
 * For further information, please refer to the iShell manual.
 */
public class PluginSpyGlass2iShell extends Plugin {
	private static Category log = Logging.get(PluginSpyGlass2iShell.class);

	private static final int SPYGLASS_PACKET_TYPE = 145;

	private Composite container;

	private Spyglass spyglass = null;

	private Deque<de.uniluebeck.itm.spyglass.packet.Packet> queue = new ArrayDeque<de.uniluebeck.itm.spyglass.packet.Packet>(50);

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public int[] init() {
		// iShell init (called on each plug-in start)
		CTabItem tabItem = getTabItem(getName());
		tabItem.setToolTipText(getName());
		tabItem.setImage(IconTheme.lookup("system-search"));
		container = this.getTabContainer(true);

		// Create the configuration for SpyGlass
		SpyglassConfiguration config = new SpyglassConfiguration();
		config.setFps(25);
		config.setPacketDeliveryInitialDelay(500);
		config.setPacketDeliveryDelay(0);
		config.setCanvas(new Canvas2D());
		config.setPacketReader(new PacketReader() {

			@Override
			public de.uniluebeck.itm.spyglass.packet.Packet getNextPacket() {
				synchronized (queue) {
					return queue.pollLast();
				}
			}
		});

		config.setPluginManager(new SpyGlass2iShellPluginManager(this));
		config.setNodePositioner(new RandomNodePositioner());

		// Application objects
		AppWindow appWindow = new AppWindow(container.getDisplay(), container);
		spyglass = new Spyglass(config);
		new UIController(spyglass, appWindow);

		// Start visualization
		spyglass.start();

		return new int[] { SPYGLASS_PACKET_TYPE };
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void receivePacket(MessagePacket packet) {
		de.uniluebeck.itm.spyglass.packet.Packet spyglassPacket = new de.uniluebeck.itm.spyglass.packet.Packet();
		spyglassPacket.setContent(packet.getContent());
		synchronized (queue) {
			queue.push(spyglassPacket);
		}
	}
	

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void shutdown() {
		spyglass.setVisualizationRunning(false);
		container.dispose();
		spyglass = null;
		container = null;
		log.info("SpyGlass end. Done.");
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String getName() {
		return "SpyGlass";
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return getName() + " is a visualization framework for binary data packets arriving from the WSN";
	}

}
