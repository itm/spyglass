// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.drawing;

import java.util.EventListener;

// --------------------------------------------------------------------------------
/**
 * @author dariush
 *
 */
public interface BoundingBoxIsDirtyListener extends EventListener {

	public void syncNeeded(DrawingObject dob);
}
