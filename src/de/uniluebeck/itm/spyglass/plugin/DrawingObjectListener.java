package de.uniluebeck.itm.spyglass.plugin;

import java.util.EventListener;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public interface DrawingObjectListener extends EventListener {
	
	public void drawingObjectChanged(Plugin p, final DrawingObject dob, final AbsoluteRectangle oldBoundingBox);
	
	public void drawingObjectAdded(Plugin p, final DrawingObject dob);
	
	public void drawingObjectRemoved(Plugin p, final DrawingObject dob);
	
}
