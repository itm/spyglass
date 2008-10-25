package de.uniluebeck.itm.spyglass.gui.databinding.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Checks if an integer or an array of integers lays within the given range.
 * 
 * @author Dariush Forouher
 * 
 */
public class IntegerRangeValidator implements IValidator {
	
	private int min;
	private int max;
	
	// --------------------------------------------------------------------------------
	/**
	 * @param min
	 *            minimal value
	 * @param max
	 *            maximal value
	 */
	public IntegerRangeValidator(final int min, final int max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof int[]) {
			final int[] list = (int[]) value;
			
			for (int i = 0; i < list.length; i++) {
				if ((list[i] > max) || (list[i] < min)) {
					return ValidationStatus.error(String.format(
							"Values should be between %d and %d!", min, max));
				}
			}
			return ValidationStatus.ok();
		} else if (value instanceof Integer) {
			final int i = (Integer) value;
			
			if ((i <= max) && (i >= min)) {
				return ValidationStatus.ok();
			} else {
				return ValidationStatus.error(String.format("Value should be between %d and %d!",
						min, max));
			}
		} else {
			return ValidationStatus.error("Unknown data!");
		}
	}
}
