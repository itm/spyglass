/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class ColorToRGBConverter implements IConverter {

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object arg0) {
		return ((Color) arg0).getRGB();
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object getFromType() {
		return Color.class;
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object getToType() {
		return RGB.class;
	}

}
