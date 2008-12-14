package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.view.DrawingArea;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.positions.PixelPosition;
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * A map object for the MapPainterPlugin
 *  
 * @author Dariush Forouher
 * 
 */
public class Map extends DrawingObject {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(Map.class);
	
	private static final boolean DEBUG = false;
	
	/**
	 * Reference to the corresponding config
	 */
	private final MapPainterXMLConfig config;
	
	final private MapPainterPlugin plugin;
	
	Map(final MapPainterXMLConfig config, final MapPainterPlugin plugin) {
		this.config = config;
		this.plugin = plugin;
		
		config.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				updateBoundingBox();
			}
			
		});
	}
	
	@Override
	public void draw(final DrawingArea drawingArea, final GC gc) {
		
		final PixelRectangle clippAreaPx = new PixelRectangle(gc.getClipping());
		
		final AbsoluteRectangle clippingArea = drawingArea.pixelRect2AbsRect(clippAreaPx);
		
		if (DEBUG) {
			final Color boxColor = new Color(null, 0, 0, 0);
			gc.setBackground(boxColor);
			for (final DataPoint p : plugin.dataStore) {
				final PixelPosition px = drawingArea.absPoint2PixelPoint(p.position);
				gc.fillRectangle(px.x - 5, px.y - 5, 10, 10);
			}
			boxColor.dispose();
		}
		
		final AbsoluteRectangle drawRect = new AbsoluteRectangle();
		final AbsolutePosition curUL = config.getLowerLeft();
		while (curUL.y <= config.getLowerLeft().y + config.getHeight()
				- config.getGridElementHeight()) {
			while (curUL.x <= config.getLowerLeft().x + config.getWidth()
					- config.getGridElementWidth()) {
				
				drawRect.setHeight(config.getGridElementHeight());
				drawRect.setWidth(config.getGridElementWidth());
				drawRect.setUpperLeft(curUL);
				
				// Clipping
				if (clippingArea.intersects(drawRect)) {
				
					drawElement(drawingArea, gc, drawRect);
					
				}

				curUL.x += config.getGridElementWidth();

			}
			
			curUL.x = config.getLowerLeft().x;
			curUL.y += config.getGridElementWidth();
		}
		
		if (DEBUG) {
			drawBoundingBox(drawingArea, gc);
		}
	}

	/**
	 * Draw the Element drawRect.
	 */
	private void drawElement(final DrawingArea drawingArea, final GC gc, final AbsoluteRectangle drawRect) {
		final PixelRectangle pxRect = drawingArea.absRect2PixelRect(drawRect);
		
		final double average = calculateValue(drawRect.getCenter());
		final RGB avgColor = calculateColor(average);
		final Color color = new Color(gc.getDevice(), avgColor);

		gc.setBackground(color);
		gc.fillRectangle(pxRect.toSWTRectangle());
		
		if (DEBUG) {
			gc.drawText(String.format("%.0f", average), pxRect.getUpperLeft().x, pxRect
					.getUpperLeft().y);
		}
		
		color.dispose();
	}
	
	/**
	 * Calculate the color for the given value.
	 */
	private RGB calculateColor(final double value) {
		
		// normalize value between minValue and maxValue to the range [0;1]
		final double lambda = (value - config.getMinValue())
				/ (config.getMaxValue() - config.getMinValue());
		
		// Calculate new Color
		final int[] rgb = new int[3];
		for (int k = 0; k < 3; k++) {
			final double d = config.getMinColorRGB()[k] + lambda
					* (config.getMaxColorRGB()[k] - config.getMinColorRGB()[k]);
			
			if (d < 0) {
				rgb[k] = 0;
			} else if (d > 255) {
				rgb[k] = 255;
			} else {
				rgb[k] = (int) d;
			}
			
		}
		// log.debug(String.format("Element %s with value %f gets color [%d,%d,%d]; lambda=%f",
		// centerPoint, average, rgb[0], rgb[1], rgb[2], lambda));
		
		return new RGB(rgb[0], rgb[1], rgb[2]);
		// final Random r = new Random();
		// return new RGB(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		
	}
	
	/**
	 * Calculate the value at the given point in space based on sourrounding nodes.
	 */
	private double calculateValue(final AbsolutePosition point) {
		final List<DataPoint> neighbors;
		synchronized (plugin.dataStore) {
			neighbors = plugin.dataStore.kNN(point, config.getK());
		}
		
		double sum = 0;
		for (final DataPoint dataPoint : neighbors) {
			sum += dataPoint.value;
		}
		sum /= neighbors.size();
		
		return sum;
	}
	
	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return config.getBoundingBox();
	}
}
