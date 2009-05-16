package de.uniluebeck.itm.spyglass.gui.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.view.TransformChangedEvent.Type;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * The drawing area is the place, where all nodes etc. are painted on. this class contains all
 * information about the dimensions of the drawing area and offers methods to transform between
 * reference frames.
 *
 * Attention: Methods of this class MUST only be invoked from the SWT-GUI thread, unless explicitly
 * stated otherwise!
 *
 * @author Dariush Forouher
 */
public class DrawingArea extends Canvas {

	protected static Logger log = SpyglassLoggerFactory.getLogger(DrawingArea.class);

	/**
	 * Scale factor applied while zooming.
	 */
	public final static double ZOOM_FACTOR = 1.1;

	/**
	 * Maximum zoom level allowed (since world-coordinates are integer, there is no point in
	 * infinite zoom)
	 */
	private final double ZOOM_MAX = 50;

	/**
	 * Limitation of the zoom. Any zoom level which completely shows this rectangle will be
	 * considered forbidden.
	 */
	private static final AbsoluteRectangle MAX_ZOMM_OUT = new AbsoluteRectangle(-((int) Math.pow(2, 17)), -((int) Math.pow(2, 17)), 2 * ((int) Math
			.pow(2, 17)), 2 * ((int) Math.pow(2, 17)));

	/**
	 * x-coordinate of the upper-left point of the world.
	 */
	private static final int WORLD_LOWER_LEFT_X = -((int) Math.pow(2, 15));

	/**
	 * y-coordinate of the upper-left point of the world.
	 */
	private static final int WORLD_LOWER_LEFT_Y = -((int) Math.pow(2, 15));

	/**
	 * width of the world.
	 */
	private static final int WORLD_WIDTH = 2 * ((int) Math.pow(2, 15));

	/**
	 * height of the world.
	 */
	private static final int WORLD_HEIGHT = 2 * ((int) Math.pow(2, 15));

	/**
	 * Number of pixels to be moved when Up/Down/Left/right is pressed.
	 */
	private final int MOVE_OFFSET = 20;

	/**
	 * The transformation matrix. it transforms coordinates from the absolute reference frame to the
	 * reference frame of the drawing area
	 */
	protected AffineTransform at = createInitialTransform();

	/**
	 * Mutex to synchronize access to "at"
	 */
	protected Object transformMutex = new Object();

	/**
	 * Listerńers for the DrawingAreaTransformEvent.
	 */
	private final EventListenerList listeners = new EventListenerList();

	/**
	 * This color is used for area outside of the the map
	 */
	protected final Color canvasOutOfMapColor = new Color(getDisplay(), 50, 50, 50);

	/**
	 * This color is used as the background color
	 */
	protected final Color canvasBgColor = new Color(getDisplay(), 255, 255, 255);

	/**
	 * True, while the user moves the map via mouse
	 */
	protected volatile boolean mouseDragInProgress = false;

	/**
	 * the starting point of the movement business.
	 */
	protected volatile PixelPosition mouseDragStartPosition = null;

	/**
	 * Dispose children
	 */
	private DisposeListener disposeListener = new DisposeListener() {

		@Override
		public void widgetDisposed(final DisposeEvent e) {
			canvasBgColor.dispose();
			canvasOutOfMapColor.dispose();
		}

	};

	/**
	 * PaintListener
	 *
	 * Draws areas outside of the world in a gray color.
	 */
	private PaintListener paintListener = new PaintListener() {

		@Override
		public void paintControl(final PaintEvent arg0) {

			arg0.gc.setBackground(canvasOutOfMapColor);

			final PixelRectangle world = absRect2PixelRect(getGlobalBoundingBox());
			final PixelRectangle canvas = getDrawingRectangle();

			//log.debug("Pixel world is: "+world);

			final int edgeN = world.getUpperLeft().y;
			if (edgeN > 0) {
				arg0.gc.fillRectangle(0, 0, canvas.getWidth(), edgeN);
			}
			final int edgeE = canvas.getWidth() - world.getWidth() - world.getUpperLeft().x;
			if (edgeE > 0) {
				arg0.gc.fillRectangle(world.getWidth() + world.getUpperLeft().x, 0, edgeE, canvas.getHeight());
			}
			final int edgeS = canvas.getHeight() - world.getHeight() - world.getUpperLeft().y;
			if (edgeS > 0) {
				arg0.gc.fillRectangle(0, world.getHeight() + world.getUpperLeft().y, canvas.getWidth(), edgeS);
			}
			final int edgeW = world.getUpperLeft().x;
			if (edgeW > 0) {
				arg0.gc.fillRectangle(0, 0, edgeW, canvas.getHeight());
			}

			arg0.gc.setBackground(canvasBgColor);

		}
	};

