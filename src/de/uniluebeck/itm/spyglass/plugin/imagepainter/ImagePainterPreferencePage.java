/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.eclipse.swt.widgets.Composite;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class ImagePainterPreferencePage extends PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> {

	public ImagePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		super(dialog, spyglass, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}

	public ImagePainterPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass, final ImagePainterPlugin plugin) {
		super(dialog, spyglass, plugin, BasicOptions.ALL_BUT_SEMANTIC_TYPES);
	}

	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createContentsInternal(parent);
		final ImagePainterOptionsComposite optionsComposite = new ImagePainterOptionsComposite(composite);

		optionsComposite.setDatabinding(dbc, config, spyglass);

		return composite;
	}

	@Override
	public Class<? extends Plugin> getPluginClass() {
		return ImagePainterPlugin.class;
	}

}