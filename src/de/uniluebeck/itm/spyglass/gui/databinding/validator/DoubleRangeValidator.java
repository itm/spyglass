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
 * Instances of this class validate that a {@link Double} value ranges between a certain minimum and
 * maximum value.
 * 
 * @author Oliver Kleine
 * @author Sebastian Ebers
 * 
 */
public class DoubleRangeValidator implements IValidator {

	private final double minValue;
	private final double maxValue;
	private final String fieldName;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the minimum value to be treated as valid
	 * @param maxValue
	 *            the maximum value to be treated as valid
	 */
	public DoubleRangeValidator(final double minValue, final double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.fieldName = "";
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param fieldName
	 *            the name of the field which provides the value to be validated
	 * @param minValue
	 *            the minimum value to be treated as valid
	 * @param maxValue
	 *            the maximum value to be treated as valid
	 */
	public DoubleRangeValidator(final String fieldName, final double minValue, final double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.fieldName = fieldName + ": ";
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof Double) {
			final double d = (Double) value;
			if ((d > minValue) && (d <= maxValue)) {
				return ValidationStatus.ok();
			}
			return ValidationStatus.error(fieldName
					+ String.format("Value must be larger then %f.3 and smaller or equal to %f.3!", minValue, maxValue));
		}
		return ValidationStatus.error(fieldName + "Unknown datatype");
	}

}
