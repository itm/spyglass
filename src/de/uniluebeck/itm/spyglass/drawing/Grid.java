package de.uniluebeck.itm.spyglass.drawing;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

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

		final Rectangle clipping = gc.getClipping();

		final Color color = new Color(gc.getDevice(), getColor());

		gc.setLineWidth(lineWidth);
		gc.setForeground(color);

		final AbsolutePosition pos = getPosition();
		AbsolutePosition origin, dest;
		PixelPosition pxOrigin, pxDest;
		int originX, originY, destX, destY;

		int x1, x2, y1, y2;

		originX = pos.x;
		destX = originX + (gridElementWidth * numCols);

		for (int row = 0; row <= numRows; row++) {

			originY = destY = pos.y + (row * gridElementHeight);

			origin = new AbsolutePosition(originX, originY, 0);
			dest = new AbsolutePosition(destX, destY, 0);

			pxOrigin = drawingArea.absPoint2PixelPoint(origin);
			pxDest = drawingArea.absPoint2PixelPoint(dest);

			if ((pxOrigin.y >= clipping.y) && (pxOrigin.y <= (clipping.y + clipping.height))) {

				x1 = Math.max(clipping.x, pxOrigin.x);
				y1 = pxOrigin.y;
				x2 = Math.min((clipping.x + clipping.width), pxDest.x);
				y2 = pxDest.y;

				gc.drawLine(x1, y1, x2, y2);

			}

		}

		originY = pos.y;
		destY = originY + (gridElementHeight * numRows);

		for (int col = 0; col <= numCols; col++) {

			originX = destX = pos.x + (col * gridElementWidth);

			origin = new AbsolutePosition(originX, originY, 0);
			dest = new AbsolutePosition(destX, destY, 0);

			pxOrigin = drawingArea.absPoint2PixelPoint(origin);
			pxDest = drawingArea.absPoint2PixelPoint(dest);

			if ((pxOrigin.x >= clipping.x) && (pxOrigin.x <= (clipping.x + clipping.width))) {

				x1 = pxOrigin.x;
				y1 = Math.max(clipping.y, pxOrigin.y);
				x2 = pxDest.x;
				y2 = Math.min((clipping.y + clipping.height), pxDest.y);

				gc.drawLine(x1, y1, x2, y2);

			}
		}

		color.dispose();

	}

	public synchronized void setGridElementHeight(final int gridElementHeight) {
		this.gridElementHeight = gridElementHeight;
		markBoundingBoxDirty();
	}

	public synchronized void setGridElementWidth(final int gridElementWidth) {
		this.gridElementWidth = gridElementWidth;
		markBoundingBoxDirty();
	}

	public synchronized void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
		markContentDirty();
	}

	public synchronized void setNumCols(final int numCols) {
		this.numCols = numCols;
		markBoundingBoxDirty();
	}

	public synchronized void setNumRows(final int numRows) {
		this.numRows = numRows;
		markBoundingBoxDirty();
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return new AbsoluteRectangle(getPosition(), gridElementWidth * numCols, gridElementHeight * numRows);
	}

}
