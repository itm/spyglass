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
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

//--------------------------------------------------------------------------------
/**
 * Instances of this class are widgets to create, delete and edit configurations for
 * {@link SimpleGlobalInformationPlugin}s
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationPreferencePage extends PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> {

	private static final Logger log = SpyglassLoggerFactory.getLogger(SimpleGlobalInformationPreferencePage.class);
	private SimpleGlobalInformationOptionsComposite optionsComposite;
	private Set<StatisticalInformationEvaluator> stringFormatterTable;

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
		stringFormatterTable = new TreeSet<StatisticalInformationEvaluator>();
		final Set<StatisticalInformationEvaluator> tmp = config.getStatisticalInformationEvaluators();
		for (final StatisticalInformationEvaluator statisticalInformationEvaluator : tmp) {
			stringFormatterTable.add(statisticalInformationEvaluator.clone());
		}
		optionsComposite.getStringFormatter().connectTableWithData(dbc, stringFormatterTable);
		return composite;
	}

	// --------------------------------------------------------------------------------
	@Override
	public Class<? extends Plugin> getPluginClass() {
		return SimpleGlobalInformationPlugin.class;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void loadFromModel() {
		super.loadFromModel();
		stringFormatterTable.clear();
		final Set<StatisticalInformationEvaluator> tmp = config.getStatisticalInformationEvaluators();
		for (final StatisticalInformationEvaluator statisticalInformationEvaluator : tmp) {
			stringFormatterTable.add(statisticalInformationEvaluator.clone());
		}
		optionsComposite.getStringFormatter().connectTableWithData(dbc, stringFormatterTable);
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void storeToModel() {
		super.storeToModel();

		// prevent two objects from sharing the same semantic type and description
		final Vector<StatisticalInformationEvaluator> evaluators = new Vector<StatisticalInformationEvaluator>(stringFormatterTable);
		for (int i = 0; i < evaluators.size() - 1; i++) {
			final StatisticalInformationEvaluator e1 = evaluators.get(i);
			for (int j = i + 1; j < evaluators.size(); j++) {
				final StatisticalInformationEvaluator e2 = evaluators.get(j);
				if ((e1.getSemanticType() == e2.getSemanticType()) && e1.getDescription().equals(e2.getDescription())) {
					final String message = "Two configurations with the same semantic type and description have been found. One description was"
							+ " slightly changed to prevent errors.";
					log.warn(message);
					MessageDialog.openWarning(null, "Duplicate Elements", message);
					e2.setDescription(e2.getDescription() + ".");
				}
			}
		}

		final Set<StatisticalInformationEvaluator> tmp = new TreeSet<StatisticalInformationEvaluator>();
		for (final StatisticalInformationEvaluator statisticalInformationEvaluator : evaluators) {
			tmp.add(statisticalInformationEvaluator.clone());
		}
		config.setStatisticalInformationEvaluators(tmp);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void dispose() {
		// the object will be null if the preference page was just added to the tree and not
		// selected by the user
		if (optionsComposite != null) {
			optionsComposite.dispose();
		}

		super.dispose();
	}
}