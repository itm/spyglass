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

	public static final String PROPERTYNAME_IMAGE_FILE_NAME = "imageFileName";

	public static final String PROPERTYNAME_KEEP_PROPORTIONS = "keepProportions";

	public static final String PROPERTYNAME_IMAGE_SIZE_Y = "imageSizeY";

	public static final String PROPERTYNAME_IMAGE_SIZE_X = "imageSizeX";

	public static final String PROPERTYNAME_LOWER_LEFT_Y = "lowerLeftY";

	public static final String PROPERTYNAME_LOWER_LEFT_X = "lowerLeftX";

	@Element(required = false)
	private volatile String imageFileName = "images/icons/brokenImageLink.png";

	@Element(required = false)
	private volatile  int lowerLeftX = 0;

	@Element(required = false)
	private volatile  int lowerLeftY = 0;

	@Element(required = false)
	private volatile  int imageSizeX = 0;

	@Element(required = false)
	private  volatile int imageSizeY = 0;

	@Element(required = false)
	private volatile  boolean keepProportions = true;

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
		firePropertyChange(PROPERTYNAME_LOWER_LEFT_X, oldValue, lowerLeftX);
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
		firePropertyChange(PROPERTYNAME_LOWER_LEFT_Y, oldValue, lowerLeftY);
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
		firePropertyChange(PROPERTYNAME_IMAGE_SIZE_X, oldValue, this.imageSizeX);
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
		firePropertyChange(PROPERTYNAME_IMAGE_SIZE_Y, oldValue, this.imageSizeY);
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
		firePropertyChange(PROPERTYNAME_KEEP_PROPORTIONS, oldValue, keepProportions);
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
		firePropertyChange(PROPERTYNAME_IMAGE_FILE_NAME, oldValue, this.imageFileName);
	}

}