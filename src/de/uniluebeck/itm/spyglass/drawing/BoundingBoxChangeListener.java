package de.uniluebeck.itm.spyglass.drawing;

import java.util.EventListener;

public interface BoundingBoxChangeListener extends EventListener {
	
	public void onBoundingBoxChanged(DrawingObject updatedDrawingObject);
	
}
