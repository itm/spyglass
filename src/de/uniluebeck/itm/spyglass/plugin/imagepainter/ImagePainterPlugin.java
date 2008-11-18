/*
 * ------------------------------------------------------------------------------ -- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details. ----------------------------------------------
 * ----------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Image;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.QuadTree;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class ImagePainterPlugin extends BackgroundPainterPlugin implements PropertyChangeListener {
	
	@Element(name = "parameters")
	private final ImagePainterXMLConfig xmlConfig;
	
	private final Layer layer;
	
	private Image image;
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ImagePainterPlugin() {
		super(false);
		xmlConfig = new ImagePainterXMLConfig();
		layer = new QuadTree();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new ImagePainterPreferencePage(dialog, spyglass, this);
	}
	
	public static PluginPreferencePage<ImagePainterPlugin, ImagePainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new ImagePainterPreferencePage(dialog, spyglass);
	}
	
	public List<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		synchronized (layer) {
			return layer.getDrawingObjects(area);
		}
	}
	
	public static String getHumanReadableName() {
		return "ImagePainter";
	}
	
	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}
	
	@Override
	public void handlePacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
	@Override
	protected void processPacket(final SpyglassPacket packet) {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
	@Override
	public void reset() {
		// nothing to do since this plugin doesn't keep any data
		// in memory to reset
	}
	
	@Override
	protected void updateQuadTree() {
		// since the plug-in is not interested in packets, nothing has to be
		// done here
	}
	
	@Override
	public void init(final PluginManager manager) {
		
		super.init(manager);
		
		// adding this listener here so it is called
		// on programm startup (doesn't work in constructor!)
		xmlConfig.addPropertyChangeListener(this);
		
		// dito
		loadImage();
		
	}
	
	private void loadImage() {
		
		if (image != null) {
			synchronized (layer) {
				layer.remove(image);
				image.dispose();
			}
		}
		fireDrawingObjectRemoved(image);
		
		try {
			image = new Image(xmlConfig.getImageFileName());
		} catch (final RuntimeException e) {
			image = new Image("images/icons/brokenImageLink.png");
		}
		
		final int sizeX = xmlConfig.getImageSizeX();
		final int sizeY = xmlConfig.getImageSizeY();
		final int llX = xmlConfig.getLowerLeftX();
		final int llY = xmlConfig.getLowerLeftY();
		
		final AbsolutePosition position = new AbsolutePosition(llX, llY, 0);
		image.setPosition(position);
		image.setImageSizeX(sizeX);
		image.setImageSizeY(sizeY);
		
		synchronized (layer) {
			layer.addOrUpdate(image);
		}
		fireDrawingObjectAdded(image);
		
	}
	
	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		loadImage();
	}
	
}