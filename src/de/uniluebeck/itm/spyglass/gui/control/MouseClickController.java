/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import de.uniluebeck.itm.spyglass.core.EventDispatcher;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;

// --------------------------------------------------------------------------------
/**
 * Listens to MouseClick events from the DrawingArea and dispatches them to the plugins.
 * 
 * @author Dariush Forouher
 * 
 */
public class MouseClickController implements MouseListener, PropertyChangeListener, DisposeListener {

	private EventDispatcher eventDispatcher;
	private DrawingArea drawingArea;
	private Spyglass spyglass;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param drawingArea
	 *            the drawing area
	 * @param spyglass
	 *            the spyglass instance
	 */
	public MouseClickController(final DrawingArea drawingArea, final Spyglass spyglass) {
		this.drawingArea = drawingArea;
		this.spyglass = spyglass;
		drawingArea.addMouseListener(this);
		drawingArea.addDisposeListener(this);
		eventDispatcher = new EventDispatcher(spyglass.getPluginManager(), drawingArea);
		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener("pluginManager", this);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (e.button == 1) {
			eventDispatcher.handleEvent(e);
		}
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(final MouseEvent e) {
		if (e.button > 1) {
			eventDispatcher.handleEvent(e);
		}
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(final MouseEvent e) {
		//

	}

	// --------------------------------------------------------------------------------
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		final PluginManager newManager = (PluginManager) evt.getNewValue();

		// the eventDispatcher doesn't register any listeners, so we can just put in the garbage.
		eventDispatcher = new EventDispatcher(newManager, drawingArea);

	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		spyglass.getConfigStore().getSpyglassConfig().removePropertyChangeListener("pluginManager", this);
	}

}
