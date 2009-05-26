// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.control;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;

// --------------------------------------------------------------------------------
/**
 * Listens to MouseWheelEvents from the DrawingArea and zooms in/out.
 *
 * @author Dariush Forouher
 *
 */
public class MouseWheelController implements MouseWheelListener {

	private DrawingArea drawingArea;

	public MouseWheelController(final DrawingArea drawingArea) {
		this.drawingArea = drawingArea;
		drawingArea.addMouseWheelListener(this);
	}


	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (e.count > 0) {
			drawingArea.zoomIn(e.x, e.y);
		} else {
			drawingArea.zoomOut(e.x, e.y);
		}
	}

}
