package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.widgets.Event;

/**
 * A TranformChangedEvent signals that the transform between absolute and pixel coordinates
 * has changed.
 * 
 * @author Dariush Forouher
 * 
 */
public class TransformChangedEvent extends Event {
	
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
	
	public TransformChangedEvent(final DrawingArea drawingArea, final Type type) {
		this.drawingArea = drawingArea;
		this.type = type;
	}
	
}
