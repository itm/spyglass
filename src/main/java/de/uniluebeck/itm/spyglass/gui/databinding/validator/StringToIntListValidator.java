/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.databinding.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

// --------------------------------------------------------------------------------
/**
 * Checks if the String represents a list of signed integers, seperated by commata and ranges.
 * 
 * Example: 1,2,3,-5
 * 
 * Example: 0-1000,-10-100, -20--10
 * 
 * Example: 10-20,30-50,10,10,10
 * 
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToIntListValidator implements IValidator {

	private final static String REGEX_INT = "(-?\\d+)";
	private final static String REGEX_RANGE = String.format("((%s-%s)|%s)", REGEX_INT, REGEX_INT, REGEX_INT);
	private final static String REGEX_LIST = String.format("%s(,%s)*", REGEX_RANGE, REGEX_RANGE);
	private final String fieldName;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public StringToIntListValidator() {
		fieldName = "";
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param fieldName
	 *            the name of the field which provides the value to be validated
	 */
	public StringToIntListValidator(final String fieldName) {
		this.fieldName = fieldName + ": ";
	}

	// --------------------------------------------------------------------------------
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = (String) value;

			if (s.matches(REGEX_LIST)) {
				return ValidationStatus.ok();
			}
			return ValidationStatus.error(fieldName + "Please enter a comma-seperated list consisting of integers or integer ranges!");
		}
		return ValidationStatus.error(fieldName + "Unknown data!");
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether a string is valid concerning the internal constrains.
	 * 
	 * @param str
	 *            the string to be validated
	 * @return <code>true</code> if the string is valid when compared against internal constrains.
	 */
	public static boolean isValid(final String str) {
		return str.matches(REGEX_LIST);
	}

}
