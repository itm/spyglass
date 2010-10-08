/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

/**
 * A map object for the MapPainterPlugin
 * 
 * @author Dariush Forouher
 * 
 */
public class Map extends DrawingObject {

	private static final boolean DEBUG = false;

	/**
	 * This matrix contains the values which will be then converted to colors and drawn.
	 * 
	 * storage format is: dataMatrix[row][column]
	 */
	private double[][] dataMatrix;

	private MapPainterXMLConfig xmlConfig;

	public Map(final MapPainterXMLConfig config) {
		this.xmlConfig = config;
		this.dataMatrix = new double[config.getRows()][config.getCols()];
		this.setPosition(config.getLowerLeft());
	}

	public synchronized void setMatrix(final double[][] matrix) {
		this.dataMatrix = matrix;
		markContentDirty();
	}

	@Override
	public synchronized void draw(final GC gc) {

		final PixelRectangle clippAreaPx = new PixelRectangle(gc.getClipping());

		final AbsoluteRectangle clippingArea = getDrawingArea().pixelRect2AbsRect(clippAreaPx);

		final AbsoluteRectangle drawRect = new AbsoluteRectangle();
		drawRect.setHeight(xmlConfig.getGridElementHeight());
		drawRect.setWidth(xmlConfig.getGridElementWidth());

		for (int row = 0; row < xmlConfig.getRows(); row++) {
			for (int col = 0; col < xmlConfig.getCols(); col++) {
				final AbsolutePosition newPos = new AbsolutePosition();
				newPos.x = col * xmlConfig.getGridElementWidth() + xmlConfig.getLowerLeftX();
				newPos.y = row * xmlConfig.getGridElementHeight() + xmlConfig.getLowerLeftY();
				drawRect.setLowerLeft(newPos);

				// Clipping
				if (clippingArea.intersects(drawRect)) {

					drawElement(getDrawingArea(), gc, drawRect, row, col);

				}

			}

		}

		if (DEBUG) {
			drawBoundingBox(gc);
		}
	}

	/**
	 * Draw the Element drawRect.
	 */
	private void drawElement(final DrawingArea drawingArea, final GC gc, final AbsoluteRectangle drawRect, final int row, final int col) {
		final PixelRectangle pxRect = drawingArea.absRect2PixelRect(drawRect);

		final double average;
		synchronized (dataMatrix) {
			average = dataMatrix[row][col];
		}
		final RGB avgColor = calculateColor(average);
		final Color color = new Color(gc.getDevice(), avgColor);

		gc.setBackground(color);
		gc.fillRectangle(pxRect.toSWTRectangle());

		if (DEBUG) {
			gc.drawText(String.format("%.0f", average), pxRect.getUpperLeft().x, pxRect.getUpperLeft().y);
		}

		color.dispose();
	}

	/**
	 * Calculate the color for the given value.
	 */
	private RGB calculateColor(final double value) {

		// normalize value between minValue and maxValue to the range [0;1]
		final double lambda = (value - xmlConfig.getMinValue()) / (xmlConfig.getMaxValue() - xmlConfig.getMinValue());

		// Calculate new Color
		final int[] rgb = new int[3];
		for (int k = 0; k < 3; k++) {
			final double d = xmlConfig.getMinColorRGB()[k] + lambda * (xmlConfig.getMaxColorRGB()[k] - xmlConfig.getMinColorRGB()[k]);

			if (d < 0) {
				rgb[k] = 0;
			} else if (d > 255) {
				rgb[k] = 255;
			} else {
				rgb[k] = (int) d;
			}

		}
		// log.debug(String.format("Element %s with value %f gets color [%d,%d,%d]; lambda=%f",
		// centerPoint, average, rgb[0], rgb[1], rgb[2], lambda));

		// final Random r = new Random();
		// return new RGB(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		return new RGB(rgb[0], rgb[1], rgb[2]);

	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return xmlConfig.getBoundingBox();
	}
}
