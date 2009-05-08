package de.uniluebeck.itm.spyglass.gui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.UIController;

public class OpenPreferencesAction extends Action {

	private final ImageDescriptor imageDescriptor = getImageDescriptor("open_preferences.png");

	private final Shell parentShell;

	private final Spyglass spyglass;

	public OpenPreferencesAction(final Shell parentShell, final Spyglass spyglass) {
		this.parentShell = parentShell;
		this.spyglass = spyglass;
	}

	@Override
	public void run() {
		UIController.openPreferencesDialog(parentShell, spyglass);
	};

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
