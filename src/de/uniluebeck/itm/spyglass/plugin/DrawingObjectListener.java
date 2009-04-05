package de.uniluebeck.itm.spyglass.plugin;

import java.util.EventListener;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;

public interface DrawingObjectListener extends EventListener {

	public void drawingObjectAdded(Plugin p, final DrawingObject dob);

	public void drawingObjectRemoved(Plugin p, final DrawingObject dob);

}
