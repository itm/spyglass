/*---------------------------------------------import org.eclipse.swt.widgets.Event;
-----
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.widgets.Event;

/**
 * A TranformChangedEvent signals that the transform between absolute and pixel coordinates has
 * changed.
 * 
 * @author Dariush Forouher
 * 
 */
public class TransformChangedEvent extends Event {

	/**
	 * Event types
	 * 
	 */
	public enum Type {
		/**
		 * The user has zoomed (and potentially also moved) the drawing area
		 */
		ZOOM_MOVE,

		/**
		 * The user has only moved the drawing area
		 */
		MOVE

	}

	/**
	 * The DrawingArea from which the event originated.
	 */
	public final DrawingArea drawingArea;

	/**
	 * Type of the modification
	 */
	public final Type type;

	// --------------------------------------------------------------------------------
	/**
	 * Fired when the transform between absolute and pixel coordinates has changed.
	 * 
	 * @param drawingArea
	 *            the drawing area
	 * @param type
	 *            the change event's type
	 */
	public TransformChangedEvent(final DrawingArea drawingArea, final Type type) {
		this.drawingArea = drawingArea;
		this.type = type;
	}

}
