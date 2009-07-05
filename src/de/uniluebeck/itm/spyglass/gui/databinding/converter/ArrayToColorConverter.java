/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

//--------------------------------------------------------------------------------
/**
 * Converts an array of integers (representing RGB color values) into an swt-Color object.
 * 
 * Note: This class leaks ressources of type Color. But as a framework to dispose them would be
 * pretty complex and ugly, and as this databinding stuff is only deployed in the probably seldom
 * used PrefDialog, I hope we can live with this leak.
 * 
 * @author Dariush Forouher
 * 
 */
public class ArrayToColorConverter extends Converter {

	private Display device;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param device
	 *            the device on which to allocate the color
	 */
	public ArrayToColorConverter(final Display device) {
		super(new int[3], null);
		this.device = device;
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {
		final int[] color = (int[]) fromObject;
		return new Color(device, color[0], color[1], color[2]);
	}

}
