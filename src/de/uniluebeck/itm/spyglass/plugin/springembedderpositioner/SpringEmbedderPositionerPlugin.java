/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.springembedderpositioner;

import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class SpringEmbedderPositionerPlugin extends NodePositionerPlugin {

	@Element(name = "parameters")
	private final SpringEmbedderPositionerXMLConfig xmlConfig;

	public SpringEmbedderPositionerPlugin() {
		xmlConfig = new SpringEmbedderPositionerXMLConfig();
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public AbsolutePosition getPosition(final int nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginPreferencePage<SpringEmbedderPositionerPlugin, SpringEmbedderPositionerXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SpringEmbedderPositionerPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<SpringEmbedderPositionerPlugin, SpringEmbedderPositionerXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SpringEmbedderPositionerPreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "SpringEmbedderPositioner";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateLayer() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean offersMetric() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Integer> getNodeList() {
		// TODO Auto-generated method stub
		return null;
	}

}