/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.core;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.SpyglassCanvas;
import de.uniluebeck.itm.spyglass.packet.PacketReader;
import de.uniluebeck.itm.spyglass.plugin.NodePositionerPlugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;

//--------------------------------------------------------------------------------
/**
 * Spyglass configuration file, mainly used by the deserialization process of the Spyglass class. The persistence
 * framework to utilize the serialization and deserialization to/from an XML file is "SimpleXML"
 * (http://simple.sourceforge.net/).
 */
@Root
public class SpyglassConfiguration {
	@Element
	private PacketReader packetReader = null;

	@Element
	private SpyglassCanvas canvas = null;

	@Element
	private PluginManager pluginManager = null;
	
	@Element
	private long packetDeliveryDelay = 100;

	@Element
	private long packetDeliveryInitialDelay = 1000;
	
	@Element(name = "framesPerSecond")
	private long fps = 25;

	@Element
	private NodePositionerPlugin nodePositioner;

	public long getFps() {
		return fps;
	}

	public void setFps(long fps) {
		this.fps = fps;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public SpyglassCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(SpyglassCanvas canvas) {
		this.canvas = canvas;
	}

	public PacketReader getPacketReader() {
		return packetReader;
	}

	public void setPacketReader(PacketReader packetReader) {
		this.packetReader = packetReader;
	}

	public void setNodePositioner(NodePositionerPlugin randomNodePositioner) {
		this.nodePositioner = randomNodePositioner;
	}

	public NodePositionerPlugin getNodePositioner() {
		return nodePositioner;
		
	}

	public long getPacketDeliveryDelay() {
		return packetDeliveryDelay;
	}

	public void setPacketDeliveryDelay(long packetDeliveryDelay) {
		this.packetDeliveryDelay = packetDeliveryDelay;
	}

	public long getPacketDeliveryInitialDelay() {
		return packetDeliveryInitialDelay;
	}

	public void setPacketDeliveryInitialDelay(long packetDeliveryInitialDelay) {
		this.packetDeliveryInitialDelay = packetDeliveryInitialDelay;
	}
	

}
