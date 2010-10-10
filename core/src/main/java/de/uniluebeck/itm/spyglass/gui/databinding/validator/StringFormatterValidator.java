// --------------------------------------------------------------------------------
/**
 * 
 */
package de.uniluebeck.itm.spyglass.gui.databinding.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.uniluebeck.itm.spyglass.util.StringFormatter;

// --------------------------------------------------------------------------------
/**
 * @author Oliver Kleine
 * 
 */
public class StringFormatterValidator implements IValidator {

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(final Object arg0) {
		try {
			new StringFormatter((String) arg0);
			return ValidationStatus.ok();
		} catch (final Exception e) {
			return ValidationStatus.error("Not a valid format string!");
		}
	}

}
