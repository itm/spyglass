/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.template;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.SortedSet;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

//--------------------------------------------------------------------------------
/**
 * This is a template plugin which can be used for coding new plugins.
 * 
 * By inheritance it is a BackgroundPainter.
 * 
 * Look also for the other classes in this java package. These classes (a Plugin, an XMLConfig and a
 * PreferencePage) represent the minimum any plugin writer must implement.
 * 
 * To activate this Plugin, you have to add it to PluginManager.availablePluginsTypes
 * 
 * @author Dariush Forouher
 * 
 */
public class TemplatePlugin extends BackgroundPainterPlugin implements PropertyChangeListener {

	/**
	 * Our configuration. Plugin and configuration are coupled tightly together (one-2-one
	 * relationship)
	 */
	@Element(name = "parameters")
	private final TemplateXMLConfig xmlConfig = new TemplateXMLConfig();

	/**
	 * The Datastructure where to store DrawingObjects. If this plugin would not draw anything on
	 * the DrawingArea, the layer could be ommited.
	 */
	private final Layer layer = Layer.Factory.createQuadTreeLayer();

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public TemplatePlugin() {

		// Tell the super class we wish to receive packets
		super(true);

	}

	// --------------------------------------------------------------------------------
	@Override
	public PluginPreferencePage<TemplatePlugin, TemplateXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new TemplatePreferencePage(dialog, spyglass, this);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a widget used for the configuration of an instance of this class
	 * 
	 * @param dialog
	 *            the dialog where the widget is attached
	 * @param spyglass
	 *            a {@link Spyglass} instance
	 * @return a widget used for the configuration of an instance of this class
	 */
	public static PluginPreferencePage<TemplatePlugin, TemplateXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new TemplatePreferencePage(dialog, spyglass);
	}

	// --------------------------------------------------------------------------------
	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

	// --------------------------------------------------------------------------------
	public static String getHumanReadableName() {
		return "TemplatePainter";
	}

	// --------------------------------------------------------------------------------
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	// --------------------------------------------------------------------------------
	@Override
	protected void processPacket(final SpyglassPacket p) {

		// A new packet has arrived, deal with it.

	}

	// --------------------------------------------------------------------------------
	@Override
	protected void resetPlugin() {
		synchronized (layer) {
			layer.clear();
		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final PluginManager manager) throws Exception {

		super.init(manager);
		xmlConfig.addPropertyChangeListener(this);

	}

	// --------------------------------------------------------------------------------
	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		xmlConfig.removePropertyChangeListener(this);
	}

	// --------------------------------------------------------------------------------
	@Override
	public Set<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// Some parameter in the config file has been modified.
	}

}