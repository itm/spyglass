package de.uniluebeck.itm.spyglass.drawing;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;

import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;

public class Grid extends DrawingObject {
	
	private static final Logger log = Logger.getLogger(Grid.class);
	
	private int gridElementHeight;
	private int numCols;
	private float lineWidth;
	private int gridElementWidth;
	private int numRows;
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		
		AbsolutePosition origin;
		AbsoluteRectangle absRect;
		PixelRectangle pxRect;
		int originX, originY;
		
		for (int row = 0; row < numRows; row++) {
			
			for (int col = 0; col < numCols; col++) {
				
				originX = getPosition().x + (col * gridElementWidth);
				originY = getPosition().y + (row * gridElementHeight);
				origin = new AbsolutePosition(originX, originY, 0);
				
				absRect = new AbsoluteRectangle(origin, gridElementWidth, gridElementHeight);
				pxRect = drawingArea.absRect2PixelRect(absRect);
				
				gc.drawRectangle(pxRect.getUpperLeft().x, pxRect.getUpperLeft().y, pxRect
						.getWidth(), pxRect.getHeight());
				
			}
			
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
	
	public void setLineWidth(final float lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public void setNumCols(final int numCols) {
		this.numCols = numCols;
	}
	
	public void setNumRows(final int numRows) {
		this.numRows = numRows;
	}
	
}
