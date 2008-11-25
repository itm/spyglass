package de.uniluebeck.itm.spyglass.plugin;

import org.eclipse.swt.widgets.Event;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

/**
 * NodePositionChangedEvent
 * 
 * @author Dariush Forouher
 * 
 */
public class NodePositionChangedEvent extends Event {

	/**
	 * The sender ID of the node
	 */
	int node;

	/**
	 * The old position of the node (may be null if it has no known old position)
	 */
	AbsolutePosition oldPosition;

	/**
	 * The new obtained position.
	 */
	AbsolutePosition newPosition;

	public NodePositionChangedEvent(final int node, final AbsolutePosition oldPosition, final AbsolutePosition newPosition) {
		super();
		this.node = node;
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

}
