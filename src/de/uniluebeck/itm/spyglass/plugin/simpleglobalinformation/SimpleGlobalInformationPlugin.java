/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.simpleframework.xml.Element;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.view.GlobalInformationBar;
import de.uniluebeck.itm.spyglass.gui.view.GlobalInformationWidget;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.packet.Uint16ListPacket;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.globalinformation.GlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.Tools;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.StatisticalInformationEvaluator;
import de.uniluebeck.itm.spyglass.xmlconfig.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;

// --------------------------------------------------------------------------------
/**
 * Instances of this class provide statistical information which will be displayed on the
 * {@link GlobalInformationBar}
 * 
 * @author Sebastian Ebers
 * 
 */
public class SimpleGlobalInformationPlugin extends GlobalInformationPlugin {

	/**
	 * An object which logs messages
	 */
	private static final Logger log = SpyglassLoggerFactory.getLogger(SimpleGlobalInformationPlugin.class);

	/**
	 * The configuration
	 */
	@Element(name = "parameters")
	private final SimpleGlobalInformationXMLConfig xmlConfig;

	/**
	 * A string holding the information about the average node degree
	 */
	private String avgNodeDegString;

	/**
	 * Object performing the statistical operation to determine the average node degree
	 */
	private StatisticalOperation avgNodeDegEvaluator;

	/**
	 * The widget where the information to be shown are placed in
	 */
	private SimpleGlobalInformationWidget widget;

	/**
	 * A list of semantic types of packages which provide information about neighborhood
	 * relationships
	 */
	private List<Integer> semanticTypes4Neighborhoods;

	/**
	 * Listens for changes of configuration properties
	 */
	private PropertyChangeListener pcl;

	private volatile long totalPacketCount;

	private AtomicInteger numPackets;

