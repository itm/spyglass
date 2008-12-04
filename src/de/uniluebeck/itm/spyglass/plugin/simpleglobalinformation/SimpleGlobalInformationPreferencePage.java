/* 
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.xmlconfig.StatisticalInformationEvaluator;

//--------------------------------------------------------------------------------
/**
 * Instances of this class are widgets to create, delete and edit configurations for
 * {@link SimpleGlobalInformationPlugin}s
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationPreferencePage extends PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> {

	private Set<StatisticalInformationEvaluator> tempStringFormatterSettingsList = new TreeSet<StatisticalInformationEvaluator>();

	private SimpleGlobalInformationOptionsComposite optionsComposite;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param dialog
	 *            the {@link PluginPreferenceDialog} window
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 */
	public SimpleGlobalInformationPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param dialog
	 *            the {@link PluginPreferenceDialog} window
	 * @param spyglass
	 *            the active {@link Spyglass} object
	 * @param plugin
	 *            a {@link SimpleGlobalInformationPlugin} instance
	 */
	public SimpleGlobalInformationPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass,
			final SimpleGlobalInformationPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}

	// --------------------------------------------------------------------------------
	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		optionsComposite = new SimpleGlobalInformationOptionsComposite(composite, SWT.NONE);

		optionsComposite.setDatabinding(dbc, config);

		tempStringFormatterSettingsList = config.getStatisticalInformationEvaluators();
		optionsComposite.getStringFormatter().connectTableWithData(dbc, tempStringFormatterSettingsList);

		return composite;
	}

	// --------------------------------------------------------------------------------
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return SimpleGlobalInformationPlugin.class;
	}

	// public static void main(final String[] args) throws IOException {
	// final Display display = Display.getDefault();
	// final Shell shell = new Shell(display);
	// final Spyglass sg = new Spyglass();
	// final PluginPreferenceDialog inst = new PluginPreferenceDialog(shell, sg);
	// inst.open();
	// }
}