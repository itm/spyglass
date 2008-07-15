package de.uniluebeck.itm.spyglass.gui.view;
import de.uniluebeck.itm.spyglass.packet.AbsolutePosition;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import de.uniluebeck.itm.spyglass.packet.AbsoluteRect;

public class DrawingArea {

	private AppWindow appWindow;
	private AbsolutePosition upperLeft;
	private float zoom;

	public DrawingArea(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 
	 * @param appWindow
	 */
	public void DrawingArea(AppWindow appWindow){

	}

	/**
	 * 
	 * @param absPoint
	 */
	public Point absPoint2PixelPoint(AbsolutePosition absPoint){
		return null;
	}

	/**
	 * 
	 * @param absRect
	 */
	public Rectangle absRect2PixelRect(AbsoluteRect absRect){
		return null;
	}

	/**
	 * Entspricht der derzeitigen Zeichenfl�che in Pixeln, wird vom appWindow
	 * ausgelesen
	 */
	public Rectangle getDrawingRectangle(){
		return null;
	}

	/**
	 * Berechnet sich aus upperLeft, Zoom und getDrawingRectangle
	 */
	public AbsolutePosition getLowerRight(){
		return null;
	}

	/**
	 * Verschiebung der Zeichenfl�che um die gegebene Anzahl an Pixel
	 * 
	 * @param pixelX
	 * @param pixelY
	 */
	public void move(int pixelX, int pixelY){

	}

	/**
	 * 
	 * @param point
	 */
	public AbsolutePosition pixelPoint2absPoint(Point point){
		return null;
	}

	/**
	 * 
	 * @param rect
	 */
	public AbsoluteRect pixelRect2AbsRect(Rectangle rect){
		return null;
	}

}