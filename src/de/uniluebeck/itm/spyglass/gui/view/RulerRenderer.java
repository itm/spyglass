// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * This class is responsible for redrawing the rulers when they
 * send out a PaintEvent.
 *
 * @author Dariush Forouher
 */
public class RulerRenderer implements PaintListener, PropertyChangeListener, DisposeListener {

	SpyglassGuiComponent gui;
	Spyglass spyglass;

	public RulerRenderer(final SpyglassGuiComponent gui, final Spyglass spyglass) {
		this.gui = gui;
		this.spyglass = spyglass;
		gui.getRulerH().addPaintListener(this);
		gui.getRulerV().addPaintListener(this);
		gui.getUnitArea().addPaintListener(this);

		gui.addDisposeListener(this);

		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().addPropertyChangeListener(this);
	}

	@Override
	public void paintControl(final PaintEvent e) {

		final Point2D upperLeft = gui.getDrawingArea().getUpperLeftPrecise();
		final Point2D lowerRight = gui.getDrawingArea().getLowerRightPrecise();
		final PixelRectangle pxRect = gui.getDrawingArea().getDrawingRectangle();

		// this paintListener is used for all three areas. so we have to check
		// which one we're supposed to redraw...
		if (e.widget == gui.getRulerH()) {
			gui.getRulerH().drawRuler(pxRect, upperLeft, lowerRight, e.gc, RulerArea.HORIZONTAL);
		} else if (e.widget == gui.getRulerV()) {
			gui.getRulerV().drawRuler(pxRect, upperLeft, lowerRight, e.gc, RulerArea.VERTICAL);
		} else {
			final String unit = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().getMetrics().getUnit();
			gui.getUnitArea().drawUnit(e.gc, unit);
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {

		gui.changeRulerVis();

	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings().removePropertyChangeListener(this);	}
}
