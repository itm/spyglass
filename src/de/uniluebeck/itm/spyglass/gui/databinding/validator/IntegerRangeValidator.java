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

/**
 * Checks if an integer or an array of integers lays within the given range.
 * 
 * @author Dariush Forouher
 * @author Sebastian Ebers
 * 
 */
public class IntegerRangeValidator implements IValidator {

	private int min;
	private int max;
	private String fieldName;

	// --------------------------------------------------------------------------------
	/**
	 * Constrcutor
	 * 
	 * @param min
	 *            minimal value
	 * @param max
	 *            maximal value
	 */
	public IntegerRangeValidator(final int min, final int max) {
		this.fieldName = "";
		this.min = min;
		this.max = max;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param fieldName
	 *            the name of the field which provides the value to be validated
	 * @param min
	 *            the minimum value to be treated as valid
	 * @param max
	 *            the maximum value to be treated as valid
	 */
	public IntegerRangeValidator(final String fieldName, final int min, final int max) {
		this.fieldName = fieldName + ": ";
		this.min = min;
		this.max = max;
	}

	@Override
	public IStatus validate(final Object value) {
		if (value instanceof int[]) {
			final int[] list = (int[]) value;

			for (int i = 0; i < list.length; i++) {
				if ((list[i] > max) || (list[i] < min)) {
					return ValidationStatus.error(String.format(fieldName + "Values should be between %d and %d!", min, max));
				}
			}
			return ValidationStatus.ok();
		} else if (value instanceof Integer) {
			final int i = (Integer) value;

			if ((i <= max) && (i >= min)) {
				return ValidationStatus.ok();
			}
			return ValidationStatus.error(fieldName + String.format("Value should be between %d and %d!", min, max));
		} else {
			return ValidationStatus.error(fieldName + "Unknown data!");
		}
	}
}
