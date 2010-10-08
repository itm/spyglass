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
import org.eclipse.swt.widgets.Display;

// --------------------------------------------------------------------------------
/**
 * @author bimschas
 * 
 */
public class RGBToColorConverter implements IConverter {

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {
		// will produce swt ressource leak
		return new Color(Display.getDefault(), (RGB) fromObject);
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object getFromType() {
		return RGB.class;
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object getToType() {
		return Color.class;
	}

}
