package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class ZoomOutAction extends Action {
	
	private static final Logger log = SpyglassLogger.get(ZoomOutAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_out.png");
	
	private final DrawingArea drawingArea;
	
	public ZoomOutAction(final DrawingArea da) {
		this.drawingArea = da;
	}
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_OUT.");
		drawingArea.zoomOut();
	};
	
	@Override
	public String getText() {
		return "Zoom Out";
	}
	
	@Override
	public String getToolTipText() {
		return "Zoom Out";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
