/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Widget;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Abstract base class for all Spyglass plug-ins.<br>
 * This abstract class implements the {@link Runnable} interface to enhance the performance of
 * packet handling. When new packets arrive, they are just dropped in a queue to enable the
 * dispatcher's thread to go on. The plug-in's packet handling thread is automatically notified
 * about the arrival and starts processing the packet. Since this occurs in a thread separated from
 * the main thread a slow plug in does not slow down the whole application.
 * 
 * @author Sebastian Ebers
 */
@Root
public abstract class Plugin implements Runnable {
	
	/**
	 * Indicator of the plug-in's activation state
	 */
	private boolean isActive = true;
	
	/**
	 * The plug-in's manager (which manages all currently available plug-ins as well)
	 */
	private PluginManager pluginManager;
	
	/**
	 * The queue where packets are dropped by the packet dispatcher and which is maintained
	 * concurrently
	 */
	private ConcurrentLinkedQueue<SpyglassPacket> packetQueue = null;
	
	/**
	 * Object which is used to log different kinds of messages
	 */
	private static final Logger log = SpyglassLogger.getLogger(Plugin.class);
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor<br>
	 * If the plug-in has to administer a packet queue a new thread will be started.
	 * 
	 * @param needsPacketQueue
	 *            indicates whether or not the plug-in has to administer a packet queue
	 */
	public Plugin(final boolean needsPacketQueue) {
		if (needsPacketQueue) {
			packetQueue = new ConcurrentLinkedQueue<SpyglassPacket>();
			final Thread t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * This method handles a Packet object. Usually, a plugin creates a new DrawingObject for each
	 * packet it handles.
	 * 
	 * @param packet
	 *            The packet object to handle.
	 */
	public abstract void handlePacket(SpyglassPacket packet);
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public abstract String getName();
	
	// --------------------------------------------------------------------------------
	/**
	 * holt sich die information aus dem PluginXMLConfig-Objekt
	 */
	public final boolean isActive() {
		return isActive;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Löscht den Zustand des Plugins, z.B. QuadTree leeren, Instanzvariablen auf Default stellen
	 */
	public abstract void reset();
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the plug-in's activation state
	 * 
	 * @param isActive
	 *            indicates the plug-in's activation state
	 */
	public final void setActive(final boolean isActive) {
		this.isActive = isActive;
		if (pluginManager != null) {
			pluginManager.setPluginStatus(this, isActive);
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the facility which administers this plug-in along with others
	 * 
	 * @param pluginManager
	 *            the plug-in's manager
	 */
	public final void setPluginManager(final PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the facility which administers this plug-in along with others
	 * 
	 * @return the plug-in's manager
	 */
	public final PluginManager getPluginManager() {
		return pluginManager;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Creates and returns a widget which can be used to configure the plug-in
	 * 
	 * @param cs
	 *            the instance which is used to load and store the configuration
	 * 
	 * @return a widget which can be used to configure the plug-in
	 */
	public abstract PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> createPreferencePage(final ConfigStore cs);
	
	// --------------------------------------------------------------------------------
	/**
	 * Creates and returns a widget which can be used to configure the plug-in's type.<br>
	 * Since the type does not need to provide information about configuration options of a certain
	 * plug-in instance this method can be called in a static way.
	 * 
	 * @param parent
	 *            the parent widget
	 * @param cs
	 *            the instance which is used to load and store the configuration
	 * @return a widget which can be used to configure the plug-in's type
	 */
	public static PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> createTypePreferencePage(final Widget parent, final ConfigStore cs) {
		throw new RuntimeException("This method must only be called on subclasses and must be implemented in every instantiable subclass.");
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the plug-in's name which has to be in a human readable style
	 * 
	 * @return the plug-in's name which has to be in a human readable style
	 */
	public static String getHumanReadableName() {
		throw new RuntimeException("This method must only be called on subclasses and must be implemented by every abstract or instantiable suclass.");
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns the plug-in's configuration parameters
	 * 
	 * @return the plug-in's configuration parameters
	 */
	public abstract PluginXMLConfig getXMLConfig();
	
	// --------------------------------------------------------------------------------
	/**
	 * Handles a mouse event
	 * 
	 * @param e
	 *            the mouse event
	 * @param drawingArea
	 *            the drawing area in which the event occured
	 */
	public boolean handleEvent(final MouseEvent e, final DrawingArea drawingArea) {
		return false;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Returns <tt>true</tt> if the plug-in is visible
	 * 
	 * @return <tt>true</tt> if the plug-in is visible
	 */
	public boolean isVisible() {
		return getXMLConfig().isVisible();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Processes a packet<br>
	 * This method is used to do the time consuming work on the plug in prior to any operations that
	 * have any influence on the quad tree. This is useful since the quad tree stays in a valid
	 * state and can be used by the main thread while the time consuming work goes on.<br>
	 * This method must not have any write operations on the quad tree!
	 * 
	 * @param packet
	 *            the packet
	 */
	protected abstract void processPacket(Packet packet);
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the visibility state of a plug in
	 * 
	 * @param setVisible
	 *            isVisible
	 */
	public void setVisible(final boolean setVisible) {
		getXMLConfig().setVisible(setVisible);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the plug-in's configuration parameters
	 * 
	 * @param xmlConfig
	 *            the plug-in's configuration parameters
	 */
	public abstract void setXMLConfig(PluginXMLConfig xmlConfig);
	
	// --------------------------------------------------------------------------------
	/**
	 * Updates the quad tree after all sensible information provided by a packet have been
	 * processed. This method should be processed quickly since the graphical user interface has to
	 * wait while the quad tree is updated.
	 */
	protected abstract void updateQuadTree();
	
	// --------------------------------------------------------------------------------
	/**
	 * This method returns an identification string representing the plugin. it is primarily used
	 * for identifiing plugins (and which classes they are instanciated of) in log messages.
	 */
	@Override
	public abstract String toString();
	
	// --------------------------------------------------------------------------------
	/**
	 * Concurrently processes packets which are available in the packet queue and update's the quad
	 * tree.
	 */
	public final void run() {
		
		while (!Thread.currentThread().isInterrupted()) {
			processPacket(getPacketFromQueue(true));
			updateQuadTree();
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Inserts the specified packet at the tail of the packet queue.
	 * 
	 * @return <tt>true</tt> (as specified by {@link Queue#offer})
	 * @throws NullPointerException
	 *             if the specified packet is null
	 */
	protected boolean enqueuePacket(final SpyglassPacket packet) {
		boolean success = false;
		synchronized (packetQueue) {
			success = packetQueue.offer(packet);
			packetQueue.notify();
		}
		return success;
	}
	
	/**
	 * Retrieves and removes the head of the packet queue, or returns <tt>null</tt> if it is empty.
	 * 
	 * @param wait
	 *            indicates whether or not the caller wants to wait for a packet if the packet queue
	 *            is currently empty
	 * @return the head of the packet queue, or <tt>null</tt> if it is empty
	 */
	private SpyglassPacket getPacketFromQueue(final boolean wait) {
		
		synchronized (packetQueue) {
			// wait for the arrival of a new packet if the packet queue is
			// empty, and the caller want's to wait
			if (wait && packetQueue.isEmpty()) {
				try {
					packetQueue.wait();
				} catch (final InterruptedException e) {
					log.error("Error while waiting for a notification of the arrival of a new packet", e);
				}
			}
			return packetQueue.poll();
		}
	}
	
}
