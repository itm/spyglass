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
import de.uniluebeck.itm.spyglass.plugin.nodepositioner.NodePositionerPlugin.Position;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;

/**
 * A primitive drawing object, representing a circle.
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
	public Circle(final int id) {
		super(id);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public DrawingObject clone() throws CloneNotSupportedException {
		final Circle clone = new Circle();
		clone.setId(getId());
		clone.setDiameter(diameter);
		clone.setColorR(getColorR());
		clone.setColorG(getColorG());
		clone.setColorB(getColorB());
		clone.setPosition(new Position(getPosition().x, getPosition().y));
		
		return clone;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public int getDiameter() {
		return diameter;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void setDiameter(final int diameter) {
		this.diameter = diameter;
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
	
	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public void update(final DrawingObject other) {
		if (other instanceof Circle) {
			super.update(other);
			final Circle c = (Circle) other;
			setDiameter(c.getDiameter());
		}
	}
	
	public short getLineWidth() {
		return lineWidth;
	}
	
	public void setLineWidth(final short lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		final Color color = new Color(null, this.getColorR(), this.getColorG(), this.getColorB());
		final Color bg = new Color(null, this.getBgColorR(), this.getBgColorG(), this.getBgColorB());
		
		gc.setForeground(color);
		gc.setBackground(bg);
		gc.setLineWidth(this.getLineWidth());
		
		gc.fillOval(((int) this.getPosition().x - (this.getDiameter() / 2)), ((int) this.getPosition().y - (this.getDiameter() / 2)), this
				.getDiameter(), this.getDiameter());
		gc.drawOval(((int) this.getPosition().x - (this.getDiameter() / 2)), ((int) this.getPosition().y - (this.getDiameter() / 2)), this
				.getDiameter(), this.getDiameter());
		
		color.dispose();
		bg.dispose();
	}
	
	@Override
	public AbsoluteRectangle getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
