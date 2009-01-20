/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import java.util.EventListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseEvent;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

/**
 * Abstract base class for all SpyGlass plug-ins.<br>
 * This abstract class implements the {@link Runnable} interface to enhance the performance of
 * packet handling. When new packets arrive, they are just dropped in a queue to enable the
 * dispatcher's thread to go on. The plug-in's packet handling thread is automatically notified
 * about the arrival and starts processing the packet. Since this occurs in a thread separated from
 * the main thread a slow plug in does not slow down the whole application.
 * 
 * @author Sebastian Ebers
 */
@Root
public abstract class Plugin implements Runnable, Comparable<Plugin> {

	/**
	 * Reference to the plug-in manager
	 */
	protected PluginManager pluginManager;

	/**
	 * The queue where packets are dropped by the packet dispatcher and which is maintained
	 * concurrently
	 */
	private ConcurrentLinkedQueue<SpyglassPacket> packetQueue = null;

	/** The thread used to consume packets from the packet queue */
	private Thread packetConsumerThread;

	/**
	 * Object which is used to log different kinds of messages
	 */
	private static final Logger log = SpyglassLoggerFactory.getLogger(Plugin.class);

	/**
	 * This list contains the DrawingObjectListeners.
	 */
	private final EventListenerList listeners = new EventListenerList();

	// --------------------------------------------------------------------------------
	/**
	 * Constructor<br>
	 * If the plug-in has to administer a packet queue a thread to consume packets from the queue
	 * will be started when the plug-in is activated and stopped when the plug-in is deactivated.<br>
	 * When a new plug-in is created, the Method {@link Plugin#init(PluginManager)} has to be called
	 * which is usually done in the {@link PluginManager} when the plug-in is added to the list.
	 * 
	 * @param needsPacketQueue
	 *            indicates whether or not the plug-in has to administer a packet queue
	 */
	public Plugin(final boolean needsPacketQueue) {
		if (needsPacketQueue) {
			packetQueue = new ConcurrentLinkedQueue<SpyglassPacket>();
		}
	}

	/**
	 * Constructor creating a temporal storage for incoming packages
	 */
	public Plugin() {
		this(true);
	}

