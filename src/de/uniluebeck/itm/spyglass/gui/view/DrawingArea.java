package de.uniluebeck.itm.spyglass.gui.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import de.uniluebeck.itm.spyglass.core.Spyglass;
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
 * Note: As this is an SWT-widget you should be careful when calling methods on this object from
 * other threads than the SWT GUI thread.
 * 
 * @author Dariush Forouher
 */
public class DrawingArea extends Canvas {
	
	private static Logger log = SpyglassLogger.get(DrawingArea.class);
	
	/**
	 * Reference to UI-Thread
	 */
	private Spyglass spyglass;
	
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
	 * The transformation matrix. it transforms coordinates from the absolute reference frame to the
	 * reference frame of the drawing area
	 * 
	 * Note: Only modifing calls to this object are serialized with synchronized(). By replacing the
	 * transform with a new instance in one single step, reading the transform can be lock-free.
	 * 
	 */
	private AffineTransform at = new AffineTransform();
	
	/**
	 * Mutex to serialize modifing calls
	 * 
	 * (to ensure that <code>at</code> is not modified concurrently.
	 */
	private Object mutex = new Object();
	
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
	
	/**
	 * Listerers for the DrawingAreaTransformEvent.
	 */
	private final EventListenerList listeners = new EventListenerList();
	
	/**
	 * This color is used for area outside of the the map
	 */
	private final Color canvasOutOfMapColor = new Color(getDisplay(), 50, 50, 50);
	
	/**
	 * This color is used as the background color
	 */
	private final Color canvasBgColor = new Color(getDisplay(), 255, 255, 255);
	
