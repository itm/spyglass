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

public class PlayPlayPauseAction extends Action {

	private static final Logger log = SpyglassLoggerFactory.getLogger(PlayPlayPauseAction.class);

	private final ImageDescriptor pauseImageDescriptor = getImageDescriptor("play_pause.png");

	private final ImageDescriptor playImageDescriptor = getImageDescriptor("play_play.png");

	private boolean isPaused;

	private final Spyglass spyglass;

	public PlayPlayPauseAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
		this.isPaused = spyglass.isPaused();
	}

	@Override
	public void run() {

		isPaused = !spyglass.isPaused();

		spyglass.setPaused(isPaused);

		log.debug("packet reader is " + (isPaused ? "" : "not ") + "paused.");

		setText(getText());
		setToolTipText(getToolTipText());
		setImageDescriptor(getImageDescriptor());

	};

	@Override
	public String getText() {
		return isPaused ? "Resume" : "Pause";
	}

	@Override
	public String getToolTipText() {
		return isPaused ? "Play" : "Pause";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return isPaused ? playImageDescriptor : pauseImageDescriptor;
	}

}
