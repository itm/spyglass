package de.uniluebeck.itm.spyglass.drawing;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

public class Grid extends DrawingObject {
	
	private static final Logger log = Logger.getLogger(Grid.class);
	
	private int gridElementHeight;
	private int numCols;
	private int lineWidth;
	private int gridElementWidth;
	private int numRows;
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		
		gc.setLineWidth(lineWidth);
		gc.setForeground(new Color(gc.getDevice(), getColorR(), getColorG(), getColorB()));
		
		final AbsolutePosition pos = getPosition();
		AbsolutePosition origin, dest;
		PixelPosition pxOrigin, pxDest;
		int originX, originY, destX, destY;
		
		originX = pos.x;
		destX = originX + (gridElementWidth * numCols);
		
		for (int row = 0; row <= numRows; row++) {
			
			originY = destY = pos.y + (row * gridElementHeight);
			
			origin = new AbsolutePosition(originX, originY, 0);
			dest = new AbsolutePosition(destX, destY, 0);
			
			pxOrigin = drawingArea.absPoint2PixelPoint(origin);
			pxDest = drawingArea.absPoint2PixelPoint(dest);
			
			gc.drawLine(pxOrigin.x, pxOrigin.y, pxDest.x, pxDest.y);
			
		}
		
		originY = pos.y;
		destY = originY + (gridElementHeight * numRows);
		
		for (int col = 0; col <= numCols; col++) {
			
			originX = destX = pos.x + (col * gridElementWidth);
			
			origin = new AbsolutePosition(originX, originY, 0);
			dest = new AbsolutePosition(destX, destY, 0);
			
			pxOrigin = drawingArea.absPoint2PixelPoint(origin);
			pxDest = drawingArea.absPoint2PixelPoint(dest);
			
			gc.drawLine(pxOrigin.x, pxOrigin.y, pxDest.x, pxDest.y);
			
		}
		
	}
	
	@Override
	public AbsoluteRectangle getBoundingBox() {
		return new AbsoluteRectangle(getPosition(), gridElementWidth * numCols, gridElementHeight
				* numRows);
	}
	
	public void setGridElementHeight(final int gridElementHeight) {
		this.gridElementHeight = gridElementHeight;
	}
	
	public void setGridElementWidth(final int gridElementWidth) {
		this.gridElementWidth = gridElementWidth;
	}
	
	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public void setNumCols(final int numCols) {
		this.numCols = numCols;
	}
	
	public void setNumRows(final int numRows) {
		this.numRows = numRows;
	}
	
}
