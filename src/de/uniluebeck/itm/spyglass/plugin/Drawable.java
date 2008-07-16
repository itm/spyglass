package de.uniluebeck.itm.spyglass.plugin;

import java.util.List;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;

public interface Drawable {

	public float getTimeout();

	public boolean isVisible();
	

	/**
	 * 
	 * @param drawingArea
	 */
	public abstract List<DrawingObject> getDrawingObjects(DrawingArea drawingArea);


}