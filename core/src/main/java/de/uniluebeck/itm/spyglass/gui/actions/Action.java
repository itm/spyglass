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
