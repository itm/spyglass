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
import org.eclipse.swt.graphics.RGB;

//--------------------------------------------------------------------------------
/**
 * Converts an swt-Color object into an Array representing the RGB values.
 * 
 * @author Dariush Forouher
 * 
 */
public class ColorToArrayConverter extends Converter {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ColorToArrayConverter() {

		// leaks one Color ressource at construction
		super(null, new int[3]);
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {
		final Color color = (Color) fromObject;
		final RGB rgb = color.getRGB();
		return new int[] { rgb.red, rgb.green, rgb.blue };
	}

}
