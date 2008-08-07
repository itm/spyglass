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
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
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
public class PluginSpyGlass2iShell extends ishell.plugins.Plugin {
	private static Category log = SpyglassLogger.get(PluginSpyGlass2iShell.class);
	
	private static final int SPYGLASS_PACKET_TYPE = 0x91;
	
	private Composite container;
	
	private Spyglass spyglass = null;
	
	private SpyglassConfiguration config;
	
	// private Deque<SpyglassPacket> queue = new ArrayDeque<SpyglassPacket>(50);
	
	private IShellToSpyGlassPacketBroker packetBroker;
	
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
		packetBroker = (IShellToSpyGlassPacketBroker) config.getPacketReader();
		
		// TODO
		// Application objects
		final AppWindow appWindow = new AppWindow(container.getDisplay(), container);
		spyglass = new Spyglass(true, config);
		new UIController(spyglass, appWindow);
		spyglass.getDrawingArea().setAppWindow(appWindow);
		
		// Add Toolbar Actions
		addToolBarAction(new PlaySelectInputAction());
		addToolBarAction(new PlayPlayPauseAction(spyglass));
		addToolBarAction(new PlayResetAction());
		addToolBarAction(new RecordSelectOutputAction());
		addToolBarAction(new RecordRecordAction());
		addToolBarAction(new ZoomInAction(spyglass.getDrawingArea()));
		addToolBarAction(new ZoomOutAction(spyglass.getDrawingArea()));
		addToolBarAction(new ZoomCompleteMapAction(spyglass));
		addToolBarAction(new OpenPreferencesAction(container.getShell(), spyglass));
		
		// Start visualization
		spyglass.setVisualizationRunning(true);
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
		log.debug("receivePacket called from iShell");
		
		if (isPaused()) {
			return;
		}
		final SpyglassPacket spyglassPacket = new SpyglassPacket();
		try {
			spyglassPacket.deserialize(packet.getContent());
		} catch (final SpyglassPacketException e) {
			log.error("Illegal Packet, could not deserialize it.", e);
			return;
		}
		
		log.debug("Received Packet in spyglass from ishell: " + spyglassPacket);
		
		packetBroker.push(spyglassPacket);
		
	}
	
	// --------------------------------------------------------------------------
	// ------
	/**
	 * TODO: shutdown of the other thread doesn't work correcty...
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
