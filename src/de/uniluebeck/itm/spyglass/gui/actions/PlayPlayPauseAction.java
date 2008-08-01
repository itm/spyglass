package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.Spyglass;

public class PlayPlayPauseAction extends Action {
	
	private static final Category log = Logging.get(PlayPlayPauseAction.class);
	
	private final ImageDescriptor pauseImageDescriptor = getImageDescriptor("play_pause.png");
	
	private final ImageDescriptor playImageDescriptor = getImageDescriptor("play_play.png");
	
	private boolean isPlaying;
	
	private final Spyglass spyglass;
	
	public PlayPlayPauseAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
		this.isPlaying = spyglass.isVisualizationRunning();
	}
	
	@Override
	public void run() {
		
		isPlaying = !spyglass.isVisualizationRunning();
		spyglass.setVisualizationRunning(isPlaying);
		
		log.debug("Set visualization " + (isPlaying ? "" : "not ") + "running.");
		
		setText(isPlaying ? "Pause" : "Play");
		setToolTipText(isPlaying ? "Pause" : "Play");
		setImageDescriptor(isPlaying ? pauseImageDescriptor : playImageDescriptor);
		
	};
	
	@Override
	public String getText() {
		return isPlaying ? "Pause" : "Play";
	}
	
	@Override
	public String getToolTipText() {
		return isPlaying ? "Pause" : "Play";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return isPlaying ? pauseImageDescriptor : playImageDescriptor;
	}
	
}
