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

import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link ObjectPainterPlugin}
 * 
 * @author Sebastian Ebers
 * 
 */
public class ObjectPainterXMLConfig extends PluginXMLConfig {

	public static final String PROPERTYNAME_IMAGE_SIZE_X = "imageSizeX";

	public static final String PROPERTYNAME_IMAGE_SIZE_Y = "imageSizeY";

	public static final String PROPERTYNAME_KEEP_PROPORTIONS = "keepProportions";

	public static final String PROPERTYNAME_IMAGE_FILE_NAME = "imageFileName";

	@Element(name = PROPERTYNAME_IMAGE_FILE_NAME)
	private String imageFileName = "images/icons/brokenImageLink.png";

	@Element(name = PROPERTYNAME_IMAGE_SIZE_X)
	private int imageSizeX = 0;

	@Element(name = PROPERTYNAME_IMAGE_SIZE_Y)
	private int imageSizeY = 0;

	@Element(name = PROPERTYNAME_KEEP_PROPORTIONS)
	private boolean keepProportions = true;

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

		firePropertyChange(PROPERTYNAME_IMAGE_FILE_NAME, this.imageFileName, this.imageFileName = imageFileName);
	}

	public boolean equals(final ObjectPainterXMLConfig o) {

		if (!super.equals(o)) {
			return false;
		}

		return imageFileName.equals((o).imageFileName) && (imageSizeX == o.imageSizeX) && (imageSizeY == o.imageSizeY);
	}

}