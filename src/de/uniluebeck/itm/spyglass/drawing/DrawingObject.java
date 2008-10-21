/*
 * ---------------------------------------------------------------------- This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details. ------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.drawing;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

// --------------------------------------------------------------------------------
/**
 * Abstract class that represents a drawing object. Every DrawingObject should have an unique id, a
 * position and a color, given by the RGB components in a R8G8B8 format.
 */
@Root
public abstract class DrawingObject {
	
	private int id = 0;
	
	private AbsolutePosition position = new AbsolutePosition(0, 0, 0);
	
	@Element
	private int colorR = 200;
	
	@Element
	private int colorG = 0;
	
	@Element
	private int colorB = 0;
	
	@Element
	private int bgColorR = 255;
	
	@Element
	private int bgColorG = 255;
	
	@Element
	private int bgColorB = 255;
	
	private long paintOrderId;
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public DrawingObject() {
		this.id = -1;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public DrawingObject(final int id) {
		this.id = id;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void update(final DrawingObject other) {
		this.id = other.id;
		this.paintOrderId = other.paintOrderId;
		this.position.x = other.position.x;
		this.position.y = other.position.y;
		setColor(other.getColorR(), other.getColorG(), other.getColorB());
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public AbsolutePosition getPosition() {
		return position;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the position of the upper left point of the <code>DrawingObject</code>.
	 */
	public void setPosition(final AbsolutePosition position) {
		this.position = position;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getId() {
		return id;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setId(final int id) {
		this.id = id;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getColorR() {
		return colorR;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColorR(final int colorR) {
		this.colorR = colorR;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getColorG() {
		return colorG;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColorG(final int colorG) {
		this.colorG = colorG;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getColorB() {
		return colorB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColorB(final int colorB) {
		this.colorB = colorB;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setColor(final int R, final int G, final int B) {
		this.colorR = R;
		this.colorG = G;
		this.colorB = B;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "paintOrderId: " + paintOrderId + ", id: " + id + ", " + super.toString();
	}
	
	public String toString(final int tabCount) {
		final StringBuffer buff = new StringBuffer();
		for (int i = 0; i < tabCount; i++) {
			buff.append("\t");
		}
		buff.append(toString());
		return buff.toString();
	}
	
	public int getBgColorR() {
		return bgColorR;
	}
	
	public void setBgColorR(final int bgColorR) {
		this.bgColorR = bgColorR;
	}
	
	public int getBgColorG() {
		return bgColorG;
	}
	
	public void setBgColor(final int bgColorG) {
		this.bgColorG = bgColorG;
	}
	
	public int getBgColorB() {
		return bgColorB;
	}
	
	public void setBgColorB(final int bgColorB) {
		this.bgColorB = bgColorB;
	}
	
	public void setBgColor(final int r, final int b, final int g) {
		this.bgColorR = r;
		this.bgColorB = b;
		this.bgColorG = g;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Forces this DrawingObject to paint itself on the given GC.
	 * 
	 * Optionally the DrawingObject can inform itself about the current zoom level by queriing the
	 * additonally given drawingArea.
	 * 
	 * @param drawingArea
	 * @param gc
	 */
	public abstract void draw(DrawingArea drawingArea, GC gc);
	
	// --------------------------------------------------------------------------------
	/**
	 * Return the bounding box of this drawing object
	 */
	public abstract AbsoluteRectangle getBoundingBox();
	
	protected void drawBoundingBox(final DrawingArea drawingArea, final GC gc) {
		
		final PixelRectangle rect = drawingArea.absRect2PixelRect(getBoundingBox());
		final int x = rect.getUpperLeft().x;
		final int y = rect.getUpperLeft().y;
		final int width = rect.getWidth();
		final int height = rect.getHeight();
		final Color color = new Color(gc.getDevice(), 255, 0, 0);
		gc.setLineWidth(1);
		gc.setForeground(color);
		gc.drawRectangle(x, y, width, height);
		color.dispose();
		
	}
	
}
