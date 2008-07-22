package de.uniluebeck.itm.spyglass.gui.actions;

import ishell.util.Logging;

import java.net.URL;

import org.apache.log4j.Category;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencesDialog;

public class Actions {
	
	private static final Category log = Logging.get(Actions.class);
	
	public static final IAction PLAY_SELECT_INPUT = new Action() {
		
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
			return ImageDescriptor.createFromURL(getResourceUrl("play_select_input.png"));
		}
		
	};
	
	private static final URL getResourceUrl(final String suffix) {
		return Actions.class.getResource(suffix);
	}
	
	public static final IAction PLAY_PLAY_PAUSE = new Action() {
		
		private final ImageDescriptor pauseImageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("play_pause.png"));
		
		private final ImageDescriptor playImageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("play_play.png"));
		
		private boolean isPlaying = false;
		
		@Override
		public void run() {
			
			isPlaying = !isPlaying;
			
			setText(isPlaying ? "Pause" : "Play");
			setToolTipText(isPlaying ? "Pause" : "Play");
			setImageDescriptor(isPlaying ? pauseImageDescriptor : playImageDescriptor);
			
			log.debug("Pressed button PLAY_PLAY.");
			
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
	
	public static final IAction PLAY_RESET = new Action() {
		
		private final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("play_reset.png"));
		
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
		
	};
	
	public static final IAction RECORD_SELECT_OUTPUT = new Action() {
		
		private final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("record_select_output.png"));
		
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
		
	};
	
	public static final IAction RECORD_RECORD = new Action() {
		
		private boolean isRecording = false;
		
		private final ImageDescriptor imageDescriptorRecording = ImageDescriptor.createFromURL(getResourceUrl("record_record.png"));
		
		private final ImageDescriptor imageDescriptorPausing = ImageDescriptor.createFromURL(getResourceUrl("record_pause.png"));
		
		@Override
		public void run() {
			
			isRecording = !isRecording;
			
			setText(isRecording ? "Pause" : "Record");
			setToolTipText(isRecording ? "Pause" : "Record");
			setImageDescriptor(isRecording ? imageDescriptorPausing : imageDescriptorRecording);
			
			log.debug("Pressed button RECORD_RECORD.");
			
		};
		
		@Override
		public String getToolTipText() {
			return isRecording ? "Pause" : "Record";
		}
		
		@Override
		public String getText() {
			return isRecording ? "Pause" : "Record";
		}
		
		@Override
		public ImageDescriptor getImageDescriptor() {
			return isRecording ? imageDescriptorPausing : imageDescriptorRecording;
		}
		
	};
	
	public static final IAction ZOOM_IN = new Action() {
		
		private final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("zoom_in.png"));
		
		@Override
		public void run() {
			log.debug("Pressed button ZOOM_IN.");
		};
		
		@Override
		public String getText() {
			return "Zoom In";
		}
		
		@Override
		public ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		
	};
	
	public static final IAction ZOOM_OUT = new Action() {
		
		private final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("zoom_out.png"));
		
		@Override
		public void run() {
			log.debug("Pressed button ZOOM_OUT.");
		};
		
		@Override
		public String getText() {
			return "Zoom Out";
		}
		
		@Override
		public String getToolTipText() {
			return "Zoom Out";
		}
		
		@Override
		public ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		
	};
	
	public static final IAction ZOOM_COMPLETE_MAP = new Action() {
		
		private final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("zoom_complete_map.png"));
		
		@Override
		public void run() {
			log.debug("Pressed button ZOOM_COMPLETE_MAP.");
		};
		
		@Override
		public String getText() {
			return "Zoom Complete Map";
		}
		
		@Override
		public String getToolTipText() {
			return "Zoom Complete Map";
		}
		
		@Override
		public ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		
	};
	
	public static final IAction OPEN_PREFERENCES = new Action() {
		
		private final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(getResourceUrl("open_preferences.png"));
		
		@Override
		public void run() {
			new PluginPreferencesDialog(null).open();
			log.debug("Pressed button OPEN_PREFERENCES.");
		};
		
		@Override
		public String getText() {
			return "Open Preferences";
		}
		
		@Override
		public String getToolTipText() {
			return "Open Preferences";
		}
		
		@Override
		public ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		
	};
	
}
