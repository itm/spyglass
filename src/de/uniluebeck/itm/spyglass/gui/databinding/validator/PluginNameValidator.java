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

import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;

// --------------------------------------------------------------------------------
/**
 * Checks if the given String is already in use as a name of a plugin
 * 
 * @author Dariush Forouher
 * 
 */
public class PluginNameValidator implements IValidator {

	private PluginManager manager;
	private Plugin owner;

	// --------------------------------------------------------------------------------
	/**
	 * Constrcutor
	 * 
	 * @param manager
	 *            the spyglass plug-in manager
	 * @param owner
	 *            this plug-in will be excluded from the check (can be null)
	 */
	public PluginNameValidator(final PluginManager manager, final Plugin owner) {
		this.manager = manager;
		this.owner = owner;
	}

	@Override
	public IStatus validate(final Object value) {

		for (final Plugin p : manager.getPlugins()) {
			if (p.getInstanceName().equals(value) && (p != owner)) {
				return ValidationStatus.error("Plugin name already in use!");
			}
		}
		return ValidationStatus.ok();
	}
}
