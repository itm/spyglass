// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.drawing;

import java.util.EventListener;


// --------------------------------------------------------------------------------
/**
 * @author Dariush Forouher
 *
 */
public interface ContentChangedListener extends EventListener {

	public void onContentChanged(DrawingObject updatedDrawingObject);

}
