package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

public class RecordSelectOutputAction extends Action {
	
	private static final Category log = Logging.get(RecordSelectOutputAction.class);
	
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
