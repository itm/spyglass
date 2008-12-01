package de.uniluebeck.itm.spyglass.plugin;

import org.eclipse.swt.widgets.Event;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

/**
 * NodePositionChangedEvent
 * 
 * @author Dariush Forouher
 * 
 */
public class NodePositionEvent extends Event {

	public enum Change {
		ADDED,
		REMOVED,
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

	/**
	 * The old position of the node (may be null if the node is new)
	 */
	public AbsolutePosition oldPosition;

	/**
	 * The new obtained position. (may be null if the node has been removed)
	 */
	public AbsolutePosition newPosition;

	public NodePositionEvent(final int node, final Change change, final AbsolutePosition oldPosition, final AbsolutePosition newPosition) {
		super();
		this.node = node;
		this.change = change;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

}
