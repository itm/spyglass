package de.uniluebeck.itm.spyglass.gui.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

// --------------------------------------------------------------------------------
/**
 * Checks if the String represents a list of integers, seperated by commata and ranges.
 * 
 * Example: 1,2,3
 * 
 * Example: 0-1000
 * 
 * Example: 10-20,30-50,10,10,10
 * 
 * @author Dariush Forouher
 * 
 */
public class StringToIntListValidator implements IValidator {
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = (String) value;
			
			if (s.matches("(-1)|((\\d+)|(\\d+-\\d+)(,((\\d+)|(\\d+-\\d+)))*)?")) {
				return ValidationStatus.ok();
			} else {
				return ValidationStatus
						.error("Please enter a comma-seperated list consisting of integers or integer ranges!");
			}
		} else {
			return ValidationStatus.error("Unknown data!");
		}
	}
}
