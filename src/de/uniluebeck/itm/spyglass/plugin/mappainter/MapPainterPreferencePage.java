/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class MapPainterPreferencePage extends PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> {

	private MapPainterPrefComposite optionsComposite;

	public MapPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public MapPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final MapPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected Composite createContents(final Composite parent) {

		final Composite composite = super.createContents(parent);

		optionsComposite = new MapPainterPrefComposite(composite, SWT.NONE);

		optionsComposite.setDatabinding(dbc, config, spyglass, this);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return MapPainterPlugin.class;
	}

}