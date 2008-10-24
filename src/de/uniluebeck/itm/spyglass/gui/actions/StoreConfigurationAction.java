package de.uniluebeck.itm.spyglass.gui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;

public class StoreConfigurationAction extends Action {
	
	private final ImageDescriptor imageDescriptor = getImageDescriptor("page_save.png");
	
	private final Shell parentShell;
	
	private final Spyglass spyglass;
	
	public StoreConfigurationAction(final Shell parentShell, final Spyglass spyglass) {
		this.parentShell = parentShell;
		this.spyglass = spyglass;
	}
	
	@Override
	public void run() {
		spyglass.getConfigStore().storeFileSystem();
	};
	
	@Override
	public String getText() {
		return "Store Configuration";
	}
	
	@Override
	public String getToolTipText() {
		return "Store Configuration";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}
	
}
