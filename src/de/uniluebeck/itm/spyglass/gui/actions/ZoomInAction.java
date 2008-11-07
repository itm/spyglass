package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class ZoomInAction extends Action {
	
	private static final Logger log = SpyglassLoggerFactory.getLogger(ZoomInAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_in.png");
	
	private final DrawingArea drawingArea;
	
	public ZoomInAction(final DrawingArea da) {
		this.drawingArea = da;
	}
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_IN.");
		drawingArea.zoomIn();
	};
	
	@Override
	public String getText() {
		return "Zoom In";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
}
