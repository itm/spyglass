package de.uniluebeck.itm.spyglass.drawing.primitive;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.gui.view.DrawingAreaTransformEvent;
import de.uniluebeck.itm.spyglass.gui.view.DrawingAreaTransformListener;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.StringFormatter;

// --------------------------------------------------------------------------------
/**
 * Instances of this class represent a sensor node.
 * 
 * @author Sebastian Ebers
 * 
 */
public class Node extends DrawingObject {

	private static final SpyglassLogger log = (SpyglassLogger) SpyglassLoggerFactory.getLogger(Node.class);

	private final int nodeID;

	private String denotation;

	private String stringFormatterResult;

	private volatile boolean isExtended;

	private int lineWidth;

	private Object drawingAreaMutex = new Object();

	private DrawingArea drawingArea;

	/**
	 * True, if this node object has registered <code>drawingAreaListener</code> with the drawing
	 * area.
	 */
	private boolean listenerConnected = false;

	/**
	 * The bounding box (in Absolute coordinates) depends on the current zoom level. to make sure
	 * that we always return a correct bounding box, we listen for changes in the drawing area and
	 * update the bounding box accordingly.
	 * 
	 * Note: this hack is only necessary, since a Node "doesn't zoom". Most other DrawingObjects
	 * don't have to bother.
	 */
	private DrawingAreaTransformListener drawingAreaListener = new DrawingAreaTransformListener() {

		@Override
		public void handleEvent(final DrawingAreaTransformEvent e) {
			setDrawingArea(e.drawingArea);
			updateBoundingBox();
		}
	};

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 */
	public Node(final int nodeID, final String denotation) {
		this(nodeID, denotation, null, false, null);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 * @param stringFormatterResult
	 *            detailed information generated by one or more {@link StringFormatter} (only shown
	 *            in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 * @param drawingArea
	 *            the currently active drawing area
	 */
	public Node(final int nodeID, final String denotation, final String stringFormatterResult, final boolean isExtended, final DrawingArea drawingArea) {
		this(nodeID, denotation, stringFormatterResult, isExtended, new int[] { 255, 0, 0 }, 1, drawingArea);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 * @param stringFormatterResult
	 *            detailed information generated by one or more {@link StringFormatter} (only shown
	 *            in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 * @param lineColorRGB
	 *            the line colo's RGB values
	 * @param lineWidth
	 *            the line's width
	 * @param drawingArea
	 *            the currently active drawing area
	 */
	public Node(final int nodeID, final String denotation, final String stringFormatterResult, final boolean isExtended, final int[] lineColorRGB,
			final int lineWidth, final DrawingArea drawingArea) {
		this(nodeID, denotation, stringFormatterResult, isExtended, lineColorRGB, lineWidth, drawingArea, new AbsolutePosition(0, 0, 0));
	}

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param nodeID
	 *            the identifier of the node which is visualized
	 * @param denotation
	 *            the denotation of the visualization
	 * @param stringFormatterResult
	 *            detailed information generated by one or more {@link StringFormatter} (only shown
	 *            in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 * @param lineColorRGB
	 *            the line colo's RGB values
	 * @param lineWidth
	 *            the line's width
	 * @param drawingArea
	 *            the currently active drawing area
	 * @param position
	 *            the node's absolute position
	 */
	public Node(final int nodeID, final String denotation, final String stringFormatterResult, final boolean isExtended, final int[] lineColorRGB,
			final int lineWidth, final DrawingArea drawingArea, final AbsolutePosition position) {
		super();
		this.nodeID = nodeID;
		this.denotation = denotation;
		this.stringFormatterResult = stringFormatterResult;
		this.isExtended = isExtended;
		this.lineWidth = lineWidth;
		this.setColor(new RGB(lineColorRGB[0], lineColorRGB[1], lineColorRGB[2]));
		setDrawingArea(drawingArea);
		setPosition(position);
		updateBoundingBox();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the drawingArea
	 */
	public DrawingArea getDrawingArea() {
		synchronized (drawingAreaMutex) {
			return drawingArea;
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param drawingArea
	 *            the drawingArea to set
	 */
	void setDrawingArea(final DrawingArea drawingArea) {
		synchronized (drawingAreaMutex) {
			this.drawingArea = drawingArea;
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Updates the parameters
	 * 
	 * @param denotation
	 *            the denotation of the visualization
	 * @param stringFormatterResult
	 *            detailed information generated by one or more {@link StringFormatter} (only shown
	 *            in extended mode)
	 * @param isExtended
	 *            indicates if the extended mode is active
	 * @param lineColorRGB
	 *            the line colo's RGB values
	 * @param lineWidth
	 *            the line's width
	 */
	public void update(final String denotation, final String stringFormatterResult, final boolean isExtended, final int[] lineColorRGB,
			final int lineWidth) {
		this.denotation = denotation;
		this.stringFormatterResult = stringFormatterResult;
		this.isExtended = isExtended;
		this.lineWidth = lineWidth;
		this.setColor(new RGB(lineColorRGB[0], lineColorRGB[1], lineColorRGB[2]));
		updateBoundingBox();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the denotation
	 */
	public String getDenotation() {
		return denotation;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param denotation
	 *            the denotation to set
	 */
	public void setDenotation(final String denotation) {
		if ((this.denotation == null) || !this.denotation.equals(denotation)) {
			this.denotation = denotation;
			updateBoundingBox();
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns detailed information generated by one or more {@link StringFormatter} (only shown in
	 * extended mode)
	 * 
	 * @return detailed information generated by one or more {@link StringFormatter} (only shown in
	 *         extended mode)
	 */
	public String getStringFormatterResult() {
		return stringFormatterResult;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets detailed information generated by one or more {@link StringFormatter} (only shown in
	 * extended mode)
	 * 
	 * @param stringFormatterResult
	 *            detailed information generated by one or more {@link StringFormatter} (only shown
	 *            in extended mode)
	 */
	public void setDescription(final String stringFormatterResult) {
		this.stringFormatterResult = stringFormatterResult;
		updateBoundingBox();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the isExtended
	 */
	public boolean isExtended() {
		return isExtended;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Activates or deactivates the display of additional information
	 * 
	 * @param isExtended
	 *            if <code>true</code> the information will be displayed
	 */
	public void setExtended(final boolean isExtended) {
		this.isExtended = isExtended;
		updateBoundingBox();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param lineWidth
	 *            the lineWidth to set
	 */
	public void setLineWidth(final int lineWidth) {
		this.lineWidth = lineWidth;
		updateBoundingBox();
	}

	// --------------------------------------------------------------------------------
	/**
	 * @return the nodeID
	 */
	public int getNodeID() {
		return nodeID;
	}

	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {

		try {
			if (this.drawingArea == null) {
				setDrawingArea(drawingArea);
				updateBoundingBox();
			} else {
				setDrawingArea(drawingArea);
			}
			
			if (!listenerConnected) {
				listenerConnected = true;
				drawingArea.addDrawingAreaTransformListener(this.drawingAreaListener);
			}

			// set the colors and the width of the rectangle's line
			final Color color = new Color(null, this.getColor());
			final Color bg = new Color(null, this.getBgColor());
			gc.setForeground(color);
			gc.setBackground(bg);
			gc.setLineWidth(lineWidth);

			// get the information to be displayed
			final String string = getInformationString();

			// determine the size parameters of the rectangle which represents the node in respect
			// to the sting to be displayed
			final Point size = gc.textExtent(string);
			final int width = size.x + lineWidth + 3; // +1 for correct display with uneven line
			// width
			final int height = size.y + lineWidth + 3; // and +2 for the oval to be drawn

			// get the node's position in the drawing area
			final AbsolutePosition pos = getPosition();
			final PixelPosition upperLeft = drawingArea.absPoint2PixelPoint(pos != null ? pos : new AbsolutePosition(0, 0, 0));

			// the rectangles upper left pint will be in the middle of the surrounding line. Since
			// the rectangle's upper left edge represents the object location, it has to be adapted
			final PixelPosition upperLeftRect = new PixelPosition(upperLeft.x + lineWidth / 2, upperLeft.y + lineWidth / 2);

			// determine the bounding box (this time use the objects upper left point and not the
			// adapted one
			// boundingBox = determineBoundingBox(drawingArea, upperLeft, lineWidth, width, height);

			// the new rectangle starts at the determined upper left position. Its with and height
			// was determined in respect to the text which is to be displayed
			final PixelRectangle pxRect = new PixelRectangle(upperLeftRect, width, height);
			gc.fillRectangle(pxRect.toSWTRectangle());
			gc.drawRectangle(pxRect.toSWTRectangle());

			// place the string inside the rectangle with respect to the side effects of the line
			// width (see above)
			gc.drawText(string, 3 + upperLeftRect.x + lineWidth / 2, 3 + upperLeftRect.y + lineWidth / 2,true);

			// draw an oval to indicate that the objects location is the rectangles upper left edge
			gc.setLineWidth(lineWidth + 1);
			gc.drawOval(upperLeftRect.x, upperLeftRect.y, 3, 3);

			// dispose the no longer used colors
			color.dispose();
			bg.dispose();
		} catch (final SWTException e) {
			if (e.getMessage().contains("Widget is disposed")) {
				log.warn("The drawing operation could not be performed: " + e);
			} else {
				log.error(e, e, false);
			}
		}
		
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a string which is to be displayed in the node object.<br>
	 * This string contains information like e.g. the node's identifier and - in respect to the
	 * extended information status - the information of the available {@link StringFormatter}
	 * instances as well.
	 * 
	 * @return a string which is to be displayed in the node object
	 */
	String getInformationString() {
		// get the detailed information by querying the string formatter
		final String descriptionString = (stringFormatterResult == null) ? "" : stringFormatterResult.toString();

		// create the string to be displayed
		final String string = (isExtended) ? denotation + "\r\n" + descriptionString : denotation;
		return string;
	}

	@Override
	public String toString() {
		return "[Node " + nodeID + "]";
	}

	@Override
	protected AbsoluteRectangle calculateBoundingBox() {

		final DrawingArea drawingArea = getDrawingArea();

		// to prevent NPEs
		AbsolutePosition pos = getPosition();
		if (pos == null) {
			pos = new AbsolutePosition(0, 0, 0);
		}

		// if no drawing area is available yet, the bounding box is to be set quite small
		if ((drawingArea == null) || drawingArea.isDisposed()) {
			return new AbsoluteRectangle(pos, 0, 0);
		}

		// get the information to be displayed
		final String string = getInformationString();
		final int lineWidth = getLineWidth();
		// determine the size parameters of the rectangle which represents the node
		// in respect to the sting to be displayed
		final org.eclipse.swt.graphics.Image i = new Image(null, 1, 1);
		final GC gc = new GC(i);
		final Point size = gc.textExtent(string);
		final int width = size.x + lineWidth + 3; // +3: see above
		final int height = size.y + lineWidth + 3;

		final PixelPosition upperLeft = drawingArea.absPoint2PixelPoint(pos);

		// since the rectangle's line is spread according to its width with the
		// actual position in it's center, the upper left position of the bounding box
		// has to adapt to this
		final int bbUpperLeftX = upperLeft.x;
		final int bbUpperLeftY = upperLeft.y;

		// the line width has to be counted twice because two lines with the same
		// width are drawn on the drawing area
		final int bbWidht = width + 2 * lineWidth;
		final int bbHeight = height + 2 * lineWidth;
		final PixelRectangle bbArea = new PixelRectangle(bbUpperLeftX, bbUpperLeftY, bbWidht + 1, bbHeight + 1);

		gc.dispose();
		i.dispose();

		return getDrawingArea().pixelRect2AbsRect(bbArea);

	}
}