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
import de.uniluebeck.itm.spyglass.positions.PixelRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * A map object for the MapPainterPlugin
 * 
 * TODO: put the framepoints in the positions-list and add a property-listener on framePointNumbers
 * (makes stuff easier)
 * 
 * @author Dariush Forouher
 * 
 */
public class Map extends DrawingObject {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(Map.class);
	
	/**
	 * Reference to the corresponding config
	 */
	MapPainterXMLConfig config;
	
	class Node {
		int id;
		double value;
		AbsolutePosition position;
	}
	
	/**
	 * Data of the nodes
	 */
	HashMap<Integer, Double> values = new HashMap<Integer, Double>();
	
	/**
	 * Positions of the nodes
	 */
	HashMap<Integer, AbsolutePosition> positions = new HashMap<Integer, AbsolutePosition>();
	
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
		
		final AbsoluteRectangle area = drawingArea.pixelRect2AbsRect(clippAreaPx);
		
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
				
				final RGB avgColor;
				synchronized (this) {
					avgColor = calculateColor(drawRect.getCenter());
				}
				final Color color = new Color(null, avgColor);
				gc.setBackground(color);
				gc.fillRectangle(pxRect.toSWTRectangle());
				color.dispose();
				
				curUL.x += config.getGridElementWidth();
			}
			curUL.x = config.getLowerLeft().x;
			curUL.y += config.getGridElementWidth();
		}
		
		drawBoundingBox(drawingArea, gc);
	}
	
	/**
	 * Calculate the color for the given point.
	 */
	private RGB calculateColor(final AbsolutePosition centerPoint) {
		
		final ArrayList<Integer> sorted = new ArrayList<Integer>(this.positions.keySet());
		
		Collections.sort(sorted, new Comparator<Integer>() {
			
			@Override
			public int compare(final Integer o1, final Integer o2) {
				
				final double dist1 = positions.get(o1).getDistance(centerPoint);
				final double dist2 = positions.get(o2).getDistance(centerPoint);
				
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
		for (int i = 0; i < sorted.size() - 1; i++) {
			final double dist1 = positions.get(sorted.get(i)).getDistance(centerPoint);
			final double dist2 = positions.get(sorted.get(i + 1)).getDistance(centerPoint);
			if (!(dist1 <= dist2)) {
				positions.get(sorted.get(i + 1)).getDistance(centerPoint);
			}
			assert dist1 <= dist2 : dist1 + " larger than " + dist2;
		}
		
		// because of the framepoints there should always be at least three points
		assert sorted.size() >= 3 : "Not enough nodes available";
		
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
		
		// normalize value between minValue and maxValue to the range [0;1]
		final double lambda = (average - config.getMinValue())
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
	
	@Override
	protected AbsoluteRectangle calculateBoundingBox() {
		return config.getBoundingBox();
	}
}
