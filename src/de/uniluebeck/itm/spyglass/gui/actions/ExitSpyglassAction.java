package de.uniluebeck.itm.spyglass.gui.actions;

import org.eclipse.jface.window.ApplicationWindow;

public class ExitSpyglassAction extends Action {
	
//	private final ImageDescriptor imageDescriptor = getImageDescriptor("open_preferences.png");
	
	private final ApplicationWindow window;
	
	public ExitSpyglassAction(final ApplicationWindow window) {
		this.window = window;
	}
	
	@Override
	public void run() {
		window.close();
	};
	
	@Override
	public String getText() {
		return "E&xit";
	}
	
	@Override
	public String getToolTipText() {
		return "Exit Spyglass";
	}
	
//	@Override
//	public ImageDescriptor getImageDescriptor() {
//		return imageDescriptor;
//	}
	
}
