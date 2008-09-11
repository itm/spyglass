package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.Spyglass;

public class PlayPlayPauseAction extends Action {
	
	private static final Category log = Logging.get(PlayPlayPauseAction.class);
	
	private final ImageDescriptor pauseImageDescriptor = getImageDescriptor("play_pause.png");
	
	private final ImageDescriptor playImageDescriptor = getImageDescriptor("play_play.png");
	
	private boolean isPaused;
	
	private final Spyglass spyglass;
	
	public PlayPlayPauseAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
		this.isPaused = spyglass.getPacketProducerTask().getPaused();
	}
	
	@Override
	public void run() {
		
		isPaused = !spyglass.getPacketProducerTask().getPaused();
		
		spyglass.getPacketProducerTask().setPaused(isPaused);
		
		log.debug("packet reader is " + (isPaused ? "" : "not ") + "paused.");
		
		setText(getText());
		setToolTipText(getToolTipText());
		setImageDescriptor(getImageDescriptor());
		
	};
	
	@Override
	public String getText() {
		return isPaused ? "Play" : "Pause";
	}
	
	@Override
	public String getToolTipText() {
		return isPaused ? "Play" : "Pause";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return isPaused ? playImageDescriptor : pauseImageDescriptor;
	}
	
}
