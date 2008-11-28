package de.uniluebeck.itm.spyglass.drawing;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;

public class Grid extends DrawingObject {
	
	private int gridElementHeight;
	private int numCols;
	private int lineWidth;
	private int gridElementWidth;
	private int numRows;
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		
		final Color color = new Color(gc.getDevice(), getColor());
		
		gc.setLineWidth(lineWidth);
		gc.setForeground(color);
		
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
		
		color.dispose();
		
	}
	
	public void setGridElementHeight(final int gridElementHeight) {
		setGridElementHeight(gridElementHeight, true);
	}
	
	public void setGridElementHeight(final int gridElementHeight,
			final boolean fireBoundBoxChangeEvent) {
		this.gridElementHeight = gridElementHeight;
		updateBoundingBox(fireBoundBoxChangeEvent);
	}
	
	public void setGridElementWidth(final int gridElementWidth) {
		setGridElementWidth(gridElementWidth, true);
	}
	
	public void setGridElementWidth(final int gridElementWidth,
			final boolean fireBoundingBoxChangeEvent) {
		this.gridElementWidth = gridElementWidth;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}
	
	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public void setNumCols(final int numCols) {
		setNumCols(numCols, true);
	}
	
	public void setNumCols(final int numCols, final boolean fireBoundingBoxChangeEvent) {
		this.numCols = numCols;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}
	
	public void setNumRows(final int numRows) {
		setNumRows(numRows, true);
	}
	
	public void setNumRows(final int numRows, final boolean fireBoundingBoxChangeEvent) {
		this.numRows = numRows;
		updateBoundingBox(fireBoundingBoxChangeEvent);
	}
	
	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return new AbsoluteRectangle(getPosition(), gridElementWidth * numCols, gridElementHeight
				* numRows);
	}
	
}
