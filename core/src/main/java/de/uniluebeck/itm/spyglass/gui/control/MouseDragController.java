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
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

// --------------------------------------------------------------------------------
/**
 * Listens to Mouse Drag events from the DrawingArea and moves the visible area accordingly.
 * 
 * @author Dariush Forouher
 * 
 */
public class MouseDragController implements MouseListener, MouseMoveListener {

	private DrawingArea drawingArea;

	/**
	 * True, while the user moves the map via mouse
	 */
	private boolean mouseDragInProgress = false;

	/**
	 * the starting point of the movement business.
	 */
	private PixelPosition mouseDragStartPosition = null;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param drawingArea
	 *            the drawing area
	 */
	public MouseDragController(final DrawingArea drawingArea) {
		this.drawingArea = drawingArea;
		drawingArea.addMouseListener(this);
		drawingArea.addMouseMoveListener(this);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void mouseDown(final MouseEvent e) {
		mouseDragInProgress = true;
		mouseDragStartPosition = new PixelPosition(e.x, e.y);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void mouseUp(final MouseEvent arg0) {
		mouseDragInProgress = false;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void mouseMove(final MouseEvent arg0) {

		// if a movement is in progress, update the drawing area by
		// applying the current delta.
		if (mouseDragInProgress) {

			final PixelPosition mouseDragStopPosition = new PixelPosition(arg0.x, arg0.y);

			final int deltaX = mouseDragStopPosition.x - mouseDragStartPosition.x;
			final int deltaY = mouseDragStopPosition.y - mouseDragStartPosition.y;

			drawingArea.move(deltaX, deltaY);

			mouseDragStartPosition = mouseDragStopPosition;
		}

	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		//
	}

}
