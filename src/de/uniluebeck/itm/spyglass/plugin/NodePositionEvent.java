/*
 * ---------------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007
 * by the SwarmNet (www.swarmnet.de) project SpyGlass is free software; you can redistribute
 * it and/or modify it under the terms of the BSD License. Refer to spyglass-licence.txt
 * file in the root of the SpyGlass source tree for further details. 
 * --------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import org.eclipse.swt.widgets.Event;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

//------------------------------------------------------------------------------
/**
 * NodePositionChangedEvent
 * 
 * @author Dariush Forouher
 * 
 */
public class NodePositionEvent extends Event {

	/** The possibilities for this event */
	public enum Change {
		/** Indicates that a node was added */
		ADDED,
		/** Indicates that a node was removed */
		REMOVED,
		/** Indicates that a node's position changed */
		MOVED
	}

	/**
	 * What happened to the node
	 */
	public Change change;

	/**
	 * The sender ID of the node
	 */
	public int node;

	// ------------------------------------------------------------------------------
	/**
	 * The old position of the node (may be null if the node is new)
	 */
	public AbsolutePosition oldPosition;

	// ------------------------------------------------------------------------------
	/**
	 * The new obtained position. (may be null if the node has been removed)
	 */
	public AbsolutePosition newPosition;

	// ------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param node
	 *            a node's identifier
	 * @param change
	 *            the reason of the event (indicates what happened to the node)
	 * @param oldPosition
	 *            the node's previous position
	 * @param newPosition
	 *            the node's new position
	 * 
	 */
	public NodePositionEvent(final int node, final Change change, final AbsolutePosition oldPosition, final AbsolutePosition newPosition) {
		super();
		this.node = node;
		this.change = change;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

}
