/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.nodesensorrange;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementMap;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link NodeSensorRangePlugin}
 * 
 * @author Sebastian Ebers, Daniel Bimschas
 * 
 */
public class NodeSensorRangeXMLConfig extends PluginXMLConfig implements PropertyChangeListener {

	public static class CircleRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_CIRCLE_RADIUS, required = false)
		private int circleRadius = 100;

		public CircleRange() {

		}

		public CircleRange(final int radius) {
			this.circleRadius = radius;
		}

		public CircleRange(final CircleRange other) {
			this(other.circleRadius);
		}

		public int getCircleRadius() {
			return circleRadius;
		}

		public void setCircleRadius(final int radius) {
			final int old = this.circleRadius;
			this.circleRadius = radius;
			firePropertyChange(PROPERTYNAME_CIRCLE_RADIUS, old, this);
		}

		@Override
		protected NodeSensorRange clone() {
			return new CircleRange(this);
		}

	}

	public static class ConeRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_CONE_ORIENTATION, required = false)
		private int coneOrientation = 0;

		@Element(name = PROPERTYNAME_CONE_RADIUS, required = false)
		private int coneRadius = 100;

		@Element(name = PROPERTYNAME_CONE_VIEWANGLE, required = false)
		private int coneViewAngle = 45;

		public ConeRange() {

		}

		public ConeRange(final int orientation, final int radius, final int viewAngle) {
			this.coneOrientation = orientation;
			this.coneRadius = radius;
			this.coneViewAngle = viewAngle;
		}

		public ConeRange(final ConeRange other) {
			this(other.coneOrientation, other.coneRadius, other.coneViewAngle);
		}

		public int getConeOrientation() {
			return coneOrientation;
		}

		public int getConeRadius() {
			return coneRadius;
		}

		public int getConeViewAngle() {
			return coneViewAngle;
		}

		public void setConeOrientation(final int orientation) {
			final int old = this.coneOrientation;
			this.coneOrientation = orientation;
			firePropertyChange(PROPERTYNAME_CONE_ORIENTATION, old, this);
		}

		public void setConeRadius(final int radius) {
			final int old = this.coneRadius;
			this.coneRadius = radius;
			firePropertyChange(PROPERTYNAME_CONE_RADIUS, old, this);
		}

		public void setConeViewAngle(final int viewAngle) {
			final int old = this.coneViewAngle;
			this.coneViewAngle = viewAngle;
			firePropertyChange(PROPERTYNAME_CONE_VIEWANGLE, old, this);
		}

		@Override
		protected NodeSensorRange clone() {
			return new ConeRange(this);
		}

	}

	public static class Config extends PropertyBean {

		@Element(name = PROPERTYNAME_LINE_WIDTH, required = false)
		private int lineWidth = 1;

		@Element(name = PROPERTYNAME_BACKGROUND_ALPHA, required = false)
		private int backgroundAlpha = 30;

		@ElementArray(name = PROPERTYNAME_BACKGROUND_R_G_B, required = false)
		private int[] backgroundRGB = { 0, 255, 0 };

		@ElementArray(name = PROPERTYNAME_COLOR_R_G_B, required = false)
		private int[] colorRGB = { 0, 0, 0 };

		@Element(name = PROPERTYNAME_RANGE, required = false)
		private NodeSensorRange range = new CircleRange();

		@Element(name = PROPERTYNAME_RANGE_TYPE, required = false)
		private String rangeType = "Circle";

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 */
		public Config() {
			// nothing to do
		}

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 * 
		 * @param backgroundAlpha
		 * @param backgroundRGB
		 * @param colorRGB
		 * @param range
		 * @param rangeType
		 */
		public Config(final int backgroundAlpha, final int[] backgroundRGB, final int[] colorRGB, final NodeSensorRange range, final String rangeType) {
			super();
			this.backgroundAlpha = backgroundAlpha;
			this.backgroundRGB = backgroundRGB;
			this.colorRGB = colorRGB;
			this.range = range;
			this.rangeType = rangeType;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Copy constructor. Makes a deep copy.
		 * 
		 * @param other
		 *            object to copy values from
		 */
		public Config(final Config other) {
			this(other.backgroundAlpha, other.backgroundRGB, other.colorRGB, other.range.clone(), other.rangeType);
		}

		public int getBackgroundAlpha() {
			return backgroundAlpha;
		}

		public int[] getBackgroundRGB() {
			return backgroundRGB;
		}

		public int[] getColorRGB() {
			return colorRGB;
		}

		public NodeSensorRange getRange() {
			return range;
		}

		public String getRangeType() {
			return rangeType;
		}

		public int getLineWidth() {
			return lineWidth;
		}

		public void setLineWidth(final int lineWidth) {
			firePropertyChange(PROPERTYNAME_LINE_WIDTH, this.lineWidth, this.lineWidth = lineWidth);
		}

		public void setBackgroundAlpha(final int backgroundAlpha) {
			firePropertyChange(PROPERTYNAME_BACKGROUND_ALPHA, this.backgroundAlpha, this.backgroundAlpha = backgroundAlpha);
		}

		public void setBackgroundRGB(final int[] backgroundRGB) {
			firePropertyChange(PROPERTYNAME_BACKGROUND_R_G_B, this.backgroundRGB, this.backgroundRGB = backgroundRGB);
		}

		public void setColorRGB(final int[] colorRGB) {
			firePropertyChange(PROPERTYNAME_COLOR_R_G_B, this.colorRGB, this.colorRGB = colorRGB);
		}

		public void setRange(final NodeSensorRange range) {
			firePropertyChange(PROPERTYNAME_RANGE, this.range, this.range = range);
		}

		public void setRangeType(final String rangeType) {
			firePropertyChange(PROPERTYNAME_RANGE_TYPE, this.rangeType, this.rangeType = rangeType);
		}

		@Override
		protected Config clone() {
			return new Config(this);
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * "Flag" base class grouping together {@link NodeSensorRangeXMLConfig.RectangleRange},
	 * {@link NodeSensorRangeXMLConfig.CircleRange} &amp; {@link NodeSensorRangeXMLConfig.ConeRange}
	 * 
	 * @author Daniel Bimschas
	 */
	public static abstract class NodeSensorRange extends PropertyBean {

		@Override
		protected abstract NodeSensorRange clone();

	}

	public static class RectangleRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_RECTANGLE_HEIGHT, required = false)
		private int rectangleHeight = 100;

		@Element(name = PROPERTYNAME_RECTANGLE_ORIENTATION, required = false)
		private int rectangleOrientation = 45;

		@Element(name = PROPERTYNAME_RECTANGLE_WIDTH, required = false)
		private int rectangleWidth = 100;

		public RectangleRange() {
			// nothing to do
		}

		public RectangleRange(final int orientation, final int width, final int height) {
			this.rectangleHeight = height;
			this.rectangleOrientation = orientation;
			this.rectangleWidth = width;
		}

		public RectangleRange(final RectangleRange other) {
			this(other.rectangleOrientation, other.rectangleWidth, other.rectangleHeight);
		}

		public int getRectangleHeight() {
			return rectangleHeight;
		}

		public int getRectangleOrientation() {
			return rectangleOrientation;
		}

		public int getRectangleWidth() {
			return rectangleWidth;
		}

		public void setRectangleHeight(final int rectangleHeight) {
			final int old = this.rectangleHeight;
			this.rectangleHeight = rectangleHeight;
			firePropertyChange(PROPERTYNAME_RECTANGLE_HEIGHT, old, this);
		}

		public void setRectangleOrientation(final int rectangleOrientation) {
			final int old = this.rectangleOrientation;
			this.rectangleOrientation = rectangleOrientation;
			firePropertyChange(PROPERTYNAME_RECTANGLE_ORIENTATION, old, this);
		}

		public void setRectangleWidth(final int rectangleWidth) {
			final int old = this.rectangleWidth;
			this.rectangleWidth = rectangleWidth;
			firePropertyChange(PROPERTYNAME_RECTANGLE_WIDTH, old, this);
		}

		@Override
		protected NodeSensorRange clone() {
			return new RectangleRange(this);
		}

	}

	public static final String PROPERTYNAME_LINE_WIDTH = "lineWidth";

	public static final String PROPERTYNAME_BACKGROUND_ALPHA = "backgroundAlpha";

	public static final String PROPERTYNAME_BACKGROUND_R_G_B = "backgroundRGB";

	public static final String PROPERTYNAME_CIRCLE_RADIUS = "circleRadius";

	public static final String PROPERTYNAME_COLOR_R_G_B = "colorRGB";

	public static final String PROPERTYNAME_CONE_ORIENTATION = "coneOrientation";

	public static final String PROPERTYNAME_CONE_RADIUS = "coneRadius";

	public static final String PROPERTYNAME_CONE_VIEWANGLE = "coneViewAngle";

	public static final String PROPERTYNAME_DEFAULT_CONFIG = "defaultConfig";

	public static final String PROPERTYNAME_NODE_RANGES = "nodeRanges";

	public static final String PROPERTYNAME_RANGE = "range";

	public static final String PROPERTYNAME_RANGE_TYPE = "rangeType";

	public static final String PROPERTYNAME_RECTANGLE_HEIGHT = "rectangleHeight";

	public static final String PROPERTYNAME_RECTANGLE_ORIENTATION = "rectangleOrientation";

	public static final String PROPERTYNAME_RECTANGLE_WIDTH = "rectangleWidth";

	public static final String PROPERTYVALUE_RANGE_TYPE_CIRCLE = "Circle";

	public static final String PROPERTYVALUE_RANGE_TYPE_CONE = "Cone";

	public static final String PROPERTYVALUE_RANGE_TYPE_RECTANGLE = "Rectangle";

	@Element(name = PROPERTYNAME_DEFAULT_CONFIG, required = false)
	private final Config defaultConfig = new Config();

	@ElementMap(entry = PROPERTYNAME_NODE_RANGES, key = "nodeID", attribute = true, valueType = NodeSensorRange.class, required = false)
	private HashMap<Integer, NodeSensorRange> nodeRanges = new HashMap<Integer, NodeSensorRange>();

	public void addNodeRange(final int nodeId, final NodeSensorRange range) {
		nodeRanges.put(nodeId, range);
		range.addPropertyChangeListener(this);
	}

	public boolean equals(final NodeSensorRangeXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		return nodeRanges.equals((o).nodeRanges);
	}

	public Config getDefaultConfig() {
		return defaultConfig;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

	public void removeNodeRange(final int nodeId) {
		nodeRanges.remove(nodeId).removePropertyChangeListener(this);
	}

}