	/**
	 *
	 * mouse drag and drop: used for moving the drawing area.
	 */
	private MouseListener mouseListener = new org.eclipse.swt.events.MouseAdapter() {

		@Override
		public void mouseDown(final MouseEvent e) {
			mouseDragInProgress = true;
			mouseDragStartPosition = new PixelPosition(e.x, e.y);
		}

		@Override
		public void mouseUp(final MouseEvent arg0) {
			mouseDragInProgress = false;
		}

	};

	/**
	 * move listener: redraw the canvas while movement is in progress
	 */
	private MouseMoveListener mouseMoveListener = new MouseMoveListener() {

		@Override
		public void mouseMove(final MouseEvent arg0) {

			// if a movement is in progress, update the drawing area by
			// appling the current
			// delta.
			if (mouseDragInProgress) {

				final PixelPosition mouseDragStopPosition = new PixelPosition(arg0.x, arg0.y);

				final int deltaX = mouseDragStopPosition.x - mouseDragStartPosition.x;
				final int deltaY = mouseDragStopPosition.y - mouseDragStartPosition.y;

				move(deltaX, deltaY);

				mouseDragStartPosition = mouseDragStopPosition;
			}

		}
	};

	/**
	 * Key listener: for moving
	 */
	private KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(final KeyEvent arg0) {
			// log.debug("pressed" + arg0);
			if (arg0.keyCode == 16777219) {
				move(MOVE_OFFSET, 0);
			}
			if (arg0.keyCode == 16777220) {
				move(-MOVE_OFFSET, 0);
			}
			if (arg0.keyCode == 16777217) {
				move(0, MOVE_OFFSET);
			}
			if (arg0.keyCode == 16777218) {
				move(0, -MOVE_OFFSET);
			}
		}

	};

	/**
	 * handle changes on the horizontal scrollbar
	 */
	private final SelectionListener scrollListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			scroll();
		}

	};

	/**
	 * We have to react when the canvas is resized.
	 */
	private final ControlListener controlListener = new ControlAdapter() {

		@Override
		public void controlResized(final ControlEvent e) {
			boolean isValid;
			synchronized (transformMutex) {
				isValid = isValidTransformation(at);
			}

			if (!isValid) {
				log.error("Resizing resulted in illegal transform. Resetting matrix.");

				// there is a redraw() in here
				adjustToValidMatrix();
			}

			syncScrollBars();

		}

	};

	/**
	 * Mouse wheel: used for zooming
	 */
	private MouseWheelListener mouseWheelListener = new MouseWheelListener() {

		@Override
		public void mouseScrolled(final MouseEvent arg0) {
			if (arg0.count > 0) {
				zoomIn(arg0.x, arg0.y);
			} else {
				zoomOut(arg0.x, arg0.y);
			}

		}
	};

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
		//
		super(parent, style | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);

		init();

	}

	private void init() {
		setBackground(canvasBgColor);

		initKeyMouse();

		initScrollBars();

		addControlListener(this.controlListener);

		addPaintListener(paintListener);

		addDisposeListener(disposeListener);

	}

	/**
	 * Add Mouse and Key listener
	 */
	private void initKeyMouse() {
		// handle user events on the canvas (moving, zoom)
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseMoveListener);
		addMouseWheelListener(mouseWheelListener);
		addKeyListener(keyListener);
	}

	/**
	 * Configure scroll bars
	 */
	private void initScrollBars() {
		// handle ScrollBar events
		getHorizontalBar().addSelectionListener(scrollListener);
		getVerticalBar().addSelectionListener(scrollListener);

		getHorizontalBar().setMinimum(0);
		getHorizontalBar().setMaximum(WORLD_WIDTH);
		getHorizontalBar().setPageIncrement(1000);
		getHorizontalBar().setIncrement(100);

		getVerticalBar().setMinimum(0);
		getVerticalBar().setMaximum(WORLD_HEIGHT);
		getVerticalBar().setPageIncrement(1000);
		getVerticalBar().setIncrement(100);

		syncScrollBars();

		// The canvas must not act on mouse wheel events (normally it would move the
		// scroll bars) since the mouse wheel already controls the zoom.
		addListener(SWT.MouseWheel, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				event.doit = false;
			}
		});

	}

	/**
	 * Maps a point from the absolute reference frame to the reference frame of the drawing area.
	 *
	 * This method is thread-safe.
	 *
	 * @param absPoint
	 *            a point in the absolute reference frame
	 * @return the determined reference frame of the drawing area
	 */
	public PixelPosition absPoint2PixelPoint(AbsolutePosition absPoint) {

		synchronized (transformMutex) {
			absPoint = inv(absPoint);

			final Point2D pxPoint = at.transform(absPoint.toPoint2D(), null);

			return new PixelPosition(pxPoint);
		}

	}

	/**
	 * Transforms a rectangle from absolute coordinates into a one with pixel coordinates.
	 *
	 * It is guaranteed that the resulting pixel rectangle will contain at least the area of the
	 * original absolute rectangle (iow: the rounding is always done to the outside).
	 *
	 * This method is thread-safe.
	 *
	 * @param absRect
	 */
	public PixelRectangle absRect2PixelRect(AbsoluteRectangle absRect) {

		final PixelRectangle rect = new PixelRectangle();

		synchronized (transformMutex) {
			absRect = inv(absRect);

			Point2D a = at.transform(absRect.getLowerLeft().toPoint2D(), null);
			final PixelPosition upperLeftPx = new PixelPosition(a);
			rect.setUpperLeft(upperLeftPx);

			final AbsolutePosition lowerRightAbs = new AbsolutePosition();
			lowerRightAbs.x = absRect.getLowerLeft().x + absRect.getWidth();
			lowerRightAbs.y = absRect.getLowerLeft().y + absRect.getHeight();
			a = at.transform(lowerRightAbs.toPoint2D(), null);
			final PixelPosition lowerRightPx = new PixelPosition((int) Math.floor(a.getX() + 1), (int) Math.floor(a.getY() + 1));

			rect.setWidth(Math.abs(lowerRightPx.x - upperLeftPx.x));
			rect.setHeight(Math.abs(upperLeftPx.y - lowerRightPx.y));
		}

		return rect;

	}

	/**
	 * return a rectangle descrbing the dimensions of the drawing area.
	 */
	public PixelRectangle getDrawingRectangle() {
		this.checkWidget();

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
		this.checkWidget();

		final AbsoluteRectangle absRect = new AbsoluteRectangle();

		final AbsolutePosition lowerLeft = this.getLowerLeft();
		final AbsolutePosition upperRight = this.getUpperRight();

		final int height = Math.abs(lowerLeft.y - upperRight.y);
		final int width = Math.abs(upperRight.x - lowerLeft.x);

		absRect.setLowerLeft(lowerLeft);
		absRect.setHeight(height);
		absRect.setWidth(width);

		// log.debug(String.format("Size of the drawing area in px: %dx%d; Pos=%s", width, height,
		// upperLeft));

		return absRect;
	}

	/**
	 * return the absolute point represented by the upper right point of the drawing area.
	 */
	public AbsolutePosition getUpperRight() {
		this.checkWidget();

		try {

			synchronized (transformMutex) {
				final Point2D upperRight = new Point2D.Double(this.getDrawingRectangle().getWidth(), 0);
				final Point2D upperRight2D = at.inverseTransform(upperRight, null);
				return inv(new AbsolutePosition(upperRight2D));
			}

		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}

	/**
	 * return the absolute point represented by the lower right point of the drawing area.
	 */
	public Point2D getLowerRightPrecise() {
		this.checkWidget();

		try {
			synchronized (transformMutex) {

				final Point2D lowerRight = new Point2D.Double(this.getDrawingRectangle().getWidth(), this.getDrawingRectangle().getHeight());
				final Point2D lowerRight2D = at.inverseTransform(lowerRight, null);
				return new Point2D.Double(lowerRight2D.getX(), -lowerRight2D.getY());
			}
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}

	/**
	 * return the absolute point represented by the lower left point of the drawing area.
	 *
	 * This method is thread-safe.
	 *
	 */
	public AbsolutePosition getLowerLeft() {

		try {
			synchronized (transformMutex) {

				final Point2D lowerLeft2D = at.inverseTransform(new Point2D.Double(0, this.getDrawingRectangle().getHeight()), null);
				return inv(new AbsolutePosition(lowerLeft2D));
			}
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}

	/**
	 * return the absolute point represented by the upper left point of the drawing area.
	 *
	 * This method is thread-safe.
	 *
	 */
	public Point2D getUpperLeftPrecise() {

		try {
			synchronized (transformMutex) {

				final Point2D upperLeft2D = at.inverseTransform(new Point2D.Double(0, 0), null);

				return new Point2D.Double(upperLeft2D.getX(), -upperLeft2D.getY());
			}
		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}

	private AbsoluteRectangle inv(final AbsoluteRectangle rect) {
		final AbsoluteRectangle ret = new AbsoluteRectangle(rect);
		ret.rectangle.y = -ret.rectangle.y - ret.rectangle.height;
		return ret;
	}

	private AbsolutePosition inv(final AbsolutePosition rect) {
		final AbsolutePosition ret = rect.clone();
		ret.y *= -1;
		return ret;
	}

	/**
	 * Moves the drawing area by the given number of pixels
	 *
	 * @param pixelX
	 * @param pixelY
	 */
	public void move(final int pixelX, final int pixelY) {
		this.checkWidget();

		//log.debug("Moving by "+pixelX+","+pixelY);

		final AffineTransform atCopy;
		synchronized (transformMutex) {

			atCopy = new AffineTransform(at);

			// Build the translation matrix
			final AffineTransform sca = AffineTransform.getTranslateInstance(pixelX, pixelY);

			atCopy.preConcatenate(sca);

			if (!isValidTransformation(atCopy)) {
				return;
			}

			// add the translation matrix to the transformation matrix.
			at = atCopy;
		}

		fireTransformEvent(Type.MOVE);

		// redraw the canvas
		redraw();

		// This is an optimized alternative to above command.
		// Unfortunatly it results in graphical errors, likely resulted
		// by approximations in the transformation matrix
		// final GC gc = new GC(this);
		// gc.copyArea(0, 0, getDrawingRectangle().getWidth(), getDrawingRectangle().getHeight(),
		// pixelX, pixelY, true);
		// gc.dispose();

		syncScrollBars();
	}

	/**
	 * Checks, if the given Transformation, concatinated to the current <code>at</code>, would
	 * result in a legal transformation matrix with respect to the global boundingBox (which must
	 * never be left)
	 */
	protected boolean isValidTransformation(final AffineTransform at2) {
		boolean ok = false;
		// log.debug("Canvas: " + this.canvasRect);

		try {

			final Point2D upperLeft2D = at2.inverseTransform(new Point2D.Double(0, 0), null);

			final Point2D lowerRight = new Point2D.Double(this.getDrawingRectangle().getWidth(), this.getDrawingRectangle().getHeight());
			final Point2D lowerRight2D = at2.inverseTransform(lowerRight, null);

			// log.debug(String.format("upperLeft2D=%s, lowerRight2D=%s", upperLeft2D,
			// lowerRight2D));

			ok = DrawingArea.MAX_ZOMM_OUT.contains(upperLeft2D.getX(), upperLeft2D.getY())
					&& DrawingArea.MAX_ZOMM_OUT.contains(lowerRight2D.getX(), lowerRight2D.getY());

		} catch (final NoninvertibleTransformException e) {
			log.error("Transformation matrix in illegal state!", e);
		}

		// log.debug("val status: " + ok);
		return ok;
	}

	/**
	 * Maps a point from the reference frame of the drawing are to the absolute reference frame.
	 *
	 * This method is thread-safe.
	 *
	 * @param point
	 *            a point in the reference frame of the drawing area
	 */
	public AbsolutePosition pixelPoint2AbsPoint(final PixelPosition point) {

		try {
			synchronized (transformMutex) {
				final Point2D a = at.inverseTransform(point.toPoint2D(), null);
				return inv(new AbsolutePosition(a));
			}

		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}
	}

	/**
	 * Transforms a rectangle from pixel coordinates into a one with absolute coordinates.
	 *
	 * It is guaranteed that the resulting absolute rectangle will contain at least the area of the
	 * original pixel rectangle (iow: the rounding is always done to the outside).
	 *
	 * This method is thread-safe.
	 *
	 * @param rect
	 */
	public AbsoluteRectangle pixelRect2AbsRect(final PixelRectangle rect) {

		try {

			synchronized (transformMutex) {
				final AbsoluteRectangle absRect = new AbsoluteRectangle();

				Point2D a = at.inverseTransform(rect.getUpperLeft().toPoint2D(), null);
				final AbsolutePosition upperLeftAbs = new AbsolutePosition(a);
				absRect.setLowerLeft(upperLeftAbs);

				final PixelPosition lowerRight = new PixelPosition();
				lowerRight.x = rect.getUpperLeft().x + rect.getWidth();
				lowerRight.y = rect.getUpperLeft().y + rect.getHeight();
				a = at.inverseTransform(lowerRight.toPoint2D(), null);
				final AbsolutePosition lowerRightAbs = new AbsolutePosition((int) Math.floor(a.getX() + 1), (int) Math.floor(a.getY() + 1), 0);

				absRect.setWidth(Math.abs(lowerRightAbs.x - upperLeftAbs.x));
				absRect.setHeight(Math.abs(upperLeftAbs.y - lowerRightAbs.y));

				return inv(absRect);
			}

		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}

	}

	/**
	 * Returns the current zoom level. The zoom level can take any value in the range 0-ZOOM_MAX.
	 *
	 * This method is thread-safe.
	 *
	 */
	public double getZoom() {

		synchronized (transformMutex) {
			return at.getScaleX();

		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Zoom Out. Use the given pixel position as the zoom center
	 *
	 * @param px
	 * @param py
	 */
	public void zoomOut(final int px, final int py) {
		this.checkWidget();

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
		this.checkWidget();

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

			synchronized (transformMutex) {
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
			}

		} catch (final NoninvertibleTransformException e) {
			throw new RuntimeException("Transformation matrix in illegal state!", e);
		}

		fireTransformEvent(Type.ZOOM_MOVE);

		// redraw the canvas
		redraw();

		syncScrollBars();

	}

	/**
	 * Zoom in and implicitly asume that the center of the drawing area is the scale center.
	 */
	public void zoomIn(final double factor) {
		this.checkWidget();

		this.zoom(this.getDrawingRectangle().getWidth() / 2, this.getDrawingRectangle().getHeight() / 2, factor);

	}

	/**
	 * Zoom out and implicitly asume that the center of the drawing area is the scale center.
	 */
	public void zoomOut(final double factor) {
		this.checkWidget();

		this.zoom(this.getDrawingRectangle().getWidth() / 2, this.getDrawingRectangle().getHeight() / 2, 1 / factor);

	}

	/**
	 * Adjusts the transformation matrix to make the given rectangle fit exactly in the drawing
	 * area.
	 *
	 */
	public void autoZoom(final AbsoluteRectangle rect) {
		this.checkWidget();

		log.debug("Auto zooming to " + rect);

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
		newAt.translate(-rect.getLowerLeft().x, -rect.getLowerLeft().y);

		// finally move the rect to the center of the drawing area

		final AbsolutePosition lowerRight = rect.getLowerLeft();
		lowerRight.x += rect.getWidth();
		lowerRight.y += rect.getHeight();
		final Point2D lowerRightPx = newAt.transform(lowerRight.toPoint2D(), null);

		final double deltaX = DAwidth - lowerRightPx.getX();
		final double deltaY = DAhright - lowerRightPx.getY();

		newAt.preConcatenate(AffineTransform.getTranslateInstance(deltaX / 2, deltaY / 2));

		synchronized (transformMutex) {
			// replace the matrix
			this.at = newAt;
		}

		fireTransformEvent(Type.MOVE);

		// redraw the canvas
		redraw();

		syncScrollBars();
	}

	/**
	 * Returns the global bounding box describing the whole world. (absolute coordinates outside
	 * this bounding box can be considered an error.)
	 *
	 * This method is thread-safe.
	 *
	 */
	public static AbsoluteRectangle getGlobalBoundingBox() {

		return new AbsoluteRectangle(WORLD_LOWER_LEFT_X, WORLD_LOWER_LEFT_Y, WORLD_WIDTH, WORLD_HEIGHT);

	}

	/**
	 * Adjust the Transform ,until it is valid again.
	 *
	 * Note: this method is not thread-safe!
	 *
	 * TODO: adjust heuristic for the case when the drawing area is enlarged for more then 2x
	 *
	 */
	protected void adjustToValidMatrix() {

		AffineTransform atCopy;

		synchronized (transformMutex) {
			atCopy = new AffineTransform(at);
		}

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
					atCopy = new AffineTransform(); // TODO
					break;
				}

			} catch (final NoninvertibleTransformException e1) {
				log.error("Transformation matrix in illegal state!", e1);
				break;
			}

		}

		// TODO: what about the gap betweeen the two synchr. blocks?
		synchronized (transformMutex) {
			at = atCopy;
		}

		fireTransformEvent(Type.ZOOM_MOVE);

		// redraw the canvas
		redraw();

		syncScrollBars();
	}

	private AffineTransform createInitialTransform() {
		return AffineTransform.getScaleInstance(1, 1);
	}

	/**
	 * Handle a scroll event
	 */
	protected void scroll() {

		final int selectY = -getVerticalBar().getSelection() - WORLD_LOWER_LEFT_Y;
		final int selectX = getHorizontalBar().getSelection() + WORLD_LOWER_LEFT_X;

		final AbsolutePosition newPos = new AbsolutePosition(selectX, selectY);

		int dx = getLowerLeft().x - newPos.x;
		int dy = getLowerLeft().y + getAbsoluteDrawingRectangle().getHeight() - newPos.y;

		// don't allow using scrollbars if we're at the border
		if ((getHorizontalBar().getSelection() == getHorizontalBar().getMinimum()) && (dx < 0)) {
			dx = 0;
		}
		if ((getHorizontalBar().getSelection() == getHorizontalBar().getMaximum()) && (dx > 0)) {
			dx = 0;
		}
		if ((getVerticalBar().getSelection() == getVerticalBar().getMinimum()) && (dy < 0)) {
			dy = 0;
		}
		if ((getVerticalBar().getSelection() == getVerticalBar().getMaximum()) && (dy > 0)) {
			dy = 0;
		}

		synchronized (transformMutex) {
			at.concatenate(AffineTransform.getTranslateInstance(dx, -dy));
		}

		fireTransformEvent(Type.MOVE);

		// redraw the canvas
		redraw();

		syncScrollBars();
	}

	/**
	 * Readjust the scrollbars to the current position
	 */
	protected void syncScrollBars() {
		getHorizontalBar().setSelection(getLowerLeft().x - WORLD_LOWER_LEFT_X);
		getVerticalBar().setSelection(-getLowerLeft().y - getAbsoluteDrawingRectangle().getHeight()  - WORLD_LOWER_LEFT_Y);
		getHorizontalBar().setThumb(getAbsoluteDrawingRectangle().getWidth());
		getVerticalBar().setThumb(getAbsoluteDrawingRectangle().getHeight());
	}

	/**
	 * Add a new Listener for changes in the transform of this drawing area.
	 *
	 * This listener can be used to listen for changes in zoom or movement of the drawing area.
	 *
	 * This method is thread-safe.
	 *
	 */
	public void addDrawingAreaTransformListener(final TransformChangedListener listener) {
		if (listener == null) {
			return;
		}
		listeners.add(TransformChangedListener.class, listener);
	}

	/**
	 * Remove the given listener.
	 *
	 * This method is thread-safe.
	 *
	 */
	public void removeDrawingAreaTransformListener(final TransformChangedListener listener) {
		if (listener == null) {
			return;
		}

		listeners.remove(TransformChangedListener.class, listener);
	}

	/**
	 * Fires a DrawingAreaTransformEvent.
	 */
	private void fireTransformEvent(final TransformChangedEvent.Type type) {

		final TransformChangedEvent event = new TransformChangedEvent(this, type);

		// Fire the event (call-back method)
		for (final TransformChangedListener l : listeners.getListeners(TransformChangedListener.class)) {
			l.handleEvent(event);
		}
	}

	/**
	 * Returns a copy of the transformation matrix used to transform coordinates from the absolute
	 * reference frame to the pixel reference frame.
	 *
	 * Note that this transform does not incorporate the inverting of the y-axis between absolute and pixel
	 * coordinates.
	 *
	 * This method is thread-safe.
	 *
	 */
	public AffineTransform getTransform() {
		synchronized (transformMutex) {
			return new AffineTransform(at);
		}
	}
}