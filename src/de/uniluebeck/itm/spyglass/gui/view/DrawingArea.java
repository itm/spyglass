package de.uniluebeck.itm.spyglass.gui.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.apache.log4j.Category;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;

/**
 * The drawing area is the place, where all nodes etc. are painted on. this class contains all
 * information about the dimensions of the drawing area and offers methods to transform between
 * reference frames.
 * 
 * @author Dariush Forouher
 */
@Root
public class DrawingArea {
	
	private static Category log = SpyglassLogger.get(DrawingArea.class);
	
	/**
	 * Scale factor applied while zooming.
	 */
	private final double ZOOM_FACTOR = 1.1;
	
	/**
	 * Maximum zoom level allowed (since world-coordinates are integer, there is no point in
	 * infinite zoom)
	 */
	private final double ZOOM_MAX = 50;
	
	/**
	 * Reference to the appWindow
	 */
	private AppWindow appWindow;
	
	/**
	 * The transformation matrix. it transforms coordinates from the absolute reference frame to the
	 * reference frame of the drawing area
	 * 
	 * Note: At the moment only modifing calls to this objects are serialized with synchronized().
	 * Since we are currently redrawing the screen 25 times a second, a messed up transformation
	 * should not be visible for a noticable time.
	 */
	private AffineTransform at = new AffineTransform();
	
	/**
	 * x-coordinate of the upper-left point of the world.
	 */
	private static final int WORLD_UPPER_LEFT_X = -((int) Math.pow(2, 16));
	
	/**
	 * y-coordinate of the upper-left point of the world.
	 */
	private static final int WORLDS_UPPER_LEFT_Y = -((int) Math.pow(2, 16));
	
	/**
	 * width of the world.
	 */
	private static final int WORLD_WIDTH = 2 * ((int) Math.pow(2, 16));
	
	/**
	 * height of the world.
	 */
	private static final int WORLD_HEIGHT = 2 * ((int) Math.pow(2, 16));
	
	private final ControlListener controlListener = new ControlListener() {
		
		@Override
		public void controlMoved(final ControlEvent e) {
			// not of interest.
		}
		
		@Override
		public void controlResized(final ControlEvent e) {
			log.debug("Control resized: " + e);
			log.debug("New canvas: " + getDrawingCanvasRectangle());
			
			synchronized (at) {
				
				if (!isValidTransformation(new AffineTransform(at))) {
					log.error("Resizing resulted in illegal transform. Resetting matrix.");
					
					adjustToValidMatrix();
				}
			}
			
		}
		
	};
	
	/**
	 * Maps a point from the absolute reference frame to the reference frame of the drawing area.
	 * 
	 * 
	 * @param absPoint
	 *            a point in the absolute reference frame
	 */
	public PixelPosition absPoint2PixelPoint(final AbsolutePosition absPoint) {
		
		final Point2D pxPoint = at.transform(absPoint.toPoint2D(), null);
		
		return new PixelPosition(pxPoint);
	}
	
	/**
	 * Transforms a rectangle from absolute coordinates into a one with pixel coordinates.
	 * 
	 * 
	 * @param absRect
	 */
	public PixelRectangle absRect2PixelRect(final AbsoluteRectangle absRect) {
		
		final PixelRectangle rect = new PixelRectangle();
		
		final PixelPosition upperLeft = this.absPoint2PixelPoint(absRect.getUpperLeft());
		rect.setUpperLeft(upperLeft);
		
		final AbsolutePosition lowerRightAbs = new AbsolutePosition();
		lowerRightAbs.x = absRect.getUpperLeft().x + absRect.getWidth();
		lowerRightAbs.y = absRect.getUpperLeft().y + absRect.getHeight();
		final PixelPosition lowerRight = this.absPoint2PixelPoint(lowerRightAbs);
		
		rect.setWidth(Math.abs(upperLeft.x - lowerRight.x));
		rect.setHeight(Math.abs(upperLeft.y - lowerRight.y));
		
		return rect;
	}
	
	/**
	 * Returns the transformation matrix.
	 * 
	 * this matrix maps points from the absolute reference frame to the reference frame of the
	 * drawing area.
	 * 
	 * @param absRect
	 */
	public AffineTransform getTransform() {
		return (AffineTransform) at.clone();
	}
	
