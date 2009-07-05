/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class ObjectPainterPreferencePage extends PluginPreferencePage<ObjectPainterPlugin, ObjectPainterXMLConfig> {

	public ObjectPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public ObjectPainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final ObjectPainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = super.createContents(parent);

		final ObjectPainterOptionsComposite optionsComposite = new ObjectPainterOptionsComposite(composite);

		optionsComposite.setDatabinding(dbc, config, spyglass, this);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return ObjectPainterPlugin.class;
	}

}