	// --------------------------------------------------------------------------------
	/**
	 * Create a new DrawingArea.
	 * 
	 * @param parent
	 * @param style
	 * @param spyglass
	 *            Reference to spyglass.
	 */
	public DrawingArea(final Composite parent, final int style, final Spyglass spyglass) {
		super(parent, style | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND
				| SWT.DOUBLE_BUFFERED);
		
		this.spyglass = spyglass;
		
		setBackground(canvasBgColor);
		
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(final PaintEvent e) {
				// TODO: draw the gray borders
				// drawBackground(e.gc);
			}
		});
		
		addControlListener(this.controlListener);
		getHorizontalBar().addSelectionListener(scrollListenerH);
		getVerticalBar().addSelectionListener(scrollListenerV);
		syncScrollBars();
		
		// The canvas must not act on mouse wheel events (normally it would move the
		// scroll bars)
		// since the mouse wheel already controls the zoom.
		addListener(SWT.MouseWheel, new Listener() {
			
			@Override
			public void handleEvent(final Event event) {
				event.doit = false;
			}
		});
		
		// Redraw the canvas, if the transform has been modified.
		addDrawingAreaTransformListener(new DrawingAreaTransformListener() {
			
			@Override
			public void handleEvent(final DrawingAreaTransformEvent e) {
				redraw();
				
			}
		});
		
	}
	
	private final SelectionListener scrollListenerH = new SelectionAdapter() {
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			scrollHorizontally((ScrollBar) e.widget);
		}
		
	};
	private final SelectionListener scrollListenerV = new SelectionAdapter() {
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			scrollVertically((ScrollBar) e.widget);
		}
		
	};
	
	private final ControlListener controlListener = new ControlAdapter() {
		
		@Override
		public void controlResized(final ControlEvent e) {
			log.debug("Control resized: " + e);
			
			log.debug("New canvas: " + getClientArea());
			
			if (!isValidTransformation(new AffineTransform(at))) {
				log.error("Resizing resulted in illegal transform. Resetting matrix.");
				
				adjustToValidMatrix();
			}
			
			syncScrollBars();
			
		}
		
	};
	
	/**
	 * Draw the background.
	 * 
	 * Space which lies inside the map (-2^15 to 2^15) will be colored in <code>canvasBgColor</code>
	 * , whereas space outside this area is colored <code>canvasOutOfMapColor</code>.
	 */
	private void drawBackground(final GC gc) {
		gc.setBackground(canvasOutOfMapColor);
		gc.fillRectangle(getClientArea());
		
		final AbsolutePosition absPoint = new AbsolutePosition();
		absPoint.x = -32768;
		absPoint.y = -32768;
		
		final AbsoluteRectangle completeMap = new AbsoluteRectangle();
		completeMap.setUpperLeft(absPoint);
		completeMap.setHeight(2 * 32768);
		completeMap.setWidth(2 * 32768);
		
		PixelRectangle pxRect = null;
		
		final AbsoluteRectangle visibleArea = getAbsoluteDrawingRectangle();
		
		// This is a workaround, since GC has problems with huge negative numbers
		if (completeMap.contains(visibleArea)) {
			pxRect = getDrawingRectangle();
		} else {
			pxRect = absRect2PixelRect(completeMap);
		}
		
		gc.setBackground(canvasBgColor);
		gc.fillRectangle(pxRect.getUpperLeft().x, pxRect.getUpperLeft().y, pxRect.getWidth(),
				pxRect.getHeight());
	}
	
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
	 * return a rectangle descrbing the dimensions of the drawing area.
	 */
	public PixelRectangle getDrawingRectangle() {
		final PixelRectangle ret = new PixelRectangle();
		ret.setUpperLeft(new PixelPosition(0, 0));
		ret.setHeight(getClientArea().height);
		ret.setWidth(getClientArea().width);
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
		
		synchronized (mutex) {
			
			final AffineTransform atCopy = new AffineTransform(at);
			
			// Build the translation matrix
			final AffineTransform sca = AffineTransform.getTranslateInstance(pixelX, pixelY);
			
			atCopy.preConcatenate(sca);
			
			if (!isValidTransformation(atCopy)) {
				return;
			}
			
			// add the translation matrix to the transformation matrix.
			at = atCopy;
		}
		fireDrawingAreaTransformEvent();
		
		syncScrollBars();
	}
	
	/**
	 * Checks, if the given Transformation, concatinated to the current <code>at</code>, would
	 * result in a legal transformation matrix with respect to the global boundingBox (which must
	 * never be left)
	 */
	private boolean isValidTransformation(final AffineTransform at2) {
		boolean ok = false;
		// log.debug("Canvas: " + this.canvasRect);
		
		try {
			
			final Point2D upperLeft2D = at2.inverseTransform(new Point2D.Double(0, 0), null);
			
			final Point2D lowerRight = new Point2D.Double(this.getDrawingRectangle().getWidth(),
					this.getDrawingRectangle().getHeight());
			final Point2D lowerRight2D = at2.inverseTransform(lowerRight, null);
			
			// log.debug(String.format("upperLeft2D=%s, lowerRight2D=%s", upperLeft2D,
			// lowerRight2D));
			
			ok = DrawingArea.getGlobalBoundingBox()
					.contains(upperLeft2D.getX(), upperLeft2D.getY())
					&& DrawingArea.getGlobalBoundingBox().contains(lowerRight2D.getX(),
							lowerRight2D.getY());
			
		} catch (final NoninvertibleTransformException e) {
			log.error("Transformation matrix in illegal state!", e);
		}
		
		// log.debug("val status: " + ok);
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
		lowerRight.x = rect.getUpperLeft().x + rect.getWidth();
		lowerRight.y = rect.getUpperLeft().y + rect.getHeight();
		final AbsolutePosition lowerRightAbs = this.pixelPoint2AbsPoint(lowerRight);
		
		absRect.setWidth(Math.abs(lowerRightAbs.x - upperLeftAbs.x));
		absRect.setHeight(Math.abs(upperLeftAbs.y - lowerRightAbs.y));
		
		return absRect;
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
		
		synchronized (mutex) {
			
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
				at.concatenate(sca);
				
			} catch (final NoninvertibleTransformException e) {
				throw new RuntimeException("Transformation matrix in illegal state!", e);
			}
		}
		
		fireDrawingAreaTransformEvent();
		
		syncScrollBars();
		
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
	 */
	public void autoZoom(final AbsoluteRectangle rect) {
		log.debug("Auto zooming to " + rect);
		
		synchronized (mutex) {
			
			// create a new matrix from scratch
			final AffineTransform newAt = new AffineTransform();
			
			// dimensions of the drawing area
			final int DAwidth = getClientArea().width;
			final int DAhright = getClientArea().height;
			
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
		fireDrawingAreaTransformEvent();
		syncScrollBars();
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
		synchronized (mutex) {
			
			AffineTransform atCopy = new AffineTransform(at);
			
			while (!isValidTransformation(atCopy)) {
				log.debug("Zooming in to get back in into the global BBox.");
				
				final int px = getDrawingRectangle().getWidth() / 2;
				final int py = getDrawingRectangle().getHeight() / 2;
				
				// The centerpoint of the zoom (where the user clicked)
				try {
					final Point2D a = atCopy.inverseTransform(new Point2D.Float(px, py), null);
					
					// Build the scale matrix
					final AffineTransform sca = new AffineTransform();
					sca.translate(a.getX(), a.getY());
					sca.scale(ZOOM_FACTOR, ZOOM_FACTOR);
					sca.translate(-a.getX(), -a.getY());
					
					atCopy.concatenate(sca);
					
					if ((atCopy.getScaleX() > ZOOM_MAX)) {
						log.debug("Giving up, resetting to initial matrix.");
						atCopy = new AffineTransform();
						break;
					}
					
				} catch (final NoninvertibleTransformException e1) {
					log.error("Transformation matrix in illegal state!", e1);
					break;
				}
				
			}
			
			at = atCopy;
		}
		fireDrawingAreaTransformEvent();
		
		syncScrollBars();
	}
	
	/**
	 * Handle a vertical scroll on the given scroll bar
	 */
	private void scrollVertically(final ScrollBar scrollBar) {
		
		final double ty = at.getTranslateY() / at.getScaleY();
		final double select = scrollBar.getSelection();
		final double diff = -select - ty;
		
		synchronized (mutex) {
			
			final AffineTransform atCopy = new AffineTransform(at);
			atCopy.concatenate(AffineTransform.getTranslateInstance(0, diff));
			at = atCopy;
		}
		fireDrawingAreaTransformEvent();
		
		syncScrollBars();
	}
	
	/**
	 * Handle a horizontal scroll on the given scroll bar
	 */
	private void scrollHorizontally(final ScrollBar scrollBar) {
		
		final double tx = at.getTranslateX() / at.getScaleX();
		final double select = scrollBar.getSelection();
		final double diff = -select - tx;
		
		synchronized (mutex) {
			
			final AffineTransform atCopy = new AffineTransform(at);
			atCopy.concatenate(AffineTransform.getTranslateInstance(diff, 0));
			at = atCopy;
		}
		fireDrawingAreaTransformEvent();
		
		syncScrollBars();
	}
	
	/**
	 * Readjust the scrollbars to the current position
	 */
	private void syncScrollBars() {
		
		final AbsoluteRectangle bboxVisible = spyglass.getBoundingBox();
		final AbsoluteRectangle bboxCurrent = getAbsoluteDrawingRectangle();
		final AbsoluteRectangle bbox = bboxCurrent.union(bboxVisible);
		
		getHorizontalBar().setMinimum(bbox.getUpperLeft().x);
		getHorizontalBar().setMaximum(
				bbox.getWidth() + bbox.getUpperLeft().x - bboxCurrent.getWidth());
		getHorizontalBar().setSelection(getUpperLeft().x);
		
		getVerticalBar().setMinimum(bbox.getUpperLeft().y);
		getVerticalBar().setMaximum(
				bbox.getHeight() + bbox.getUpperLeft().y - bboxCurrent.getHeight());
		getVerticalBar().setSelection(getUpperLeft().y);
		
		// Enlarge thumbs (disabled, since this screws up the math)
		// appWindow.getGui().getCanvas().getHorizontalBar().setThumb(this.canvasRect.width);
		// appWindow.getGui().getCanvas().getVerticalBar().setThumb(this.canvasRect.height);
		
		// Disable scroll bars when appropriate
		if (bboxVisible.intersection(bboxCurrent).getWidth() == bboxVisible.getWidth()) {
			getHorizontalBar().setEnabled(false);
		} else {
			getHorizontalBar().setEnabled(true);
		}
		
		if (bboxVisible.intersection(bboxCurrent).getHeight() == bboxVisible.getHeight()) {
			getVerticalBar().setEnabled(false);
		} else {
			getVerticalBar().setEnabled(true);
		}
		
	}
	
	/**
	 * Add a new Listener for changes in the transform of this drawing area.
	 * 
	 * This listener can be used to listen for changes in zoom or movement of the drawing area.
	 * 
	 */
	public void addDrawingAreaTransformListener(final DrawingAreaTransformListener listener) {
		if (listener == null) {
			return;
		}
		
		log.debug("Added new listener: " + listener);
		listeners.add(DrawingAreaTransformListener.class, listener);
	}
	
	/**
	 * Remove the given listener.
	 */
	public void removeDrawingAreaTransformListener(final DrawingAreaTransformListener listener) {
		if (listener == null) {
			return;
		}
		
		log.debug("Removing listener: " + listener);
		listeners.remove(DrawingAreaTransformListener.class, listener);
	}
	
	/**
	 * Fires a DrawingAreaTransformEvent.
	 */
	private void fireDrawingAreaTransformEvent() {
		// Get listeners
		final EventListener[] list = listeners.getListeners(DrawingAreaTransformListener.class);
		
		log.debug("Fire redraw event");
		
		// Fire the event (call-back method)
		for (int i = list.length - 1; i >= 0; i -= 1) {
			((DrawingAreaTransformListener) list[i])
					.handleEvent(new DrawingAreaTransformEvent(this));
		}
	}
	
	@Override
	public void dispose() {
		this.canvasBgColor.dispose();
		this.canvasOutOfMapColor.dispose();
	}
	
}