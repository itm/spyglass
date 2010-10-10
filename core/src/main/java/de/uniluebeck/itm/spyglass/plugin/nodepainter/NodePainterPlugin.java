/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodepainter;

import de.uniluebeck.itm.spyglass.plugin.Drawable;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are used create and administer visualizations of sensor nodes.
 * 
 * @author Sebastian Ebers
 */
public abstract class NodePainterPlugin extends Plugin implements Drawable {

	// private final Logger log = SpyglassLoggerFactory.getLogger(NodePainterPlugin.class);

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public NodePainterPlugin() {
		// for this plug-in a packet queue has to be used
		super(true);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the time the plug-in's drawing objects will be visible
	 * 
	 * @return he time the plug-in's drawing objects will be visible
	 */
	public final int getTimeout() {
		return getXMLConfig().getTimeout();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the plug-in's denotation in a human readable style
	 * 
	 * @return the plug-in's denotation in a human readable style
	 */
	public static String getHumanReadableName() {
		return "NodePainter";
	}

	@Override
	public String toString() {
		return NodePainterPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}

}
