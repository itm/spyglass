/*
 * ---------------------------------------------------------------------- This
 * file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.globalinformation;

import de.uniluebeck.itm.spyglass.plugin.GlobalInformation;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

// --------------------------------------------------------------------------------
/**
 * Implementations of this class are plug-ins which provide global information.
 * 
 * @author Sebastian Ebers
 */
public abstract class GlobalInformationPlugin extends Plugin implements GlobalInformation {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor enabling the plug-in to receive packets
	 */
	public GlobalInformationPlugin() {
		super(true);
	}

	// --------------------------------------------------------------------------------
	public static String getHumanReadableName() {
		return "GlobalInformation";
	}

	// --------------------------------------------------------------------------------
	@Override
	public String toString() {
		return GlobalInformationPlugin.getHumanReadableName() + "." + this.getInstanceName();
	}

}
