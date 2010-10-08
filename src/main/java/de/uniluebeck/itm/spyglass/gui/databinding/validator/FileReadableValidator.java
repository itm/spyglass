/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
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
			if (s.length()==0) {
				return ValidationStatus.warning("Enter a filename");
			}
			final File f = new File(s);
			if (f.canRead()) {
				return ValidationStatus.ok();
			} else if (!f.exists()) {
				return ValidationStatus.error("File not found!");
			} else {
				return ValidationStatus.error("File not readable!");
			}
		}
		return ValidationStatus.error("Unknown data!");
	}
}
