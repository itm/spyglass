package de.uniluebeck.itm.spyglass.gui.validator;

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
	
	PluginManager manager;
	Plugin owner;
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 * @param manager
	 *            the spyglass plugin manager
	 * @param owner
	 *            this plugin will be excluded from the check (can be null)
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
