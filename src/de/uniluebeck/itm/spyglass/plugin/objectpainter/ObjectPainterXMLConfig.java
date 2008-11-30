/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.objectpainter;

import org.eclipse.swt.graphics.RGB;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;

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

	public static final String PROPERTYNAME_DRAW_LINE = "drawLine";

	public static final String PROPERTYNAME_LINE_COLOR = "lineColor";

	public static final String PROPERTYNAME_PACKET_TYPE_3D = "packetType3D";

	@Element(required=false)
	private String imageFileName = "images/icons/brokenImageLink.png";

	@Element(required=false)
	private int imageSizeX = 0;

	@Element(required=false)
	private int imageSizeY = 0;

	@Element(required=false)
	private boolean keepProportions = true;

	@Element(required=false)
	private boolean drawLine = true;

	@Element(required=false)
	private boolean packetType3D = true;

	@ElementArray(required=false)
	private int[] lineColor = new int[] { 255, 0, 0 };

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
	public boolean isPacketType3D() {
		return packetType3D;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param drawLine
	 */
	public void setPacketType3D(final boolean packetType3D) {
		final boolean oldValue = this.packetType3D;
		this.packetType3D = packetType3D;
		firePropertyChange("packetType3D", oldValue, packetType3D);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return
	 */
	public boolean isDrawLine() {
		return drawLine;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param drawLine
	 */
	public void setDrawLine(final boolean drawLine) {
		final boolean oldValue = this.drawLine;
		this.drawLine = drawLine;
		firePropertyChange("drawLine", oldValue, drawLine);
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

	public int[] getLineColor() {
		return lineColor;
	}

	public RGB getLineColorRGB() {
		return new RGB(lineColor[0], lineColor[1], lineColor[2]);
	}

	public void setLineColor(final int[] color) {
		final int[] oldValue = this.lineColor;
		this.lineColor = color;
		firePropertyChange("lineColor", oldValue, color);
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

		firePropertyChange("imageFileName", this.imageFileName, this.imageFileName = imageFileName);
	}

	public boolean equals(final ObjectPainterXMLConfig o) {

		if (!super.equals(o)) {
			return false;
		}

		return imageFileName.equals((o).imageFileName) && (imageSizeX == o.imageSizeX) && (imageSizeY == o.imageSizeY);
	}

}