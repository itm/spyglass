package de.uniluebeck.itm.spyglass.gui.converter;

import org.eclipse.core.databinding.conversion.IConverter;

public class BooleanInversionConverter implements IConverter {
	
	@Override
	public Object convert(final Object fromObject) {
		
		return !((Boolean) fromObject).booleanValue();
	}
	
	@Override
	public Object getFromType() {
		return new Boolean(true);
	}
	
	@Override
	public Object getToType() {
		return new Boolean(true);
	}
	
}
