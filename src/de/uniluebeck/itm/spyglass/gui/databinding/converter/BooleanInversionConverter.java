package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Inverts a boolean value.
 * 
 * @author Sebastian Ebers
 * 
 */
public class BooleanInversionConverter extends Converter {
	
	public BooleanInversionConverter() {
		super(new Boolean(true), new Boolean(true));
	}
	
	@Override
	public Object convert(final Object fromObject) {
		
		return !((Boolean) fromObject).booleanValue();
		
	}
}
