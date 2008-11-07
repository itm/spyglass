package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class PlayResetAction extends Action {
	
	private static final Logger log = SpyglassLoggerFactory.get(PlayResetAction.class);
	
	private Spyglass spyglass;
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("play_reset.png");
	
	public PlayResetAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		log.debug("Pressed button PLAY_RESET.");
		spyglass.reset();
	}
	
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
