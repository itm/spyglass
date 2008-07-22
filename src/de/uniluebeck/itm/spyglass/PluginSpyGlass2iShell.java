/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass;

import ishell.device.MessagePacket;
import ishell.plugins.Plugin;
import ishell.util.IconTheme;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Category;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.core.SpyglassConfiguration;
import de.uniluebeck.itm.spyglass.gui.UIController;
import de.uniluebeck.itm.spyglass.gui.actions.Actions;
import de.uniluebeck.itm.spyglass.gui.view.AppWindow;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

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
public class PluginSpyGlass2iShell extends Plugin {
	private static Category log = SpyglassLogger.get(PluginSpyGlass2iShell.class);
	
	private static final int SPYGLASS_PACKET_TYPE = 145;
	
	private Composite container;
	
	private Spyglass spyglass = null;
	
	private SpyglassConfiguration config;
	
	private final Deque<de.uniluebeck.itm.spyglass.packet.Packet> queue = new ArrayDeque<de.uniluebeck.itm.spyglass.packet.Packet>(50);
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	class PluginAction extends Action {
		private final de.uniluebeck.itm.spyglass.plugin.Plugin plugin;
		
		// ----------------------------------------------------------------------
		// ----------
		/**
		 * 
		 */
		public PluginAction(final de.uniluebeck.itm.spyglass.plugin.Plugin plugin) {
			super();
			this.plugin = plugin;
			
			setText("Reset");
			setToolTipText("Clear");
			setImageDescriptor(IconTheme.lookupDescriptor("edit-clear"));
		}
		
		// ----------------------------------------------------------------------
		// ----------
		/**
		 * 
		 */
		@Override
		public void run() {
			log.debug("Ich wurde geklickt");
			
			if (config != null) {
				for (final de.uniluebeck.itm.spyglass.plugin.Plugin p : config.getPluginManager().getActivePlugins()) {
					p.reset();
				}
				
			}
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
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
		
		final ConfigStore cs = new ConfigStore(true);
		// Create the configuration for SpyGlass
		config = cs.getSpyglassConfig();
		// config.setFps(5);
		// config.setPacketDeliveryInitialDelay(500);
		// config.setPacketDeliveryDelay(10);
		// config.setCanvas(new Canvas2D());
		config.setPacketReader(new PacketReader() {
			
			@Override
			public de.uniluebeck.itm.spyglass.packet.Packet getNextPacket() {
				synchronized (queue) {
					return queue.pollLast();
				}
			}
		});
		/*
		 * DagstuhlNodePainter dagstuhlPlugin = new DagstuhlNodePainter();
		 * DagstuhlConnectivityPainter dagstuhlConnectivityPainter = new
		 * DagstuhlConnectivityPainter(); DagstuhlRoutePainter dagstuhlRoutePainter = new
		 * DagstuhlRoutePainter();
		 */
		// config.setPluginManager(new SpyGlass2iShellPluginManager(this));
		/*
		 * config.getPluginManager().addPlugin(dagstuhlRoutePainter);
		 * config.getPluginManager().addPlugin(dagstuhlConnectivityPainter);
		 * config.getPluginManager().addPlugin(dagstuhlPlugin);
		 */
		// config.getPluginManager().addPlugin(new FlegsensNodePainterPlugin());
		// ConfigNodePositioner cnp = new ConfigNodePositioner();
		// config.getPluginManager().addPlugin(cnp);
		// config.setNodePositioner(cnp);
		// config.setNodePositioner(new RandomNodePositioner()); // TODO
		// Add Toolbar Actions
		addToolBarAction(Actions.PLAY_SELECT_INPUT);
		addToolBarAction(Actions.PLAY_PLAY_PAUSE);
		addToolBarAction(Actions.PLAY_RESET);
		addToolBarAction(Actions.RECORD_SELECT_OUTPUT);
		addToolBarAction(Actions.RECORD_RECORD);
		addToolBarAction(Actions.ZOOM_IN);
		addToolBarAction(Actions.ZOOM_OUT);
		addToolBarAction(Actions.ZOOM_COMPLETE_MAP);
		addToolBarAction(Actions.OPEN_PREFERENCES);
		
		// Application objects
		final AppWindow appWindow = new AppWindow(container.getDisplay(), container);
		spyglass = new Spyglass(true, config);
		new UIController(spyglass, appWindow);
		
		// Start visualization
		spyglass.setVisualizationRunning(false);
		spyglass.start();
		
		return new int[] { SPYGLASS_PACKET_TYPE };
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public void receivePacket(final MessagePacket packet) {
		if (isPaused()) {
			return;
		}
		
		final de.uniluebeck.itm.spyglass.packet.Packet spyglassPacket = new de.uniluebeck.itm.spyglass.packet.Packet();
		spyglassPacket.setContent(packet.getContent());
		log.debug("Received Packet in spyglass from ishell: " + spyglassPacket);
		synchronized (queue) {
			queue.push(spyglassPacket);
		}
	}
	
	// --------------------------------------------------------------------------
	// ------
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
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public String getName() {
		return "SpyGlass";
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return getName() + " is a visualization framework for binary data packets arriving from the WSN";
	}
	
}