	/**
	 * Returns the Rectangle describing the drawing area (short-cut method)
	 */
	private Rectangle getDrawingCanvasRectangle() {
		return this.appWindow.getGui().getCanvas().getClientArea();
	}
	
	/**
	 * return a rectangle descrbing the dimensions of the drawing area.
	 */
	public PixelRectangle getDrawingRectangle() {
		final PixelRectangle ret = new PixelRectangle();
		ret.setUpperLeft(new PixelPosition(0, 0));
		ret.setHeight(getDrawingCanvasRectangle().height);
		ret.setWidth(getDrawingCanvasRectangle().width);
		return ret;
	}
	
	/**
	 * return a rectangle descrbing the dimensions and position of the currently visible part of the
	 * absolute frame of reference.
	 */
	public AbsoluteRectangle getAbsoluteDrawingRectangle() {
		final AbsoluteRectangle absRect = new AbsoluteRectangle();
		
		final AbsolutePosition upperLeft = this.getUpperLeft();
		final AbsolutePosition lowerRight = this.getLowerRight();
		
		final int height = Math.abs(upperLeft.y - lowerRight.y);
		final int width = Math.abs(lowerRight.x - upperLeft.x);
		
		absRect.setUpperLeft(upperLeft);
		absRect.setHeight(height);
		absRect.setWidth(width);
		
		// log.debug(String.format("Size of the drawing area in px: %dx%d; Pos=%s", width, height,
		// upperLeft));
		
		return absRect;
	}
	
