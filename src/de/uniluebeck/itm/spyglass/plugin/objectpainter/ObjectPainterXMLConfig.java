/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link ObjectPainterPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class ObjectPainterXMLConfig extends PluginXMLConfig {
	
	@Element(name = "image")
	private String imageFileName = "images/icons/brokenImageLink.png";
	
	@Element
	private AbsoluteRectangle size = new AbsoluteRectangle();
	
	// --------------------------------------------------------------------------------
	/**
	 * Constrcutor
	 */
	public ObjectPainterXMLConfig() {
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the imageFileName
	 */
	public String getImageFileName() {
		return imageFileName;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param imageFileName
	 *            the imageFileName to set
	 */
	public void setImageFileName(final String imageFileName) {
		this.imageFileName = imageFileName;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return the size
	 */
	public AbsoluteRectangle getSize() {
		return size;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(final AbsoluteRectangle size) {
		this.size = size;
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
}