	private StatsTimerTask statsTimerTask;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleGlobalInformationPlugin() {
		xmlConfig = new SimpleGlobalInformationXMLConfig();
		avgNodeDegEvaluator = new StatisticalOperation(10, STATISTICAL_OPERATIONS.AVG);
		avgNodeDegString = "avg. node degree: ";
		semanticTypes4Neighborhoods = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes4Neighborhoods());
		numPackets = new AtomicInteger(0);
		totalPacketCount = 0;
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);
		pcl = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				refreshConfigurationParameters();
			}
		};
		xmlConfig.addPropertyChangeListener(pcl);

		new Timer("SGI-Statistics", true).schedule((statsTimerTask = new StatsTimerTask()), 1000, 1000);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void setWidget(final GlobalInformationWidget widget) {
		final GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		widget.setLayout(layout);

		this.widget = new SimpleGlobalInformationWidget(widget, SWT.NONE);
		final Thread refreshThread = new Thread() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {

				while (!isInterrupted()) {
					refreshNodeCounts();
					try {
						sleep(1000);
					} catch (final InterruptedException e) {
						log.warn("GlobalInformationPlugin's sleep was interrupted");
					}

				}
			}
		};
		refreshThread.setDaemon(true);
		refreshThread.start();
		refreshConfigurationParameters();
	}

	// --------------------------------------------------------------------------------
	@Override
	public PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleGlobalInformationPreferencePage(dialog, spyglass, this);
	}

	public static PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createTypePreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleGlobalInformationPreferencePage(dialog, spyglass);
	}

	public static String getHumanReadableName() {
		return "SimpleGlobalInformation";
	}

	@Override
	public PluginXMLConfig getXMLConfig() {
		return xmlConfig;
	}

	@Override
	protected void processPacket(final SpyglassPacket packet) {

		numPackets.incrementAndGet();
		++totalPacketCount;

		// if the widget was not initialized, yet, nothing is to do here
		if ((widget == null) || (widget.getDisplay() == null)) {
			return;
		}

		final int packetSemanticType = packet.getSemanticType();

		if (semanticTypes4Neighborhoods.contains(packetSemanticType) && (packet instanceof Uint16ListPacket)) {
			avgNodeDegString = "avg. node degree: "
					+ new DecimalFormat("0.0#").format(avgNodeDegEvaluator
							.addValue(((Uint16ListPacket) packet).getNeighborhoodPacketNodeIDs().size()));
		}

		final StatisticalInformationEvaluator sfs = xmlConfig.getStatisticalInformationEvaluators4Type(packetSemanticType);

		if (sfs != null) {
			try {
				sfs.parse(packet);
			} catch (final SpyglassPacketException e) {
				log.error("Error parsing a packet in the " + getHumanReadableName()
						+ ".\r\nPlease check the values in the StringFormatter for semantic type " + packetSemanticType + "!", e);
			}

			synchronized (widget) {
				if (!widget.isDisposed()) {
					widget.getDisplay().syncExec(new Runnable() {
						@SuppressWarnings("synthetic-access")
						@Override
						public void run() {
							// the widget might have been disposed while we were waiting
							if (widget.isDisposed()) {
								return;
							}

							widget.createOrUpdateLabel(sfs);
						}
					});
				}

			}

		}

	}

	// --------------------------------------------------------------------------------
	@Override
	public void shutdown() throws Exception {
		super.shutdown();
		if ((widget != null) && !widget.isDisposed()) {
			widget.getDisplay().asyncExec(new Runnable() {
				// ------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					// the widget might have been disposed while we were waiting
					if (widget.isDisposed()) {
						return;
					}

					widget.clear();
					((GlobalInformationWidget) widget.getParent()).setShow(false);
				}
			});
		}
		xmlConfig.removePropertyChangeListener(pcl);
	}

	@Override
	protected void resetPlugin() {

		final Set<StatisticalInformationEvaluator> sfSettings = xmlConfig.getStatisticalInformationEvaluators();
		for (final StatisticalInformationEvaluator statisticalInformationEvaluator : sfSettings) {
			statisticalInformationEvaluator.reset();
		}
		avgNodeDegEvaluator.reset();
		avgNodeDegString = "avg. node degree: ";
		numPackets.set(0);
		totalPacketCount = 0;
		statsTimerTask.reset();
		refreshNodeCounts();
	}

	@Override
	protected void updateLayer() {
		// nothing to do here
	}

	// --------------------------------------------------------------------------------
	/**
	 * Performs internal refreshments of the configuration parameters.<br>
	 * This includes the widget and the active state of the plug-in.
	 */
	public void refreshConfigurationParameters() {
		setActive(isActive());
		semanticTypes4Neighborhoods = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes4Neighborhoods());
		refreshNodeCounts();
		final Set<StatisticalInformationEvaluator> sfSettings = xmlConfig.getStatisticalInformationEvaluators();

		if (sfSettings != null) {
			widget.getDisplay().asyncExec(new Runnable() {
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					// the widget might have been disposed while we were waiting
					if (widget.isDisposed()) {
						return;
					}

					final Collection<StatisticalInformationEvaluator> sfss = new LinkedList<StatisticalInformationEvaluator>();
					for (final StatisticalInformationEvaluator sfs : sfSettings) {
						sfss.add(sfs);
						widget.createOrUpdateLabel(sfs);
					}
					widget.retaingLabels(sfss);

				}
			});
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Refreshes the values of the average node degree and the total number of nodes which ware
	 * currently active in the sensor net. The associated labels of the widget are updated, too.
	 */
	private void refreshNodeCounts() {

		final String numNodes = "# Nodes: " + getPluginManager().getNodePositioner().getNumNodes();

		if ((widget != null) && !widget.isDisposed()) {
			widget.getDisplay().asyncExec(new Runnable() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {

					// the widget might have been disposed while we were waiting
					if (widget.isDisposed()) {
						return;
					}

					if (isActive()) {

						if (xmlConfig.isShowNumNodes()) {
							widget.createOrUpdateNumNodes(numNodes);
						} else {
							widget.removeNumNodes();
						}
						if (xmlConfig.isShowNodeDegree()) {
							widget.createOrUpdateAVGNodeDeg(avgNodeDegString);
						} else {
							widget.removeAVGNodeDeg();
						}
						widget.pack(true);
					}
				}
			});
		}

	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/**
	 * Instances of this class are widgets displaying the configured statistical information.
	 * 
	 * @author Sebastian Ebers
	 * 
	 */
	private class SimpleGlobalInformationWidget extends Composite {

		private Label labelNumNodes;
		private Label labelAVGNodeDegree;
		private Label labelTotalPacketCount;
		private Label labelPacketsPerSecond;
		private Map<StatisticalInformationEvaluator, Label> sfLabels = new TreeMap<StatisticalInformationEvaluator, Label>();

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 * 
		 * @param parent
		 *            the parent widget
		 * @param style
		 *            the style to use
		 */
		public SimpleGlobalInformationWidget(final Composite parent, final int style) {
			super(parent, style);
			final GridLayout layout = new GridLayout();
			this.setLayout(layout);
			createOrUpdateTotalPacketCount("# Packets:");
			createOrUpdatePacketsPerSecond("# Packets per Second:");
		}

		// --------------------------------------------------------------------------------
		/**
		 * Creates or updates the label which displays the information about the total number of
		 * currently active nodes.<br>
		 * If the label already exists, it will be updated, otherwise the label will be created.
		 * 
		 * @param num
		 *            the string to be displayed
		 */
		public void createOrUpdateNumNodes(final String num) {
			if (labelNumNodes == null) {
				labelNumNodes = new Label(this, SWT.NONE);
				labelNumNodes.setToolTipText("The number of currently active nodes");
			}
			labelNumNodes.setText(num);
			labelNumNodes.pack();
			pack();
			super.getParent().redraw();
		}

		public void createOrUpdateTotalPacketCount(final String cnt) {
			if (labelTotalPacketCount == null) {
				labelTotalPacketCount = new Label(this, SWT.NONE);
				labelTotalPacketCount.setToolTipText("The total number of received packets");
			}
			labelTotalPacketCount.setText(cnt);
			labelTotalPacketCount.pack();
			pack();
			super.getParent().redraw();
		}

		public void createOrUpdatePacketsPerSecond(final String cnt) {
			if (labelPacketsPerSecond == null) {
				labelPacketsPerSecond = new Label(this, SWT.NONE);
				labelPacketsPerSecond.setToolTipText("Packets/second averaged over the last [1|30|60] seconds");
			}
			labelPacketsPerSecond.setText(String.valueOf(cnt));
			labelPacketsPerSecond.pack();
			pack();
			super.getParent().redraw();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes the label which displays the information about the total number of currently
		 * active nodes
		 */
		public void removeNumNodes() {
			if ((labelNumNodes != null)) {
				labelNumNodes.dispose();
				labelNumNodes = null;
				super.getParent().redraw();
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Creates or updates the label which displays the information about the average node degree
		 * of the sensor net.<br>
		 * If the label already exists, it will be updated, otherwise the label will be created.
		 * 
		 * @param text
		 *            the text to be shown
		 */
		public void createOrUpdateAVGNodeDeg(final String text) {
			if (labelAVGNodeDegree == null) {
				labelAVGNodeDegree = new Label(this, SWT.NONE);
				labelAVGNodeDegree.setToolTipText("The average number of neighbors per node");
			}
			labelAVGNodeDegree.setText(text);
			labelAVGNodeDegree.pack();
			pack();
			super.getParent().redraw();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes the label which displays the information about the average node degree of the
		 * sensor net.
		 */
		public void removeAVGNodeDeg() {
			if ((labelAVGNodeDegree != null)) {
				labelAVGNodeDegree.dispose();
				labelAVGNodeDegree = null;
				super.getParent().redraw();
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Creates or updates a label which provides a certain statistical information.<br>
		 * If the label already exists, it will be updated, otherwise the label will be created.
		 * 
		 * @param siEvaluator
		 *            an object which encapsulates the statistical information
		 */
		public void createOrUpdateLabel(final StatisticalInformationEvaluator siEvaluator) {
			synchronized (sfLabels) {
				final String text = siEvaluator.getDescription() + " " + siEvaluator.getValue();
				Label label = sfLabels.get(siEvaluator);

				if (label == null) {
					label = new Label(this, SWT.NONE);
					sfLabels.put(siEvaluator, label);
					label.setText(text);
					label.pack(true);
					super.getParent().redraw();
				}
				// if the text did not change, no update is necessary
				else if (!label.getText().equals(text)) {
					label.setText(text);
					label.pack(true);
					super.getParent().redraw();
				}
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes the label which provides a certain statistical information.
		 * 
		 * @param siEvaluator
		 *            an object which encapsulates the statistical information
		 */
		public void removeLabel(final StatisticalInformationEvaluator siEvaluator) {
			Display.getDefault().syncExec(new Runnable() {
				// ------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					synchronized (sfLabels) {
						final Label label = sfLabels.get(siEvaluator);
						if (label != null) {
							label.dispose();
							sfLabels.remove(siEvaluator);
						}
					}
				}
			});
			super.getParent().redraw();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes all labels providing statistical information which are no longer relevant.
		 * 
		 * @param siEvaluators
		 *            a list containing objects which encapsulate the relevant statistical
		 *            information
		 */
		public void retaingLabels(final Collection<StatisticalInformationEvaluator> siEvaluators) {
			final Collection<StatisticalInformationEvaluator> keySet = new LinkedList<StatisticalInformationEvaluator>();
			synchronized (sfLabels) {
				keySet.addAll(sfLabels.keySet());
			}
			for (final StatisticalInformationEvaluator key : keySet) {
				if (!siEvaluators.contains(key)) {
					removeLabel(key);
				}
			}
		}

		/**
		 * Disposes all labels.
		 */
		public void clear() {
			removeAVGNodeDeg();
			removeNumNodes();
			final Collection<StatisticalInformationEvaluator> keySet = new LinkedList<StatisticalInformationEvaluator>();
			synchronized (sfLabels) {
				keySet.addAll(sfLabels.keySet());
			}
			for (final StatisticalInformationEvaluator key : keySet) {
				removeLabel(key);
			}
		}

	}

	private class StatsTimerTask extends TimerTask {
		private int times = 0;
		private String perSec = "";
		private String per30Sec = "     ";
		private String per60Sec = "     ";
		private StatisticalOperation statistics30sec = new StatisticalOperation(30, STATISTICAL_OPERATIONS.AVG, new DecimalFormat("0.00"));
		private StatisticalOperation statistics60sec = new StatisticalOperation(60, STATISTICAL_OPERATIONS.AVG, new DecimalFormat("0.00"));

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 */
		public StatsTimerTask() {
			super();
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public synchronized void run() {
			final int val = numPackets.getAndSet(0);

			perSec = new DecimalFormat("0.00").format(val);
			statistics30sec.addValue(val);
			statistics60sec.addValue(val);

			if ((times) >= 59) {
				per60Sec = statistics60sec.getValueFormatted();
				per30Sec = statistics30sec.getValueFormatted();
			} else if ((++times) >= 30) {
				per30Sec = statistics30sec.getValueFormatted();
			}

			final String pps = "# PPS: [ " + perSec + " | " + per30Sec + " | " + per60Sec + " ]";

			if (widget != null) {
				synchronized (widget) {
					if (!widget.isDisposed()) {
						widget.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								widget.createOrUpdateTotalPacketCount("# Packets: " + totalPacketCount);
								widget.createOrUpdatePacketsPerSecond(pps);
							}
						});
					}
				}
			}
		}

		// --------------------------------------------------------------------------------
		/**
		 * Resets the timer tasks statistical values
		 */
		public synchronized void reset() {
			times = 0;
			statistics30sec.reset();
			statistics60sec.reset();
			perSec = "     ";
			per30Sec = "     ";
			per60Sec = "     ";
		}
	}

}