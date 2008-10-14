/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.imagepainter;

import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link ImagePainterPlugin}
 * 
 * @author Sebastian Ebers, Dariush Forouher, Daniel Bimschas
 * 
 */
public class ImagePainterXMLConfig extends PluginXMLConfig {
	
	@Element(name = "image")
	private String imageFileName = "images/icons/brokenImageLink.png";
	
	@Element(name = "lowerLeftX")
	private int lowerLeftX = 0;
	
	@Element(name = "lowerLeftY")
	private int lowerLeftY = 0;
	
	@Element(name = "imageSizeX")
	private int imageSizeX = 0;
	
	@Element(name = "imageSizeY")
	private int imageSizeY = 0;
	
	@Element(name = "keepProportions")
	private boolean keepProportions = true;
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getLowerLeftX() {
		return lowerLeftX;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lowerLeftX
	 */
	public void setLowerLeftX(final int lowerLeftX) {
		final int oldValue = this.lowerLeftX;
		this.lowerLeftX = lowerLeftX;
		firePropertyChange("lowerLeftX", oldValue, lowerLeftX);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getLowerLeftY() {
		return lowerLeftY;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param lowerLeftY
	 */
	public void setLowerLeftY(final int lowerLeftY) {
		final int oldValue = this.lowerLeftY;
		this.lowerLeftY = lowerLeftY;
		firePropertyChange("lowerLeftY", oldValue, lowerLeftY);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getImageSizeX() {
		return imageSizeX;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param imageSizeX
	 */
	public void setImageSizeX(final int imageSizeX) {
		final int oldValue = this.imageSizeX;
		this.imageSizeX = imageSizeX;
		firePropertyChange("imageSizeX", oldValue, this.imageSizeX);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public int getImageSizeY() {
		return imageSizeY;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param imageSizeY
	 */
	public void setImageSizeY(final int imageSizeY) {
		final int oldValue = this.imageSizeY;
		this.imageSizeY = imageSizeY;
		firePropertyChange("imageSizeY", oldValue, this.imageSizeY);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public boolean isKeepProportions() {
		return keepProportions;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @param keepProportions
	 */
	public void setKeepProportions(final boolean keepProportions) {
		final boolean oldValue = this.keepProportions;
		this.keepProportions = keepProportions;
		firePropertyChange("keepProportions", oldValue, keepProportions);
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
		final String oldValue = this.imageFileName;
		this.imageFileName = imageFileName;
		firePropertyChange("imageFileName", oldValue, this.imageFileName);
	}
	
	public boolean equals(final ImagePainterXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		
		return imageFileName.equals((o).imageFileName) && (imageSizeX == o.imageSizeX)
				&& (imageSizeY == o.imageSizeY) && (lowerLeftX == o.lowerLeftX)
				&& (lowerLeftY == o.lowerLeftY);
	}
	
}