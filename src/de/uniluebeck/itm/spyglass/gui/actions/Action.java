package de.uniluebeck.itm.spyglass.gui.actions;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

public abstract class Action extends org.eclipse.jface.action.Action {
	
	private URL getResourceUrl(final String suffix) {
		return Action.class.getResource(suffix);
	}
	
	protected ImageDescriptor getImageDescriptor(final String fileName) {
		return ImageDescriptor.createFromURL(getResourceUrl(fileName));
	}
	
}
