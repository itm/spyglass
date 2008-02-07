/* ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.       
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project    
 * SpyGlass is free software; you can redistribute it and/or modify it  
 * under the terms of the BSD License. Refer to spyglass-licence.txt    
 * file in the root of the SpyGlass source tree for further details.  
------------------------------------------------------------------------*/
package de.uniluebeck.itm.spyglass.drawing;

import org.apache.log4j.Category;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.drawing.primitive.Circle;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.drawing.primitive.Rectangle;
import de.uniluebeck.itm.spyglass.util.Logging;

// --------------------------------------------------------------------------------
/**
 * A 2D-canvas that is able to draw a DrawingObject onto a SWT graphics context (org.eclipse.swt.graphics.GC).
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
	public void draw(DrawingObject drawingObj, GC gc) {
		this.gc = gc;

		if (drawingObj instanceof Line)
			drawLine((Line) drawingObj);
		else if (drawingObj instanceof Rectangle)
			drawRectangle((Rectangle) drawingObj);
		else if (drawingObj instanceof Circle)
			drawCircle((Circle) drawingObj);
		else
			log.warn("Unknown drawing object. Ignored.");
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawLine(Line line) {
		if (log.isDebugEnabled())
			log.debug("Drawing line " + line);

		Color color = new Color(null, line.getColorR(), line.getColorG(), line.getColorB());
		gc.setForeground(color);

		// TODO: Implement the drawing of the line primitive
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawRectangle(Rectangle rect) {
		if (log.isDebugEnabled())
			log.debug("Drawing rectangle " + rect);

		Color color = new Color(null, rect.getColorR(), rect.getColorG(), rect.getColorB());

		gc.setForeground(color);
		gc.drawRectangle(rect.getPosition().x - (rect.getWidth() / 2), rect.getPosition().y - (rect.getHeight() / 2), rect.getWidth(), rect
				.getHeight());
	}

	// --------------------------------------------------------------------------------
	/**
	 * 
	 */
	private void drawCircle(Circle circle) {
		if (log.isDebugEnabled())
			log.debug("Drawing circle: " + circle);

		Color color = new Color(null, circle.getColorR(), circle.getColorG(), circle.getColorB());

		gc.setForeground(color);
		gc.drawOval(circle.getPosition().x - (circle.getDiameter() / 2), circle.getPosition().y - (circle.getDiameter() / 2), circle.getDiameter(),
				circle.getDiameter());
	}

}
