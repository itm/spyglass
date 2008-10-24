package de.uniluebeck.itm.spyglass.gui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;

public class LoadConfigurationAction extends Action {
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("page_gear.png");
	
	private final Shell parentShell;
	
	private final Spyglass spyglass;
	
	public LoadConfigurationAction(final Shell parentShell, final Spyglass spyglass) {
		this.parentShell = parentShell;
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		spyglass.getConfigStore().loadFromFileSystem();
	};
	
	@Override
	public String getText() {
		return "Load Configuration";
	}
	
	@Override
	public String getToolTipText() {
		return "Load Configuration";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
