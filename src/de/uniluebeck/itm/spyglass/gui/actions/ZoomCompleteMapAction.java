package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * Auto Zoom.
 * 
 * This action modifies zoom and position to make all visible DrawingObjects fit exactly into the
 * drawing area.
 * 
 * @author Dariush Forouher
 * 
 */
public class ZoomCompleteMapAction extends Action {
	
	private static final Logger log = SpyglassLoggerFactory.getLogger(ZoomCompleteMapAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_complete_map.png");
	
	private final Spyglass spyglass;
	
	private final DrawingArea drawingArea;
	
	/**
	 * This Listener is called when the drawingArea is redrawn.
	 */
	private final PaintListener spyglassListener = new PaintListener() {
		
		@Override
		public void paintControl(final PaintEvent e) {
			if (t != null) {
				synchronized (t) {
					t.notify();
				}
			}
		}
		
	};
	
	public ZoomCompleteMapAction(final Spyglass spyglass, final DrawingArea drawingArea) {
		this.spyglass = spyglass;
		this.drawingArea = drawingArea;
		drawingArea.addPaintListener(spyglassListener);
	}
	
	/**
	 * The current Zoom-Thread. It's null when there is currently no zooming going on.
	 */
	private ZoomThread t = null;
	
	/**
	 * The zooming must happen asynchronously to the SWT-Thread, since we have to run the algorithm
	 * multiple times over multiple screen redraws.
	 * 
	 * This is currently neccessary, since after each AutoZoom (call to recalculateDrawingArea())
	 * the boundingBoxes of certain DrawingObjects may change in an unpredictable way. This makes it
	 * neccessary to rerun the algorithm on the new bounding boxes. unfortunatly, the new
	 * boundingBoxes are currently only calculated on a screen redraw.
	 * 
	 * thus we have to wait async to the SWT-Thread for multiple screen redraws, and do an autoZoom
	 * after each one.
	 * 
	 * @author Dariush Forouher
	 * 
	 */
	private class ZoomThread extends Thread {
		@Override
		public void run() {
			// log.debug("Started asynchronous ZOOM_COMPLETE_MAP.");
			
			// 10 seems a good value for now.
			for (int i = 0; i < 10; i++) {
				
				final AbsoluteRectangle maxRect = spyglass.getAutoZoomBoundingBox();
				if ((maxRect != null) && (maxRect.getHeight()>0) && (maxRect.getWidth()>0)) {
					drawingArea.getDisplay().syncExec(new Runnable() {
						
						@Override
						public void run() {
							drawingArea.autoZoom(maxRect);
							
						}
					});
					
				}
				
				// Wait until the next screen redraw happes.
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (final InterruptedException e) {
					log.error("", e);
				}
			}
			
			// allow a new thread to be created
			t = null;
			
			// log.debug("Stopped ZOOM_COMPLETE_MAP thread.");
		}
	};
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_COMPLETE_MAP.");
		
		// Only start one thread at a time
		if (t == null) {
			
			// See Comment on ZoomThread why we have to do this async.
			t = new ZoomThread();
			t.start();
		}
	}
	
	@Override
	public String getText() {
		return "Zoom Complete Map";
	}
	
	@Override
	public String getToolTipText() {
		return "Zoom Complete Map";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
