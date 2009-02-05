package de.uniluebeck.itm.spyglass.drawing;

import java.util.EventListener;

import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

public interface BoundingBoxChangeListener extends EventListener {

	public void onBoundingBoxChanged(DrawingObject updatedDrawingObject, AbsoluteRectangle oldBox);

}
