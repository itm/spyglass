/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing;

import ishell.util.Logging;

import org.apache.log4j.Category;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.primitive.Circle;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;
import de.uniluebeck.itm.spyglass.drawing.primitive.Text.TextJustification;

// --------------------------------------------------------------------------------
/**
 * A 2D-canvas that is able to draw a DrawingObject onto a SWT graphics context
 * (org.eclipse.swt.graphics.GC).
 */
@Root
public class Canvas2D implements SpyglassCanvas {
	private static Category log = Logging.get(Canvas2D.class);

	private GC gc = null;

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public synchronized void draw(DrawingObject drawingObj, GC gc) {
		this.gc = gc;

		if (drawingObj instanceof Line)
			drawLine((Line) drawingObj);
		else if (drawingObj instanceof Rectangle)
			drawRectangle((Rectangle) drawingObj);
		else if (drawingObj instanceof Circle)
			drawCircle((Circle) drawingObj);
		else if (drawingObj instanceof de.uniluebeck.itm.spyglass.drawing.primitive.Text)
			drawText((de.uniluebeck.itm.spyglass.drawing.primitive.Text) drawingObj);
		else
			log.warn("Unknown drawing object. Ignored.");
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawLine(Line line) {
		Color color = new Color(null, line.getColorR(), line.getColorG(), line.getColorB());
		gc.setForeground(color);
		gc.setLineWidth(line.getLineWidth());
		gc.drawLine((int) (line.getPosition().x), (int) (line.getPosition().y), (int) (line.getEnd().x), (int) (line.getEnd().y));
		// TODO: Implement the drawing of the line primitive
		color.dispose();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawText(de.uniluebeck.itm.spyglass.drawing.primitive.Text text) {
		Color color = new Color(null, text.getColorR(), text.getColorG(), text.getColorB());
		Color bgColor = new Color(null, text.getBgColorR(), text.getBgColorG(), text.getBgColorB());
		Font f  = new Font(gc.getDevice(), "Arial", 6, SWT.NORMAL);
		
		gc.setFont(f);
		gc.setForeground(color);
		gc.setBackground(bgColor);
		String s = text.getText();
		int offsetX = 0;
		Point p = gc.stringExtent(s);
		if (text.getJustification() == TextJustification.center)
			offsetX = (p.x / -2)+1;
		else if (text.getJustification() == TextJustification.right)
			offsetX = -(p.x)+1;
		int offsetY = p.y / -2;
		gc.drawString(s, (int) (text.getPosition().x) + offsetX, (int) (text.getPosition().y) + offsetY);
		// TODO: Implement the drawing of the line primitive
		color.dispose();
		bgColor.dispose();
		f.dispose();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawRectangle(Rectangle rect) {
		Color color = new Color(null, rect.getColorR(), rect.getColorG(), rect.getColorB());
		Color bg = new Color(null, rect.getBgColorR(), rect.getBgColorG(), rect.getBgColorB());
		
		gc.setForeground(color);
		gc.setBackground(bg);
		gc.setLineWidth(rect.getLineWidth());
		gc.fillRectangle((int) (rect.getPosition().x - (rect.getWidth() / 2)), (int) (rect.getPosition().y - (rect.getHeight() / 2)),
				rect.getWidth(), rect.getHeight());
		gc.drawRectangle((int) (rect.getPosition().x - (rect.getWidth() / 2)), (int) (rect.getPosition().y - (rect.getHeight() / 2)),
				rect.getWidth(), rect.getHeight());
		
		color.dispose();
		bg.dispose();
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawCircle(Circle circle) {
		Color color = new Color(null, circle.getColorR(), circle.getColorG(), circle.getColorB());
		Color bg = new Color(null, circle.getBgColorR(), circle.getBgColorG(), circle.getBgColorB());

		gc.setForeground(color);
		gc.setBackground(bg);
		gc.setLineWidth(circle.getLineWidth());
		
		gc.fillOval(((int) circle.getPosition().x - (circle.getDiameter() / 2)), ((int) circle.getPosition().y - (circle.getDiameter() / 2)), circle
				.getDiameter(), circle.getDiameter());
		gc.drawOval(((int) circle.getPosition().x - (circle.getDiameter() / 2)), ((int) circle.getPosition().y - (circle.getDiameter() / 2)), circle
				.getDiameter(), circle.getDiameter());
		
		color.dispose();
		bg.dispose();
	}

}
