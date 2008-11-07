package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.Converter;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

/**
 * Creates an StringFormatter instance out of an String
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToStringFormatterConverter extends Converter {
	
	public StringToStringFormatterConverter() {
		super("", new StringFormatter(""));
	}
	
	private static final Logger log = SpyglassLoggerFactory.get(StringToStringFormatterConverter.class);
	
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
	
}
