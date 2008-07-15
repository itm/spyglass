/* -----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
-------------------------------------------------------------------------*/
package de.bsi.flegsens;

import java.util.List;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.swt.widgets.Widget;

import de.uniluebeck.itm.spyglass.configuration.PreferencesConfigurationWidget;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.packet.Packet;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin.Position;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * A plugin to paint a node.
 */
public class FlegsensNodePainterPlugin extends de.uniluebeck.itm.spyglass.plugin.NodePainterPlugin {
	private static Category log = Logging.get(FlegsensNodePainterPlugin.class);

	// --------------------------------------------------------------------------------
	/**
	 * @see Plugin#handlePacket(Packet)
	 */
	@Override
	public void handlePacket(Packet packet) {
		if (log.isDebugEnabled())
			log.debug("Handling packet " + packet);

		log.debug("pm: " + getPluginManager());
		log.debug("np: " + getPluginManager().getNodePositioner());

		Position p = getPluginManager().getNodePositioner().getPosition(packet.getId());
		if (p != null) {
			DrawingObject rect = new Rectangle();
			rect.setId(11);
			rect.setColorR(255);
			rect.setColorG(0);
			rect.setColorB(0);
			rect.setPosition(p);

			//getSubLayer().addOrUpdateDrawingObject(rect); // TODO
		}
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
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
