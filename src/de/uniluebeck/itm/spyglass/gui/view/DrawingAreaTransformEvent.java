package de.uniluebeck.itm.spyglass.gui.view;

import org.eclipse.swt.widgets.Event;

/**
 * DrawingAreaTransformEvent
 * 
 * @author Dariush Forouher
 * 
 */
public class DrawingAreaTransformEvent extends Event {
	
	/**
	 * The DrawingArea from which the event originated.
	 */
	public DrawingArea drawingArea;
	
	public DrawingAreaTransformEvent(final DrawingArea drawingArea) {
		super();
		this.drawingArea = drawingArea;
	}
	
}
