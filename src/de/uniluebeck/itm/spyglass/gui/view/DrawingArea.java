package de.uniluebeck.itm.spyglass.gui.view;

import org.apache.log4j.Category;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

/**
 * The drawing area is the place, where all nodes etc. are painted on. this class contains all
 * information about the dimensions of the draw
 * 
 * @author dariush
 * 
 */
public class DrawingArea {
	
	private static Category log = SpyglassLogger.get(DrawingArea.class);
	
	private final AppWindow appWindow;
	
	/**
	 * The upper left point of the currently visible area.
	 */
	private final AbsolutePosition upperLeft = new AbsolutePosition(0, 0, 0);
	
	/**
	 * The zoom level. a zoomlevel of 1 means that px coordinates are identical to absolute
	 * coordinates.
	 */
	private final float zoom = 1;
	
	/**
	 * 
	 * @param appWindow
	 */
	public DrawingArea(final AppWindow appWindow) {
		this.appWindow = appWindow;
	}
	
	/**
	 * 
	 * @param absPoint
	 */
	public PixelPosition absPoint2PixelPoint(final AbsolutePosition absPoint) {
		final PixelPosition pos = new PixelPosition();
		pos.x = (int) (zoom * (absPoint.x - upperLeft.x));
		pos.y = (int) (zoom * (absPoint.y - upperLeft.y));
		return pos;
	}
	
	/**
	 * 
	 * @param absRect
	 */
	public PixelRectangle absRect2PixelRect(final AbsoluteRectangle absRect) {
		final PixelRectangle rect = new PixelRectangle();
		rect.setHeight((int) (absRect.getHeight() * zoom));
		rect.setWidth((int) (absRect.getWidth() * zoom));
		rect.setUpperLeft(this.absPoint2PixelPoint(absRect.getUpperLeft()));
		return rect;
	}
	
	/**
	 * Entspricht der derzeitigen Zeichenfläche in Pixeln, wird vom appWindow ausgelesen
	 */
	public PixelRectangle getDrawingRectangle() {
		final int height = this.appWindow.getGui().getBounds().height;
		final int width = this.appWindow.getGui().getBounds().width;
		log.debug(String.format("Size of the drawing area in px: %dx%d\n", width, height));
		return null;
	}
	
	/**
	 * Entspricht der derzeitigen Zeichenfläche in Pixeln, wird vom appWindow ausgelesen
	 */
	public AbsoluteRectangle getAbsoluteDrawingRectangle() {
		final AbsoluteRectangle absRect = new AbsoluteRectangle();
		absRect.setUpperLeft(this.upperLeft);
		final int height = this.appWindow.getGui().getBounds().height;
		final int width = this.appWindow.getGui().getBounds().width;
		absRect.setHeight((int) (height / zoom));
		absRect.setWidth((int) (width / zoom));
		log.debug(String.format("Size of the drawing area in px: %dx%d\n", width, height));
		return absRect;
	}
	
	/**
	 * Berechnet sich aus upperLeft, Zoom und getDrawingRectangle
	 */
	public AbsolutePosition getLowerRight() {
		return null; // TODO
	}
	
	/**
	 * Verschiebung der Zeichenfläche um die gegebene Anzahl an Pixel
	 * 
	 * @param pixelX
	 * @param pixelY
	 */
	public void move(final int pixelX, final int pixelY) {
		// TODO
	}
	
	/**
	 * 
	 * @param point
	 */
	public AbsolutePosition pixelPoint2absPoint(final PixelPosition point) {
		final AbsolutePosition pos = new AbsolutePosition();
		pos.x = (int) ((point.x - upperLeft.x) / zoom);
		pos.y = (int) ((point.y - upperLeft.y) / zoom);
		return pos;
	}
	
	/**
	 * 
	 * @param rect
	 */
	public AbsoluteRectangle pixelRect2AbsRect(final PixelRectangle rect) {
		final AbsoluteRectangle absRect = new AbsoluteRectangle();
		absRect.setHeight((int) (rect.getHeight() / zoom));
		absRect.setWidth((int) (rect.getWidth() / zoom));
		absRect.setUpperLeft(this.pixelPoint2absPoint(rect.getUpperLeft()));
		return absRect;
	}
	
}