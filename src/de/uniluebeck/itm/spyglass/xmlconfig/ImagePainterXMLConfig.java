/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.xmlconfig;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.plugin.ImagePainterPlugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a
 * {@link ImagePainterPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class ImagePainterXMLConfig extends PluginXMLConfig {
	
	@Element(name = "image")
	private String imageFileName = "images/icons/brokenImageLink.png";
	
	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ImagePainterXMLConfig() {
		
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
	@Override
	public void finalize() throws Throwable {
		super.finalize();
	}
	
}