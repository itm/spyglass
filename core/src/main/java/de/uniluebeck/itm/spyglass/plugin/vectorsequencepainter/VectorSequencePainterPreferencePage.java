/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class VectorSequencePainterPreferencePage extends PluginPreferencePage<VectorSequencePainterPlugin, VectorSequencePainterXMLConfig> {

	public VectorSequencePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL);
	}

	public VectorSequencePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final VectorSequencePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		final VectorSequencePainterOptionsComposite optionsComposite = new VectorSequencePainterOptionsComposite(composite);

		optionsComposite.setDatabinding(dbc, config, this);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return VectorSequencePainterPlugin.class;
	}

}