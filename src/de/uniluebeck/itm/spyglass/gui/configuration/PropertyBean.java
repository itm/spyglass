package de.uniluebeck.itm.spyglass.gui.configuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// --------------------------------------------------------------------------------
/**
 * This class provides an number of property change listeners.
 * 
 * All implementing subclasses should call {@link firePropertyChange} if a property changes.
 * 
 * @author Dariush Forouher
 * 
 */
public abstract class PropertyBean {
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	protected void firePropertyChange(final String propertyName, final Object oldValue,
			final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
}
