/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.vectorsequencepainter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Deque;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.drawing.DrawingObject;
import de.uniluebeck.itm.spyglass.drawing.primitive.Line;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.layer.Layer;
import de.uniluebeck.itm.spyglass.packet.Int16ListPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.NeedsMetric;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.relationpainter.RelationPainterPlugin;
import de.uniluebeck.itm.spyglass.positions.AbsolutePosition;
import de.uniluebeck.itm.spyglass.positions.AbsoluteRectangle;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class VectorSequencePainterPlugin extends RelationPainterPlugin implements NeedsMetric, PropertyChangeListener {

	private static final Logger log = SpyglassLoggerFactory.getLogger(VectorSequencePainterPlugin.class);

	@Element(name = "parameters")
	private final VectorSequencePainterXMLConfig xmlConfig;

	private final Object lock = new Object();

	private Layer layer = Layer.Factory.createQuadTreeLayer();

	public VectorSequencePainterPlugin() {
		xmlConfig = new VectorSequencePainterXMLConfig();
	}

	@Override
	public PluginPreferencePage<VectorSequencePainterPlugin, VectorSequencePainterXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new VectorSequencePainterPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<VectorSequencePainterPlugin, VectorSequencePainterXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new VectorSequencePainterPreferencePage(dialog, spyglass);
	}

	public SortedSet<DrawingObject> getDrawingObjects(final AbsoluteRectangle area) {
		return layer.getDrawingObjects(area);
	}

	public static String getHumanReadableName() {
		return "VectorSequencePainter";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	private class Sequence {

		public List<AbsolutePosition> coordinates;

		public Set<Line> lines = new HashSet<Line>();

		public Sequence(final List<AbsolutePosition> coordinates) {

			this.coordinates = coordinates;

			Line line;

			for (int i = 1; i < coordinates.size(); i++) {

				line = new Line();
				line.setPosition(coordinates.get(i - 1));
				line.setEnd(coordinates.get(i));

				final int[] color = xmlConfig.getLineColorRGB();
				line.setColor(new RGB(color[0], color[1], color[2]));
				line.setLineWidth(xmlConfig.getLineWidth());

				lines.add(line);

			}
		}

		public boolean equals(final Sequence other) {
			if (coordinates.size() != other.coordinates.size()) {
				return false;
			}
			for (int i = 0; i < coordinates.size(); i++) {
				if (!coordinates.get(i).equals(other.coordinates.get(i))) {
					return false;
				}
			}
			return true;
		}

	}

	private Set<Sequence> sequences = new HashSet<Sequence>();

	private Hashtable<Sequence, Long> sequenceTimes = new Hashtable<Sequence, Long>();

	private Deque<Sequence> newSequences = new LinkedList<Sequence>();

	private Deque<Sequence> removedSequences = new LinkedList<Sequence>();

	private int dimension = 2;

	private boolean dimensionError = false;

	private boolean packetTypeError = false;

	private static final String TIMER_NAME = "VectorSequencePainterPlugin-Timout-Timer";

	private Timer timer;

	private void handleTimeout() {
		synchronized (lock) {

			final long now = System.currentTimeMillis();
			final long timeout = (1000 * xmlConfig.getTimeout());
			long diff;

			for (final Sequence s : sequences) {
				diff = (now - sequenceTimes.get(s));
				if (diff > timeout) {
					if (!sequences.remove(s)) {
						log.fatal("Unexpected case.");
					}
					removedSequences.add(s);
				}
			}

			updateLayer();
		}
	}

	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);
		xmlConfig.addPropertyChangeListener(this);
		handleTimeoutChangeEvent(xmlConfig.getTimeout());
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		xmlConfig.removePropertyChangeListener(this);
		reset();
		if (timer != null) {
			timer.cancel();
		}
	}

	private Sequence getEqualSequence(final Sequence seq) {
		for (final Sequence s : sequences) {
			if (s.equals(seq)) {
				return s;
			}
		}
		return null;
	}

	@Override
	protected void processPacket(final SpyglassPacket p) {

		if (p instanceof Int16ListPacket) {

			final Int16ListPacket packet = (Int16ListPacket) p;
			final List<AbsolutePosition> coordinateList;

			try {

				synchronized (lock) {

					if (dimension == 2) {
						coordinateList = packet.getCoordinates2D();
					} else {
						coordinateList = packet.getCoordinates3D();
					}

					final Sequence sequence = new Sequence(coordinateList);
					Sequence eqSeq = getEqualSequence(sequence);

					if (eqSeq == null) {
						sequences.add(sequence);
						newSequences.push(sequence);
						eqSeq = sequence;
					}

					sequenceTimes.put(eqSeq, System.currentTimeMillis());

				}

			} catch (final ParseException e) {

				if (!dimensionError) {

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openWarning(null, "Packet parsing error", "Could not parse received packet as " + dimension
									+ "D coordinate list. Please check the dimension setting of the VectorSequencePainterPlugin named "
									+ xmlConfig.getName() + ".");
						}
					});

					dimensionError = true;
					reset();

				}
			}

		} else {

			if (!packetTypeError) {

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openWarning(null, "Packet type error",
								"The received packet has the wrong type. Please check the semantic type settings of the VectorSequencePainterPlugin named "
										+ xmlConfig.getName() + ".");
					}
				});

				packetTypeError = true;
				reset();

			}
		}

		updateLayer();

	}

	@Override
	protected void resetPlugin() {

		synchronized (lock) {

			newSequences.clear();

			for (final Sequence s : sequences) {
				removedSequences.push(s);
			}
			sequences.clear();

			updateLayer();

		}

	}

	private void updateLayer() {

		synchronized (lock) {

			Sequence s;

			while (newSequences.size() > 0) {
				s = newSequences.poll();
				for (final Line l : s.lines) {
					layer.add(l);
					fireDrawingObjectAdded(l);
				}
			}

			while (removedSequences.size() > 0) {
				s = removedSequences.poll();
				final Set<DrawingObject> dos = layer.getDrawingObjects();
				for (final Line l : s.lines) {
					if (dos.contains(l)) {
						try {
							layer.remove(l);
						} catch (final RuntimeException e) {
							System.out.println("bla");
						}
						fireDrawingObjectRemoved(l);
					}
				}
			}

		}

	}

	@Override
	public Set<DrawingObject> getAutoZoomDrawingObjects() {
		return layer.getDrawingObjects();
	}

	// --------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 *
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equals(VectorSequencePainterXMLConfig.PROPERTYNAME_LINE_COLOR_R_G_B)) {
			handleColorChangeEvent((int[]) event.getNewValue());
		} else if (event.getPropertyName().equals(VectorSequencePainterXMLConfig.PROPERTYNAME_LINE_WIDTH)) {
			handleLineWidthChangeEvent((Integer) event.getNewValue());
		} else if (event.getPropertyName().equals(VectorSequencePainterXMLConfig.PROPERTYNAME_DIMENSION)) {
			handleDimensionChangeEvent((Integer) event.getNewValue());
		} else if (event.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_SEMANTIC_TYPES)) {
			handleSemanticTypeChangeEvent();
		} else if (event.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_TIMEOUT)) {
			handleTimeoutChangeEvent((Integer) event.getNewValue());
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param timeout
	 */
	private void handleTimeoutChangeEvent(final int timeout) {
		if (timer != null) {
			timer.cancel();
		}
		if (timeout > 0) {
			timer = new Timer(TIMER_NAME);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handleTimeout();
				}
			}, 1000 * timeout, 1000 * timeout);
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 */
	private void handleSemanticTypeChangeEvent() {
		packetTypeError = false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param newValue
	 */
	private void handleDimensionChangeEvent(final int newValue) {
		dimension = newValue;
		dimensionError = false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param width
	 */
	private void handleLineWidthChangeEvent(final int width) {

		synchronized (lock) {

			for (final Sequence s : sequences) {
				for (final Line l : s.lines) {
					final AbsoluteRectangle oldBoundingBox = l.getBoundingBox();
					l.setLineWidth(width);
				}
			}

			for (final Sequence s : newSequences) {
				for (final Line l : s.lines) {
					final AbsoluteRectangle oldBoundingBox = l.getBoundingBox();
					l.setLineWidth(width);
				}
			}

		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * @param newValue
	 */
	private void handleColorChangeEvent(final int[] color) {

		synchronized (lock) {

			for (final Sequence s : sequences) {
				for (final Line l : s.lines) {
					l.setColor(new RGB(color[0], color[1], color[2]));
				}
			}

			for (final Sequence s : newSequences) {
				for (final Line l : s.lines) {
					l.setColor(new RGB(color[0], color[1], color[2]));
				}
			}

		}

	}


}