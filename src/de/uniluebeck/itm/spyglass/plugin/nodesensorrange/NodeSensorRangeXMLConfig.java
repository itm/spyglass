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
		private float circleRadius = 0;

		public CircleRange() {

		}

		public CircleRange(final float radius) {
			this.circleRadius = radius;
		}

		public float getCircleRadius() {
			return circleRadius;
		}

		public void setCircleRadius(final float radius) {
			final float old = this.circleRadius;
			this.circleRadius = radius;
			firePropertyChange(PROPERTYNAME_CIRCLE_RADIUS, old, this);
		}

	}

	public static class ConeRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_CONE_ORIENTATION, required = false)
		private float coneOrientation = 0;

		@Element(name = PROPERTYNAME_CONE_RADIUS, required = false)
		private float coneRadius = 0;

		@Element(name = PROPERTYNAME_CONE_VIEWANGLE, required = false)
		private float coneViewAngle = 0;

		public ConeRange() {

		}

		public ConeRange(final byte orientation, final float radius, final float viewAngle) {
			this.coneOrientation = orientation;
			this.coneRadius = radius;
			this.coneViewAngle = viewAngle;
		}

		public float getConeOrientation() {
			return coneOrientation;
		}

		public float getConeRadius() {
			return coneRadius;
		}

		public float getConeViewAngle() {
			return coneViewAngle;
		}

		public void setConeOrientation(final float orientation) {
			final float old = this.coneOrientation;
			this.coneOrientation = orientation;
			firePropertyChange(PROPERTYNAME_CONE_ORIENTATION, old, this);
		}

		public void setConeRadius(final float radius) {
			final float old = this.coneRadius;
			this.coneRadius = radius;
			firePropertyChange(PROPERTYNAME_CONE_RADIUS, old, this);
		}

		public void setConeViewAngle(final float viewAngle) {
			final float old = this.coneViewAngle;
			this.coneViewAngle = viewAngle;
			firePropertyChange(PROPERTYNAME_CONE_VIEWANGLE, old, this);
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

	}

	public static class RectangleRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_RECTANGLE_HEIGHT, required = false)
		private float rectangleHeight = 0;

		@Element(name = PROPERTYNAME_RECTANGLE_ORIENTATION, required = false)
		private float rectangleOrientation = 0;

		@Element(name = PROPERTYNAME_RECTANGLE_WIDTH, required = false)
		private float rectangleWidth = 0;

		public RectangleRange() {

		}

		public RectangleRange(final byte orientation, final float width, final float height) {
			this.rectangleHeight = height;
			this.rectangleOrientation = orientation;
			this.rectangleWidth = width;
		}

		public float getRectangleHeight() {
			return rectangleHeight;
		}

		public float getRectangleOrientation() {
			return rectangleOrientation;
		}

		public float getRectangleWidth() {
			return rectangleWidth;
		}

		public void setRectangleHeight(final float height) {
			final float old = this.rectangleHeight;
			this.rectangleHeight = height;
			firePropertyChange(PROPERTYNAME_RECTANGLE_HEIGHT, old, this);
		}

		public void setRectangleOrientation(final float orientation) {
			final float old = this.rectangleOrientation;
			this.rectangleOrientation = orientation;
			firePropertyChange(PROPERTYNAME_RECTANGLE_ORIENTATION, old, this);
		}

		public void setRectangleWidth(final float width) {
			final float old = this.rectangleWidth;
			this.rectangleWidth = width;
			firePropertyChange(PROPERTYNAME_RECTANGLE_WIDTH, old, this);
		}

	}

	public static final String PROPERTYNAME_CIRCLE_RADIUS = "circleRadius";

	public static final String PROPERTYNAME_CONE_ORIENTATION = "coneOrientation";

	public static final String PROPERTYNAME_CONE_RADIUS = "coneRadius";

	public static final String PROPERTYNAME_CONE_VIEWANGLE = "coneViewAngle";

	public static final String PROPERTYNAME_DEFAULT_COLOR_R_G_B = "defaultColorRGB";

	public static final String PROPERTYNAME_DEFAULT_RANGE = "defaultRange";

	public static final String PROPERTYNAME_DEFAULT_RANGE_TYPE = "defaultRangeType";

	public static final String PROPERTYNAME_NODE_RANGES = "nodeRanges";

	public static final String PROPERTYNAME_RECTANGLE_HEIGHT = "rectangleHeight";

	public static final String PROPERTYNAME_RECTANGLE_ORIENTATION = "rectangleOrientation";

	public static final String PROPERTYNAME_RECTANGLE_WIDTH = "rectangleWidth";

	public static final String PROPERTYVALUE_RANGE_TYPE_CIRCLE = "Circle";

	public static final String PROPERTYVALUE_RANGE_TYPE_RECTANGLE = "Rectangle";

	public static final String PROPERTYVALUE_RANGE_TYPE_CONE = "Cone";

	@ElementArray(name = PROPERTYNAME_DEFAULT_COLOR_R_G_B, required = false)
	private int[] defaultColorRGB = { 0, 0, 0 };

	@Element(name = PROPERTYNAME_DEFAULT_RANGE, required = false)
	private NodeSensorRange defaultRange = new CircleRange(1);

	@Element(name = PROPERTYNAME_DEFAULT_RANGE_TYPE, required = false)
	private String defaultRangeType = "Circle";

	@ElementMap(entry = PROPERTYNAME_NODE_RANGES, key = "nodeID", attribute = true, valueType = NodeSensorRange.class, required = false)
	private HashMap<Integer, NodeSensorRange> nodeRanges = new HashMap<Integer, NodeSensorRange>();

	public boolean equals(final NodeSensorRangeXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		return nodeRanges.equals((o).nodeRanges);
	}

	public int[] getDefaultColorRGB() {
		return defaultColorRGB;
	}

	public NodeSensorRange getDefaultRange() {
		return defaultRange;
	}

	public String getDefaultRangeType() {
		return defaultRangeType;
	}

	public void setDefaultRangeType(final String defaultRangeType) {
		firePropertyChange(PROPERTYNAME_DEFAULT_RANGE_TYPE, this.defaultRangeType, this.defaultRangeType = defaultRangeType);
	}

	public void setDefaultColorRGB(final int[] colorRGB) {
		firePropertyChange(PROPERTYNAME_DEFAULT_COLOR_R_G_B, this.defaultColorRGB, this.defaultColorRGB = colorRGB);
	}

	public void setDefaultRange(final NodeSensorRange defaultRange) {
		firePropertyChange(PROPERTYNAME_DEFAULT_RANGE, this.defaultRange, this.defaultRange = defaultRange);
	}

	public void addNodeRange(final int nodeId, final NodeSensorRange range) {
		nodeRanges.put(nodeId, range);
		range.addPropertyChangeListener(this);
	}

	public void removeNodeRange(final int nodeId) {
		nodeRanges.remove(nodeId).removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

}