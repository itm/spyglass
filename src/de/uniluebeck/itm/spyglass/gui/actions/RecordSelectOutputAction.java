package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class RecordSelectOutputAction extends Action {
	
	private static final Logger log = SpyglassLogger.get(RecordSelectOutputAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("record_select_output.png");
	
	@Override
	public void run() {
		log.debug("Pressed button RECORD_SELECT_OUTPUT.");
	};
	
	@Override
	public String getToolTipText() {
		return "Select Output";
	}
	
	@Override
	public String getText() {
		return "Select Output";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
