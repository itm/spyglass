package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class PlayResetAction extends Action {
	
	private static final Logger log = SpyglassLogger.get(PlayResetAction.class);
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("play_reset.png");
	
	@Override
	public void run() {
		log.debug("Pressed button PLAY_RESET.");
	};
	
	@Override
	public String getText() {
		return "Reset";
	}
	
	@Override
	public String getToolTipText() {
		return "Reset";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
