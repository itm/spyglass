package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class PlaySelectInputAction extends Action {
	
	private static final Logger log = SpyglassLoggerFactory.getLogger(PlaySelectInputAction.class);
	
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
