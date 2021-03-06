/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.mappainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.packet.FloatListPacket;
import de.uniluebeck.itm.spyglass.packet.IntListPacket;
import de.uniluebeck.itm.spyglass.packet.LongListPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.VariableListPacket;
import de.uniluebeck.itm.spyglass.plugin.NeedsMetric;
import de.uniluebeck.itm.spyglass.plugin.NodePositionEvent;
import de.uniluebeck.itm.spyglass.plugin.NodePositionListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.backgroundpainter.BackgroundPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * This plugin extracts scalar values from packets and draws a temperature-color map where different
 * scalars represent different colors.
 * 
 * @author Dariush Forouher
 * 
 */
public class MapPainterPlugin extends BackgroundPainterPlugin implements PropertyChangeListener, NodePositionListener, NeedsMetric {

	protected static Logger log = SpyglassLoggerFactory.getLogger(MapPainterPlugin.class);

	@Element(name = "parameters")
	private final MapPainterXMLConfig xmlConfig = new MapPainterXMLConfig();

	DataStore dataStore = new DataStore();

	/**
	 * The drawing object representing the map.
	 */
	private Map map = null;

	private boolean dataChanged = true;

	private Timer timer = null;

	private TimerTask task = null;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MapPainterPlugin() {
		super(true);
	}

