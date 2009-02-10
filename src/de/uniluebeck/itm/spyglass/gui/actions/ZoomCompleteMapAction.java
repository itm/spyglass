package de.uniluebeck.itm.spyglass.gui.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import de.uniluebeck.itm.spyglass.SpyglassApp;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
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
	
	private  DrawingArea drawingArea;
	
	private final SpyglassApp app;

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

	public ZoomCompleteMapAction( final Spyglass spyglass, final DrawingArea drawingArea) {
		this.spyglass = spyglass;
		this.drawingArea = drawingArea;
		this.app = null;
		drawingArea.addPaintListener(spyglassListener);
	}
	
	public ZoomCompleteMapAction(final SpyglassApp da, final Spyglass spyglass) {
		this.app = da;
		this.spyglass = spyglass;
		this.drawingArea = null;
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
				
				final AbsoluteRectangle maxRect1 = calcBBox();
				
				final AbsoluteRectangle maxRect = maxRect1;
				if ((maxRect != null) && (maxRect.getHeight()>0) && (maxRect.getWidth()>0)) {
					drawingArea.getDisplay().syncExec(new Runnable() {
						
						@Override
						public void run() {
							// the drawingArea might have been disposed while we were waiting
							if ((drawingArea != null) && drawingArea.isDisposed()) {
								return;
							}
							
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

		/**
		 * This method gets the bounding boxes of all visible drawingObjects (which are applicable for
		 * AutoZoom and scrollbars) and merges them.
		 */
		private AbsoluteRectangle calcBBox() {
			final List<Plugin> list = spyglass.getPluginManager().getVisibleActivePlugins();
			
			final List<DrawingObject> dobs = new ArrayList<DrawingObject>();
			
			for (final Plugin plugin : list) {
				if (plugin instanceof Drawable) {
					final Drawable plugin2 = (Drawable) plugin;
			
					dobs.addAll(plugin2.getAutoZoomDrawingObjects());
				}
			}
			
			AbsoluteRectangle maxRect1 = new AbsoluteRectangle();
			
			for (final DrawingObject drawingObject : dobs) {
				final AbsoluteRectangle nextRect = drawingObject.getBoundingBox();
				if (nextRect == null) {
					continue;
				}
			
				if (maxRect1 == null) {
					maxRect1 = nextRect;
				} else {
					maxRect1 = maxRect1.union(nextRect);
				}
			}
			return maxRect1;
		}
	};
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_COMPLETE_MAP.");
		
		if ((app != null) && (drawingArea == null)) {
			drawingArea = app.getDrawingArea();
			drawingArea.addPaintListener(spyglassListener);
		}
		

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
