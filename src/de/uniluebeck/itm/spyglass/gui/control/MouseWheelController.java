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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;

// --------------------------------------------------------------------------------
/**
 * Listens to MouseWheelEvents from the DrawingArea and zooms in/out.
 * 
 * @author Dariush Forouher
 * 
 */
public class MouseWheelController implements MouseWheelListener {

	private DrawingArea drawingArea;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param drawingArea
	 *            the drawing area
	 */
	public MouseWheelController(final DrawingArea drawingArea) {
		this.drawingArea = drawingArea;
		drawingArea.addMouseWheelListener(this);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (e.count > 0) {
			drawingArea.zoomIn(e.x, e.y);
		} else {
			drawingArea.zoomOut(e.x, e.y);
		}
	}

}
