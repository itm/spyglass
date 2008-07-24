package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

public class ZoomCompleteMapAction extends Action {
	
	private static final Category log = Logging.get(ZoomCompleteMapAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("zoom_complete_map.png");
	
	@Override
	public void run() {
		log.debug("Pressed button ZOOM_COMPLETE_MAP.");
	};
	
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
