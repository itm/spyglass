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
 * Checks if the String matches a given regular expression.
 * 
 * @author Dariush Forouher
 * 
 */
public class StringRegExValidator implements IValidator {

	private final String pattern;
	private final String errorMsg;
	private final String fieldName;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param pattern
	 *            the pattern to be treated as valid
	 * @param errorMsg
	 *            the message to be shown if the string proves to be invalid
	 * 
	 */
	public StringRegExValidator(final String pattern, final String errorMsg) {
		this.fieldName = "";
		this.pattern = pattern;
		this.errorMsg = errorMsg;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param fieldName
	 *            the name of the field which provides the value to be validated
	 * @param pattern
	 *            the pattern to be treated as valid
	 * @param errorMsg
	 *            the message to be shown if the string proves to be invalid
	 * 
	 */
	public StringRegExValidator(final String fieldName, final String pattern, final String errorMsg) {
		this.fieldName = fieldName + ": ";
		this.pattern = pattern;
		this.errorMsg = errorMsg;
	}

	// --------------------------------------------------------------------------------
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = (String) value;

			if (s.matches(pattern)) {
				return ValidationStatus.ok();
			}
			return ValidationStatus.error(fieldName + errorMsg);
		}
		return ValidationStatus.error(fieldName + "Unknown data!");
	}
}
