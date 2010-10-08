/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.actions;

import org.eclipse.jface.window.ApplicationWindow;

public class ExitSpyglassAction extends Action {

	// private final ImageDescriptor imageDescriptor = getImageDescriptor("open_preferences.png");

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

	// @Override
	// public ImageDescriptor getImageDescriptor() {
	// return imageDescriptor;
	// }

}
