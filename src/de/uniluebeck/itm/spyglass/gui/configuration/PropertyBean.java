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
public abstract class PropertyBean implements SpyglassPropertyChangeSupport {

	/** This is a utility class that can be used by beans that support bound properties. */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.SpyglassPropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.SpyglassPropertyChangeSupport#addPropertyChangeListeners(de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean)
	 */
	public void addPropertyChangeListeners(final PropertyBean other) {
		final PropertyChangeListener[] listeners = other.propertyChangeSupport.getPropertyChangeListeners();
		for (int i = 0; i < listeners.length; i++) {
			propertyChangeSupport.addPropertyChangeListener(listeners[i]);
		}
	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.SpyglassPropertyChangeSupport#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.SpyglassPropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	// --------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uniluebeck.itm.spyglass.gui.configuration.SpyglassPropertyChangeSupport#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
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

	@Override
	protected PropertyBean clone() throws CloneNotSupportedException {
		final PropertyBean b = (PropertyBean) super.clone();

		// we DONT want to clone the list of observers, so clear it
		b.propertyChangeSupport = new PropertyChangeSupport(b);

		return b;
	}

}
