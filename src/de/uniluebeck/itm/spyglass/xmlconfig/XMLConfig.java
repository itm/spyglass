package de.uniluebeck.itm.spyglass.xmlconfig;

import java.lang.reflect.Method;

import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;

public abstract class XMLConfig extends PropertyBean {
	
	/**
	 * Copy the data from <code>other</code> into this object.
	 * 
	 * @param other
	 *            another PluginXMLConfig object. <code>other</code> must be of exactly the same
	 *            type as the object this method is called on.
	 * @throws RuntimeException
	 *             if there is a problem during reflection. Such an exception can almost surely only
	 *             be caused by an implementation bug in the XMLConfig class.
	 * @throws ClassCastException
	 *             if the given object is of the wrong type.
	 */
	public final void overwriteWith(final XMLConfig other) {
		
		if (!this.getClass().equals(other.getClass())) {
			throw new ClassCastException("Cannot cast " + other.getClass() + " to "
					+ this.getClass() + ".");
		}
		
		// From here on we assume that "this" and "other" both have the same type
		final Class<? extends XMLConfig> clazz = this.getClass();
		
		for (final Method setter : clazz.getMethods()) {
			
			// iterate through all setter methods
			if (setter.getName().startsWith("set")) {
				try {
					
					final Method getter = findCorrespondingGetterMethod(clazz, setter);
					
					final Object newValue = getter.invoke(other);
					setter.invoke(this, newValue);
					
				} catch (final NoSuchMethodException e) {
					throw new RuntimeException("Could not find corresponding getter method to "
							+ setter.getName() + " in class " + clazz.getName(), e);
				} catch (final Exception e) {
					throw new RuntimeException(
							"Something wicked happened during reflection. Maybe there is something wrong with the getter/setter methods of class "
									+ clazz.getName(), e);
				}
				
			}
		}
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * Finds the corresponding getter-Method of the given setter.
	 * 
	 * @param clazz
	 * @param setter
	 * @return
	 * @throws NoSuchMethodException
	 */
	private Method findCorrespondingGetterMethod(final Class<? extends XMLConfig> clazz,
			final Method setter) throws NoSuchMethodException {
		
		final String propertyName = setter.getName().substring(3);
		
		for (final Method getter : clazz.getMethods()) {
			if (getter.getName().equalsIgnoreCase("get" + propertyName)) {
				return getter;
			}
		}
		throw new NoSuchMethodException("Could not find a corresponding getter for "
				+ setter.getName() + ".");
	}
}
