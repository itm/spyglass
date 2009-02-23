package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// --------------------------------------------------------------------------------
/**
 * This class provides an number of property change listeners.
 * 
 * All implementing subclasses should call
 * {@link PropertyBean#firePropertyChange(String, Object, Object)} if a property changes.
 * 
 * @author Dariush Forouher
 * @author Sebastian Ebers
 * 
 */
public abstract class PropertyBean {

	/** This is a utility class that can be used by beans that support bound properties. */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

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
	public void addPropertyChangeListeners(final PropertyBean other) {
		final PropertyChangeListener[] listeners = other.propertyChangeSupport.getPropertyChangeListeners();
		for (int i = 0; i < listeners.length; i++) {
			propertyChangeSupport.addPropertyChangeListener(listeners[i]);
		}
	}

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
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

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
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

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
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Report a bound property update to any registered listeners. No event is fired if old and new
	 * are equal and non-null.
	 * 
	 * <p>
	 * This is merely a convenience wrapper around the more general firePropertyChange method that
	 * takes {@code PropertyChangeEvent} value.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	// --------------------------------------------------------------------------------
	/**
	 * Removes all registered property change listeners.
	 */
	protected void removeAllPropertyChangeListeners() {
		final PropertyChangeListener[] listeners = propertyChangeSupport.getPropertyChangeListeners();
		for (int i = 0; i < listeners.length; i++) {
			removePropertyChangeListener(listeners[i]);
		}
	}

}
