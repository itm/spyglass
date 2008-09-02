package de.uniluebeck.itm.spyglass.gui.converter;

import org.eclipse.core.databinding.conversion.IConverter;

import de.uniluebeck.itm.spyglass.util.StringFormatter;

/**
 * Creates an StringFormatter instance out of an String
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToStringFormatterConverter implements IConverter {
	
	@Override
	public Object convert(final Object fromObject) {
		if (fromObject instanceof String) {
			return new StringFormatter((String) fromObject);
		}
		return null;
	}
	
	@Override
	public Object getFromType() {
		return "";
	}
	
	@Override
	public Object getToType() {
		return new StringFormatter("");
	}
	
}
