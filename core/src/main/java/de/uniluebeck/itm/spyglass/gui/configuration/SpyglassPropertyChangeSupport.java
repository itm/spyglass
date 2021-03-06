/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeListener;

// --------------------------------------------------------------------------------
/**
 * @author sebers
 * 
 */
public interface SpyglassPropertyChangeSupport {

	// --------------------------------------------------------------------------------
	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered for all
	 * properties. The same listener object may be added more than once, and will be called as many
	 * times as it is added. If <code>listener</code> is null, no exception is thrown and no action
	 * is taken.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener);

	// --------------------------------------------------------------------------------
	/**
	 * Add PropertyChangeListeners to the listener list. The listeners are registered for all
	 * properties. The same listener object may be added more than once, and will be called as many
	 * times as it is added. If <code>listener</code> is null, no exception is thrown and no action
	 * is taken.
	 * 
	 * @param other
	 *            another {@link PropertyBean} which listeners are to be added
	 */
	public void addPropertyChangeListeners(final PropertyBean other);

	// --------------------------------------------------------------------------------
	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be invoked only when
	 * a call on firePropertyChange names that specific property. The same listener object may be
	 * added more than once. For each property, the listener will be invoked the number of times it
	 * was added for that property. If <code>propertyName</code> or <code>listener</code> is null,
	 * no exception is thrown and no action is taken.
	 * 
	 * @param propertyName
	 *            The name of the property to listen on.
	 * @param listener
	 *            The PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

	// --------------------------------------------------------------------------------
	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a PropertyChangeListener
	 * that was registered for all properties. If <code>listener</code> was added more than once to
	 * the same event source, it will be notified one less time after being removed. If
	 * <code>listener</code> is null, or was never added, no exception is thrown and no action is
	 * taken.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener);

	// --------------------------------------------------------------------------------
	/**
	 * Remove a PropertyChangeListener for a specific property. If <code>listener</code> was added
	 * more than once to the same event source for the specified property, it will be notified one
	 * less time after being removed. If <code>propertyName</code> is null, no exception is thrown
	 * and no action is taken. If <code>listener</code> is null, or was never added for the
	 * specified property, no exception is thrown and no action is taken.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener);

}