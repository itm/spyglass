/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.GeneralSettingsXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Preference page used to administer the application's general settings
 */
public class GeneralPreferencePage extends AbstractDatabindingPreferencePage {

	private static Logger log = SpyglassLoggerFactory.getLogger(GeneralPreferencePage.class);

	/**
	 * reference to the general setting config
	 */
	private final GeneralSettingsXMLConfig config;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param spyglass
	 *            the spyglass instance
	 */
	public GeneralPreferencePage(final Spyglass spyglass) {
		config = spyglass.getConfigStore().getSpyglassConfig().getGeneralSettings();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		getDefaultsButton().setEnabled(false);
		getApplyButton().setEnabled(false);
	}

	// --------------------------------------------------------------------------------
	@Override
	protected Control createContents(final Composite parent) {
		return createContentsInternal(parent);
	}

	// --------------------------------------------------------------------------------
	@Override
	protected Composite createContentsInternal(final Composite parent) {
		final Composite composite = super.createContentsInternal(parent);

		final GeneralPreferencesComposite comp = new GeneralPreferencesComposite(composite, SWT.NONE);

		comp.setDatabinding(dbc, config);

		// the initialization of the fields has dirtied the page
		resetDirtyFlag();

		return comp;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void performDefaults() {
		log.info("Pressed button restore");
		loadFromModel();

	}

	// --------------------------------------------------------------------------------
	@Override
	protected void resetDirtyFlag() {
		super.resetDirtyFlag();

		if (getDefaultsButton() != null) {
			getDefaultsButton().setEnabled(false);
		}
		if (getApplyButton() != null) {
			getApplyButton().setEnabled(false);
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public void markFormDirty() {
		super.markFormDirty();

		if (isValid()) {
			if (getDefaultsButton() != null) {
				getDefaultsButton().setEnabled(true);
			}
			if (getApplyButton() != null) {
				getApplyButton().setEnabled(true);
			}
		}

	}

}
