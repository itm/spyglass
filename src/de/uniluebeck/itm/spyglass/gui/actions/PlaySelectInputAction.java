package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

public class PlaySelectInputAction extends Action {
	
	private static final Category log = Logging.get(PlaySelectInputAction.class);
	
	@Override
	public void run() {
		log.debug("Pressed Button PLAY_SELECT_INPUT.");
	}
	
	@Override
	public String getText() {
		return "Select Input";
	}
	
	@Override
	public String getToolTipText() {
		return "Select Input";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return getImageDescriptor("play_select_input.png");
	}
	
}
