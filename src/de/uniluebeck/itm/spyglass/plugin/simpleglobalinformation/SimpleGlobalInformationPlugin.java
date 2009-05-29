/* 
 * ----------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the
 * SwarmNet (www.swarmnet.de) project SpyGlass is free software;
 * you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further details.
 * ------------------------------------------------------------------------
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
import java.util.TreeSet;
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
import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.StatisticalInformationEvaluator.STATISTICAL_OPERATIONS;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import de.uniluebeck.itm.spyglass.util.Tools;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

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
	private IDBasedStatisticalOperation avgNodeDegEvaluator;

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

	private PNCountTimerTask pNCountTimerTask;

	private Set<Integer> packetSemanticTypes;

	private Set<StatisticalInformationEvaluator> evaluators;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleGlobalInformationPlugin() {
		xmlConfig = new SimpleGlobalInformationXMLConfig();
		avgNodeDegEvaluator = new IDBasedStatisticalOperation(STATISTICAL_OPERATIONS.AVG);
		avgNodeDegString = "avg. node degree: ";
		semanticTypes4Neighborhoods = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes4Neighborhoods());
		totalPacketCount = 0;
		packetSemanticTypes = new TreeSet<Integer>();
		evaluators = new TreeSet<StatisticalInformationEvaluator>();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final PluginManager manager) throws Exception {
		super.init(manager);
		pcl = new PropertyChangeListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_ACTIVE)) {
					setActive((Boolean) evt.getNewValue());
				} else if (evt.getPropertyName().equals(SimpleGlobalInformationXMLConfig.PROPERTYNAME_SEMANTIC_TYPES4_NEIGHBORHOODS)) {
					semanticTypes4Neighborhoods = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes4Neighborhoods());
					avgNodeDegEvaluator.reset();
				} else if (evt.getPropertyName().equals(SimpleGlobalInformationXMLConfig.PROPERTYNAME_STATISTICAL_INFORMATION_EVALUATORS)) {
					synchronized (evaluators) {
						evaluators = xmlConfig.getStatisticalInformationEvaluators();
					}
					refreshStatIEConf();
				} else if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_SEMANTIC_TYPES)) {
					final int[] stypes = (int[]) evt.getNewValue();
					if ((stypes.length > 1) || (stypes[0] != -1)) {
						synchronized (evaluators) {
							packetSemanticTypes.retainAll(Tools.intArrayToIntegerList(stypes));
							evaluators = xmlConfig.getStatisticalInformationEvaluators();
						}
						refreshStatIEConf();
					}
				}
			}
		};
		xmlConfig.addPropertyChangeListener(pcl);

		new Timer("SGI-Statistics", true).schedule((pNCountTimerTask = new PNCountTimerTask()), 1000, 1000);
	}

	// --------------------------------------------------------------------------------
	@Override
	public void setWidget(final GlobalInformationWidget widget) {
		final GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		widget.setLayout(layout);

		semanticTypes4Neighborhoods = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes4Neighborhoods());
		this.widget = new SimpleGlobalInformationWidget(widget, SWT.NONE);

		synchronized (widget) {
			widget.getDisplay().timerExec(1000, new Runnable() {
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					if (!widget.isDisposed()) {
						updateWidget();
						widget.getDisplay().timerExec(1000, this);
					}
				}
			});

		}
	}

	// --------------------------------------------------------------------------------
	@Override
	public PluginPreferencePage<SimpleGlobalInformationPlugin, SimpleGlobalInformationXMLConfig> createPreferencePage(
			final PluginPreferenceDialog dialog, final Spyglass spyglass) {
		return new SimpleGlobalInformationPreferencePage(dialog, spyglass, this);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns a widget used for the configuration of an instance of this class
	 * 
	 * @param dialog
	 *            the dialog where the widget is attached
	 * @param spyglass
	 *            a Spyglass instance
	 * @return a widget used for the configuration of an instance of this class
	 */
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

		pNCountTimerTask.numPackets.incrementAndGet();
		++totalPacketCount;

		final int packetSemanticType = packet.getSemanticType();

		// check if the semantic type was found in a previously parsed packet, yet
		final boolean newSemanticType = (packetSemanticTypes.add(packetSemanticType));

		if (semanticTypes4Neighborhoods.contains(packetSemanticType) && (packet instanceof Uint16ListPacket)) {
			avgNodeDegString = "avg. node degree: "
					+ new DecimalFormat("0.0#").format(avgNodeDegEvaluator.addValue(packet.getSenderId(), ((Uint16ListPacket) packet)
							.getNeighborhoodPacketNodeIDs().size()));
		}

		final Collection<StatisticalInformationEvaluator> sfss = xmlConfig.getStatisticalInformationEvaluators4Type(packetSemanticType);
		sfss.addAll(xmlConfig.getStatisticalInformationEvaluators4Type(-1));
		for (final StatisticalInformationEvaluator sfs : sfss) {
			if ((sfs != null) && !((sfs.getExpression() == null) || sfs.getExpression().equals(""))) {
				try {
					sfs.parse(packet);
				} catch (final SpyglassPacketException e) {
					log.error("Error parsing a packet in the " + getHumanReadableName()
							+ ".\r\nPlease check the values in the StringFormatter for semantic type " + packetSemanticType + "!", e);
				}

			}
		}

		// if the semantic type is new, the evaluator might not have a label, yet
		if (newSemanticType) {
			synchronized (evaluators) {
				for (final StatisticalInformationEvaluator sfs : sfss) {
					evaluators.add(sfs);
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
		totalPacketCount = 0;
		pNCountTimerTask.reset();
		synchronized (evaluators) {
			packetSemanticTypes.clear();
			evaluators = new TreeSet<StatisticalInformationEvaluator>(sfSettings);
		}
		refreshStatIEConf();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Performs internal refreshments of the configuration parameters of the statistical information
	 * evaluators.
	 */
	private void refreshStatIEConf() {

		if ((evaluators != null) && (widget != null)) {
			widget.getDisplay().asyncExec(new Runnable() {
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					// the widget might have been disposed while we were waiting
					if ((widget == null) || widget.isDisposed()) {
						return;
					}
					final Collection<StatisticalInformationEvaluator> retain = new TreeSet<StatisticalInformationEvaluator>();
					synchronized (evaluators) {
						for (final StatisticalInformationEvaluator eval : evaluators) {
							final int semanticType = eval.getSemanticType();
							if ((semanticType == -1) || packetSemanticTypes.contains(semanticType)) {
								retain.add(eval);
							}
						}
						evaluators.retainAll(retain);

					}
					widget.retaingLabels(retain);
					updateWidget();
				}
			});
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * This helper updates all labels of the object's widget.<br>
	 * Since this accesses an SWT Widget, it is necessary to catch its monitor before this method
	 * can be run securely.
	 */
	private void updateWidget() {
		if ((widget != null) && !widget.isDisposed()) {
			if (isActive()) {
				widget.createOrUpdateTotalPacketCount("# Packets: " + totalPacketCount);
				widget.createOrUpdatePacketsPerSecond(pNCountTimerTask.getPacketsPerSecond());

				if (xmlConfig.isShowNumNodes()) {
					widget.createOrUpdateNumNodes(pNCountTimerTask.getNumNodes());
				} else {
					widget.removeNumNodes();
				}
				if (xmlConfig.isShowNodeDegree()) {
					widget.createOrUpdateAVGNodeDeg(avgNodeDegString);
				} else {
					widget.removeAVGNodeDeg();
				}

				for (final StatisticalInformationEvaluator evaluator : evaluators) {
					widget.createOrUpdateLabel(evaluator);
				}

				widget.pack(true);
			}
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
		private Map<Integer, Label> sfLabels = new TreeMap<Integer, Label>();

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
				final int key = siEvaluator.hashCode();
				Label label = sfLabels.get(key);

				if (label == null) {
					label = new Label(this, SWT.NONE);
					sfLabels.put(key, label);
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
		 *            an object's identifier which encapsulates the statistical information
		 */
		public void removeLabel(final Integer siEvaluator) {
			Display.getDefault().syncExec(new Runnable() {
				// ------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					synchronized (sfLabels) {
						final Label label = sfLabels.get(siEvaluator.hashCode());
						if (label != null) {
							label.dispose();
							sfLabels.remove(siEvaluator.hashCode());
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

			final Collection<Integer> obsoleteKeys = new LinkedList<Integer>();
			synchronized (sfLabels) {
				obsoleteKeys.addAll(sfLabels.keySet());
			}

			final Collection<Integer> evalKeys = new LinkedList<Integer>();
			for (final StatisticalInformationEvaluator eval : siEvaluators) {
				evalKeys.add(eval.hashCode());
			}

			obsoleteKeys.removeAll(evalKeys);
			for (final Integer integer : obsoleteKeys) {
				removeLabel(integer);
			}

		}

		/**
		 * Disposes all labels.
		 */
		public void clear() {
			removeAVGNodeDeg();
			removeNumNodes();
			final Collection<Integer> keySet = new LinkedList<Integer>();
			synchronized (sfLabels) {
				keySet.addAll(sfLabels.keySet());
			}
			for (final Integer key : keySet) {
				removeLabel(key);
			}
		}

	}

	// --------------------------------------------------------------------------------
	/**
	 * Task to refresh the packet and node counts
	 * 
	 * @author Sebastian Ebers
	 * 
	 */
	private class PNCountTimerTask extends TimerTask {

		AtomicInteger numPackets = new AtomicInteger(0);

		private String numNodes;
		private int times = 0;
		private String perSec = "";
		private String per30Sec = "     ";
		private String per60Sec = "     ";
		private StatisticalOperation statistics30sec = new StatisticalOperation(30, STATISTICAL_OPERATIONS.AVG);
		private StatisticalOperation statistics60sec = new StatisticalOperation(60, STATISTICAL_OPERATIONS.AVG);

		private volatile String packetsPerSecond = "# PPS: [ " + perSec + " | " + per30Sec + " | " + per60Sec + " ]";

		// --------------------------------------------------------------------------------
		/**
		 * Constructor
		 */
		public PNCountTimerTask() {
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
				per60Sec = new DecimalFormat("0.00").format(statistics60sec.getValue());
				per30Sec = new DecimalFormat("0.00").format(statistics30sec.getValue());
			} else if ((++times) >= 30) {
				per30Sec = new DecimalFormat("0.00").format(statistics30sec.getValue());
			}

			packetsPerSecond = "# PPS: [ " + perSec + " | " + per30Sec + " | " + per60Sec + " ]";
			numNodes = "# Nodes: " + getPluginManager().getNodePositioner().getNumNodes();

		}

		// --------------------------------------------------------------------------------
		/**
		 * @return the packetsPerSecond
		 */
		protected String getPacketsPerSecond() {
			return packetsPerSecond;
		}

		// --------------------------------------------------------------------------------
		/**
		 * @return the numNodes
		 */
		protected String getNumNodes() {
			return numNodes;
		}

		// --------------------------------------------------------------------------------
		/**
		 * Resets the timer tasks statistical values
		 */
		@SuppressWarnings("synthetic-access")
		public synchronized void reset() {
			times = 0;
			statistics30sec.reset();
			statistics60sec.reset();
			perSec = "     ";
			per30Sec = "     ";
			per60Sec = "     ";
			numPackets.set(0);
			numNodes = "# Nodes: " + getPluginManager().getNodePositioner().getNumNodes();
		}
	}

}