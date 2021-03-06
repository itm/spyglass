/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.nodesensorrange.NodeSensorRangeXMLConfig.Config;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class NodeSensorRangePreferencePage extends PluginPreferencePage<NodeSensorRangePlugin, NodeSensorRangeXMLConfig> {

	private static final Logger log = SpyglassLoggerFactory.getLogger(NodeSensorRangePreferencePage.class);
	private final ISetChangeListener setChangeListener;

	/**
	 * Reference to the map backing {@link tableData}. This map is a copy of the one from the
	 * XMLConfig. Changes in the table reflect on this map. the entries of this map are propagated
	 * tho the original map in the XMLConfig only when storeToModel() is called.
	 */
	private HashSet<Config> tempTable = new HashSet<Config>();

	private NodeSensorRangeOptionsComposite optionsComposite;

	public NodeSensorRangePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
		setChangeListener = new ISetChangeListener() {
			// --------------------------------------------------------------------------------
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				markFormDirty();
			}
		};
	}

	public NodeSensorRangePreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final NodeSensorRangePlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
		setChangeListener = new ISetChangeListener() {
			// --------------------------------------------------------------------------------
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				markFormDirty();
			}
		};
	}

	@Override
	protected Composite createContents(final Composite parent) {

		final Composite composite = createContentsInternal(parent);

		optionsComposite = new NodeSensorRangeOptionsComposite(composite);
		optionsComposite.setDatabinding(dbc, config.getDefaultConfig(), this);

		tempTable = config.getPerNodeConfigsClone();

		optionsComposite.getPerNodeConfigurationComposite().connectTableWithData(dbc, tempTable, setChangeListener);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return NodeSensorRangePlugin.class;
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#dispose()
	 */
	@Override
	public void dispose() {
		// the object will be null if the preference page was just added to the tree and not
		// selected by the user
		if (optionsComposite != null) {
			optionsComposite.dispose();
		}
		super.dispose();
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#loadFromModel()
	 */
	@Override
	protected void loadFromModel() {
		super.loadFromModel();

		log.info("load from model");

		tempTable = config.getPerNodeConfigsClone();
		optionsComposite.getPerNodeConfigurationComposite().connectTableWithData(dbc, tempTable, setChangeListener);
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage#storeToModel()
	 */
	@Override
	public void storeToModel() {
		super.storeToModel();

		log.info("store to model");

		final Config updatedDefaultConfig = optionsComposite.getDefaultConfig();
		if (!config.getDefaultConfig().equals(updatedDefaultConfig)) {
			config.setDefaultConfig(updatedDefaultConfig);
		}
		config.setPerNodeConfigs(this.tempTable);
		tempTable = config.getPerNodeConfigsClone();
		optionsComposite.getPerNodeConfigurationComposite().connectTableWithData(dbc, tempTable, setChangeListener);
	}

}