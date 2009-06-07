package de.uniluebeck.itm.spyglass.gui.actions;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;

import de.uniluebeck.itm.spyglass.SpyglassApp;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class ZoomAction extends Action implements IPropertyChangeListener {

	private static final Logger log = SpyglassLoggerFactory.getLogger(ZoomAction.class);

	/**
	 * Value added per zoom process
	 */
	private final static double ESCALATING_VALUE = 0.1;

	/**
	 * initial zoom multiplier
	 */
	private final static double INITIAL_VALUE = 1.1;

	/**
	 * time to wait between zooms (in ms)
	 */
	private final static int ZOOM_PERIOD = 100;


	public enum Type {
		ZOOM_IN,
		ZOOM_OUT
	}

	private final DrawingArea drawingArea;

	private final SpyglassApp app;

	private final Type type;

	private Timer timer = null;

	private double zoomFactor;

	private Runnable zoomRunnable = new Runnable() {

		@Override
		public void run() {
			if (!drawingArea.isDisposed()) {

				switch (type) {
					case ZOOM_IN:
						drawingArea.zoomIn(zoomFactor);
						break;
					case ZOOM_OUT:
						drawingArea.zoomOut(zoomFactor);
						break;
				}
				zoomFactor += ESCALATING_VALUE;
			}
		}

	};

	public ZoomAction(final DrawingArea da, final Type type) {
		this.drawingArea = da;
		app = null;
		this.type = type;

		this.addPropertyChangeListener(this);
	}

	public ZoomAction(final SpyglassApp da, final Type type) {
		this.app = da;
		drawingArea = null;
		this.type = type;

		this.addPropertyChangeListener(this);
	}


	@Override
	public void run() {

		if (app != null) {
			switch (type) {
				case ZOOM_IN:
					app.getDrawingArea().zoomIn(DrawingArea.ZOOM_FACTOR);
					break;
				case ZOOM_OUT:
					app.getDrawingArea().zoomOut(DrawingArea.ZOOM_FACTOR);
					break;
			}
		}
	};

	@Override
	public String getText() {
		switch (type) {
			case ZOOM_IN:
				return "Zoom &in";
			case ZOOM_OUT:
				return "Zoom &out";
		}
		return "";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		switch (type) {
			case ZOOM_IN:
				return getImageDescriptor("zoom_in.png");
			case ZOOM_OUT:
				return getImageDescriptor("zoom_out.png");
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (isChecked()) {
			log.debug("Starting zoom");
			startTimer();
		} else {
			log.debug("Stopping zoom");
			timer.cancel();
		}
	}

	/**
	 * Start a new Timer which periodically zooms
	 */
	private void startTimer() {
		zoomFactor = INITIAL_VALUE;
		timer = new Timer("Zoom-Timer");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!Display.getDefault().isDisposed()) {
					Display.getDefault().syncExec(zoomRunnable);
				}

			}

		}, 0, ZOOM_PERIOD);
	}
}
