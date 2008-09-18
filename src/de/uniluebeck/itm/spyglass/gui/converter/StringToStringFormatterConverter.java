package de.uniluebeck.itm.spyglass.gui.converter;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.IConverter;

import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

/**
 * Creates an StringFormatter instance out of an String
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToStringFormatterConverter implements IConverter {
	
	private static final Logger log = SpyglassLogger.get(StringToStringFormatterConverter.class);
	
	@Override
	public Object convert(final Object fromObject) {
		try {
			if (fromObject instanceof String) {
				return new StringFormatter((String) fromObject);
			}
		} catch (final IllegalArgumentException e) {
			log
					.error(
							"An error occured while trying to use a string to initialize a StringFormatter object",
							e);
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
