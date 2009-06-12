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
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.RGB;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class contain the configuration parameters of a {@link NodeSensorRangePlugin}
 * 
 * @author Sebastian Ebers, Daniel Bimschas
 * 
 */
public class NodeSensorRangeXMLConfig extends PluginXMLConfig implements PropertyChangeListener {

	private static final Logger log = SpyglassLoggerFactory.getLogger(NodeSensorRangeXMLConfig.class);

	public static class CircleRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_CIRCLE_RADIUS, required = false)
		private int circleRadius = 100;

		public int getCircleRadius() {
			return circleRadius;
		}

		public void setCircleRadius(final int radius) {
			final int old = this.circleRadius;
			this.circleRadius = radius;
			firePropertyChange(PROPERTYNAME_CIRCLE_RADIUS, old, this);
		}

	}

	public static class ConeRange extends NodeSensorRange {

		@Element(name = PROPERTYNAME_CONE_ORIENTATION, required = false)
		private int coneOrientation = 0;

		@Element(name = PROPERTYNAME_CONE_RADIUS, required = false)
		private int coneRadius = 100;

		@Element(name = PROPERTYNAME_CONE_VIEWANGLE, required = false)
		private int coneViewAngle = 45;

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

	}

	// --------------------------------------------------------------------------------
	/**
	 * @author bimschas
	 * 
	 */
	public static class Config extends PropertyBean implements Cloneable {

		@Element(name = PROPERTYNAME_NODE_ID, required = false)
		private int nodeId = -1;

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
		private RANGE_TYPE rangeType = RANGE_TYPE.Circle;

		public int getBackgroundAlpha() {
			return backgroundAlpha;
		}

		public RGB getBackgroundRGB() {
			return new RGB(backgroundRGB[0], backgroundRGB[1], backgroundRGB[2]);
		}

		public RGB getColorRGB() {
			return new RGB(colorRGB[0], colorRGB[1], colorRGB[2]);
		}

		public NodeSensorRange getRange() {
			return range;
		}

		public RANGE_TYPE getRangeType() {
			return rangeType;
		}

		public int getLineWidth() {
			return lineWidth;
		}

		public int getNodeId() {
			return nodeId;
		}

		public void setNodeId(final int nodeId) {
			firePropertyChange(PROPERTYNAME_NODE_ID, this.nodeId, this.nodeId = nodeId);
		}

		public void setLineWidth(final int lineWidth) {
			firePropertyChange(PROPERTYNAME_LINE_WIDTH, this.lineWidth, this.lineWidth = lineWidth);
		}

		public void setBackgroundAlpha(final int backgroundAlpha) {
			firePropertyChange(PROPERTYNAME_BACKGROUND_ALPHA, this.backgroundAlpha, this.backgroundAlpha = backgroundAlpha);
		}

		public void setBackgroundRGB(final RGB backgroundRGB) {
			firePropertyChange(PROPERTYNAME_BACKGROUND_R_G_B, getBackgroundRGB(), this.backgroundRGB = new int[] { backgroundRGB.red,
					backgroundRGB.green, backgroundRGB.blue });
		}

		public void setColorRGB(final RGB colorRGB) {
			firePropertyChange(PROPERTYNAME_COLOR_R_G_B, getColorRGB(), this.colorRGB = new int[] { colorRGB.red, colorRGB.green, colorRGB.blue });
		}

		public void setRange(final NodeSensorRange range) {
			firePropertyChange(PROPERTYNAME_RANGE, this.range, this.range = range);
		}

		public void setRangeType(final RANGE_TYPE rangeType) {
			firePropertyChange(PROPERTYNAME_RANGE_TYPE, this.rangeType, this.rangeType = rangeType);
		}

		@Override
		public Config clone() {
			try {
				final Config copy = (Config) super.clone();
				copy.backgroundRGB = this.backgroundRGB.clone();
				copy.colorRGB = this.colorRGB.clone();
				copy.range = this.range.clone();

				return copy;
			} catch (final CloneNotSupportedException e) {
				throw new RuntimeException("Bug", e);
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Config) {
				final Config c = (Config) o;
				return c.nodeId == this.nodeId;
			} else {
				return false;
			}
		}

		public int hashCodeOrig() {
			return super.hashCode();
		}

		@Override
		public int hashCode() {
			return nodeId;
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * "Flag" base class grouping together {@link NodeSensorRangeXMLConfig.RectangleRange},
	 * {@link NodeSensorRangeXMLConfig.CircleRange} &amp; {@link NodeSensorRangeXMLConfig.ConeRange}
	 * 
	 * @author Daniel Bimschas
	 */
	public static abstract class NodeSensorRange extends PropertyBean implements Cloneable {

		@Override
		public final NodeSensorRange clone() {
			try {
				return (NodeSensorRange) super.clone();
			} catch (final CloneNotSupportedException e) {
				throw new RuntimeException("Bug", e);
			}
		}
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

	}

	public static final String PROPERTYNAME_NODE_ID = "nodeId";

	public static final String PROPERTYNAME_LINE_WIDTH = "lineWidth";

	public static final String PROPERTYNAME_BACKGROUND_ALPHA = "backgroundAlpha";

	public static final String PROPERTYNAME_BACKGROUND_R_G_B = "backgroundRGB";

	public static final String PROPERTYNAME_CIRCLE_RADIUS = "circleRadius";

	public static final String PROPERTYNAME_COLOR_R_G_B = "colorRGB";

	public static final String PROPERTYNAME_CONE_ORIENTATION = "coneOrientation";

	public static final String PROPERTYNAME_CONE_RADIUS = "coneRadius";

	public static final String PROPERTYNAME_CONE_VIEWANGLE = "coneViewAngle";

	public static final String PROPERTYNAME_DEFAULT_CONFIG = "defaultConfig";

	public static final String PROPERTYNAME_PER_NODE_CONFIGS = "perNodeConfigs";

	public static final String PROPERTYNAME_RANGE = "range";

	public static final String PROPERTYNAME_RANGE_TYPE = "rangeType";

	public static final String PROPERTYNAME_RECTANGLE_HEIGHT = "rectangleHeight";

	public static final String PROPERTYNAME_RECTANGLE_ORIENTATION = "rectangleOrientation";

	public static final String PROPERTYNAME_RECTANGLE_WIDTH = "rectangleWidth";

	// --------------------------------------------------------------------------------

	public enum RANGE_TYPE {

		Circle,

		Cone,

		Rectangle,
	}

	@Element(name = PROPERTYNAME_DEFAULT_CONFIG, required = false)
	private final Config defaultConfig = new Config();

	@ElementList(entry = PROPERTYNAME_PER_NODE_CONFIGS, required = false)
	private HashSet<Config> perNodeConfigs = new HashSet<Config>();

	public boolean equals(final NodeSensorRangeXMLConfig o) {
		if (!super.equals(o)) {
			return false;
		}
		return perNodeConfigs.equals((o).perNodeConfigs);
	}

	public Config getDefaultConfig() {
		return defaultConfig;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

	/**
	 * Return a deep clone of the perNodeConfigs set.
	 */
	public HashSet<Config> getPerNodeConfigs() {
		return perNodeConfigs;
	}

	/**
	 * Return a deep clone of the perNodeConfigs set.
	 */
	public HashSet<Config> getPerNodeConfigsClone() {
		final HashSet<Config> set = new HashSet<Config>();
		for (final Config c : perNodeConfigs) {
			set.add(c.clone());
		}
		return set;
	}

	public void setPerNodeConfigs(final HashSet<Config> perNodeConfigs) {
		firePropertyChange(PROPERTYNAME_PER_NODE_CONFIGS, this.perNodeConfigs, this.perNodeConfigs = perNodeConfigs);
	}

}