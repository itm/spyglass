package de.uniluebeck.itm.spyglass.packetgenerator.gui;

import java.net.URL;

import org.apache.log4j.Category;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

public class PluginGeneratorGUIActions {
	
	private static final Category log = SpyglassLogger.get(PluginGeneratorGUIActions.class);
	
	private final PacketGeneratorIShellPlugin gen;
	
	private final URL getResourceUrl(final String suffix) {
		return de.uniluebeck.itm.spyglass.gui.actions.Action.class.getResource(suffix);
	}
	
	public PluginGeneratorGUIActions(final PacketGeneratorIShellPlugin gen) {
		this.gen = gen;
	}
	
	public final IAction PLAY_PLAY_PAUSE = new Action() {
		
		private final ImageDescriptor pauseImageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("play_pause.png"));
		
		private final ImageDescriptor playImageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("play_play.png"));
		
		private boolean isPlaying = true;
		
		@Override
		public void run() {
			
			isPlaying = !isPlaying;
			
			setText(isPlaying ? "Pause" : "Play");
			setToolTipText(isPlaying ? "Pause" : "Play");
			setImageDescriptor(isPlaying ? pauseImageDescriptor : playImageDescriptor);
			
			if (isPlaying) {
				log.debug("Resuming PacketGenerator.");
				gen.generator.resume();
			} else {
				log.debug("Pausing PacketGenerator.");
				gen.generator.pause();
			}
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
		
	};
	
}
