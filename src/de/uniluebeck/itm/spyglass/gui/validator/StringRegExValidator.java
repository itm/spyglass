package de.uniluebeck.itm.spyglass.gui.validator;

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
	
	String pattern;
	String errorMsg;
	
	public StringRegExValidator(final String pattern, final String errorMsg) {
		this.pattern = pattern;
		this.errorMsg = errorMsg;
	}
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = (String) value;
			
			if (s.matches(pattern)) {
				return ValidationStatus.ok();
			} else {
				return ValidationStatus.error(errorMsg);
			}
		} else {
			return ValidationStatus.error("Unknown data!");
		}
	}
}
