// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.control;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;

// --------------------------------------------------------------------------------
/**
 *
 * Listens to KeyEvents from the DrawingArea and moves the visible area accordingly.
 *
 * @author Dariush Forouher
 *
 */
public class KeyController implements KeyListener {

	private DrawingArea drawingArea;

	/**
	 * Number of pixels to be moved when Up/Down/Left/right is pressed.
	 */
	private final int MOVE_OFFSET = 20;

	public KeyController(final DrawingArea drawingArea) {
		this.drawingArea = drawingArea;
		drawingArea.addKeyListener(this);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		if (e.keyCode == 16777219) {
			drawingArea.move(MOVE_OFFSET, 0);
		}
		if (e.keyCode == 16777220) {
			drawingArea.move(-MOVE_OFFSET, 0);
		}
		if (e.keyCode == 16777217) {
			drawingArea.move(0, MOVE_OFFSET);
		}
		if (e.keyCode == 16777218) {
			drawingArea.move(0, -MOVE_OFFSET);
		}	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		//
	}

}
