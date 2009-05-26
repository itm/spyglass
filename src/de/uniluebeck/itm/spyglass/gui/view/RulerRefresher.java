// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.view;


// --------------------------------------------------------------------------------
/**
 * Listens to changes of the transform of the drawingArea and updates the rulers
 * if it changed.
 *
 * @author Dariush Forouher
 *
 */
public class RulerRefresher implements TransformChangedListener {

	SpyglassGuiComponent gui;

	public RulerRefresher(final SpyglassGuiComponent gui) {
		this.gui = gui;
		gui.getDrawingArea().addTransformChangedListener(this);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.view.TransformChangedListener#handleEvent(de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent)
	 */
	@Override
	public void handleEvent(final TransformChangedEvent e) {

		// refresh rulers
		gui.getRulerH().redraw();
		gui.getRulerV().redraw();

	}

}
