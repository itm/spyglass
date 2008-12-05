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
import java.util.TreeMap;

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
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
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
	private static final SpyglassLogger log = (SpyglassLogger) SpyglassLoggerFactory.getLogger(SimpleGlobalInformationPlugin.class);

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
	 * The widget where the information to be shown are placed in
	 */
	private SimpleGlobalInformationWidget widget;

	/**
	 * Object performing the statistical operation to determine the average node degree
	 */
	private StatisticalOperation avgNodeDegEvaluator;

	/**
	 * A list of semantic types of packages which provide information about neighborhood
	 * relationships
	 */
	private List<Integer> semanticTypes4Neighborhoods;

	/**
	 * Listens for changes of configuration properties
	 */
	private PropertyChangeListener pcl;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SimpleGlobalInformationPlugin() {
		xmlConfig = new SimpleGlobalInformationXMLConfig();
		avgNodeDegEvaluator = new StatisticalOperation(10, STATISTICAL_OPERATIONS.AVG);
		avgNodeDegString = "avg. node degree: ";
		semanticTypes4Neighborhoods = Tools.intArrayToIntegerList(xmlConfig.getSemanticTypes4Neighborhoods());
	}

	// --------------------------------------------------------------------------------
	@Override
	public void init(final PluginManager manager) {
		super.init(manager);
		pcl = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				evt.getPropertyName();
				refreshConfigurationParameters();
			}
		};
		xmlConfig.addPropertyChangeListener(pcl);
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

		String value = "";
		if (sfs != null) {
			try {
				value = sfs.parse(packet);
			} catch (final SpyglassPacketException e) {
				log.error("Error parsing a packet in the " + getHumanReadableName()
						+ ".\r\nPlease check the values in the StringFormatter for semantic type " + packetSemanticType + "!", e, false);
				value = sfs.getDescription() + " NaN";
			}

			final String val = value;
			synchronized (widget) {

				widget.getDisplay().syncExec(new Runnable() {
					@SuppressWarnings("synthetic-access")
					@Override
					public void run() {
						widget.createOrUpdateLabel(packetSemanticType, val);
					}
				});
			}

		}

	}

	// --------------------------------------------------------------------------------
	@Override
	public void shutdown() {
		super.shutdown();
		if ((widget != null) && !widget.isDisposed()) {
			widget.getDisplay().asyncExec(new Runnable() {
				// ------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					widget.clear();
					((GlobalInformationWidget) widget.getParent()).setShow(false);
				}
			});
		}
		xmlConfig.removePropertyChangeListener(pcl);
	}

	@Override
	public void reset() {
		if (widget != null) {
			widget.getDisplay().asyncExec(new Runnable() {
				// ------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					widget.clear();
				}
			});
		}
	}

	@Override
	protected void updateQuadTree() {
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
					final Collection<Integer> semanticTypes = new LinkedList<Integer>();
					for (final StatisticalInformationEvaluator sfs : sfSettings) {
						final int semanticType = sfs.getSemanticType();
						semanticTypes.add(semanticType);
						widget.createOrUpdateLabel(semanticType, sfs.getDescription());
					}
					widget.retaingLabels(semanticTypes);

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

		if (!widget.isDisposed()) {
			widget.getDisplay().asyncExec(new Runnable() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {

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
			}
			labelNumNodes.setText(num);
			labelNumNodes.pack();
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
		 * Creates or updates the label which provides information related to a certain semantic
		 * type.<br>
		 * If the label already exists, it will be updated, otherwise the label will be created.
		 * 
		 * @param semanticType
		 *            the semantic type
		 * @param text
		 *            the text that will be displayed
		 */
		public void createOrUpdateLabel(final int semanticType, String text) {
			synchronized (sfLabels) {
				Label label = sfLabels.get(semanticType);
				// null cannot be handled by a label. However, its semantic is the same as the empty
				// string
				if (text == null) {
					text = "";
				}
				if (label == null) {
					label = new Label(this, SWT.NONE);
					sfLabels.put(semanticType, label);
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
		 * Removes the label which provides information related to a certain semantic type.
		 * 
		 * @param semanticType
		 *            the related semantic type
		 */
		public void removeLabel(final int semanticType) {
			Display.getDefault().syncExec(new Runnable() {
				// --------------------------------------------------------------------------------
				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					synchronized (sfLabels) {
						final Label label = sfLabels.get(semanticType);
						if (label != null) {
							label.dispose();
							sfLabels.remove(semanticType);
						}
					}
				}
			});
			super.getParent().redraw();
		}

		// --------------------------------------------------------------------------------
		/**
		 * Removes all labels which are related to semantic types which cannot be found in the
		 * provided list of valid semantic types.
		 * 
		 * @param semanticTypes
		 *            the list with valid semantic types
		 */
		public void retaingLabels(final Collection<Integer> semanticTypes) {
			final Collection<Integer> keySet = new LinkedList<Integer>();
			synchronized (sfLabels) {
				keySet.addAll(sfLabels.keySet());
			}
			for (final Integer key : keySet) {
				if (!semanticTypes.contains(key)) {
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
			final Collection<Integer> keySet = new LinkedList<Integer>();
			synchronized (sfLabels) {
				keySet.addAll(sfLabels.keySet());
			}
			for (final Integer key : keySet) {
				removeLabel(key);
			}
		}

	}

}