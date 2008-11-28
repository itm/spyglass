package de.uniluebeck.itm.spyglass.gui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.SelectPacketSourceDialog;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class PlaySelectInputAction extends Action {

	private static final Logger log = SpyglassLoggerFactory.getLogger(PlaySelectInputAction.class);
	private final Spyglass spyglass;
	private final Shell parentShell;

	// --------------------------------------------------------------------------------
	/**
	 */
	public PlaySelectInputAction(final Shell parentShell, final Spyglass spyglass) {
		this.parentShell = parentShell;
		this.spyglass = spyglass;
	}

	@Override
	public void run() {
		log.debug("Pressed Button PLAY_SELECT_INPUT.");
		if (new SelectPacketSourceDialog(parentShell, spyglass).open() == Window.OK) {
			spyglass.reset();
		}

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
		return getImageDescriptor("play_select_input.png");
	}

}
