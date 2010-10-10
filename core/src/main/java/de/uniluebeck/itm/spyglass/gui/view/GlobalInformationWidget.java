/*
 * -------------------------------------------------------------------------------- This file is
 * part of the WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet
 * (www.swarmnet.de) project SpyGlass is free software; you can redistribute it and/or modify it
 * under the terms of the BSD License. Refer to spyglass-licence.txt file in the root of the
 * SpyGlass source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandItem;

import de.uniluebeck.itm.spyglass.plugin.globalinformation.GlobalInformationPlugin;

// --------------------------------------------------------------------------------
/**
 * Instances of this class are widgets which can be used to display global information.
 * 
 * @author Sebastian Ebers
 * 
 */
public class GlobalInformationWidget extends Composite {

	private GlobalInformationBar giBar;
	private final GlobalInformationPlugin plugin;
	private ExpandItem expandItem;
	private boolean show = true;

	// --------------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param giBar
	 *            the bar where the widget is attached to
	 * @param style
	 *            the style to be used
	 * @param plugin
	 *            the plug-in which provides the information
	 */
	public GlobalInformationWidget(final GlobalInformationBar giBar, final int style, final GlobalInformationPlugin plugin) {
		super(giBar.getExpandBar(), style);
		this.plugin = plugin;
		this.giBar = giBar;
		plugin.getXMLConfig().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {

				if (evt.getPropertyName().equals("name") || evt.getPropertyName().equals("active") || evt.getPropertyName().equals("visible")) {
					redraw();
				}

			}
		});
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns whether the widget is to be displayed.
	 * 
	 * @return <code>true</code> if the widget is to be displayed
	 */
	public boolean isShow() {
		return plugin.isActive() && plugin.isVisible() && show;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the {@link ExpandItem} which is attached to the global information bar
	 * 
	 * @return the {@link ExpandItem} which is attached to the global information bar
	 */
	public ExpandItem getExpandItem() {
		return expandItem;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Sets the {@link ExpandItem} which is attached to the global information bar
	 * 
	 * @param expandItem
	 *            the {@link ExpandItem} which is attached to the global information bar
	 * @exception IllegalArgumentException
	 *                is thrown if the argument is <code>null</code>
	 */
	public void setExpandItem(final ExpandItem expandItem) {
		if (expandItem != null) {
			this.expandItem = expandItem;
		} else {
			throw new IllegalArgumentException("The argument cannot be null");
		}
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the plug-in which provides the information
	 * 
	 * @return the plug-in which provides the information
	 */
	public GlobalInformationPlugin getPlugin() {
		return plugin;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Returns the title to be used for the information
	 * 
	 * @return the title to be used for the information
	 */
	public String getTitle() {
		return plugin.getInstanceName();
	}

	// --------------------------------------------------------------------------------
	@Override
	public void redraw() {
		super.redraw();
		giBar.refresh(this);
	}

	// --------------------------------------------------------------------------------
	/**
	 * @param show
	 *            the show to set
	 */
	public void setShow(final boolean show) {
		this.show = show;
	}

}
