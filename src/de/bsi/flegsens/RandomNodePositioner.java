/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.bsi.flegsens;

import ishell.util.Logging;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Category;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * 
 */
public class RandomNodePositioner extends NodePositionerPlugin {
	private static Category log = Logging.get(RandomNodePositioner.class);

	private Random r = new Random();

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public Position getPosition(int nodeId) {
		Position p = new Position(Math.abs(r.nextFloat() * 800), Math.abs(r.nextFloat() * 800));
		log.debug("Random position: " + p);
		return p;
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void handlePacket(Packet packet) {
		//
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferencesConfigurationWidget createPreferencesWidget(
			Widget parent, ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferencesConfigurationWidget createTypePreferencesWidget(
			Widget parent, ConfigStore cs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DrawingObject> getDrawingObjects(DrawingArea drawingArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHumanReadableName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void processPacket(Packet packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXMLConfig(PluginXMLConfig xmlConfig) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateQuadTree() {
		// TODO Auto-generated method stub
		
	}

}
