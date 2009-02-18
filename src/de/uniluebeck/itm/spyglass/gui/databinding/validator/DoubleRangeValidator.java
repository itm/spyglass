// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.gui.databinding.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

// --------------------------------------------------------------------------------
/**
 * @author Oliver Kleine
 * 
 */
public class DoubleRangeValidator implements IValidator {

	private double minValue;
	private double maxValue;

	// --------------------------------------------------------------------------------
	/**
	 */
	public DoubleRangeValidator(final double minValue, final double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
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
			} else {
				return ValidationStatus.error(String.format("Value must be larger then %f.3 and smaller or equal to %f.3!", minValue, maxValue));
			}
		} else {
			return ValidationStatus.error("Unknown datatype");
		}
	}

}
