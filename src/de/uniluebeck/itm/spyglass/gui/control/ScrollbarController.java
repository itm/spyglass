// --------------------------------------------------------------------------------
/**
 *
 */
package de.uniluebeck.itm.spyglass.gui.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedListener;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;

// --------------------------------------------------------------------------------
/**
 * Listens to movements on the scroll bar of the drawingArea and moves the
 * visible area accordingly.
 *
 * It also listens to changes of the drawingArea Transform and updates the scrollbars
 * to reflect the current zoomlevel and position.
 *
 * @author Dariush Forouher
 *
 */
public class ScrollbarController implements SelectionListener, TransformChangedListener {

	DrawingArea drawingArea;

	public ScrollbarController(final DrawingArea drawingArea) {
		this.drawingArea = drawingArea;

		// handle ScrollBar events
		drawingArea.getHorizontalBar().addSelectionListener(this);
		drawingArea.getVerticalBar().addSelectionListener(this);
		drawingArea.addTransformChangedListener(this);

		// The canvas must not act on mouse wheel events (normally it would move the
		// scroll bars) since the mouse wheel already controls the zoom.
		drawingArea.addListener(SWT.MouseWheel, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				event.doit = false;
			}
		});

		drawingArea.getHorizontalBar().setMinimum(0);
		drawingArea.getHorizontalBar().setMaximum(DrawingArea.WORLD_WIDTH);
		drawingArea.getHorizontalBar().setPageIncrement(1000);
		drawingArea.getHorizontalBar().setIncrement(100);

		drawingArea.getVerticalBar().setMinimum(0);
		drawingArea.getVerticalBar().setMaximum(DrawingArea.WORLD_HEIGHT);
		drawingArea.getVerticalBar().setPageIncrement(1000);
		drawingArea.getVerticalBar().setIncrement(100);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {
		scroll();
	}


	/**
	 * Handle a scroll event
	 */
	protected void scroll() {

		final int selectY = -drawingArea.getVerticalBar().getSelection() - DrawingArea.WORLD_LOWER_LEFT_Y;
		final int selectX = drawingArea.getHorizontalBar().getSelection() + DrawingArea.WORLD_LOWER_LEFT_X;

		final AbsolutePosition newPos = new AbsolutePosition(selectX, selectY);

		int dx = drawingArea.getLowerLeft().x - newPos.x;
		int dy = drawingArea.getLowerLeft().y + drawingArea.getAbsoluteDrawingRectangle().getHeight() - newPos.y;

		// don't allow using scrollbars if we're at the border
		if ((drawingArea.getHorizontalBar().getSelection() == drawingArea.getHorizontalBar().getMinimum()) && (dx < 0)) {
			dx = 0;
		}
		if ((drawingArea.getHorizontalBar().getSelection() == drawingArea.getHorizontalBar().getMaximum()) && (dx > 0)) {
			dx = 0;
		}
		if ((drawingArea.getVerticalBar().getSelection() == drawingArea.getVerticalBar().getMinimum()) && (dy < 0)) {
			dy = 0;
		}
		if ((drawingArea.getVerticalBar().getSelection() == drawingArea.getVerticalBar().getMaximum()) && (dy > 0)) {
			dy = 0;
		}

		drawingArea.moveAbs(dx, -dy);
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.control.TransformChangedListener#handleEvent(de.uniluebeck.itm.spyglass.gui.control.TransformChangedEvent)
	 */
	@Override
	public void handleEvent(final TransformChangedEvent e) {
		drawingArea.getHorizontalBar().setSelection(drawingArea.getLowerLeft().x - DrawingArea.WORLD_LOWER_LEFT_X);
		drawingArea.getVerticalBar().setSelection(-drawingArea.getLowerLeft().y - drawingArea.getAbsoluteDrawingRectangle().getHeight()  - DrawingArea.WORLD_LOWER_LEFT_Y);
		drawingArea.getHorizontalBar().setThumb(drawingArea.getAbsoluteDrawingRectangle().getWidth());
		drawingArea.getVerticalBar().setThumb(drawingArea.getAbsoluteDrawingRectangle().getHeight());
	}
}
