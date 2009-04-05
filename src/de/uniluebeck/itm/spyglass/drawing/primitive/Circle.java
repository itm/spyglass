/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

/**
 * A primitive drawing object, representing a circle.
 *
 * TODO: this class has never been used and is potentially broken!
 */
@Root
public class Circle extends DrawingObject {
	@Element
	private int diameter = 10;
	private short lineWidth = 1;

	// --------------------------------------------------------------------------------
	/**
	 *
	 */
	public Circle() {
		super();
	}

	// --------------------------------------------------------------------------------
	/**
	 *
	 */
	public synchronized int getDiameter() {
		return diameter;
	}

	// --------------------------------------------------------------------------------
	/**
	 *
	 */
	public synchronized void setDiameter(final int diameter) {
		this.diameter = diameter;
		markBoundingBoxDirty();
	}

	// --------------------------------------------------------------------------------
	/**
	 *
	 */
	@Override
	public String toString() {
		// TODO Implement
		return super.toString();
	}

	public synchronized short getLineWidth() {
		return lineWidth;
	}

	public synchronized void setLineWidth(final short lineWidth) {
		this.lineWidth = lineWidth;
		markContentDirty();
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		final Color color = new Color(null, this.getColor());
		final Color bg = new Color(null, this.getBgColor());

		gc.setForeground(color);
		gc.setBackground(bg);
		gc.setLineWidth(this.getLineWidth());

		final PixelPosition px = drawingArea.absPoint2PixelPoint(this.getPosition());

		gc.fillOval((px.x - (this.getDiameter() / 2)), (px.y - (this.getDiameter() / 2)), this.getDiameter(), this.getDiameter());
		gc.drawOval((px.x - (this.getDiameter() / 2)), (px.y - (this.getDiameter() / 2)), this.getDiameter(), this.getDiameter());

		color.dispose();
		bg.dispose();
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

}
