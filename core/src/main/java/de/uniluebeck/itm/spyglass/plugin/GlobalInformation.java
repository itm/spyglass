/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin;

import de.uniluebeck.itm.spyglass.gui.view.GlobalInformationWidget;

// --------------------------------------------------------------------------------
/**
 * Implementations of this interface provide global information to be shown by the application in a
 * decided area.
 * 
 * @author Sebastian Ebers
 * 
 */
public interface GlobalInformation {

	/**
	 * Sets the widget to be used to display global information.<br>
	 * This widget has to be maintained during the whole life cycle of the plug-in which implements
	 * this interface. A plug-in which does not want the widget to be used has to set its visibility
	 * state to <tt>invisible</tt>
	 * 
	 * @param widget
	 *            the widget to be used to display global information
	 */
	public void setWidget(GlobalInformationWidget widget);

}