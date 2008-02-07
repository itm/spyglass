/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.plugin;

import org.simpleframework.xml.*;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.layer.SubLayer;
import de.uniluebeck.itm.spyglass.packet.Packet;

// --------------------------------------------------------------------------------
/**
 * Base class for all Spyglass plugins. A plugin has at least a priority, that determines the drawing order, a state
 * (active or not) and a default drawing object that is being used for drawing jobs.
 */
@Root
public abstract class Plugin implements Comparable<Plugin> {
	@Attribute
	private int priority = 0;

	@Attribute
	private boolean isActive = true;

	@Element
	private DrawingObject defaultDrawingObject = null;

	private SubLayer subLayer = new SubLayer();

	// --------------------------------------------------------------------------------
	/**
	 * This method handles a Packet object. Usually, a plugin creates a new DrawingObject for each packet it handles.
	 * 
	 * @param packet
	 *            The packet object to handle.
	 */
	public abstract void handlePacket(Packet packet);

	// --------------------------------------------------------------------------------
	/**
	 * Creates and returns a new DrawingObject by getting and cloning the plugin's default DrawingObject.
	 * 
	 * @return The new default DrawingObject for the plugin.
	 */
	protected DrawingObject createDrawingObject() {
		DrawingObject object = null;

		try {
			object = getDefaultDrawingObject().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return object;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public SubLayer getSubLayer() {
		return subLayer;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setSubLayer(SubLayer subLayer) {
		this.subLayer = subLayer;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getPriority() {
		return priority;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public int compareTo(Plugin p) {
		if (priority < p.priority)
			return 1;
		else if (priority > p.priority)
			return -1;
		else
			return 0;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public boolean isActive() {
		return isActive;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public DrawingObject getDefaultDrawingObject() {
		return defaultDrawingObject;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setDefaultDrawingObject(DrawingObject defaultDrawingObject) {
		this.defaultDrawingObject = defaultDrawingObject;
	}

}
