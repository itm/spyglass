/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass.
 * Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD License.
 * Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for further
 * details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.plugin.GlobalInformation;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.plugin.globalinformation.GlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.plugin.simpleglobalinformation.SimpleGlobalInformationPlugin;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are used as panel to display global information on the right-hand side of
 * the drawing area.<br>
 * Actually, an {@link ExpandBar} will be used as underlying widget.
 * 
 * @see ExpandBar
 * @see ExpandItem
 * @see GlobalInformation
 * 
 * @author Sebastian Ebers
 * 
 */
public class GlobalInformationBar extends PropertyBean {

	/**
	 * Widgets containing the actual information and which are wrapped in {@link ExpandItem}s
	 */
	private final ConcurrentLinkedQueue<GlobalInformationWidget> widgets;

	/**
	 * The {@link ExpandBar} where the information are to be attached and shown
	 */
	private ExpandBar expandBar;

	/**
	 * Indicates whether visible widgets are present at the global information bar
	 */
	private boolean hasVisibleWidgets;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the layout style to use
	 * @param spyglass
	 *            a spyglass instance
	 */
	public GlobalInformationBar(final Composite parent, final int style, final Spyglass spyglass) {
		expandBar = new ExpandBar(parent, style);
		widgets = new ConcurrentLinkedQueue<GlobalInformationWidget>();
		setPluginManager(spyglass.getPluginManager());
		expandBar.setSpacing(3);
		spyglass.getConfigStore().getSpyglassConfig().addPropertyChangeListener(new PropertyChangeListener() {
			// --------------------------------------------------------------------------------
			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("pluginManager")) {
					setPluginManager((PluginManager) evt.getNewValue());
				}
			}
		});
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the bar where the widgets of all {@link SimpleGlobalInformationPlugin}s are placed
	 * 
	 * @return the bar where the widgets of all {@link SimpleGlobalInformationPlugin}s are placed
	 */
	public ExpandBar getExpandBar() {
		return expandBar;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the {@link PluginManager}. The object will initialize the management of all plug-ins of
	 * the type {@link GlobalInformationPlugin}. Additionally, a listener which listens for changes
	 * of the plug-in list managed by the {@link PluginManager} will be initiated.
	 * 
	 * @param pluginManager
	 *            the pluginManager to set
	 */
	private void setPluginManager(final PluginManager pluginManager) {

		// set all GlobalInformationPlugins into the managed state
		for (final Plugin p : pluginManager.getActivePlugins()) {
			if (p instanceof GlobalInformationPlugin) {
				manage((GlobalInformationPlugin) p);
			}
		}

		// manage SGI plug-ins which are currently inactive as soon as they change that state
		for (final Plugin p : pluginManager.getPlugins()) {
			if (p instanceof GlobalInformationPlugin) {
				p.getXMLConfig().addPropertyChangeListener(new PropertyChangeListener() {
					// --------------------------------------------------------------------------------
					@SuppressWarnings("synthetic-access")
					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_ACTIVE) && ((Boolean) evt.getNewValue())) {
							manage((SimpleGlobalInformationPlugin) p);
						}

						if (evt.getPropertyName().equals(PluginXMLConfig.PROPERTYNAME_VISIBLE)) {
							setHasVisibleWidgets(checkVisibleWidgets());
						}
					}

				});
			}
		}

		// create a listener to be able to handle instantiations of new plug-ins and finalizations
		// of existing ones appropriately
		pluginManager.addPluginListChangeListener(new PluginListChangeListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
				if (p instanceof GlobalInformationPlugin) {
					switch (what) {
						case NEW_PLUGIN:
							manage((GlobalInformationPlugin) p);
							break;

						case PLUGIN_REMOVED:
							detach((GlobalInformationPlugin) p);
							break;

						default:
							break;
					}
					setHasVisibleWidgets(checkVisibleWidgets());
				}
			}
		});
		setHasVisibleWidgets(checkVisibleWidgets());
	}

	// --------------------------------------------------------------------------------
	/**
	 * Checks if any plug-in which might currently be attached might have a visible widget
	 */
	private boolean checkVisibleWidgets() {
		for (final GlobalInformationWidget w : widgets) {
			if (w.getPlugin().isActive() && w.getPlugin().isVisible()) {
				return true;
			}
		}
		return false;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets whether any plug-in which might currently be attached might have a visible widget
	 * 
	 * @param hasVisibleWidgets
	 *            <code>true</code> if any plug-in which might currently be attached might have a
	 *            visible widget
	 */
	private void setHasVisibleWidgets(final boolean hasVisibleWidgets) {
		final boolean hadVisisbleWidgets = this.hasVisibleWidgets;
		this.hasVisibleWidgets = hasVisibleWidgets;
		firePropertyChange("hasVisibleWidgets", hadVisisbleWidgets, hasVisibleWidgets);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether any plug-in which might currently be attached might have a visible widget
	 * 
	 * @return <code>true</code> if any plug-in which might currently be attached might have a
	 *         visible widget
	 */
	public boolean isHasVisibleWidgets() {
		return hasVisibleWidgets;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets a plug-in to be managed by the global information bar.<br>
	 * If the plug-in is already managed, this call will be ignored.
	 * 
	 * @param plugin
	 *            the plug-in to be managed
	 */
	public void manage(final GlobalInformationPlugin plugin) {

		// first check if the plug-in is already managed
		for (final GlobalInformationWidget w : widgets) {
			// compare the instances and not the objects' contents
			if (w.getPlugin() == plugin) {
				return;
			}
		}
		addNewItem(plugin);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Detaches a plug-in from its state to be managed by the global information bar
	 * 
	 * @param plugin
	 *            the plug-in to be detached
	 */
	public void detach(final GlobalInformationPlugin plugin) {
		synchronized (widgets) {
			for (final GlobalInformationWidget w : widgets) {
				// the instances have to be compared here
				if (w.getPlugin() == plugin) {
					detach(w.getExpandItem());
					widgets.remove(w);
				}
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Detaches an item from the global information bar
	 * 
	 * @param item
	 *            the item to be detached
	 */
	private void detach(final ExpandItem item) {
		synchronized (widgets) {
			if (!item.isDisposed()) {
				item.setExpanded(false);
				item.dispose();
			}
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Attaches a plug-in's global information widget to the global information bar
	 * 
	 * @param p
	 *            a plug-in
	 */
	private void addNewItem(final GlobalInformationPlugin p) {

		// create a widget a plug-in can use to place its information
		final GlobalInformationWidget widget = new GlobalInformationWidget(this, SWT.NONE, p);
		p.setWidget(widget);
		// create an item which will be attached to the bar and which will
		// contain the previously created widget
		attach(widget);
		synchronized (widgets) {
			widgets.add(widget);
		}
	}

	/**
	 * Attaches a widget to the panel
	 * 
	 * @param widget
	 *            the widget to attach
	 */
	private void attach(final GlobalInformationWidget widget) {

		final ExpandItem item = new ExpandItem(expandBar, SWT.NONE);
		item.setText(widget.getTitle());
		item.setHeight(widget.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item.setControl(widget);
		widget.setExpandItem(item);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Refreshes a widget's display in the {@link ExpandBar}.
	 * 
	 * @param widget
	 *            the widget which display is refreshed
	 */
	public void refresh(final GlobalInformationWidget widget) {
		final Display display = expandBar.getDisplay();
		if (display != null) {
			display.asyncExec(new Runnable() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {

					// the widget might have been disposed while we were waiting
					if (widget.isDisposed()) {
						return;
					}

					final ExpandItem item = widget.getExpandItem();

					// if the widget is not to be shown or disposed
					if (!widget.isShow() && !item.isDisposed()) {
						detach(item);
					}

					// if a widget is to be shown but currently not visible
					// on the bar, a new one has to be created
					else if (widget.isShow() && item.isDisposed()) {
						attach(widget);
					}

					// if a widget is to be shown and also visible on the bar,
					// refresh its contents
					else if (widget.isShow() && !item.isDisposed()) {
						item.setText(widget.getTitle());
						widget.pack(true);
						item.setHeight(widget.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
						item.setControl(widget);
					}
				}
			});
		}

	}

}
