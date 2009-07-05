/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.linepainter;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class LinePainterPreferencePage extends PluginPreferencePage<LinePainterPlugin, LinePainterXMLConfig> {

	private LinePainterOptionsComposite optionsComposite;
	private HashMap<Integer, String> tempStringFormatters;
	private final ISetChangeListener setChangeListener;

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
		setChangeListener = new ISetChangeListener() {
			// --------------------------------------------------------------------------------
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				markFormDirty();
			}
		};
	}

	public LinePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final LinePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
		setChangeListener = new ISetChangeListener() {
			// --------------------------------------------------------------------------------
			@Override
			public void handleSetChange(final SetChangeEvent event) {
				markFormDirty();
			}
		};
	}

	@Override
	protected void loadFromModel() {
		super.loadFromModel();
		tempStringFormatters = config.getStringFormatters();
		optionsComposite.stringFormatter.connectTableWithData(dbc, tempStringFormatters, setChangeListener);
	}

	@Override
	protected void storeToModel() {
		super.storeToModel();
		config.setStringFormatters(tempStringFormatters);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);

		optionsComposite = new LinePainterOptionsComposite(composite);
		optionsComposite.setDatabinding(dbc, config, this);
		tempStringFormatters = config.getStringFormatters();
		optionsComposite.stringFormatter.connectTableWithData(dbc, tempStringFormatters, setChangeListener);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return LinePainterPlugin.class;
	}

}