	/**
	 * return the absolute point represented by the lower right point of the drawing area.
	 */
	public AbsolutePosition getLowerRight() {
		try {
			
			final Point2D lowerRight = new Point2D.Double(this.getDrawingRectangle().getWidth(),
					this.getDrawingRectangle().getHeight());
			final Point2D lowerRight2D = at.inverseTransform(lowerRight, null);
			return new AbsolutePosition(lowerRight2D);
			
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}
	
	/**
	 * return the absolute point represented by the upper left point of the drawing area.
	 */
	public AbsolutePosition getUpperLeft() {
		try {
			
			final Point2D upperLeft2D = at.inverseTransform(new Point2D.Double(0, 0), null);
			return new AbsolutePosition(upperLeft2D);
			
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}
	
	/**
	 * Moves the drawing area by the given number of pixels
	 * 
	 * @param pixelX
	 * @param pixelY
	 */
	public void move(final int pixelX, final int pixelY) {
		
		// Build the translation matrix
		final AffineTransform sca = new AffineTransform();
		
		synchronized (at) {
			sca.translate(pixelX, pixelY);
			
			final AffineTransform atTemp = new AffineTransform(at);
			atTemp.preConcatenate(sca);
			if (!isValidTransformation(atTemp)) {
				return;
			}
			
			log.debug("Moved about " + pixelX + "; " + pixelY);
			
			// add the translation matrix to the transformation matrix.
			at.preConcatenate(sca);
			
			if (!isValidTransformation(new AffineTransform())) {
				log.error("Transformation now illegal!");
			}
		}
		
	}
	
	/**
	 * Checks, if the given Transformation, concatinated to the current <code>at</code>, would
	 * result in a legal transformation matrix with respect to the global boundingBox (which must
	 * never be left)
	 */
	private boolean isValidTransformation(final AffineTransform at2) {
		boolean ok = false;
		log.debug("Canvas: " + getDrawingCanvasRectangle());
		
		try {
			
			final Point2D upperLeft2D = at2.inverseTransform(new Point2D.Double(0, 0), null);
			
			final Point2D lowerRight = new Point2D.Double(this.getDrawingRectangle().getWidth(),
					this.getDrawingRectangle().getHeight());
			final Point2D lowerRight2D = at2.inverseTransform(lowerRight, null);
			
			log.debug(String.format("upperLeft2D=%s, lowerRight2D=%s", upperLeft2D, lowerRight2D));
			
			ok = DrawingArea.getGlobalBoundingBox()
					.contains(upperLeft2D.getX(), upperLeft2D.getY())
					&& DrawingArea.getGlobalBoundingBox().contains(lowerRight2D.getX(),
							lowerRight2D.getY());
			
		} catch (final NoninvertibleTransformException e) {
			log.error("Transformation matrix in illegal state!", e);
		}
		
		log.debug("val status: " + ok);
		return ok;
	}
	
	/**
	 * Maps a point from the reference frame of the drawing are to the absolute reference frame.
	 * 
	 * @param point
	 *            a point in the reference frame of the drawing area
	 */
	public AbsolutePosition pixelPoint2AbsPoint(final PixelPosition point) {
		
		try {
			
			final Point2D a = at.inverseTransform(point.toPoint2D(), null);
			return new AbsolutePosition(a);
			
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}
	
	/**
	 * Transforms a rectangle from pixel coordinates into a one with absolute coordinates.
	 * 
	 * @param rect
	 */
	public AbsoluteRectangle pixelRect2AbsRect(final PixelRectangle rect) {
		final AbsoluteRectangle absRect = new AbsoluteRectangle();
		
		final AbsolutePosition upperLeftAbs = this.pixelPoint2AbsPoint(rect.getUpperLeft());
		absRect.setUpperLeft(upperLeftAbs);
		
		final PixelPosition lowerRight = new PixelPosition();
		lowerRight.x = rect.getUpperLeft().x + rect.getHeight();
		lowerRight.y = rect.getUpperLeft().y + rect.getWidth();
		final AbsolutePosition lowerRightAbs = this.pixelPoint2AbsPoint(lowerRight);
		
		absRect.setWidth(Math.abs(lowerRightAbs.x - upperLeftAbs.x));
		absRect.setHeight(Math.abs(upperLeftAbs.y - lowerRightAbs.y));
		
		return absRect;
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Sets the object reference to the given object.
	 * 
	 * note that this method should only be called during start up.
	 * 
	 * @param appWindow
	 *            the appWindow to set
	 */
	public void setAppWindow(final AppWindow appWindow) {
		
		// remove old listener
		if (this.appWindow != null) {
			this.appWindow.getGui().getCanvas().removeControlListener(this.controlListener);
		}
		
		this.appWindow = appWindow;
		
		// add new one
		this.appWindow.getGui().getCanvas().addControlListener(this.controlListener);
	}
	
	/**
	 * Returns the current zoom level. The zoom level can take any value in the range 0-ZOOM_MAX.
	 * 
	 */
	public double getZoom() {
		return at.getScaleX();
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Zoom Out. Use the given pixel position as the zoom center
	 * 
	 * @param px
	 * @param py
	 */
	public void zoomOut(final int px, final int py) {
		// log.debug("Zooming from " + px + "; " + py);
		
		zoom(px, py, 1 / ZOOM_FACTOR);
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Zooms in.
	 * 
	 * @param px
	 * @param py
	 */
	public void zoomIn(final int px, final int py) {
		// log.debug("Zooming into " + px + "; " + py);
		
		zoom(px, py, ZOOM_FACTOR);
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Zoom in or out by the given factor
	 * 
	 * @param px
	 * @param py
	 * @param factor
	 */
	private void zoom(final int px, final int py, final double factor) {
		try {
			// The centerpoint of the zoom (where the user clicked)
			final Point2D a = at.inverseTransform(new Point2D.Float(px, py), null);
			
			// Build the scale matrix
			final AffineTransform sca = new AffineTransform();
			sca.translate(a.getX(), a.getY());
			sca.scale(factor, factor);
			sca.translate(-a.getX(), -a.getY());
			
			final AffineTransform atTemp = new AffineTransform(at);
			atTemp.concatenate(sca);
			if (!isValidTransformation(atTemp)) {
				return;
			}
			// Abort if we leave a defined interval of allowed zoom levels
			// This is partly because the QuadTree cannot handle it if it is asked
			// to return objects outside its boundingBox.
			//
			// TODO: This isn't the perfect solution, as it depends on the size
			// of the Spyglass Window to be "regular size".
			final AffineTransform at2 = (AffineTransform) at.clone();
			at2.concatenate(sca);
			if ((at2.getScaleX() > ZOOM_MAX)) {
				return;
			}
			
			// add the scale matrix to the transformation matrix.
			synchronized (at) {
				at.concatenate(sca);
			}
			
			System.out.println("Zoom level: " + at.getScaleX());
			
			if (!isValidTransformation(new AffineTransform())) {
				log.error("Transformation now illegal!");
			}
			
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}
	
	/**
	 * Zoom in and implicitly asume that the center of the drawing area is the scale center.
	 */
	public void zoomIn() {
		this.zoomIn(this.getDrawingRectangle().getWidth() / 2, this.getDrawingRectangle()
				.getHeight() / 2);
		
	}
	
	/**
	 * Zoom out and implicitly asume that the center of the drawing area is the scale center.
	 */
	public void zoomOut() {
		this.zoomOut(this.getDrawingRectangle().getWidth() / 2, this.getDrawingRectangle()
				.getHeight() / 2);
		
	}
	
	/**
	 * Adjusts the transformation matrix to make the given rectangle fit exactly in the drawing
	 * area.
	 * 
	 * TODO: small detail - works not perfect all the time
	 */
	public void autoZoom(final AbsoluteRectangle rect) {
		log.debug("Auto zooming to " + rect);
		
		// create a new matrix from scratch
		final AffineTransform newAt = new AffineTransform();
		
		// dimensions of the drawing area
		final int DAwidth = this.getDrawingCanvasRectangle().width;
		final int DAhright = this.getDrawingCanvasRectangle().height;
		
		final int BBheight = rect.getHeight();
		final int BBwidth = rect.getWidth();
		
		final double scaleX = (double) DAwidth / (double) BBwidth;
		final double scaleY = (double) DAhright / (double) BBheight;
		final double scale = Math.min(scaleX, scaleY);
		
		// scale and move to upper left corner
		newAt.scale(scale, scale);
		newAt.translate(-rect.getUpperLeft().x, -rect.getUpperLeft().y);
		
		// finally move the rect to the center of the drawing area
		
		final AbsolutePosition lowerRight = rect.getUpperLeft();
		lowerRight.x += rect.getWidth();
		lowerRight.y += rect.getHeight();
		final Point2D lowerRightPx = newAt.transform(lowerRight.toPoint2D(), null);
		
		final double deltaX = DAwidth - lowerRightPx.getX();
		final double deltaY = DAhright - lowerRightPx.getY();
		
		newAt.preConcatenate(AffineTransform.getTranslateInstance(deltaX / 2, deltaY / 2));
		
		// replace the matrix
		this.at = newAt;
		
	}
	
	/**
	 * Returns the global bounding box describing the whole world. (absolute coordinates outside
	 * this bounding box can be considered an error.)
	 */
	public static AbsoluteRectangle getGlobalBoundingBox() {
		
		return new AbsoluteRectangle(WORLD_UPPER_LEFT_X, WORLDS_UPPER_LEFT_Y, WORLD_WIDTH,
				WORLD_HEIGHT);
		
	}
	
	/**
	 * Adjust the Transform ,until it is valid again.
	 * 
	 * Note: this method is not thread-safe!
	 * 
	 * TODO: adjust heuristic for the case when the drawing area is enlarged for more then 2x
	 * 
	 */
	private void adjustToValidMatrix() {
		while (!isValidTransformation(new AffineTransform(at))) {
			log.debug("Zooming in to get back in into the global BBox.");
			
			final int px = getDrawingRectangle().getWidth() / 2;
			final int py = getDrawingRectangle().getHeight() / 2;
			
			// The centerpoint of the zoom (where the user clicked)
			try {
				final Point2D a = at.inverseTransform(new Point2D.Float(px, py), null);
				
				// Build the scale matrix
				final AffineTransform sca = new AffineTransform();
				sca.translate(a.getX(), a.getY());
				sca.scale(ZOOM_FACTOR, ZOOM_FACTOR);
				sca.translate(-a.getX(), -a.getY());
				
				at.concatenate(sca);
				
				if ((at.getScaleX() > ZOOM_MAX)) {
					log.debug("Giving up, resetting to initial matrix.");
					at = new AffineTransform();
					break;
				}
				
			} catch (final NoninvertibleTransformException e1) {
				log.error("Transformation matrix in illegal state!", e1);
				break;
			}
			
		}
	}
}