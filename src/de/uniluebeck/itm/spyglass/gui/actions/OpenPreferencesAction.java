package de.uniluebeck.itm.spyglass.gui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;

public class OpenPreferencesAction extends Action {

	private final ImageDescriptor imageDescriptor = getImageDescriptor("open_preferences.png");

	private final Spyglass spyglass;
	private final Shell shell;

	public OpenPreferencesAction(final Spyglass spyglass, final Shell parent) {
		this.spyglass = spyglass;
		this.shell = parent;
	}

	@Override
	public void run() {

		// HACK: if there is already a prefDialog open, don't open another one
		if (spyglass.isThrottleBBoxUpdates()) {
			return;
		}

		spyglass.setThrottleBBoxUpdates(true);
		new PluginPreferenceDialog(shell, spyglass).open();
		spyglass.setThrottleBBoxUpdates(false);
	}

	@Override
	public String getText() {
		return "&Preferences";
	}

	@Override
	public String getToolTipText() {
		return "Open Preferences";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

}
