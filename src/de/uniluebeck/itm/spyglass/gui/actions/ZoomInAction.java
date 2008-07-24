package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

public class ZoomInAction extends Action {
	
	private static final Category log = Logging.get(ZoomInAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_in.png");
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_IN.");
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
