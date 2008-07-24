package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

public class PlayResetAction extends Action {
	
	private static final Category log = Logging.get(PlayResetAction.class);
	
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
