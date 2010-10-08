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

//--------------------------------------------------------------------------------
/**
 * Inverts a boolean value.
 * 
 * @author Sebastian Ebers
 * 
 */
public class BooleanInversionConverter extends Converter {

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public BooleanInversionConverter() {
		super(new Boolean(true), new Boolean(true));
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {

		return !((Boolean) fromObject).booleanValue();

	}
}
