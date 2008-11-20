package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
 * TODO: the algorithms used here are abysmally slow...
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
	
	/**
	 * Data of the nodes
	 */
	final HashMap<Integer, Double> values = new HashMap<Integer, Double>();
	
	/**
	 * Positions of the nodes
	 */
	final HashMap<Integer, AbsolutePosition> positions = new HashMap<Integer, AbsolutePosition>();
	
	Map(final MapPainterXMLConfig config) {
		this.config = config;
		
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
		
		// TODO
		final AbsoluteRectangle area = drawingArea.pixelRect2AbsRect(clippAreaPx);
		
		if (DEBUG) {
			final Color colorBla = new Color(null, 0, 0, 0);
			gc.setBackground(colorBla);
			for (final AbsolutePosition pos : this.positions.values()) {
				final PixelPosition px = drawingArea.absPoint2PixelPoint(pos);
				gc.fillRectangle(px.x - 5, px.y - 5, 10, 10);
			}
			colorBla.dispose();
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
				
				final PixelRectangle pxRect = drawingArea.absRect2PixelRect(drawRect);
				
				final double average = calculateValue(drawRect.getCenter());
				final RGB avgColor = calculateColor(average);
				
				final Color color = new Color(null, avgColor);
				gc.setBackground(color);
				gc.fillRectangle(pxRect.toSWTRectangle());
				
				if (DEBUG) {
					gc.drawText(String.format("%.0f", average), pxRect.getUpperLeft().x, pxRect
							.getUpperLeft().y);
				}
				
				color.dispose();
				
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
		
		final ArrayList<Integer> sorted = new ArrayList<Integer>(this.positions.keySet());
		
		Collections.sort(sorted, new Comparator<Integer>() {
			
			@Override
			public int compare(final Integer o1, final Integer o2) {
				
				final double dist1 = positions.get(o1).getDistance(point);
				final double dist2 = positions.get(o2).getDistance(point);
				
				if (dist1 < dist2) {
					return -1;
				} else if (dist1 > dist2) {
					return 1;
				} else {
					return 0;
				}
			}
			
		});
		
		// sorted should now contain the nearest nodes at the beginning
		
		// runtime check
		if (DEBUG) {
			for (int i = 0; i < sorted.size() - 1; i++) {
				final double dist1 = positions.get(sorted.get(i)).getDistance(point);
				final double dist2 = positions.get(sorted.get(i + 1)).getDistance(point);
				if (!(dist1 <= dist2)) {
					positions.get(sorted.get(i + 1)).getDistance(point);
				}
				assert dist1 <= dist2 : dist1 + " larger than " + dist2;
			}
			// because of the framepoints there should always be at least three points
			assert sorted.size() >= 3 : "Not enough nodes available";
			
		}
		
		// log.debug(String.format("The data of the three nodes: %f, %f, %f", values
		// .get(sorted.get(0)), values.get(sorted.get(1)), values.get(sorted.get(2))));
		
		final double average = (values.get(sorted.get(0)) + values.get(sorted.get(1)) + values
				.get(sorted.get(2))) / 3;
		
		// log
		// .debug(String
		// .format(
		// "minValue=%f, maxValue=%f, defaultValue=%f minColor=[%d,%d,%d]; maxColor [%d,%d,%d]",
		// config.getMinValue(), config.getMaxValue(), config
		// .getDefaultValue(), config.getMinColorRGB()[0], config
		// .getMinColorRGB()[1], config.getMinColorRGB()[2], config
		// .getMaxColorRGB()[0], config.getMaxColorRGB()[1], config
		// .getMaxColorRGB()[2]));
		
		return average;
	}
	
	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return config.getBoundingBox();
	}
}
