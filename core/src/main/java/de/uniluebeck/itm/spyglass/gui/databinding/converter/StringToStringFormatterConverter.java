/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding.converter;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.conversion.Converter;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

//--------------------------------------------------------------------------------
/**
 * Creates an StringFormatter instance out of an String
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToStringFormatterConverter extends Converter {

	private static final Logger log = SpyglassLoggerFactory.getLogger(StringToStringFormatterConverter.class);

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public StringToStringFormatterConverter() {
		super("", new StringFormatter(""));
	}

	// --------------------------------------------------------------------------------
	@Override
	public Object convert(final Object fromObject) {
		try {
			if (fromObject instanceof String) {
				return new StringFormatter((String) fromObject);
			}
		} catch (final IllegalArgumentException e) {
			log.error("An error occured while trying to use a string to initialize a StringFormatter object", e);
		}
		return null;
	}

}
