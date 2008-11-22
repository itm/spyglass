package de.uniluebeck.itm.spyglass.gui.databinding.validator;

import java.io.File;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Checks if a String points to file which exists and is readable.
 * 
 * @author Dariush Forouher
 * 
 */
public class FileReadableValidator implements IValidator {
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = (String) value;
			final File f = new File(s);
			if (f.canRead()) {
				return ValidationStatus.ok();
			} else if (!f.exists()) {
				return ValidationStatus.error("File not found!");
			} else {
				return ValidationStatus.error("File not readable!");
			}
		} else {
			return ValidationStatus.error("Unknown data!");
		}
	}
}
