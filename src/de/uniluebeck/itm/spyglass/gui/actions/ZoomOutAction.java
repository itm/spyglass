package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

public class ZoomOutAction extends Action {
	
	private static final Category log = Logging.get(ZoomOutAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_out.png");
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_OUT.");
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