	// --------------------------------------------------------------------------------
	/**
	 * This method handles a Packet object. Usually, a plug-in creates a new DrawingObject for each
	 * packet it handles.<br>
	 * <b>Note:</b> Nothing is done here if the plug-in is currently deactivated.
	 * 
	 * @param packet
	 *            The packet object to handle.
	 * @param InterruptedException if an interrupt occured while this method was called.
	 * @param Exception if any exception occured.
	 */
	public void handlePacket(final SpyglassPacket packet) throws Exception {
		// if the packet is not null, check if its semantic type is one of
		// those, the plug-in is interested in
		if (isActive()) {

			if (!getXMLConfig().isAllSemanticTypes()) {
				final int[] mySemanticTypes = getXMLConfig().getSemanticTypes();
				final int packetSemanticType = packet.getSemanticType();
				for (int i = 0; i < mySemanticTypes.length; i++) {
					// if the packets semantic type matches ...
					if (mySemanticTypes[i] == packetSemanticType) {
						// put it into the packet queue (the process which fetches
						// from the queue afterwards will be notified automatically)
						enqueuePacket(packet);
						break;
					}
				}
			} else {
				enqueuePacket(packet);
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the name of the plug-in instance
	 * 
	 * @return the name of the plug-in instance
	 */
	public final String getInstanceName() {
		return getXMLConfig().getName();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns if the plug-in is currently active
	 * 
	 * @return <tt>true</tt> if the plug-in is currently active
	 */
	public final boolean isActive() {
		return getXMLConfig().getActive();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Resets the plug-in's state i.e. removing all objects from the QuadTree, clearing the packet
	 * queue and setting all member variables to default.
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
		getXMLConfig().setActive(isActive);

		// TODO: Should be done via Events
		// TODO: call reset()?

		if (packetQueue != null) {
			// if the plug-in is deactivated, stop the thread if it is currenrly
			// running. Otherwise, start it
			if (!isActive()) {
				stopPacketConsumerThread();
			} else {
				startPacketConsumerThread();
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Initializes the plugin. It is called right after the plugin has been instanciated and the
	 * configuration of the plugin is set.
	 * 
	 * This methods starts the consumer thread, if necessary.
	 * 
	 * @param manager
	 *            reference to the parent PluginManager
	 * 
	 * @see PluginXMLConfig#getActive()
	 */
	public void init(final PluginManager manager) throws Exception {

		this.pluginManager = manager;

		// if the plug-in has a packet queue, it is maintained by a separate
		// thread. This thread has to be started on activation and stopped on
		// deactivation
		if (packetQueue != null) {
			// if the plug-in is deactivated, stop the thread if it is currenrly
			// running. Otherwise, start it
			if (!isActive()) {
				stopPacketConsumerThread();
			} else {
				startPacketConsumerThread();
			}
		}
	}

	/**
	 * Stops the thread which consumes the packets available in the packet queue
	 */
	private void stopPacketConsumerThread() {
		if ((packetConsumerThread != null) && !packetConsumerThread.isInterrupted()) {
			try {
				packetConsumerThread.interrupt();
				log.debug("The PacketConsumerThread of the plug-in named '" + getInstanceName() + " stopped successfully.");
			} catch (final RuntimeException e) {
				log.error("An error occured while trying to stop the plug-in's thread", e);
			}
		}
	}

	/**
	 * Starts the thread which consumes the packets available in the packet queue
	 */
	private void startPacketConsumerThread() {

		if ((packetConsumerThread == null) || packetConsumerThread.isInterrupted()) {
			try {
				// since a thread cannot be restarted, a new one has to be
				// created
				packetConsumerThread = new Thread(this, "PacketConsumerThread[" + this.getClass().getSimpleName() + "]");
				packetConsumerThread.setDaemon(true);
				packetConsumerThread.start();

				log.debug("The PacketConsumerThread of the plug-in named '" + getInstanceName() + " started successfully.");
			} catch (final RuntimeException e) {
				log.error("An error occured while trying to start the plug-in's thread", e);
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Creates and returns a widget which can be used to configure the plug-in
	 * 
	 * @param dialog
	 *            the <code>PluginPreferenceDialog</code> instance the preference page is displayed
	 *            in
	 * @param spyglass
	 *            the <code>Spyglass</code> instance
	 * 
	 * @return a widget which can be used to configure the plug-in
	 * @throws an exception when the page could not be created. this will result in a user-visible error message
	 */
	public abstract PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) throws Exception;

	// --------------------------------------------------------------------------------
	/**
	 * Returns the plug-in's denotation in a human readable style
	 * 
	 * @return the plug-in's denotation in a human readable style
	 * 
	 * @throws UnsupportedOperationException
	 *             if this operation is called on the plug-in superclass {@link Plugin}
	 */
	public static String getHumanReadableName() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"This method must only be called on subclasses and must be implemented by every abstract or instantiable suclass.");
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
	 * This method is called from the SWT Display thread. So this method should return as quickly as
	 * possible, to avoid user-visible delays.
	 * 
	 * @param e
	 *            the mouse event
	 * @param drawingArea
	 *            the drawing area in which the event occured
	 * @return <code>true</code> if the plug-in could handle the event, <code>false</code> otherwise
	 * @throws Exception any kind of exception
	 */
	public boolean handleEvent(final MouseEvent e, final DrawingArea drawingArea) throws Exception {
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns <tt>true</tt> if the plug-in is visible
	 * 
	 * @return <tt>true</tt> if the plug-in is visible
	 */
	public final boolean isVisible() {
		return getXMLConfig().getVisible();
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
	 * @throws Exception any kind of exception
	 */
	protected abstract void processPacket(SpyglassPacket packet) throws Exception;

	// --------------------------------------------------------------------------------
	/**
	 * Sets the visibility state of a plug in
	 * 
	 * @param setVisible
	 *            isVisible
	 */
	public final void setVisible(final boolean setVisible) {
		getXMLConfig().setVisible(setVisible);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the quad tree after all sensible information provided by a packet have been
	 * processed. This method should be processed quickly since the graphical user interface has to
	 * wait while the quad tree is updated.
	 * 
	 * @throws Exception if anything bad happens.
	 */
	protected abstract void updateLayer() throws Exception;

	// --------------------------------------------------------------------------------
	/**
	 * This method returns an identification string representing the plugin. it is primarily used
	 * for identifiing plugins (and which classes they are instanciated of) in log messages.
	 */
	@Override
	public abstract String toString();

	/**
	 * Compares the instance names of two plug-ins lexicographically, ignoring case differences.
	 * This method returns an integer whose sign is that of calling <code>compareTo</code> with
	 * normalized versions of the strings where case differences have been eliminated by calling
	 * <code>Character.toLowerCase(Character.toUpperCase(character))</code> on each character.
	 * <p>
	 * Note that this method does <em>not</em> take locale into account, and will result in an
	 * unsatisfactory ordering for certain locales. The java.text package provides
	 * <em>collators</em> to allow locale-sensitive ordering.
	 * 
	 * @param p
	 *            the other plug-in to be compared.
	 * @return a negative integer, zero, or a positive integer as the specified String is greater
	 *         than, equal to, or less than this String, ignoring case considerations.
	 * @see java.text.Collator#compare(String, String)
	 * @since 1.2
	 */
	public final int compareTo(final Plugin p) {

		return getInstanceName().compareToIgnoreCase(p.getInstanceName());
	}

	// --------------------------------------------------------------------------------
	/**
	 * Concurrently processes packets which are available in the packet queue and update's the quad
	 * tree.
	 */
	public final void run() {

		while (!packetConsumerThread.isInterrupted()) {
			final SpyglassPacket p = getPacketFromQueue(true);
			if (p != null) {
				try {
					processPacket(p);
					updateLayer();
				} catch (final InterruptedException e) {
					log.error("An exception occured while processing a packet in Plugin '" + getInstanceName() + "'", e);
					packetConsumerThread.interrupt();
				} catch (final Exception e) {
					log.error("An exception occured while processing a packet in Plugin '" + getInstanceName() + "'", e);
				}
			}
		}
		packetConsumerThread = null;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Inserts the specified packet at the tail of the packet queue.
	 * 
	 * @param packet
	 *            a packet
	 * @return <tt>true</tt> (as specified by {@link Queue#offer})
	 * @throws NullPointerException
	 *             if the specified packet is null
	 */
	protected final boolean enqueuePacket(final SpyglassPacket packet) {
		boolean success = false;
		if (isActive()) {
			synchronized (packetQueue) {
				success = packetQueue.offer(packet);
				packetQueue.notify();
			}
		}
		return success;
	}

	/**
	 * Retrieves and removes the head of the packet queue, or returns <tt>null</tt> if it is empty.<br>
	 * Note that this is done in an extra thread. If an {@link InterruptedException} occurs,
	 * <code>null</code> might be returned no matter if the parameter <tt>wait</tt> is set or not.
	 * 
	 * @param wait
	 *            indicates whether or not the caller wants to wait for a packet if the packet queue
	 *            is currently empty
	 * @return the head of the packet queue, or <tt>null</tt> if it is empty
	 */
	private final SpyglassPacket getPacketFromQueue(final boolean wait) {

		synchronized (packetQueue) {
			// wait for the arrival of a new packet if the packet queue is
			// empty, and the caller want's to wait
			if (wait && packetQueue.isEmpty()) {
				try {
					packetQueue.wait();
				} catch (final InterruptedException e) {
					log.info(getInstanceName()
							+ ": The packet consumer thread was interrupted while waiting for a notification of the arrival of a new packet");
					packetConsumerThread.interrupt();
				}
			}
			return packetQueue.poll();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Register a new DrawingObjectListener.
	 * 
	 * The listener will be called every time a new drawing object is added to the layer, removed
	 * from it, or modified.
	 * 
	 * @param listener
	 *            a DrawingObjectListener
	 */
	public final void addDrawingObjectListener(final DrawingObjectListener listener) {
		listeners.add(DrawingObjectListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Unregister an existing DrawingObjectListener.
	 * 
	 * @param listener
	 *            an existing DrawingObjectListener
	 */
	public final void removeDrawingObjectListener(final DrawingObjectListener listener) {
		listeners.remove(DrawingObjectListener.class, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Fire a DrawingObjectChanged event.
	 * 
	 * @param dob
	 *            The DrawingObject, which has been modified
	 * @param oldBoundingBox
	 *            The original boundingBox before the modification (may be null if the modified
	 *            drawing object occupies (at least) the same space as before the modification).
	 */
	protected final void fireDrawingObjectChanged(final DrawingObject dob, final AbsoluteRectangle oldBoundingBox) {
		// Get listeners
		final EventListener[] list = listeners.getListeners(DrawingObjectListener.class);

		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			if (oldBoundingBox != null) {
				((DrawingObjectListener) list[i]).drawingObjectChanged(dob, oldBoundingBox);
			} else {
				((DrawingObjectListener) list[i]).drawingObjectChanged(dob, dob.getBoundingBox());
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Fire a DrawingObjectAdded event.
	 * 
	 * @param dob
	 *            The DrawingObject, which has been added
	 */
	protected final void fireDrawingObjectAdded(final DrawingObject dob) {
		// Get listeners
		final EventListener[] list = listeners.getListeners(DrawingObjectListener.class);

		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			((DrawingObjectListener) list[i]).drawingObjectAdded(dob);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Fire a DrawingObjectRemoved event.
	 * 
	 * @param dob
	 *            The DrawingObject, which has been removed
	 */
	protected final void fireDrawingObjectRemoved(final DrawingObject dob) {
		// Get listeners
		final EventListener[] list = listeners.getListeners(DrawingObjectListener.class);

		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			((DrawingObjectListener) list[i]).drawingObjectRemoved(dob);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Return the responsible plug-in manager
	 * 
	 * @return the responsible plug-in manager
	 */
	protected final PluginManager getPluginManager() {
		return pluginManager;
	}

	/**
	 * Is called before the plugin is removed or before spyglass shuts down.
	 * 
	 * This method will be called exactly once, at the end of the lifetime of the plugin. It's
	 * purpose is to clean up behind, kill (eventually) remaining threads and unregister any
	 * listeners (if necessary).
	 * 
	 * @throws Exception any kind of exception
	 */
	public void shutdown() throws Exception {
		// stop the consumer thread
		stopPacketConsumerThread();
	}

}