	@Override
	public PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> createPreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new MapPainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<MapPainterPlugin, MapPainterXMLConfig> createTypePreferencePage(final PluginPreferenceDialog dialog,
			final Spyglass spyglass) {
		return new MapPainterPreferencePage(dialog, spyglass);
	}

	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		if ((map != null) && area.intersects(map.getBoundingBox())) {
			final TreeSet<DrawingObject> set = new TreeSet<DrawingObject>();
			set.add(map);
			return set;
		} else {
			return new TreeSet<DrawingObject>();
		}
	}

	public static String getHumanReadableName() {
		return "MapPainter";
	}

	@Override
	public MapPainterXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	@Override
	protected synchronized void resetPlugin() {
		// clear the drawing object
		dataStore.clear();
		updateFramepoints();
	}

	@Override
	public synchronized void init(final PluginManager manager) throws Exception {
		super.init(manager);

		createMap();

		timer = new Timer("Timer of " + this.toString());

		xmlConfig.addPropertyChangeListener(this);
		manager.addNodePositionListener(this);

		createTask();

		timer.schedule(task, 0, 1000 / xmlConfig.getRefreshFrequency());

	}

	@Override
	public synchronized void shutdown() throws Exception {
		super.shutdown();

		timer.cancel();
		timer = null;

		if (map != null) {
			fireDrawingObjectRemoved(map);
			map = null;
		}

		this.pluginManager.removeNodePositionListener(this);
		this.xmlConfig.removePropertyChangeListener(this);
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {

		final AbsolutePosition position = pluginManager.getNodePositioner().getPosition(packet.getSenderId());

		// ignore nodes outside the area
		if ((position == null) || !xmlConfig.getBoundingBox().contains(position.x, position.y)) {
			return;
		}

		if (!xmlConfig.containsSemanticType(packet.getSemanticType())) {
			return;
		}

		// log.debug(String.format("Parsing packet %s", packet));

		final double value = extractValue(packet);

		// log.debug(String.format("Parsed packet %s: Got value %f", packet, value));

		// update the map
		if (!Double.isNaN(value)) {
			final DataPoint e = new DataPoint();
			e.isFramepoint = false;
			e.position = position;
			e.nodeID = packet.getSenderId();
			e.value = value;

			synchronized (this) {
				dataStore.add(e);
				this.dataChanged = true;
			}

		}

	}

	private double extractValue(final SpyglassPacket packet) {
		double value = Double.NaN;

		switch (packet.getSyntaxType()) {
			case ISENSE_SPYGLASS_PACKET_FLOAT: {
				final FloatListPacket p = ((FloatListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0];
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}
			case ISENSE_SPYGLASS_PACKET_UINT32:
			case ISENSE_SPYGLASS_PACKET_INT64: {
				final LongListPacket p = ((LongListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0];
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}
			case ISENSE_SPYGLASS_PACKET_INT16:
			case ISENSE_SPYGLASS_PACKET_STD:
			case ISENSE_SPYGLASS_PACKET_UINT16:
			case ISENSE_SPYGLASS_PACKET_UINT8: {
				final IntListPacket p = ((IntListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0];
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}
			case ISENSE_SPYGLASS_PACKET_VARIABLE: {
				final VariableListPacket p = ((VariableListPacket) packet);
				if (p.getValues().length > 0) {
					value = p.getValues()[0].doubleValue();
				} else {
					log.error(String.format("Packet %s has no payload.", p));
				}
				break;
			}

		}
		return value;
	}

	/**
	 * We synchronize to ensure that the restart of the timer and stuff won't happen twice
	 */
	@Override
	public synchronized void propertyChange(final PropertyChangeEvent evt) {

		if (getState() != State.ALIVE) {
			return;
		}

		if (evt.getPropertyName().equals(MapPainterXMLConfig.PROPERTYNAME_REFRESH_FREQUENCY)) {

			if (task != null) {
				task.cancel();
				task = null;
			}

			createTask();

			timer.schedule(task, 1000, 1000 / xmlConfig.getRefreshFrequency());

		}

		// drop the old map object and create a fresh one.
		createMap();

	}

	private void createTask() {
		task = new TimerTask() {

			@Override
			public void run() {

				try {
					updateMatrix();
				} catch (final Exception e) {
					log.error("Exception while updating the matrix", e);
				}
			}

		};
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uniluebeck.itm.spyglass.plugin.NodePositionListener#handleEvent(de.uniluebeck.itm.spyglass
	 * .plugin.NodePositionEvent)
	 */
	@Override
	public synchronized void handleEvent(final NodePositionEvent e) {
		switch (e.change) {
			case ADDED:
				// do nothing. will be handled by handlePacket anyway
				break;
			case REMOVED: {
				// remove the node from the datastore
				final Iterator<DataPoint> it = dataStore.iterator();
				while (it.hasNext()) {
					final DataPoint p = it.next();
					if (!p.isFramepoint && (p.nodeID == e.node)) {
						it.remove();
					}
				}
				break;
			}
			case MOVED: {
				final Iterator<DataPoint> it = dataStore.iterator();
				while (it.hasNext()) {
					final DataPoint p = it.next();
					if (!p.isFramepoint && (p.nodeID == e.node)) {
						p.position = e.newPosition;
					}
				}
				break;
			}
		}

	}

	/**
	 * Create a new Map object.
	 */
	private void createMap() {

		if (map != null) {
			fireDrawingObjectRemoved(map);
			map = null;
		}

		map = new Map(xmlConfig);

		updateFramepoints();

		// now cause a redraw
		fireDrawingObjectAdded(map);
	}

	/**
	 * Remove all framepoints from the datastore and create new ones.
	 * 
	 * Framepoints are fixed artifical nodes placed outside the map. They have a defined value and
	 * give the map a default-coloring until real nodes arrive.
	 */
	private synchronized void updateFramepoints() {

		// kill all old framepoints
		final Iterator<DataPoint> it = dataStore.iterator();
		while (it.hasNext()) {
			if (it.next().isFramepoint) {
				it.remove();
			}
		}

		// add framepoints horizontally
		final float numFramePointsHorizontal = xmlConfig.getNumFramePointsHorizontal();
		final int width = xmlConfig.getBoundingBox().getWidth();
		final int height = xmlConfig.getBoundingBox().getHeight();
		final AbsolutePosition upperLeft = xmlConfig.getBoundingBox().getLowerLeft().clone();
		final Double defaultValue = new Double(xmlConfig.getDefaultValue());

		// Node IDs in Spyglass are always non-negative. So using negative numbers should guaranty
		// no collisions.
		int framePointID = -1;

		for (int i = 0; i < numFramePointsHorizontal; i++) {
			final AbsolutePosition pos = upperLeft.clone();
			pos.x += i / (numFramePointsHorizontal - 1) * width;
			newFramepoint(defaultValue, pos, framePointID--);
		}
		for (int i = 0; i < numFramePointsHorizontal; i++) {
			final AbsolutePosition pos = upperLeft.clone();
			pos.x += i / (numFramePointsHorizontal - 1) * width;
			pos.y += height;
			newFramepoint(defaultValue, pos, framePointID--);
		}

		// add framepoints vertically
		final float numFramePointsVertical = xmlConfig.getNumFramePointsVertical();
		for (int i = 0; i < numFramePointsVertical; i++) {
			final AbsolutePosition pos = upperLeft.clone();
			pos.y += i / (numFramePointsVertical - 1) * height;
			newFramepoint(defaultValue, pos, framePointID--);
		}
		for (int i = 0; i < numFramePointsVertical; i++) {
			final AbsolutePosition pos = upperLeft.clone();
			pos.y += i / (numFramePointsVertical - 1) * height;
			pos.x += width;
			newFramepoint(defaultValue, pos, framePointID--);
		}

	}

	/**
	 * Add a new framepoint to the data store
	 */
	private synchronized void newFramepoint(final Double defaultValue, final AbsolutePosition pos, final int id) {
		final DataPoint p = new DataPoint();
		p.isFramepoint = true;
		p.nodeID = id;
		p.value = defaultValue;
		p.position = pos;
		this.dataStore.add(p);
	}

	/**
	 * Calculate the value at the given point in space based on sourrounding nodes.
	 */
	private double calculateValue(final DataStore store, final AbsolutePosition point) {
		final List<DataPoint> neighbors;
		synchronized (this) {
			neighbors = store.kNN(point, xmlConfig.getK());
		}

		double sum = 0;
		for (final DataPoint dataPoint : neighbors) {
			sum += dataPoint.value;
		}
		sum /= neighbors.size();

		return sum;
	}

	/**
	 * Update the matrix. After we're done, cause a redraw.
	 * 
	 * Note, that we only lock shortly over the map. although this may result in short-time
	 * graphical errors (when a redraw occurs while this method is still running) it has the
	 * advantage of avoiding longtime blocking of the SWT-Thread when the draw() method tries to
	 * aquire the lock.
	 */
	protected void updateMatrix() {

		// the datastore and the xmlConfig may change while this method computes.
		// to avoid holding the plugin-wide lock for the whole time (which could slow down
		// responsivness of the pref-dialog), we copy all relevant data.

		final DataStore store;
		final int rows;
		final int cols;
		final int elWidth;
		final int elHeight;
		final int lowerLeftX;
		final int lowerLeftY;

		synchronized (this) {
			if (!dataChanged) {
				return;
			}
			store = dataStore.clone();
			dataChanged = false;
			rows = xmlConfig.getRows();
			cols = xmlConfig.getCols();
			elWidth = xmlConfig.getGridElementWidth();
			elHeight = xmlConfig.getGridElementHeight();
			lowerLeftX = xmlConfig.getLowerLeftX();
			lowerLeftY = xmlConfig.getLowerLeftY();
		}

		final AbsoluteRectangle drawRect = new AbsoluteRectangle();
		drawRect.setHeight(elWidth);
		drawRect.setWidth(elHeight);

		// create a new matrix
		final double[][] matrix = new double[rows][cols];
		// ... and fill it
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {

				final AbsolutePosition newPos = new AbsolutePosition();
				newPos.x = col * elWidth + lowerLeftX;
				newPos.y = row * elHeight + lowerLeftY;
				drawRect.setLowerLeft(newPos);
				final double average = calculateValue(store, drawRect.getCenter());
				matrix[row][col] = average;

			}

		}

		synchronized (this) {
			// maybe the plugin was shutdown while we were busy
			if (getState() == State.ALIVE) {
				map.setMatrix(matrix);
			}
		}
	}

}