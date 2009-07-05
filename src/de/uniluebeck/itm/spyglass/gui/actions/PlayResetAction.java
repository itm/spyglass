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

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class PlayResetAction extends Action {

	private static final Logger log = SpyglassLoggerFactory.getLogger(PlayResetAction.class);

	private Spyglass spyglass;

	private final ImageDescriptor imageDescriptor = getImageDescriptor("play_reset.png");

	public PlayResetAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}

	@Override
	public void run() {
		log.debug("Pressed button PLAY_RESET.");
		spyglass.reset();
	}

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

}
