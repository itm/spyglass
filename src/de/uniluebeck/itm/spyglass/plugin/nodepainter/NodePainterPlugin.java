/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepainter;

import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * A plugin to paint a node.
 */
public abstract class NodePainterPlugin extends Plugin implements Drawable {
	
	public abstract float getTimeout();
	
	@Override
	public abstract boolean isVisible();
	
	public static String getHumanReadableName() {
		return "NodePainter";
	}
	
	@Override
	public String toString() {
		return NodePainterPlugin.getHumanReadableName() + "." + this.getName();
	}
	
}
